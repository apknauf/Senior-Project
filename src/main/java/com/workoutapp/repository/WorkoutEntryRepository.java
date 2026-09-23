package com.workoutapp.repository;

import com.workoutapp.model.WorkoutEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutEntryRepository extends JpaRepository<WorkoutEntry, Long> {
}
