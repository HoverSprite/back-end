package hoversprite.project.sprayOrder;

import hoversprite.project.common.annotation.RestrictAccess;
import hoversprite.project.common.base.BaseService;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.common.domain.SprayStatus;
import hoversprite.project.request.SprayOrderRequest;
import hoversprite.project.response.SprayOrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SprayOrderGlobalService   extends BaseService<SprayOrderDTO, SprayOrderRequest, Long, PersonRole> {
    List<SprayOrderDTO> getOrdersByUser(long userId);

    Page<SprayOrderDTO> getOrdersByUser(long userId, Pageable pageable);

    Page<SprayOrderDTO> getOrdersByUserAndStatus(long userId, SprayStatus status, Pageable pageable);

    List<SprayOrderDTO> getOrdersBySprayer(Long sprayerId);

    Page<SprayOrderDTO> getOrdersBySprayer(long userId, Pageable pageable);

    Page<SprayOrderDTO> getOrdersBySprayerAndStatus(long userId, SprayStatus status, Pageable pageable);

    List<SprayOrderDTO> getAvailableSprayOrdersBySprayer(Long sprayerId);

    List<SprayOrderDTO> getUnAssignedSprayOrders();

    SprayOrderResponse findSprayOrderDetails(Long sprayOrderId);

    @RestrictAccess
    void automateSprayerSelection (SprayOrderDTO sprayOrder, SprayStatus previousStatus);

    @RestrictAccess
    SprayOrderDTO lockAndUnlockStatus(SprayOrderDTO sprayOrder, SprayStatus status);

    Page<SprayOrderDTO> findAll(Pageable pageable);

    Page<SprayOrderDTO> findAllByStatus(SprayStatus status, Pageable pageable);
}
