package hoversprite.project.spraySession;

import hoversprite.project.common.base.BaseService;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.request.SpraySessionRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SpraySession2GlobalService extends BaseService<SpraySessionDTO, SpraySessionRequest, Long, PersonRole> {
    List<SpraySessionDTO> findSpraySessionByDate(LocalDate date, LocalTime startTime);

    List<SpraySessionDTO> findSpraySessionByWeek(LocalDate date);
}
