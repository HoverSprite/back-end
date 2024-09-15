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

    @GetMapping
    @PreAuthorize("hasAnyRole('FARMER', 'RECEPTIONIST', 'SPRAYER')")
    public ResponseEntity<List<SprayOrderDTO>> viewOrders() {
        PersonRole role = SecurityUtils.getCurrentUserRole();
        Long userId = SecurityUtils.getCurrentUserId();

        return switch (role) {
            case FARMER -> ResponseEntity.ok(sprayOrderService.getOrdersByUser(userId));
            case SPRAYER -> ResponseEntity.ok(sprayOrderService.getOrdersBySprayer(userId));
            case RECEPTIONIST, ADMIN -> ResponseEntity.ok(sprayOrderService.findAll());
            default -> ResponseEntity.badRequest().build();
        };
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('FARMER', 'RECEPTIONIST', 'SPRAYER')")
    public ResponseEntity<?> viewOrder(@PathVariable Long orderId) {
        PersonRole role = SecurityUtils.getCurrentUserRole();
        Long userId = SecurityUtils.getCurrentUserId();

        try {
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
        }} catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'FARMER', 'SPRAYER')")
    public ResponseEntity<Map<PersonExpertise, List<Pair<PersonDTO, Integer>>>> getSortedSprayers(@PathVariable Long orderId) {
        Map<PersonExpertise, List<Pair<PersonDTO, Integer>>> sortedSprayers = sprayOrderService.getSortedAvailableSprayers(orderId);
        return ResponseEntity.ok(sortedSprayers);
    }

    @GetMapping("/sprayer/available")
    @PreAuthorize("hasAnyRole('SPRAYER')")
    public List<SprayOrderDTO> viewAssignedAvailableOrders() {
        Long userId = SecurityUtils.getCurrentUserId();
        return sprayOrderService.getAvailableSprayOrdersBySprayer(userId);
    }
}