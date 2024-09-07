package hoversprite.project.project.model.entity;

import lombok.Data;

@Data
public class TimeSlotDto {
    private Long id;
    private String startTime;
    private String endTime;
    private boolean isAvailable;
}
