package hoversprite.project.sprayOrder;


import hoversprite.project.common.domain.SprayStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

interface SprayOrderRepositoryCustom {
    List<SprayOrder> getOrdersByUser(long id);
    Page<SprayOrder> getOrdersByUser(long id, Pageable pageable);
    Page<SprayOrder> getOrdersByUserAndStatus(long id, SprayStatus status, Pageable pageable);

    Page<SprayOrder> getOrdersBySprayer(List<Long> sprayOrderIds, Pageable pageable);
    Page<SprayOrder> getOrdersBySprayerAndStatus(List<Long> sprayOrderIds, SprayStatus status, Pageable pageable);

    Page<SprayOrder> findAllByStatus(SprayStatus status, Pageable pageable);

    List<SprayOrder> getUnAssignedSprayOrders();

    List<SprayOrder> findAllAvailableSprayOrderForSprayerByIds(List<Long> ids);

}
