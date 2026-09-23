const API = '/api';

const state = {
  user: JSON.parse(localStorage.getItem('ironlog_user') || 'null'),
  exercises: [],
  editingWorkoutId: null,
};

// ---------- helpers ----------
async function api(path, options = {}) {
  const res = await fetch(API + path, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  const isJson = res.headers.get('content-type')?.includes('application/json');
  const body = isJson ? await res.json() : null;
  if (!res.ok) {
    const msg = body?.error || Object.values(body || {})[0] || 'Something went wrong';
    throw new Error(msg);
  }
  return body;
}

function $(sel, root = document) { return root.querySelector(sel); }
function $all(sel, root = document) { return [...root.querySelectorAll(sel)]; }

function showView(id) {
  $all('.view').forEach(v => v.classList.remove('active-view'));
  $(`#${id}`).classList.add('active-view');
}

function showPage(id) {
  $all('.page').forEach(p => p.classList.remove('active'));
  $all('.navlink').forEach(n => n.classList.remove('active'));
  $(`#page-${id}`).classList.add('active');
  const nav = $(`.navlink[data-page="${id}"]`);
  if (nav) nav.classList.add('active');
}

// ---------- auth ----------
$all('.tab').forEach(tab => {
  tab.addEventListener('click', () => {
    $all('.tab').forEach(t => t.classList.remove('active'));
    $all('.auth-form').forEach(f => f.classList.remove('active'));
    tab.classList.add('active');
    $(`#form-${tab.dataset.tab}`).classList.add('active');
  });
});

$('#form-login').addEventListener('submit', async (e) => {
  e.preventDefault();
  const msg = $('#login-msg');
  msg.textContent = '';
  const fd = new FormData(e.target);
  try {
    const user = await api('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username: fd.get('username'), password: fd.get('password') }),
    });
    setUser(user);
  } catch (err) {
    msg.textContent = err.message;
  }
});

$('#form-register').addEventListener('submit', async (e) => {
  e.preventDefault();
  const msg = $('#register-msg');
  msg.textContent = '';
  const fd = new FormData(e.target);
  try {
    const user = await api('/auth/register', {
      method: 'POST',
      body: JSON.stringify({
        username: fd.get('username'),
        email: fd.get('email'),
        password: fd.get('password'),
      }),
    });
    setUser(user);
  } catch (err) {
    msg.textContent = err.message;
  }
});

$('#logout').addEventListener('click', () => {
  state.user = null;
  localStorage.removeItem('ironlog_user');
  showView('view-auth');
});

function setUser(user) {
  state.user = user;
  localStorage.setItem('ironlog_user', JSON.stringify(user));
  $('#who').textContent = user.username;
  showView('view-app');
  boot();
}

// ---------- navigation ----------
$all('.navlink').forEach(nav => {
  nav.addEventListener('click', () => {
    showPage(nav.dataset.page);
    if (nav.dataset.page === 'dashboard') loadWorkouts();
    if (nav.dataset.page === 'new-workout') resetWorkoutForm();
  });
});

$all('[data-page-link]').forEach(btn => {
  btn.addEventListener('click', () => {
    showPage(btn.dataset.pageLink);
    $(`.navlink[data-page="${btn.dataset.pageLink}"]`)?.classList.add('active');
  });
});

// ---------- exercises ----------
async function loadExercises() {
  state.exercises = await api('/exercises');
  renderExerciseList();
}

function renderExerciseList() {
  const list = $('#exercise-list');
  list.innerHTML = '';
  state.exercises.forEach(ex => {
    const row = document.createElement('div');
    row.className = 'exercise-item';
    row.innerHTML = `
      <div>
        <div class="exercise-item-name">${escapeHtml(ex.name)}</div>
        <div class="exercise-item-meta">${escapeHtml(ex.description || '')}</div>
      </div>
      ${ex.muscleGroup ? `<span class="exercise-item-tag">${escapeHtml(ex.muscleGroup)}</span>` : ''}
    `;
    list.appendChild(row);
  });
}

$('#form-exercise').addEventListener('submit', async (e) => {
  e.preventDefault();
  const fd = new FormData(e.target);
  await api('/exercises', {
    method: 'POST',
    body: JSON.stringify({
      name: fd.get('name'),
      muscleGroup: fd.get('muscleGroup'),
      description: fd.get('description'),
    }),
  });
  e.target.reset();
  loadExercises();
});

// ---------- workout entry rows ----------
function addEntryRow(prefill = null) {
  const tpl = $('#entry-row-template');
  const row = tpl.content.firstElementChild.cloneNode(true);
  const select = $('.entry-exercise', row);
  select.innerHTML = state.exercises
    .map(ex => `<option value="${ex.id}">${escapeHtml(ex.name)}</option>`)
    .join('');

  if (prefill) {
    select.value = prefill.exercise.id;
    $('.entry-sets', row).value = prefill.sets;
    $('.entry-reps', row).value = prefill.reps;
    $('.entry-weight', row).value = prefill.weight ?? '';
  }

  $('.entry-remove', row).addEventListener('click', () => row.remove());
  $('#entry-list').appendChild(row);
}

$('#add-entry').addEventListener('click', () => addEntryRow());

function resetWorkoutForm() {
  state.editingWorkoutId = null;
  $('#form-workout').reset();
  $('#entry-list').innerHTML = '';
  $('#workout-msg').textContent = '';
  $('#workout-form-title').textContent = 'Log a workout';
  $('[name="workoutDate"]', $('#form-workout')).value = new Date().toISOString().slice(0, 10);
  addEntryRow();
}

$('#form-workout').addEventListener('submit', async (e) => {
  e.preventDefault();
  const msg = $('#workout-msg');
  msg.textContent = '';
  msg.classList.remove('success');

  const fd = new FormData(e.target);
  const entries = $all('.entry-row').map(row => ({
    exerciseId: Number($('.entry-exercise', row).value),
    sets: Number($('.entry-sets', row).value),
    reps: Number($('.entry-reps', row).value),
    weight: $('.entry-weight', row).value ? Number($('.entry-weight', row).value) : null,
  }));

  if (entries.length === 0) {
    msg.textContent = 'Add at least one exercise.';
    return;
  }

  const payload = {
    userId: state.user.id,
    name: fd.get('name'),
    workoutDate: fd.get('workoutDate'),
    notes: fd.get('notes'),
    entries,
  };

  try {
    if (state.editingWorkoutId) {
      await api(`/workouts/${state.editingWorkoutId}`, { method: 'PUT', body: JSON.stringify(payload) });
    } else {
      await api('/workouts', { method: 'POST', body: JSON.stringify(payload) });
    }
    msg.textContent = 'Saved!';
    msg.classList.add('success');
    setTimeout(() => {
      showPage('dashboard');
      loadWorkouts();
    }, 500);
  } catch (err) {
    msg.textContent = err.message;
  }
});

// ---------- workout list ----------
async function loadWorkouts() {
  const workouts = await api(`/workouts?userId=${state.user.id}`);
  const list = $('#workout-list');
  const empty = $('#empty-state');
  list.innerHTML = '';

  if (workouts.length === 0) {
    empty.hidden = false;
    return;
  }
  empty.hidden = true;

  workouts.forEach(w => {
    const card = document.createElement('div');
    card.className = 'workout-card';
    const exerciseNames = (w.entries || []).map(en => en.exercise.name).join(', ') || 'No exercises logged';
    card.innerHTML = `
      <div class="workout-card-head">
        <h3>${escapeHtml(w.name)}</h3>
        <span class="workout-card-date">${formatDate(w.workoutDate)}</span>
      </div>
      <div class="workout-card-exercises">${escapeHtml(exerciseNames)}</div>
      <div class="workout-card-actions">
        <button class="btn-ghost" data-edit="${w.id}">Edit</button>
        <button class="btn-ghost" data-delete="${w.id}">Delete</button>
      </div>
    `;
    $('[data-edit]', card).addEventListener('click', (ev) => { ev.stopPropagation(); editWorkout(w); });
    $('[data-delete]', card).addEventListener('click', async (ev) => {
      ev.stopPropagation();
      if (confirm('Delete this workout?')) {
        await api(`/workouts/${w.id}`, { method: 'DELETE' });
        loadWorkouts();
      }
    });
    list.appendChild(card);
  });
}

function editWorkout(w) {
  state.editingWorkoutId = w.id;
  showPage('new-workout');
  $('#workout-form-title').textContent = 'Edit workout';
  const form = $('#form-workout');
  form.reset();
  $('[name="name"]', form).value = w.name;
  $('[name="workoutDate"]', form).value = w.workoutDate;
  $('[name="notes"]', form).value = w.notes || '';
  $('#entry-list').innerHTML = '';
  (w.entries || []).forEach(en => addEntryRow(en));
  if ((w.entries || []).length === 0) addEntryRow();
}

// ---------- utils ----------
function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

function formatDate(dateStr) {
  const d = new Date(dateStr + 'T00:00:00');
  return d.toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' });
}

// ---------- boot ----------
async function boot() {
  await loadExercises();
  showPage('dashboard');
  await loadWorkouts();
  resetWorkoutForm();
}

if (state.user) {
  $('#who').textContent = state.user.username;
  showView('view-app');
  boot();
} else {
  showView('view-auth');
}
