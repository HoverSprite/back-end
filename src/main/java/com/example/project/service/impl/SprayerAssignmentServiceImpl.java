package com.example.project.service.impl;

import com.example.project.repository.SprayerAssignmentRepository;
import com.example.project.service.SprayerAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SprayerAssignmentServiceImpl implements SprayerAssignmentService {

    @Autowired
    private SprayerAssignmentRepository sprayerAssignmentRepository;
}
