package hoversprite.project.sprayOrder;

import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.request.SprayOrderRequest;
import hoversprite.project.response.SprayOrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/{userId}/farmer")
public class FarmerController {

    @Autowired
    private SprayOrderService sprayOrderService;

    @PostMapping("/orders")
    public SprayOrderDTO createOrder(@PathVariable Long userId, @RequestBody SprayOrderRequest sprayOrder) {
        return sprayOrderService.save(userId, sprayOrder, PersonRole.FARMER);
    }

    @GetMapping("/orders")
    public List<SprayOrderDTO> viewOrders(@PathVariable Long userId) {
        return sprayOrderService.getOrdersByUser(userId);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<SprayOrderResponse> viewOrder(@PathVariable Long userId, @PathVariable Long orderId) {
        return new ResponseEntity<>(sprayOrderService.findSprayOrderDetails(orderId), HttpStatus.OK);
    }

    @PutMapping("/orders/{orderId}")
    public SprayOrderDTO updateOrder(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody SprayOrderRequest sprayOrder) {
        return sprayOrderService.update(userId, orderId, sprayOrder, PersonRole.FARMER);
    }
}
