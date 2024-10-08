package hoversprite.project.sprayOrder;

import com.mysema.commons.lang.Pair;
import hoversprite.project.common.domain.PersonExpertise;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.common.domain.SprayStatus;
import hoversprite.project.partner.PersonDTO;
import hoversprite.project.request.SprayOrderRequest;
import hoversprite.project.response.SprayOrderResponse;
import hoversprite.project.sprayerAssignment.SprayerAssignmentGlobalService;
import hoversprite.project.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
class CombinedOrderController {

    @Autowired
    private SprayOrderService sprayOrderService;

    @Autowired
    private SprayerAssignmentGlobalService sprayerAssignmentGlobalService;

    @PostMapping
    @PreAuthorize("hasAnyRole('FARMER', 'RECEPTIONIST')")
    public SprayOrderDTO createOrder(@RequestBody SprayOrderRequest sprayOrder) {
        PersonRole role = SecurityUtils.getCurrentUserRole();
        Long userId = SecurityUtils.getCurrentUserId();

        // Print user ID and role
        System.out.println("User ID: " + userId);
        System.out.println("User Role: " + role);

        return sprayOrderService.save(userId, sprayOrder, role);
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('FARMER', 'RECEPTIONIST', 'SPRAYER')")
    public ResponseEntity<Page<SprayOrderDTO>> viewOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) SprayStatus status
    ) {
        PersonRole role = SecurityUtils.getCurrentUserRole();
        Long userId = SecurityUtils.getCurrentUserId();

        Pageable pageable = PageRequest.of(page, size);
        Page<SprayOrderDTO> orders;

        switch (role) {
            case FARMER:
                orders = status == null
                        ? sprayOrderService.getOrdersByUser(userId, pageable)
                        : sprayOrderService.getOrdersByUserAndStatus(userId, status, pageable);
                break;
            case SPRAYER:
                orders = status == null
                        ? sprayOrderService.getOrdersBySprayer(userId, pageable)
                        : sprayOrderService.getOrdersBySprayerAndStatus(userId, status, pageable);
                break;
            case RECEPTIONIST:
            case ADMIN:
                orders = status == null
                        ? sprayOrderService.findAll(pageable)
                        : sprayOrderService.findAllByStatus(status, pageable);
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(orders);
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

    @GetMapping("/assigned-orders")
    @PreAuthorize("hasRole('SPRAYER')")
    public ResponseEntity<List<SprayOrderDTO>> getAssignedOrdersWithCoordinates() {
        Long userId = SecurityUtils.getCurrentUserId();  // Get the current user ID

        // Fetch orders assigned to the sprayer
        List<SprayOrderDTO> assignedOrders = sprayOrderService.getOrdersBySprayer(userId);

        // Filter orders that have both latitude and longitude
        List<SprayOrderDTO> filteredOrders = assignedOrders.stream()
                .filter(order -> order.getLatitude() != null && order.getLongitude() != null)
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredOrders);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('SPRAYER')")
    public ResponseEntity<List<SprayOrderDTO>> viewAssignedOrders() {
        Long userId = SecurityUtils.getCurrentUserId();  // Get the current user ID

        // Fetch orders assigned to the sprayer
        List<SprayOrderDTO> assignedOrders = sprayOrderService.getOrdersBySprayer(userId);

        return ResponseEntity.ok(assignedOrders);
    }




}