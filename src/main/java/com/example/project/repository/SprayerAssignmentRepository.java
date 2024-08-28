package com.example.project.repository;

import com.example.project.model.entity.SprayerAssignment;
import com.example.project.repository.custom.SprayerAssignmentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprayerAssignmentRepository extends JpaRepository<SprayerAssignment, Integer>, SprayerAssignmentRepositoryCustom {
}
