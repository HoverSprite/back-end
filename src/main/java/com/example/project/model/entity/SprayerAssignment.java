package com.example.project.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "SPRAYER_ASSIGNMENT")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SprayerAssignment {
    @Id
    @Column(name = "SPRAYER_ASSIGNMENT_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPRAY_ORDER_ID", nullable = false)
    @JsonBackReference
    private SprayOrder sprayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPRAYER_ID", nullable = false)
    @JsonBackReference
    private Person sprayer;

    @Column(name = "IS_PRIMARY", nullable = false)
    private Boolean isPrimary;
}
