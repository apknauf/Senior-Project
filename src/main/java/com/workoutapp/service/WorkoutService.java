package com.workoutapp.service;

import com.workoutapp.dto.WorkoutDtos;
import com.workoutapp.model.*;
import com.workoutapp.repository.ExerciseRepository;
import com.workoutapp.repository.UserRepository;
import com.workoutapp.repository.WorkoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutService(WorkoutRepository workoutRepository,
                           UserRepository userRepository,
                           ExerciseRepository exerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public List<Workout> getWorkoutsForUser(Long userId) {
        return workoutRepository.findByUserIdOrderByWorkoutDateDesc(userId);
    }

    public Workout getById(Long id) {
        return workoutRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workout not found: " + id));
    }

    @Transactional
    public Workout create(WorkoutDtos.WorkoutRequest req) {
        User user = userRepository.findById(req.userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.userId));

        Workout workout = new Workout();
        workout.setUser(user);
        workout.setName(req.name);
        workout.setWorkoutDate(req.workoutDate);
        workout.setNotes(req.notes);
        workout.setEntries(buildEntries(req, workout));

        return workoutRepository.save(workout);
    }

    @Transactional
    public Workout update(Long id, WorkoutDtos.WorkoutRequest req) {
        Workout workout = getById(id);
        workout.setName(req.name);
        workout.setWorkoutDate(req.workoutDate);
        workout.setNotes(req.notes);

        workout.getEntries().clear();
        workout.getEntries().addAll(buildEntries(req, workout));

        return workoutRepository.save(workout);
    }

    public void delete(Long id) {
        workoutRepository.deleteById(id);
    }

    private List<WorkoutEntry> buildEntries(WorkoutDtos.WorkoutRequest req, Workout workout) {
        List<WorkoutEntry> entries = new ArrayList<>();
        if (req.entries == null) return entries;

        int order = 0;
        for (WorkoutDtos.EntryRequest er : req.entries) {
            Exercise exercise = exerciseRepository.findById(er.exerciseId)
                    .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + er.exerciseId));

            WorkoutEntry entry = new WorkoutEntry();
            entry.setWorkout(workout);
            entry.setExercise(exercise);
            entry.setSets(er.sets);
            entry.setReps(er.reps);
            entry.setWeight(er.weight);
            entry.setOrderIndex(er.orderIndex != null ? er.orderIndex : order++);
            entries.add(entry);
        }
        return entries;
    }
}
