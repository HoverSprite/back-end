package hoversprite.project.project.service.impl;

import hoversprite.project.project.model.dto.SprayOrderDTO;
import hoversprite.project.project.model.entity.SprayerAssignment;
import hoversprite.project.project.repository.SprayerAssignmentRepository;
import hoversprite.project.project.service.SprayerAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SprayerAssignmentServiceImpl implements SprayerAssignmentService {

    @Autowired
    private SprayerAssignmentRepository sprayerAssignmentRepository;

    @Override
    public SprayOrderDTO assignSprayers(Integer orderId, SprayerAssignment sprayerAssignment) {
        return null;
    }
}
