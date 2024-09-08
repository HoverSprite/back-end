package hoversprite.project.sprayerAssignment;


import hoversprite.project.base.BaseService;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.request.SprayerAssignmentRequest;
import hoversprite.project.sprayOrder.SprayOrderDTO;

import java.time.LocalDate;
import java.util.List;

public interface SprayerAssignmentGlobalService  extends BaseService<SprayerAssignmentDTO, SprayerAssignmentRequest, Long, PersonRole> {
    SprayOrderDTO assignSprayers(Integer orderId, SprayerAssignmentDTO sprayerAssignment);

    List<SprayerAssignmentDTO> findAllByIds(Iterable<Long> ids);

    List<SprayerAssignmentDTO> findAllBySprayOrderIds(List<Long> ids);

    List<Long> findUnAvailableSprayerIds(SprayOrderDTO sprayOrderDTO);

    List<SprayerAssignmentDTO> findSprayerAssignmentInTheWeek(List<Long> sprayOrderIds, List<Long> sprayerIds);
}
