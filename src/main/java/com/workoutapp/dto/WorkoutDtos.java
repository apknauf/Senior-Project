package com.workoutapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class WorkoutDtos {

    public static class EntryRequest {
        @NotNull
        public Long exerciseId;
        @Min(1)
        public Integer sets;
        @Min(1)
        public Integer reps;
        public Double weight;
        public Integer orderIndex;
    }

    public static class WorkoutRequest {
        @NotNull
        public Long userId;
        @NotBlank
        public String name;
        @NotNull
        public LocalDate workoutDate;
        public String notes;
        @Valid
        public List<EntryRequest> entries;
    }
}
