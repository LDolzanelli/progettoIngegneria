package it.unibs.ingsw.destinazioni.application.services;

import org.springframework.stereotype.Service;

import it.unibs.ingsw.destinazioni.application.port.in.ChangeMaxNumberOfTicketsUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.SystemConfigRepositoryPort;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemConfigService implements ChangeMaxNumberOfTicketsUseCase {

    private final SystemConfigRepositoryPort repository;

    @Override
    /*@ also
      @ requires repository != null;
      @ ensures repository.getMaxTicketsPerUser() == maxNumberOfTickets;
      @*/
    public void setMaxNumberOfTickets(int maxNumberOfTickets) {
        repository.setMaxTicketsPerUser(maxNumberOfTickets);

    }


    @Override
    /*@ also
      @ ensures \result == repository.getMaxTicketsPerUser();
      @ ensures \result > 0;
      @*/
    public int getMaxNumberOfTickets() {
        return repository.getMaxTicketsPerUser();
    }

}
