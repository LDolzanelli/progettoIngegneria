package it.unibs.ingsw.destinazioni.application.service;

import it.unibs.ingsw.destinazioni.application.port.in.ChangeMaxNumberOfTicketsUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.SystemConfigRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemConfigService implements ChangeMaxNumberOfTicketsUseCase {

    private final SystemConfigRepositoryPort repository;

    @Override
    public void setMaxNumberOfTickets(int maxNumberOfTickets) {
        repository.setMaxTicketsPerUser(maxNumberOfTickets);

    }

    @Override
    public int getMaxNumberOfTickets() {
        return repository.getMaxTicketsPerUser();
    }
}
