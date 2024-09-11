package hoversprite.project.sprayOrder;

import hoversprite.project.common.ErrorResponse;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.partner.PersonDTO;
import hoversprite.project.partner.PersonGlobalService;
import hoversprite.project.request.SprayOrderRequest;
import hoversprite.project.sprayerAssignment.SprayerAssignmentDTO;
import hoversprite.project.sprayerAssignment.SprayerAssignmentGlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/{userId}/receptionist")
public class ReceptionistController {

    @Autowired
    private SprayOrderService sprayOrderService;

    @Autowired
    private SprayerAssignmentGlobalService sprayerAssignmentGlobalService;

    @Autowired
    private PersonGlobalService personGlobalService;

    @GetMapping("/farmers/search")
    public ResponseEntity<?> searchFarmerByPhoneNumber(@RequestParam String phoneNumber) {
        try {
            PersonDTO farmer = personGlobalService.findFarmerByPhoneNumber(phoneNumber);
            if (farmer != null) {
                return ResponseEntity.ok(farmer);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("No farmer found with the given phone number"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while searching for the farmer: " + e.getMessage()));
        }
    }

    @PostMapping("/orders")
    public SprayOrderDTO createOrder(@PathVariable Long userId, @RequestBody SprayOrderRequest sprayOrder) {
        return sprayOrderService.save(userId, sprayOrder, PersonRole.RECEPTIONIST);
    }

    @PatchMapping("/orders/{orderId}/assign")
    public SprayOrderDTO assignSprayer(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody SprayerAssignmentDTO sprayerAssignment) {
        return sprayerAssignmentGlobalService.assignSprayers(orderId.intValue(), sprayerAssignment);
    }

    @PutMapping("/orders/{orderId}")
    public SprayOrderDTO updateOrder(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody SprayOrderRequest sprayOrder) {
        return sprayOrderService.update(userId, orderId, sprayOrder, PersonRole.RECEPTIONIST);
    }

    @GetMapping("/orders")
    public List<SprayOrderDTO> viewOrders(@PathVariable Long userId) {
        return sprayOrderService.findAll();
    }

    @GetMapping("/orders/{orderId}")
    public SprayOrderDTO viewOrder(@PathVariable Long userId, @PathVariable Long orderId) {
        return sprayOrderService.findById(orderId);
    }
}
