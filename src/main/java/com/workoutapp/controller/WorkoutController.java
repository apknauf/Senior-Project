package com.workoutapp.controller;

import com.workoutapp.dto.WorkoutDtos;
import com.workoutapp.model.Workout;
import com.workoutapp.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping
    public List<Workout> getByUser(@RequestParam Long userId) {
        return workoutService.getWorkoutsForUser(userId);
    }

    @GetMapping("/{id}")
    public Workout getById(@PathVariable Long id) {
        return workoutService.getById(id);
    }

    @PostMapping
    public Workout create(@Valid @RequestBody WorkoutDtos.WorkoutRequest req) {
        return workoutService.create(req);
    }

    @PutMapping("/{id}")
    public Workout update(@PathVariable Long id, @Valid @RequestBody WorkoutDtos.WorkoutRequest req) {
        return workoutService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workoutService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
