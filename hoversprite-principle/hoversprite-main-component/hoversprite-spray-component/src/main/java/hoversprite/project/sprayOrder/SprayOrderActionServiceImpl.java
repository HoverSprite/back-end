package hoversprite.project.sprayOrder;

import com.mysema.commons.lang.Pair;
import hoversprite.project.common.domain.PersonExpertise;
import hoversprite.project.common.domain.PersonRole;
import hoversprite.project.partner.PersonDTO;
import hoversprite.project.partner.PersonGlobalService;
import hoversprite.project.request.PersonRequest;
import hoversprite.project.request.SprayerAssignmentRequest;
import hoversprite.project.spraySession.SpraySession2GlobalService;
import hoversprite.project.spraySession.SpraySessionDTO;
import hoversprite.project.sprayerAssignment.SprayerAssignmentDTO;
import hoversprite.project.sprayerAssignment.SprayerAssignmentGlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
class SprayOrderActionServiceImpl implements SprayOrderActionService {

    @Autowired
    private SprayerAssignmentGlobalService sprayerAssignmentGlobalService;

    @Autowired
    private SpraySession2GlobalService spraySession2GlobalService;

    @Autowired
    private PersonGlobalService personGlobalService;
    @Override
    public boolean automateSprayerSelected(SprayOrderDTO sprayOrderDTO) {
        List<Long> unAvailableSprayersId = sprayerAssignmentGlobalService.findUnAvailableSprayerIds(sprayOrderDTO);
        Map<PersonExpertise, List<PersonDTO>> availableSprayers = personGlobalService.getSprayersGroupedByExpertise(unAvailableSprayersId);

        SpraySessionDTO session = spraySession2GlobalService.findById(sprayOrderDTO.getSpraySession());
        List<SpraySessionDTO> spraySessionByWeek = spraySession2GlobalService.findSpraySessionByWeek(session.getDate());
        List<Long> sprayOrderIdsByWeek = spraySessionByWeek.stream().map(SpraySessionDTO::getSprayOrder).collect(Collectors.toList());

        Map<PersonExpertise, List<Pair<PersonDTO, Integer>>> sortedAvailableSprayers = new HashMap<>();

        for (Map.Entry<PersonExpertise, List<PersonDTO>> entry : availableSprayers.entrySet()) {
            PersonExpertise expertise = entry.getKey();
            List<PersonDTO> personDTOs = entry.getValue();

            List<Pair<PersonDTO, Integer>> personWithAssignmentCount = new ArrayList<>();

            for (PersonDTO personDTO : personDTOs) {
                List<SprayerAssignmentDTO> assignments = sprayerAssignmentGlobalService.findSprayerAssignmentInTheWeek(sprayOrderIdsByWeek, Collections.singletonList(personDTO.getId()));
                int assignmentCount = assignments.size();
                personWithAssignmentCount.add(new Pair<>(personDTO, assignmentCount));
            }

            List<Pair<PersonDTO, Integer>> sortedPersonWithAssignmentCount = personWithAssignmentCount.stream()
                    .sorted(Comparator.comparingInt(Pair::getSecond))
                    .collect(Collectors.toList());

            sortedAvailableSprayers.put(expertise, sortedPersonWithAssignmentCount);
        }

        List<PersonDTO> selectedSprayers = new ArrayList<>();
        List<PersonDTO> noAssignmentsSprayersApprentice = getFilteredSprayers(sortedAvailableSprayers, PersonExpertise.APPRENTICE, false);
        List<PersonDTO> someAssignmentsSprayersApprentice = getFilteredSprayers(sortedAvailableSprayers, PersonExpertise.APPRENTICE, true);
        List<PersonDTO> noAssignmentsSprayersAdept = getFilteredSprayers(sortedAvailableSprayers, PersonExpertise.ADEPT, false);
        List<PersonDTO> someAssignmentsSprayersAdept = getFilteredSprayers(sortedAvailableSprayers, PersonExpertise.ADEPT, true);
        List<PersonDTO> noAssignmentsSprayersExpert = getFilteredSprayers(sortedAvailableSprayers, PersonExpertise.EXPERT, false);
        List<PersonDTO> someAssignmentsSprayersExpert = getFilteredSprayers(sortedAvailableSprayers, PersonExpertise.EXPERT, true);


        // START ANALYZING
        addMultipleListToSelectedSprayer(selectedSprayers, noAssignmentsSprayersApprentice,
                noAssignmentsSprayersAdept, noAssignmentsSprayersExpert);

        if (selectedSprayers.size() > 1) {
            handleSprayerSelection(selectedSprayers, sprayOrderDTO);
            return true;
        }

        if (selectedSprayers.size() == 1) {
            if (selectedSprayers.get(0).getExpertise() == PersonExpertise.APPRENTICE) {
                addSprayersWithNoAssignments(selectedSprayers, someAssignmentsSprayersAdept);
                addSprayersWithNoAssignments(selectedSprayers, someAssignmentsSprayersExpert);
                if (selectedSprayers.size() > 1) {
                    handleSprayerSelection(selectedSprayers, sprayOrderDTO);
                    return true;
                }
            }
            if (Arrays.asList(PersonExpertise.ADEPT, PersonExpertise.EXPERT).contains(selectedSprayers.get(0).getExpertise())) {
                if (selectedSprayers.get(0).getExpertise() == PersonExpertise.EXPERT) {
                    handleSprayerSelection(selectedSprayers, sprayOrderDTO);
                    return true;
                }
                addMultipleListToSelectedSprayer(selectedSprayers, someAssignmentsSprayersApprentice,
                        someAssignmentsSprayersAdept, someAssignmentsSprayersExpert);
                if (selectedSprayers.size() > 1) {
                    handleSprayerSelection(selectedSprayers, sprayOrderDTO);
                    return true;
                }

            }
        }

        if (selectedSprayers.isEmpty()) {
            addMultipleListToSelectedSprayer(selectedSprayers, someAssignmentsSprayersApprentice,
                    someAssignmentsSprayersAdept, someAssignmentsSprayersExpert);

            if (selectedSprayers.size() > 1) {
                handleSprayerSelection(selectedSprayers, sprayOrderDTO);
                return true;
            }
            if (selectedSprayers.size() == 1 && selectedSprayers.get(0).getExpertise() == PersonExpertise.EXPERT) {
                handleSprayerSelection(selectedSprayers, sprayOrderDTO);
                return true;
            }
        }

        return false;
    }

    @SafeVarargs
    private void addMultipleListToSelectedSprayer(List<PersonDTO> selectedSprayers, List<PersonDTO>... multipleList) {
        for (List<PersonDTO> list : multipleList) {
            addSprayersWithNoAssignments(selectedSprayers, list);
        }
    }

    private List<PersonDTO> limitToTwoSprayers(List<PersonDTO> sprayers) {
        return sprayers.size() > 2 ? sprayers.subList(0, 2) : sprayers;
    }

    // Method to add sprayers based on specific conditions and create assignments
    private void handleSprayerSelection(List<PersonDTO> selectedSprayers, SprayOrderDTO sprayOrderDTO) {
        selectedSprayers = limitToTwoSprayers(selectedSprayers);
        createAssigments(selectedSprayers, sprayOrderDTO.getId());
    }


    private void createAssigments(List<PersonDTO> selectedSprayers, Long sprayOrderId) {
        List<SprayerAssignmentRequest> assignmentRequests = selectedSprayers.stream()
                .map(selectedSprayer -> SprayerAssignmentRequest.builder()
                        .sprayOrder(sprayOrderId)
                        .sprayer(PersonRequest.builder().id(sprayOrderId).build())
                        .isPrimary(true).build())
                .collect(Collectors.toList());

        assignmentRequests.forEach(sprayerAssignmentRequest -> sprayerAssignmentGlobalService.save(null, sprayerAssignmentRequest, PersonRole.ADMIN));
    }

    private void addSprayersWithNoAssignments(List<PersonDTO> selectedSprayers, List<PersonDTO> noAssignedSprayers) {
        if (noAssignedSprayers != null && !noAssignedSprayers.isEmpty()) {
            selectedSprayers.add(noAssignedSprayers.get(0));
        }
    }

    private List<PersonDTO> getFilteredSprayers(Map<PersonExpertise, List<Pair<PersonDTO, Integer>>> sortedAvailableSprayers, PersonExpertise expertise, boolean biggerThan0) {
        List<Pair<PersonDTO, Integer>> sprayers = sortedAvailableSprayers.get(expertise);
        if (sprayers == null) return Collections.emptyList();
        return sprayers.stream()
                .filter(pair -> biggerThan0 ? pair.getSecond() > 0 : pair.getSecond() == 0)
                .map(Pair::getFirst)
                .collect(Collectors.toList());
    }
}
