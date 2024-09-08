package hoversprite.project.sprayOrder;

//import hoversprite.project.GeocodingUtil;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.request.SprayOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/{userId}/sprayer")
public class SprayerController {
    @Autowired
    private SprayOrderService sprayOrderService;

//    @Autowired
//    private RoutePlanningService routePlanningService;
//
//    @Autowired
//    private GeocodingUtil geocodingUtil;

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

    @GetMapping("/assignedOrders")
    public List<SprayOrderDTO> viewAssignedAvailableOrders(@PathVariable Long userId) {
        return sprayOrderService.getAvailableSprayOrdersBySprayer(userId);
    }

//    @GetMapping("/optimal-route")
//    public List<RoutePlanningService.RoutePoint> getOptimalRoute(@PathVariable Long userId, @RequestParam double startLat, @RequestParam double startLng) {
//        List<SprayOrderDTO> assignedOrders = sprayOrderService.getOrdersBySprayer(userId);
//        GeocodingUtil.LatLng startLocation = new GeocodingUtil.LatLng(startLat, startLng);
//
//        return routePlanningService.calculateOptimalRoute(startLocation, assignedOrders);
//    }

    @GetMapping("/assigned-orders")
    public List<SprayOrderDTO> getAssignedOrdersWithCoordinates(@PathVariable Long userId) {
        List<SprayOrderDTO> assignedOrders = sprayOrderService.getOrdersBySprayer(userId);
        return assignedOrders.stream()
                .filter(order -> order.getLatitude() != null && order.getLongitude() != null)
                .collect(Collectors.toList());
    }

}
