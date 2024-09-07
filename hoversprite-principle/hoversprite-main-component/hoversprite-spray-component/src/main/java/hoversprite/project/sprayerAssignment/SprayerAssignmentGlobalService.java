package hoversprite.project.sprayerAssignment;


import hoversprite.project.base.BaseService;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.request.SprayerAssignmentRequest;
import hoversprite.project.sprayOrder.SprayOrderDTO;

import java.util.List;

public interface SprayerAssignmentGlobalService  extends BaseService<SprayerAssignmentDTO, SprayerAssignmentRequest, Long, PersonRole> {
    SprayOrderDTO assignSprayers(Integer orderId, SprayerAssignmentDTO sprayerAssignment);

}
