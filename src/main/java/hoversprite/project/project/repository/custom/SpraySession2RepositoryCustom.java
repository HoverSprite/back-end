package hoversprite.project.project.repository.custom;

import hoversprite.project.project.model.entity.SpraySession_2;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SpraySession2RepositoryCustom {
    List<SpraySession_2> findSpraySessionByDate(LocalDate date, LocalTime startTime);
}
