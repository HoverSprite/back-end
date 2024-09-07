package hoversprite.project.project.service;

import hoversprite.project.project.model.dto.SprayOrderDTO;
import hoversprite.project.project.model.entity.SprayerAssignment;

public interface SprayerAssignmentService {
    SprayOrderDTO assignSprayers(Integer orderId, SprayerAssignment sprayerAssignment);
}
