package com.example.project.repository.custom;

import com.example.project.model.entity.SpraySession_2;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SpraySession2RepositoryCustom {
    List<SpraySession_2> findSpraySessionByDate(LocalDate date, LocalTime startTime);
}
