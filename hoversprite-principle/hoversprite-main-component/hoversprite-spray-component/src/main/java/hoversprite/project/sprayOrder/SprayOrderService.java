package hoversprite.project.sprayOrder;


import hoversprite.project.base.BaseService;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.request.SprayOrderRequest;

import java.util.List;

public interface SprayOrderService  extends BaseService<SprayOrderDTO, SprayOrderRequest, Long, PersonRole> {
    List<SprayOrderDTO> getOrdersByUser(long userId);

    List<SprayOrderDTO> getOrdersBySprayer(Long sprayerId);

    SprayOrderDTO findById(Long id);
}
