package hoversprite.project.sprayOrder;

import hoversprite.project.common.annotation.RestrictAccess;
import hoversprite.project.common.base.BaseService;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.common.domain.SprayStatus;
import hoversprite.project.request.SprayOrderRequest;

import java.util.List;

public interface SprayOrderGlobalService   extends BaseService<SprayOrderDTO, SprayOrderRequest, Long, PersonRole> {
    List<SprayOrderDTO> getOrdersByUser(long userId);

    List<SprayOrderDTO> getOrdersBySprayer(Long sprayerId);

    List<SprayOrderDTO> getUnAssignedSprayOrders();

    @RestrictAccess
    void automateSprayerSelection (SprayOrderDTO sprayOrder);

    @RestrictAccess
    SprayOrderDTO lockAndUnlockStatus(SprayOrderDTO sprayOrder, SprayStatus status);
}
