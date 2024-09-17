package hoversprite.project.notification;

import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.notification.NotificationService;
import hoversprite.project.partner.PersonGlobalService;
import hoversprite.project.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PersonGlobalService personGlobalService;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications() {
        PersonRole role = SecurityUtils.getCurrentUserRole();
        Long userId = SecurityUtils.getCurrentUserId();
        List<Notification> notifications = notificationService.getNotifications(userId, String.valueOf(role));
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
}