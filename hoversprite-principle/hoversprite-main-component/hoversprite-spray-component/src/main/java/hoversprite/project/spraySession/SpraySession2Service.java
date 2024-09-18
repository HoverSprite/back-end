package hoversprite.project.spraySession;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

interface SpraySession2Service extends SpraySession2GlobalService {

    Map<LocalDate, List<LocalTime>> getAvailableSlotsForWeek(LocalDate startDate, LocalDate endDate);
}
