package com.example.project.service;

import com.example.project.model.dto.SpraySessionDTO;
import com.example.project.model.entity.SpraySession_2;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SpraySession2Service {
    List<SpraySession_2> findSpraySessionByDate(LocalDate date, LocalTime startTime);

    SpraySession_2 createSpraySession(SpraySession_2 spraySession);
}
