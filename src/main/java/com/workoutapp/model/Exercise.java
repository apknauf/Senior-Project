package com.workoutapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "muscle_group", length = 50)
    private String muscleGroup;

    @Column(length = 500)
    private String description;

    public Exercise() {}

    public Exercise(String name, String muscleGroup, String description) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMuscleGroup() { return muscleGroup; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
