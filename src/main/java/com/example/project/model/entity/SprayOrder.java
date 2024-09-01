package com.example.project.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "SPRAY_ORDER")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SprayOrder {
    @Id
    @Column(name = "SPRAY_ORDER_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FARMER_ID")
    private Person farmer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEPTIONIST_ID")
    private Person receptionist;

    @Column(name = "CROP_TYPE", nullable = false)
    private CropType cropType;

    @Column(name = "AREA", precision = 10, scale = 2, nullable = false)
    private BigDecimal area;

    @Column(name = "DATE_TIME", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "COST", precision = 12)
    private Double cost;

    @Column(name = "STATUS")
    private SprayStatus status;

    @Column(name = "PAYMENT_RECEIVED_AMOUNT", precision = 12, scale = 2)
    private BigDecimal paymentReceivedAmount;

    @Column(name = "CHANGE_AMOUNT", precision = 12, scale = 2)
    private BigDecimal changeAmount;

    @Column(name = "LOCATION", nullable = false)
    private String location;

    @OneToMany(mappedBy = "sprayOrder", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SprayerAssignment> sprayerAssignments = new ArrayList<>();


    @OneToOne(mappedBy = "sprayOrder", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    private SpraySession_2 spraySession;
}
