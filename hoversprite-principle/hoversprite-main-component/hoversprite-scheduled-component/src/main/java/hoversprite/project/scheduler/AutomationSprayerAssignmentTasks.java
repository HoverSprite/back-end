package hoversprite.project.scheduler;

import hoversprite.project.common.annotation.SecureContext;
import hoversprite.project.common.domain.SprayStatus;
import hoversprite.project.partner.PersonGlobalService;
import hoversprite.project.sprayOrder.SprayOrderDTO;
import hoversprite.project.sprayOrder.SprayOrderGlobalService;
import hoversprite.project.spraySession.SpraySession2GlobalService;
import hoversprite.project.sprayerAssignment.SprayerAssignmentGlobalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class AutomationSprayerAssignmentTasks {
    private static final Logger logger = LoggerFactory.getLogger(AutomationSprayerAssignmentTasks.class);

    @Autowired
    private SecureContext secureContext;

    @Autowired
    private PersonGlobalService personGlobalService;

    @Autowired
    private SprayOrderGlobalService sprayOrderGlobalService;

    @Autowired
    private SprayerAssignmentGlobalService sprayerAssignmentGlobalService;

    @Autowired
    private SpraySession2GlobalService spraySession2GlobalService;

    @Scheduled(cron = "*/5 * * * * ?")
    public void scheduleTaskWithCronExpression() {
        List<SprayOrderDTO> unAssignedSprayOrders = sprayOrderGlobalService.getUnAssignedSprayOrders();
        if (CollectionUtils.isEmpty(unAssignedSprayOrders)) {
            logger.info("No unassigned spray orders found.");
            return;
        }

        unAssignedSprayOrders.forEach(unAssignedSprayOrder -> {
            SprayStatus previousStatus = unAssignedSprayOrder.getStatus();

            try {
                secureContext.allowAccess();
                SprayOrderDTO updateSprayOrder = sprayOrderGlobalService.lockAndUnlockStatus(unAssignedSprayOrder, SprayStatus.ASSIGN_PROCESSING);
                sprayOrderGlobalService.automateSprayerSelection(updateSprayOrder, previousStatus);
            } catch (Exception e) {
                // Log individual failures
                logger.error("Failed to process spray order: " + unAssignedSprayOrder, e);
                sprayOrderGlobalService.lockAndUnlockStatus(unAssignedSprayOrder, previousStatus);
            }
            finally {
                secureContext.denyAccess();
            }
        });
    }
}