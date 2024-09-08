package hoversprite.project.sprayerAssignment;

import hoversprite.project.base.AbstractService;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.request.SprayerAssignmentRequest;
import hoversprite.project.sprayOrder.SprayOrderDTO;
import hoversprite.project.sprayOrder.SprayOrderGlobalService;
import hoversprite.project.spraySession.SpraySession2GlobalService;
import hoversprite.project.spraySession.SpraySessionDTO;
import hoversprite.project.validator.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
class SprayerAssignmentServiceImpl extends AbstractService<SprayerAssignmentDTO, SprayerAssignmentRequest, Long, PersonRole> implements SprayerAssignmentService {

    @Autowired
    private SprayerAssignmentRepository sprayerAssignmentRepository;

    @Autowired
    private SpraySession2GlobalService spraySession2GlobalService;

    @Override
    public SprayOrderDTO assignSprayers(Integer orderId, SprayerAssignmentDTO sprayerAssignment) {
        return null;
    }

    @Override
    public List<SprayerAssignmentDTO> findAllByIds(Iterable<Long> ids) {
        return sprayerAssignmentRepository.findAllById(ids).stream().map(SprayerAssignmentMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public List<SprayerAssignmentDTO> findAllBySprayOrderIds(List<Long> ids) {
        return sprayerAssignmentRepository.findAllBySprayOrderIds(ids).stream().map(SprayerAssignmentMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public List<Long> findUnAvailableSprayerIds(SprayOrderDTO sprayOrderDTO) {
        SpraySessionDTO session = spraySession2GlobalService.findById(sprayOrderDTO.getSpraySession());
        List<SpraySessionDTO> spraySessionsFound = spraySession2GlobalService.findSpraySessionByDate(session.getDate(), session.getStartTime());
        List<Long> sprayOrderIds = spraySessionsFound.stream().map(SpraySessionDTO::getSprayOrder).collect(Collectors.toList());
        List<SprayerAssignmentDTO> assignmentsFound = findAllBySprayOrderIds(sprayOrderIds);
        return assignmentsFound.stream().map(SprayerAssignmentDTO::getSprayer).collect(Collectors.toList());
    }

    @Override
    public List<SprayerAssignmentDTO> findSprayerAssignmentInTheWeek(List<Long> sprayerOrderIds, List<Long> sprayerIds) {
        return sprayerAssignmentRepository.findAllBySprayerAndSprayOrderIds(sprayerOrderIds, sprayerIds)
                .stream().map(SprayerAssignmentMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public SprayerAssignmentDTO findById(Long aLong) {
        return sprayerAssignmentRepository.findById(aLong).map(SprayerAssignmentMapper.INSTANCE::toDto).orElse(null);
    }

    @Override
    public List<SprayerAssignmentDTO> findAll() {
        return sprayerAssignmentRepository.findAll().stream().map(SprayerAssignmentMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long aLong) {
        sprayerAssignmentRepository.deleteById(aLong);
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
