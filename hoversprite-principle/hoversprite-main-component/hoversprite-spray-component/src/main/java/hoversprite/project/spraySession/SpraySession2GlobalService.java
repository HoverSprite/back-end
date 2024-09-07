package hoversprite.project.spraySession;

import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.base.BaseService;
import hoversprite.project.request.SprayOrderRequest;
import hoversprite.project.request.SpraySessionRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SpraySession2GlobalService extends BaseService<SpraySessionDTO, SpraySessionRequest, Long, PersonRole> {
    List<SpraySessionDTO> findSpraySessionByDate(LocalDate date, LocalTime startTime);
}
