package hoversprite.project.sprayerAssignment;

import hoversprite.project.base.AbstractService;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.request.SprayerAssignmentRequest;
import hoversprite.project.sprayOrder.SprayOrderDTO;
import hoversprite.project.validator.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class SprayerAssignmentServiceImpl extends AbstractService<SprayerAssignmentDTO, SprayerAssignmentRequest, Long, PersonRole> implements SprayerAssignmentService {

    @Autowired
    private SprayerAssignmentRepository sprayerAssignmentRepository;

    @Override
    public SprayOrderDTO assignSprayers(Integer orderId, SprayerAssignmentDTO sprayerAssignment) {
        return null;
    }

    @Override
    public SprayerAssignmentDTO findById(Long aLong) {
        return null;
    }

    @Override
    public List<SprayerAssignmentDTO> findAll() {
        return null;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    protected void validateForSave(ValidationUtils validator, SprayerAssignmentRequest dto, PersonRole personRole) {

    }

    @Override
    protected void validateForUpdate(ValidationUtils validator, SprayerAssignmentRequest dto, PersonRole personRole) {

    }

    @Override
    protected SprayerAssignmentDTO executeSave(Long userId, SprayerAssignmentRequest dto, PersonRole personRole) {
        SprayerAssignment sprayerAssignment = SprayerAssignment.builder()
                .sprayer(dto.getSprayer().getId())
                .isPrimary(dto.getIsPrimary())
                .sprayOrder(dto.getSprayOrder())
                .build();
        return SprayerAssignmentMapper.INSTANCE.toDto(sprayerAssignmentRepository.save(sprayerAssignment));
    }

    @Override
    protected SprayerAssignmentDTO executeUpdate(Long userId, Long aLong, SprayerAssignmentRequest dto, PersonRole personRole) {
        return null;
    }
}
