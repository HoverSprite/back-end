package hoversprite.project;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GeocodingUtil {
    private static final String GEOCODING_API_URL = "https://nominatim.openstreetmap.org/search?format=json&q=";

    public LatLng getCoordinates(String address) {
        RestTemplate restTemplate = new RestTemplate();
        String encodedAddress = address.replace(" ", "+");
        String url = GEOCODING_API_URL + encodedAddress;

        GeocodingResponse[] response = restTemplate.getForObject(url, GeocodingResponse[].class);

        if (response != null && response.length > 0) {
            return new LatLng(
                    Double.parseDouble(response[0].lat),
                    Double.parseDouble(response[0].lon)
            );
        }

        return null;
    }

    private static class GeocodingResponse {
        public String lat;
        public String lon;
    }

    public static class LatLng {
        public double lat;
        public double lng;

        public LatLng(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }
}