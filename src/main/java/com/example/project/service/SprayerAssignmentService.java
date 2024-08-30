package com.example.project.service;

import com.example.project.model.dto.SprayOrderDTO;
import com.example.project.model.entity.SprayerAssignment;

public interface SprayerAssignmentService {
    SprayOrderDTO assignSprayers(Integer orderId, SprayerAssignment sprayerAssignment);
}
