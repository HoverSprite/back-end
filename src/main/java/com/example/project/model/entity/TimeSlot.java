package com.example.project.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a time slot in the system.
 * Each time slot defines a start and end time for when bookings can be scheduled.
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "time_slots")
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Start time cannot be null")
    @Column(nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time cannot be null")
    @Column(nullable = false)
    private LocalTime endTime;
}
