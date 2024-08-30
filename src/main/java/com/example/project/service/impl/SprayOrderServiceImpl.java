package com.example.project.service.impl;

import com.example.project.base.AbstractService;
import com.example.project.mapper.SprayOrderMapper;
import com.example.project.mapper.SpraySessionMapper;
import com.example.project.model.dto.SprayOrderDTO;
import com.example.project.model.dto.SpraySessionDTO;
import com.example.project.model.entity.SprayOrder;
import com.example.project.model.entity.SpraySession;
import com.example.project.model.entity.SpraySession_2;
import com.example.project.repository.SprayOrderRepository;
import com.example.project.service.SprayOrderService;
import com.example.project.service.SpraySession2Service;
import com.example.project.validator.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SprayOrderServiceImpl extends AbstractService<SprayOrderDTO, Integer> implements SprayOrderService {

    private static final int MAX_SPRAYS_SESSIONS_IN_ONE_HOUR = 2;
    private static final int COST_PER_DECARE = 30000;

    private static final String NO_MORE_THAN_2_SESSIONS_ALLOWED_AT_THE_SAME_TIME = "Cannot create more than 2 sessions per time slot on a given day.";

    @Autowired
    private SprayOrderRepository sprayOrderRepository;

    @Autowired
    private SpraySession2Service spraySession2Service;

    @Override
    public SprayOrderDTO findById(Integer id) {
        return SprayOrderMapper.INSTANCE.toDto(sprayOrderRepository.findById(id).orElse(null));
    }

    @Override
    public List<SprayOrderDTO> findAll() {
        return sprayOrderRepository.findAll().stream()
                .map(SprayOrderMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        sprayOrderRepository.deleteById(id);
    }

    @Override
    public void validateForSave(SprayOrderDTO dto) {

    }

    @Override
    public void validateForUpdate(SprayOrderDTO dto) {

    }

    @Override
    protected void validateForSave(ValidationUtils validator, SprayOrderDTO sprayOrderDTO) {
        SpraySessionDTO spraySessionDTO = sprayOrderDTO.getSpraySession();
        List<SpraySession_2> existingSessions = spraySession2Service.findSpraySessionByDate(spraySessionDTO.getDate(), spraySessionDTO.getStartTime());
        validator.isTrue(existingSessions.size() < MAX_SPRAYS_SESSIONS_IN_ONE_HOUR, NO_MORE_THAN_2_SESSIONS_ALLOWED_AT_THE_SAME_TIME);
    }

    @Override
    protected SprayOrderDTO executeSave(SprayOrderDTO sprayOrderDTO) {
        SpraySessionDTO spraySessionDTO = sprayOrderDTO.getSpraySession();
        SprayOrder sprayOrder = SprayOrderMapper.INSTANCE.toEntitySave(sprayOrderDTO);
        SpraySession_2 spraySession = SpraySessionMapper.INSTANCE.toEntitySave(spraySessionDTO);
        calculateCostPerArea(sprayOrder);
        spraySession.setSprayOrder(sprayOrder);
        sprayOrder.setSpraySession(spraySession);
        return SprayOrderMapper.INSTANCE.toDto(sprayOrderRepository.save(sprayOrder));
    }

    @Override
    protected void validateForUpdate(ValidationUtils validator, SprayOrderDTO dto) {

    }

    @Override
    protected SprayOrderDTO executeUpdate(SprayOrderDTO sprayOrderDTO) {
        SprayOrder existingSprayOrder = sprayOrderRepository.findById(sprayOrderDTO.getId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + sprayOrderDTO.getId()));

        existingSprayOrder.setCropType(sprayOrderDTO.getCropType());
        existingSprayOrder.setArea(sprayOrderDTO.getArea());
        existingSprayOrder.setDateTime(sprayOrderDTO.getDateTime());
        existingSprayOrder.setLocation(sprayOrderDTO.getLocation());

        SpraySession_2 updatedSpraySession = SpraySessionMapper.INSTANCE.toEntitySave(sprayOrderDTO.getSpraySession());
        if (updatedSpraySession != null && !existingSprayOrder.getSpraySession().equals(updatedSpraySession)) {
            existingSprayOrder.setSpraySession(updatedSpraySession);
            updatedSpraySession.setSprayOrder(existingSprayOrder);
        }
        return SprayOrderMapper.INSTANCE.toDto(sprayOrderRepository.save(existingSprayOrder));
    }

    private void calculateCostPerArea(SprayOrder sprayOrder) {
        sprayOrder.setCost(sprayOrder.getArea().doubleValue() * COST_PER_DECARE);
    }
}
