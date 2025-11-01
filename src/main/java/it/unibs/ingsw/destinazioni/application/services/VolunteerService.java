package it.unibs.ingsw.destinazioni.application.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import it.unibs.ingsw.destinazioni.application.exceptions.codes.UserErrorCode;
import it.unibs.ingsw.destinazioni.application.exceptions.usecases.UserException;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeCommandUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeQueryUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeValidationUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.volunteer.RemoveVolunteerUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.volunteer.VolunteerValidationUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.UserRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VolunteerService implements VolunteerValidationUseCase, RemoveVolunteerUseCase {

    private final UserRepositoryPort userRepository;
    private final VisitTypeQueryUseCase queryVisitTypeUseCase;
    private final VisitTypeValidationUseCase visitTypeValidationUseCase;
    private final VisitTypeCommandUseCase visitTypeCRUD;

    @Override
    public boolean canBeRemoved(User volunteer) {
        if (volunteer.getRole() != Role.VOLUNTEER) {

            throw new UserException(UserErrorCode.UNAUTHORIZED_REQUEST, "L'utente non è un volontario");
        }

        for (var visitType : queryVisitTypeUseCase.listAll()) {
            boolean isAnyVolunteerAssigned = visitType.getVolunteers().stream()
                    .anyMatch(visitTypeVolunteer -> visitTypeVolunteer.getId().equals(volunteer.getId()));

            if (isAnyVolunteerAssigned && !visitTypeValidationUseCase.canBeRemoved(visitType.getId())) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void removeVolunteer(User volunteer) {
        if (!canBeRemoved(volunteer)) {
            throw new UserException(UserErrorCode.UNAUTHORIZED_REQUEST, "Il volontario non può essere rimosso");
        }

        List<VisitType> visitTypes = queryVisitTypeUseCase.listAll().stream()
                .filter(visitType -> visitType.getVolunteers().stream()
                        .anyMatch(v -> v.getNickname().equals(volunteer.getNickname())))
                .toList();

        for (VisitType visitType : visitTypes) {

            List<User> volunteers = new ArrayList<>(visitType.getVolunteers());
            volunteers.removeIf(v -> v.getNickname().equals(volunteer.getNickname()));
            visitType.setVolunteers(volunteers);

            if (visitType.getVolunteers().isEmpty()) {
                visitTypeCRUD.removeVisitType(visitType.getId());
            } else {
                visitTypeCRUD.updateVisitType(visitType);
            }
        }

        userRepository.deleteByNickname(volunteer.getNickname());
    }

}
