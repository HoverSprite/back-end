package hoversprite.project.sprayOrder;

import hoversprite.project.common.ErrorResponse;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.partner.PersonDTO;
import hoversprite.project.partner.PersonGlobalService;
import hoversprite.project.request.SprayOrderRequest;
import hoversprite.project.response.SprayOrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/{userId}/farmer")
class FarmerController {

    @Autowired
    private SprayOrderService sprayOrderService;

    @Autowired
    private PersonGlobalService personGlobalService;


    @GetMapping("/search")
    public ResponseEntity<?> searchFarmerByPhoneNumber(@RequestParam String phoneNumber) {
        try {
            PersonDTO farmer = personGlobalService.findFarmerByPhoneNumber(phoneNumber);
            if (farmer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("No farmer found with the given phone number"));
            }
            return ResponseEntity.ok(farmer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while searching for the farmer: " + e.getMessage()));
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@PathVariable Long userId, @RequestBody SprayOrderRequest sprayOrder) {
        try {
            SprayOrderDTO createdOrder = sprayOrderService.save(userId, sprayOrder, PersonRole.FARMER);
            return ResponseEntity.ok(createdOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<SprayOrderDTO>> viewOrders(@PathVariable Long userId) {
        List<SprayOrderDTO> orders = sprayOrderService.getOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> viewOrder(@PathVariable Long userId, @PathVariable Long orderId) {
        try {
            return new ResponseEntity<>(sprayOrderService.findSprayOrderDetails(orderId), HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/orders/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody SprayOrderRequest sprayOrder) {
        try {
            SprayOrderDTO updatedOrder = sprayOrderService.update(userId, orderId, sprayOrder, PersonRole.FARMER);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }
}