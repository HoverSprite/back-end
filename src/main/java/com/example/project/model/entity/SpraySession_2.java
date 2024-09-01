package com.example.project.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "SPRAY_SESSION")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpraySession_2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SPRAY_SESSION_ID", nullable = false)
    private Long id;

    @Column(name = "DATE", nullable = false)
    private LocalDate date;

    @Column(name = "START_TIME", nullable = false)
    private LocalTime startTime;

    @Column(name = "END_TIME", nullable = false)
    private LocalTime endTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPRAY_ORDER_ID", nullable = false)
    @JsonBackReference
    private SprayOrder sprayOrder;
}
