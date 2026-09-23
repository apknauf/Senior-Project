package com.workoutapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "workout_entries")
public class WorkoutEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Min(1)
    @Column(nullable = false)
    private Integer sets;

    @Min(1)
    @Column(nullable = false)
    private Integer reps;

    @Column
    private Double weight; // in lbs or kg, client decides units

    @Column(name = "order_index")
    private Integer orderIndex = 0;

    public WorkoutEntry() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Workout getWorkout() { return workout; }
    public void setWorkout(Workout workout) { this.workout = workout; }

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }

    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}
