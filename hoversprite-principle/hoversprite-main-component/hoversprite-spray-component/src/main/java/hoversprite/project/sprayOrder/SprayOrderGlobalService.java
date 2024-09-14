package hoversprite.project.sprayOrder;

import hoversprite.project.common.annotation.RestrictAccess;
import hoversprite.project.common.base.BaseService;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.common.domain.SprayStatus;
import hoversprite.project.request.SprayOrderRequest;
import hoversprite.project.response.SprayOrderResponse;

import java.util.List;

public interface SprayOrderGlobalService   extends BaseService<SprayOrderDTO, SprayOrderRequest, Long, PersonRole> {
    List<SprayOrderDTO> getOrdersByUser(long userId);

    List<SprayOrderDTO> getOrdersBySprayer(Long sprayerId);

    List<SprayOrderDTO> getAvailableSprayOrdersBySprayer(Long sprayerId);

    List<SprayOrderDTO> getUnAssignedSprayOrders();

    SprayOrderResponse findSprayOrderDetails(Long sprayOrderId);

    @RestrictAccess
    void automateSprayerSelection (SprayOrderDTO sprayOrder, SprayStatus previousStatus);

    @RestrictAccess
    SprayOrderDTO lockAndUnlockStatus(SprayOrderDTO sprayOrder, SprayStatus status);
}
