package hoversprite.project.project.repository.custom;

import hoversprite.project.project.model.entity.SprayOrder;

import java.util.List;

public interface SprayOrderRepositoryCustom {
    List<SprayOrder> getOrdersByUser(Integer id);

    List<SprayOrder> getOrdersBySprayer(Integer id);
}
