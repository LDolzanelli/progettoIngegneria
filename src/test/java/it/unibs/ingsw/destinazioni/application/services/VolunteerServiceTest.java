package it.unibs.ingsw.destinazioni.application.services;

import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeCommandUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeQueryUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.visittype.VisitTypeValidationUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.UserRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.User;
import it.unibs.ingsw.destinazioni.domain.model.VisitType;
import it.unibs.ingsw.destinazioni.domain.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VolunteerServiceTest {

    private UserRepositoryPort userRepository;
    private VisitTypeQueryUseCase queryVisitTypeUseCase;
    private VisitTypeValidationUseCase visitTypeValidationUseCase;
    private VisitTypeCommandUseCase visitTypeCRUD;

    private VolunteerService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepositoryPort.class);
        queryVisitTypeUseCase = mock(VisitTypeQueryUseCase.class);
        visitTypeValidationUseCase = mock(VisitTypeValidationUseCase.class);
        visitTypeCRUD = mock(VisitTypeCommandUseCase.class);

        service = new VolunteerService(userRepository, queryVisitTypeUseCase, visitTypeValidationUseCase, visitTypeCRUD);
    }

    @Test
    void canBeRemoved_UserNotVolunteer_ShouldThrowException() {
        User user = new User("configurator", "password", Role.CONFIGURATOR);
        assertThrows(IllegalArgumentException.class, () -> service.canBeRemoved(user));
    }

    @Test
    void canBeRemoved_VolunteerNotAssignedToAnyVisitType_ShouldReturnTrue() {
        User user = new User("volunteer", "password", Role.VOLUNTEER);
        when(queryVisitTypeUseCase.listAll()).thenReturn(Set.of());

        assertTrue(service.canBeRemoved(user));
    }

    @Test
    void canBeRemoved_VolunteerAssignedButVisitTypeCannotBeRemoved_ShouldReturnFalse() {
        User user = new User(1, "volunteer", "password", Role.VOLUNTEER, false);
        VisitType visitType = mock(VisitType.class);
        when(queryVisitTypeUseCase.listAll()).thenReturn(Set.of(visitType));
        when(visitType.getVolunteers()).thenReturn(List.of(user));
        when(visitTypeValidationUseCase.canBeRemoved(anyInt())).thenReturn(false);


        assertFalse(service.canBeRemoved(user));
    }

    @Test
    void canBeRemoved_CorrectConditions_ShouldReturnTrue() {
        User user = new User(1, "volunteer", "password", Role.VOLUNTEER, false);
        VisitType visitType = mock(VisitType.class);
        when(queryVisitTypeUseCase.listAll()).thenReturn(Set.of(visitType));
        when(visitType.getVolunteers()).thenReturn(List.of(user));
        when(visitTypeValidationUseCase.canBeRemoved(anyInt())).thenReturn(true);

        assertTrue(service.canBeRemoved(user));
    }

    @Test
    void removeVolunteer_VolunteerCannotBeRemoved_ShouldThrowException() {
        User user = new User(1, "volunteer", "password", Role.VOLUNTEER, false);
        VisitType visitType = mock(VisitType.class);
        when(queryVisitTypeUseCase.listAll()).thenReturn(Set.of(visitType));
        when(visitType.getVolunteers()).thenReturn(List.of(user));
        when(visitTypeValidationUseCase.canBeRemoved(anyInt())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.removeVolunteer(user));
    }

    @Test
    void removeVolunteer_VolunteerAssignedAndOneRemains_ShouldUpdateVisitType() {
        User user1 = new User(1, "volunteer1", "password", Role.VOLUNTEER, false);
        User user2 = new User(2, "volunteer2", "password", Role.VOLUNTEER, false);
        VisitType visitType = new VisitType(2, "", "", "", null, null, //
                null, 1, 1, 1, true, List.of(), List.of(user1, user2));

        when(queryVisitTypeUseCase.listAll()).thenReturn(Set.of(visitType));

        when(visitTypeValidationUseCase.canBeRemoved(anyInt())).thenReturn(true);

        service.removeVolunteer(user1);

        assertFalse(visitType.getVolunteers().contains(user1));
        verify(visitTypeCRUD).updateVisitType(visitType);
        verify(userRepository).deleteByNickname("volunteer1");
    }

    @Test
    void removeVolunteer_VolunteerIsLastInVisit_ShouldRemoveVisitType() {
        User user = new User(1, "volunteer", "password", Role.VOLUNTEER, false);
        VisitType visitType = new VisitType(2, "", "", "", null, //
                null, null, 1, 1, 1, true, List.of(), List.of(user));

        when(queryVisitTypeUseCase.listAll()).thenReturn(Set.of(visitType));
        when(visitTypeValidationUseCase.canBeRemoved(anyInt())).thenReturn(true);

        service.removeVolunteer(user);

        verify(visitTypeCRUD).removeVisitType(2);
        verify(userRepository).deleteByNickname("volunteer");
    }

    @Test
    void removeVolunteer_VolunteerNotAssignedToAnyVisitType_ShouldDeleteUser() {
        User user = new User(1, "volunteer", "password", Role.VOLUNTEER, false);

        when(queryVisitTypeUseCase.listAll()).thenReturn(Set.of());
        when(visitTypeValidationUseCase.canBeRemoved(anyInt())).thenReturn(true);

        service.removeVolunteer(user);

        verify(userRepository).deleteByNickname("volunteer");
        verifyNoInteractions(visitTypeCRUD);
    }
}
