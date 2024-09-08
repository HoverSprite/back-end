package hoversprite.project.spraySession;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

interface SpraySession2RepositoryCustom {
    List<SpraySession_2> findSpraySessionByDate(LocalDate date, LocalTime startTime);

    List<SpraySession_2> findSpraySessionByWeek(LocalDate date);
}
