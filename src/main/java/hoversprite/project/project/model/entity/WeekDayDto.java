package hoversprite.project.project.model.entity;

import lombok.Data;
import java.util.List;

@Data
public class WeekDayDto {
    private String dayName;
    private String gregorianDate;
    private String lunarDate;
    private List<TimeSlotDto> timeSlots;
}
