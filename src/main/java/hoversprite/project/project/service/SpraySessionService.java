package hoversprite.project.project.service;

import hoversprite.project.project.model.entity.SpraySession;
import hoversprite.project.project.model.entity.WeekDayDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpraySessionService {
    List<SpraySession> findAllSpraySessions();
    Optional<SpraySession> findSpraySessionById(Long id);
    List<SpraySession> findAvailableSpraySessions();
    SpraySession createSpraySession(Long timeSlotId, Boolean isAvailable, LocalDate date);
    SpraySession updateSpraySession(Long id, SpraySession updatedSpraySession);
    void deleteSpraySession(Long id);
    boolean checkAvailability(Long timeSlotId, LocalDate date);
    List<WeekDayDto> getWeeklySchedule();
}
