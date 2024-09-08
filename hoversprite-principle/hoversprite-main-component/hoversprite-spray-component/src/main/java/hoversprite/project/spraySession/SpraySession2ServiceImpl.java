package hoversprite.project.spraySession;

import hoversprite.project.base.AbstractService;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.request.SprayOrderRequest;
import hoversprite.project.request.SpraySessionRequest;
import hoversprite.project.validator.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
class SpraySession2ServiceImpl extends AbstractService<SpraySessionDTO, SpraySessionRequest, Long, PersonRole> implements SpraySession2Service {

    private static final int MAX_SPRAYS_SESSIONS_IN_ONE_HOUR = 2;
    private static final String NO_MORE_THAN_2_SESSIONS_ALLOWED_AT_THE_SAME_TIME = "Cannot create more than 2 sessions per time slot on a given day.";


    @Autowired
    private SpraySession2Repository spraySessionRepository;

    @Override
    public List<SpraySessionDTO> findSpraySessionByDate(LocalDate date, LocalTime startTime) {
        return spraySessionRepository.findByDateAndStartTime(date, startTime).stream().map(SpraySessionMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public List<SpraySessionDTO> findSpraySessionByWeek(LocalDate date) {
        return spraySessionRepository.findSpraySessionByWeek(date).stream().map(SpraySessionMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public SpraySessionDTO findById(Long id) {
        return SpraySessionMapper.INSTANCE.toDto(spraySessionRepository.findById(id).orElse(null));
    }

    @Override
    public List<SpraySessionDTO> findAll() {
        return spraySessionRepository.findAll().stream()
                .map(SpraySessionMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        spraySessionRepository.deleteById(id);
    }

    private void verifySessionAvailable(ValidationUtils validator, SpraySessionRequest spraySessionRequest) {
        if (spraySessionRequest.getId() == null) {
            List<SpraySessionDTO> existingSessions = findSpraySessionByDate(spraySessionRequest.getDate(), spraySessionRequest.getStartTime());
            validator.isTrue(existingSessions.size() < MAX_SPRAYS_SESSIONS_IN_ONE_HOUR, NO_MORE_THAN_2_SESSIONS_ALLOWED_AT_THE_SAME_TIME);
        }
    }

    @Override
    protected void validateForSave(ValidationUtils validator, SpraySessionRequest spraySessionRequest, PersonRole role) {
        verifySessionAvailable(validator, spraySessionRequest);
    }

    @Override
    protected void validateForUpdate(ValidationUtils validator, SpraySessionRequest spraySessionRequest, PersonRole role) {
        verifySessionAvailable(validator, spraySessionRequest);
    }

    @Override
    protected SpraySessionDTO executeSave(Long userId, SpraySessionRequest spraySession, PersonRole role) {
        return SpraySessionMapper.INSTANCE.toDto(spraySessionRepository.save(SpraySessionMapper.INSTANCE.toEntitySave(spraySession)));
    }

    @Override
    protected SpraySessionDTO executeUpdate(Long userId, Long spraySessionId, SpraySessionRequest spraySession, PersonRole role) {
        SpraySession_2 updatedSpraySession = SpraySessionMapper.INSTANCE.toEntityUpdate(spraySession);
        SpraySessionDTO previousSpraySession = findById(spraySessionId);

        if (previousSpraySession != null && updatedSpraySession != null
         && (!previousSpraySession.getDate().equals(updatedSpraySession.getDate())
                || !previousSpraySession.getStartTime().equals(updatedSpraySession.getStartTime())
        )) {
            deleteById(previousSpraySession.getId());
            return SpraySessionMapper.INSTANCE.toDto(spraySessionRepository.save(updatedSpraySession));
        }
        return previousSpraySession;
    }
}
