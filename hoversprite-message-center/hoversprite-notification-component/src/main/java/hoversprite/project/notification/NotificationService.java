package hoversprite.project.notification;

import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.notification.Notification;
import hoversprite.project.notification.NotificationRepository;
import hoversprite.project.partner.PersonDTO;
import hoversprite.project.partner.PersonGlobalService;
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

    @Autowired
    private PersonGlobalService personGlobalService;

    public void notifyUser(Long userId, String userRole, String message) {
        PersonDTO person = personGlobalService.findById(userId);
        if (person == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        String userEmail = person.getEmailAddress();

        saveAndSendNotification(userId, userEmail, userRole, message);
    }

    private void saveAndSendNotification(Long userId, String userEmail, String userRole, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .userEmail(userEmail)
                .userRole(userRole)
                .message(message)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/" + userEmail, message);
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