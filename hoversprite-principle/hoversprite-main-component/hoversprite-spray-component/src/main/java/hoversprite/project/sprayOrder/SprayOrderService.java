package hoversprite.project.sprayOrder;

import com.mysema.commons.lang.Pair;
import hoversprite.project.common.domain.PersonExpertise;
import hoversprite.project.partner.PersonDTO;

import java.util.List;
import java.util.Map;

interface SprayOrderService extends SprayOrderGlobalService {

    boolean automateSprayerSelected(SprayOrderDTO sprayOrderDTO);
    Map<PersonExpertise, List<Pair<PersonDTO, Integer>>> getSortedAvailableSprayers(Long sprayOrderId);
}
