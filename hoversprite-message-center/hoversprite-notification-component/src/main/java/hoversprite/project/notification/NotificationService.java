package hoversprite.project.notification;

import hoversprite.project.notification.Notification;
import hoversprite.project.notification.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    public void notifyFarmer(Long farmerId, String message) {
        saveAndSendNotification(farmerId, "FARMER", message);
    }

    public void notifySprayer(Long sprayerId, String message) {
        saveAndSendNotification(sprayerId, "SPRAYER", message);
    }

    private void saveAndSendNotification(Long userId, String userRole, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .userRole(userRole)
                .message(message)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/" + userRole.toLowerCase() + "/" + userId, message);
    }

    public List<Notification> getNotifications(Long userId, String userRole) {
        return notificationRepository.findByUserIdAndUserRoleOrderByCreatedAtDesc(userId, userRole);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}