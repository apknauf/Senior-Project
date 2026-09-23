package com.workoutapp.repository;

import com.workoutapp.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByUserIdOrderByWorkoutDateDesc(Long userId);
}
