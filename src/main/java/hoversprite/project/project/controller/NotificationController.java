package hoversprite.project.project.controller;

import hoversprite.project.project.service.impl.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/farmer/notify")
    public void notifyFarmer(Long farmerId, String message) {
        notificationService.notifyFarmer(farmerId, message);
    }

    @MessageMapping("/sprayer/notify")
    public void notifySprayer(Long sprayerId, String message) {
        notificationService.notifySprayer(sprayerId, message);
    }

    @MessageMapping("/broadcast")
    @SendTo("/topic/notifications")
    public String broadcastMessage(String message) {
        return message;  // This will broadcast to all subscribers of /topic/notifications
    }
}
