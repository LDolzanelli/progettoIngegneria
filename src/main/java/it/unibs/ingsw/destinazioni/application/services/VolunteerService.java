package it.unibs.ingsw.destinazioni.application.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

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
    /*@ also
      @ requires queryVisitTypeUseCase != null && visitTypeValidationUseCase != null;
      @ ensures \result == (\forall VisitType vt; queryVisitTypeUseCase.listAll().contains(vt);
      @                    (!vt.getVolunteers().stream().anyMatch(v -> v.getId().equals(volunteer.getId())) ||
      @                     visitTypeValidationUseCase.canBeRemoved(vt.getId())));
      @*/
    public boolean canBeRemoved(User volunteer) {
        if (volunteer.getRole() != Role.VOLUNTEER) {
            throw new IllegalArgumentException("L'utente non è un volontario");
        }

        for (var visitType : queryVisitTypeUseCase.listAll()) {
            boolean assegnato = visitType.getVolunteers().stream().anyMatch(v -> v.getId().equals(volunteer.getId()));

            if (assegnato && !visitTypeValidationUseCase.canBeRemoved(visitType.getId())) {
                return false;
            }
        }
        return true;
    }


    @Override
    /*@ also
      @ requires queryVisitTypeUseCase != null && visitTypeCRUD != null && userRepository != null;
      @ ensures !userRepository.findByNickname(volunteer.getNickname()).isPresent();
      @ ensures (\forall VisitType vt; queryVisitTypeUseCase.listAll().contains(vt);
      @          !vt.getVolunteers().stream().anyMatch(v -> 
      @           v.getNickname().equals(volunteer.getNickname())));
      @ ensures (\forall VisitType vt; \old(vt.getVolunteers().size()) == 1 &&
      @          \old(vt.getVolunteers().get(0).getNickname().equals(volunteer.getNickname()));
      @          !queryVisitTypeUseCase.listAll().contains(vt));
      @*/
    public void removeVolunteer(User volunteer) {
        if (!canBeRemoved(volunteer)) {
            throw new IllegalArgumentException("Il volontario non può essere rimosso");
        }

        List<VisitType> visitTypes = queryVisitTypeUseCase.listAll().stream().filter(visitType -> visitType
                .getVolunteers().stream().anyMatch(v -> v.getNickname().equals(volunteer.getNickname()))).toList();

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
