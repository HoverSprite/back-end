package hoversprite.project.project.service;

import hoversprite.project.project.base.BaseService;
import hoversprite.project.project.model.dto.SprayOrderDTO;
import hoversprite.project.project.model.entity.PersonRole;

import java.util.List;

public interface SprayOrderService  extends BaseService<SprayOrderDTO, Integer, PersonRole> {
    List<SprayOrderDTO> getOrdersByUser(Integer userId);

    List<SprayOrderDTO> getOrdersBySprayer(Integer sprayerId);
}
