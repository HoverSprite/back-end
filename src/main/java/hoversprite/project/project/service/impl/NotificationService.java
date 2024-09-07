package hoversprite.project.project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyFarmer(Long farmerId, String message) {
        messagingTemplate.convertAndSend("/topic/farmer/" + farmerId, message);
    }

    public void notifySprayer(Long sprayerId, String message) {
        messagingTemplate.convertAndSend("/topic/sprayer/" + sprayerId, message);
    }
}
