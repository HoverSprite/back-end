package hoversprite.project.project.service;

import hoversprite.project.project.base.BaseService;
import hoversprite.project.project.model.dto.SpraySessionDTO;
import hoversprite.project.project.model.entity.PersonRole;
import hoversprite.project.project.model.entity.SpraySession_2;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SpraySession2Service extends BaseService<SpraySessionDTO, Integer, PersonRole> {

    List<SpraySession_2> findSpraySessionByDate(LocalDate date, LocalTime startTime);

    SpraySessionDTO createOrFindSpraySession(SpraySessionDTO spraySessionDTO);
}
