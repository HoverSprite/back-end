package hoversprite.project.partner;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping
    public String test() {
        return "Test endpoint is working!";
    }

    @GetMapping("/2")
    public String apiTest() {
        return "API test endpoint is working!";
    }

    @GetMapping("/3")
    public String anotherTest() {
        return "Another test endpoint is working!";
    }
}