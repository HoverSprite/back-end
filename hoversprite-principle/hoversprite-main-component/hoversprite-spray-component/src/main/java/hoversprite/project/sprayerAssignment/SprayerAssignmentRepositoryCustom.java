package hoversprite.project.sprayerAssignment;

import java.util.List;
interface SprayerAssignmentRepositoryCustom {
    List<SprayerAssignment> findAllBySprayOrderIds(List<Long> ids);

    List<SprayerAssignment> findAllBySprayerAndSprayOrderIds(List<Long> sprayOrderIds, List<Long> sprayerIds);

    List<SprayerAssignment> findAssignmentsBySprayer(Long sprayerId);
}
