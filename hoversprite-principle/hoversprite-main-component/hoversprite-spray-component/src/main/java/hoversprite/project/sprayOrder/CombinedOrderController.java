package hoversprite.project.sprayOrder;

import com.mysema.commons.lang.Pair;
import hoversprite.project.common.domain.PersonExpertise;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.partner.PersonDTO;
import hoversprite.project.request.SprayOrderRequest;
import hoversprite.project.response.SprayOrderResponse;
import hoversprite.project.sprayerAssignment.SprayerAssignmentGlobalService;
import hoversprite.project.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class CombinedOrderController {

    @Autowired
    private SprayOrderService sprayOrderService;

    @Autowired
    private SprayerAssignmentGlobalService sprayerAssignmentGlobalService;

    @PostMapping
    @PreAuthorize("hasAnyRole('FARMER', 'RECEPTIONIST')")
    public SprayOrderDTO createOrder(@RequestBody SprayOrderRequest sprayOrder) {
        PersonRole role = SecurityUtils.getCurrentUserRole();
        Long userId = SecurityUtils.getCurrentUserId();
        return sprayOrderService.save(userId, sprayOrder, role);
    }


    @GetMapping("/farmer")
    @PreAuthorize("hasRole('FARMER')")
    public List<SprayOrderDTO> viewFarmerOrders() {
        Long userId = SecurityUtils.getCurrentUserId();
        return sprayOrderService.getOrdersByUser(userId);
    }

    @GetMapping("/receptionist")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public List<SprayOrderDTO> viewAllOrders() {
        return sprayOrderService.findAll();
    }

    @GetMapping("/sprayer")
    @PreAuthorize("hasRole('SPRAYER')")
    public List<SprayOrderDTO> viewAssignedOrders() {
        Long userId = SecurityUtils.getCurrentUserId();
        return sprayOrderService.getOrdersBySprayer(userId);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('FARMER', 'RECEPTIONIST', 'SPRAYER')")
    public ResponseEntity<?> viewOrder(@PathVariable Long orderId) {
        PersonRole role = SecurityUtils.getCurrentUserRole();
        Long userId = SecurityUtils.getCurrentUserId();

        if (role == PersonRole.FARMER || role == PersonRole.SPRAYER) {
            SprayOrderResponse order = sprayOrderService.findSprayOrderDetails(orderId);

            boolean isSprayerFoundInOrder = order.getSprayerAssignments().stream().anyMatch(sprayerAssignmentResponse -> sprayerAssignmentResponse.getSprayer().getId().equals(userId));
            if (order.getFarmer().getId().equals(userId) || (role == PersonRole.SPRAYER && isSprayerFoundInOrder)) {
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }
        } else {
            return ResponseEntity.ok(sprayOrderService.findSprayOrderDetails(orderId));
        }
    }

    @PutMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('FARMER', 'RECEPTIONIST', 'SPRAYER')")
    public SprayOrderDTO updateOrder(@PathVariable Long orderId, @RequestBody SprayOrderRequest sprayOrder) {
        PersonRole role = SecurityUtils.getCurrentUserRole();
        Long userId = SecurityUtils.getCurrentUserId();
        return sprayOrderService.update(userId, orderId, sprayOrder, role);
    }

    @GetMapping("/{orderId}/available-sprayers")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<Map<PersonExpertise, List<Pair<PersonDTO, Integer>>>> getSortedSprayers(@PathVariable Long orderId) {
        Map<PersonExpertise, List<Pair<PersonDTO, Integer>>> sortedSprayers = sprayOrderService.getSortedAvailableSprayers(orderId);
        return ResponseEntity.ok(sortedSprayers);
    }

    @GetMapping("/sprayer/available")
    @PreAuthorize("hasRole('SPRAYER')")
    public List<SprayOrderDTO> viewAssignedAvailableOrders() {
        Long userId = SecurityUtils.getCurrentUserId();
        return sprayOrderService.getAvailableSprayOrdersBySprayer(userId);
    }
}