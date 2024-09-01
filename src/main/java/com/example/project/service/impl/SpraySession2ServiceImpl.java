package com.example.project.service.impl;

import com.example.project.base.AbstractService;
import com.example.project.mapper.SpraySessionMapper;
import com.example.project.model.dto.SpraySessionDTO;
import com.example.project.model.entity.PersonRole;
import com.example.project.model.entity.SpraySession_2;
import com.example.project.repository.SpraySession2Repository;
import com.example.project.repository.SpraySessionRepository;
import com.example.project.service.SpraySession2Service;
import com.example.project.validator.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpraySession2ServiceImpl extends AbstractService<SpraySessionDTO, Integer, PersonRole> implements SpraySession2Service {

    @Autowired
    private SpraySession2Repository spraySessionRepository;

    @Override
    public List<SpraySession_2> findSpraySessionByDate(LocalDate date, LocalTime startTime) {
        return spraySessionRepository.findByDateAndStartTime(date, startTime);
    }

    @Override
    public SpraySessionDTO createOrFindSpraySession(SpraySessionDTO spraySessionDTO) {
        LocalDate date = spraySessionDTO.getDate();
        LocalTime startTime = spraySessionDTO.getStartTime();

        List<SpraySession_2> existingSessions = findSpraySessionByDate(date, startTime);

        if (existingSessions.size() < 2) {
            // Create and return a new session if less than 2 sessions exist
            SpraySession_2 newSession = SpraySessionMapper.INSTANCE.toEntitySave(spraySessionDTO);
            SpraySession_2 savedSession = spraySessionRepository.save(newSession);
            return SpraySessionMapper.INSTANCE.toDto(savedSession);
        } else {
            // If 2 sessions exist, return an error or throw an exception
            throw new RuntimeException("Cannot create more than 2 sessions per time slot on a given day. Please choose another time slot.");
        }
    }

    @Override
    public SpraySessionDTO findById(Integer id) {
        return SpraySessionMapper.INSTANCE.toDto(spraySessionRepository.findById(id).orElse(null));
    }

    @Override
    public List<SpraySessionDTO> findAll() {
        return spraySessionRepository.findAll().stream()
                .map(SpraySessionMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        spraySessionRepository.deleteById(id);
    }

    @Override
    protected void validateForSave(ValidationUtils validator, SpraySessionDTO spraySessionDTO, PersonRole role) {
        // Validate the session creation logic here
    }

    @Override
    protected void validateForUpdate(ValidationUtils validator, SpraySessionDTO spraySessionDTO, PersonRole role) {
        // Validate the session update logic here
    }

    @Override
    protected SpraySessionDTO executeSave(Integer userId, SpraySessionDTO spraySessionDTO, PersonRole role) {
        return createOrFindSpraySession(spraySessionDTO);
    }

    @Override
    protected SpraySessionDTO executeUpdate(Integer userId, Integer id, SpraySessionDTO spraySessionDTO, PersonRole role) {
        // Update logic here if needed
        return null;
    }
}
