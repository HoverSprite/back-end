package hoversprite.project.sprayOrder;

import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.request.SprayOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/{userId}/sprayer")
public class SprayerController {
    @Autowired
    private SprayOrderService sprayOrderService;

    @GetMapping("/orders")
    public List<SprayOrderDTO> viewAssignedOrders(@PathVariable Long userId) {
        return sprayOrderService.getOrdersBySprayer(userId);
    }

    @PutMapping("/orders/{orderId}")
    public SprayOrderDTO updateOrder(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody SprayOrderRequest sprayOrder) {
        return sprayOrderService.update(userId, orderId, sprayOrder, PersonRole.SPRAYER);
    }

    @GetMapping("/orders/{orderId}")
    public SprayOrderDTO viewOrder(@PathVariable Long userId, @PathVariable Long orderId) {
        return sprayOrderService.findById(orderId);
    }
}
