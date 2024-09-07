package hoversprite.project.project.service.impl;

import hoversprite.project.project.model.entity.TimeSlot;
import hoversprite.project.project.repository.SpraySessionRepository;
import hoversprite.project.project.repository.TimeSlotRepository;
import hoversprite.project.project.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final SpraySessionRepository spraySessionRepository;
    @Autowired
    public TimeSlotServiceImpl(TimeSlotRepository timeSlotRepository, SpraySessionRepository spraySessionRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.spraySessionRepository = spraySessionRepository;
    }

    @Override
    public List<TimeSlot> findAvailableTimeSlots() {
        List<TimeSlot> allTimeSlots = timeSlotRepository.findAll();
        return allTimeSlots.stream()
                .filter(slot -> spraySessionRepository.countByTimeSlot_Id(slot.getId()) < 2)
                .collect(Collectors.toList());
    }
    @Override
    public List<TimeSlot> findAllTimeSlots() {
        return timeSlotRepository.findAll();
    }

    @Override
    public Optional<TimeSlot> findTimeSlotById(Long id) {
        return timeSlotRepository.findById(id);
    }

    @Override
    public TimeSlot createTimeSlot(TimeSlot timeSlot) {
        return timeSlotRepository.save(timeSlot);
    }

    @Override
    public TimeSlot updateTimeSlot(Long id, TimeSlot updatedTimeSlot) {
        Optional<TimeSlot> existingTimeSlot = timeSlotRepository.findById(id);
        if (existingTimeSlot.isPresent()) {
            TimeSlot timeSlot = existingTimeSlot.get();
            timeSlot.setStartTime(updatedTimeSlot.getStartTime());
            timeSlot.setEndTime(updatedTimeSlot.getEndTime());
            return timeSlotRepository.save(timeSlot);
        } else {
            throw new RuntimeException("TimeSlot not found with id: " + id);
        }
    }

    @Override
    public void deleteTimeSlot(Long id) {
        timeSlotRepository.deleteById(id);
    }
}
