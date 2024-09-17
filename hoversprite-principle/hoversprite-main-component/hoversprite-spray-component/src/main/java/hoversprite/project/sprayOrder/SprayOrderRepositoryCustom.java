package hoversprite.project.sprayOrder;


import java.util.List;

interface SprayOrderRepositoryCustom {
    List<SprayOrder> getOrdersByUser(long id);

    List<SprayOrder> getUnAssignedSprayOrders();

    List<SprayOrder> findAllAvailableSprayOrderForSprayerByIds(List<Long> ids);

}
