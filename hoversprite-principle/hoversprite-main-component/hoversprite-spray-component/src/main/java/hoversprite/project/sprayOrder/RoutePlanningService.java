//package hoversprite.project.sprayOrder;
//
//
//import hoversprite.project.sprayOrder.SprayOrderDTO;
//import hoversprite.project.GeocodingUtil.LatLng;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class RoutePlanningService {
//
//    private static final String OSRM_API_URL = "https://router.project-osrm.org/trip/v1/driving/";
//
//    public List<RoutePoint> calculateOptimalRoute(LatLng start, List<SprayOrderDTO> orders) {
//        List<LatLng> waypoints = new ArrayList<>();
//        waypoints.add(start);
//        orders.forEach(order -> waypoints.add(new LatLng(order.getLatitude(), order.getLongitude())));
//
//        String waypointsString = waypoints.stream()
//                .map(latlng -> latlng.lng + "," + latlng.lat)
//                .reduce((a, b) -> a + ";" + b)
//                .orElse("");
//
//        String url = OSRM_API_URL + waypointsString + "?source=first&destination=first&roundtrip=true";
//
//        RestTemplate restTemplate = new RestTemplate();
//        OSRMResponse response = restTemplate.getForObject(url, OSRMResponse.class);
//
//        List<RoutePoint> optimalRoute = new ArrayList<>();
//        if (response != null && response.waypoints != null) {
//            for (int i = 0; i < response.waypoints.size(); i++) {
//                Waypoint waypoint = response.waypoints.get(i);
//                String location = i == 0 ? "Start" : (i == response.waypoints.size() - 1 ? "End" : orders.get(i - 1).getLocation());
//                optimalRoute.add(new RoutePoint(waypoint.location[1], waypoint.location[0], location));
//            }
//        }
//
//        return optimalRoute;
//    }
//
//    private static class OSRMResponse {
//        public List<Waypoint> waypoints;
//    }
//
//    private static class Waypoint {
//        public double[] location;
//    }
//
//    public static class RoutePoint {
//        public double lat;
//        public double lng;
//        public String location;
//
//        public RoutePoint(double lat, double lng, String location) {
//            this.lat = lat;
//            this.lng = lng;
//            this.location = location;
//        }
//    }
//}
