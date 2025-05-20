package it.unibs.ingsw.destinazioni.application.service;

import it.unibs.ingsw.destinazioni.application.port.in.ManageAreaOfInterestUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.QueryAreaOfInterestUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.AreaOfInterestRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.AreaOfInterest;
import org.springframework.stereotype.Service;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AreaOfInterestService implements ManageAreaOfInterestUseCase, QueryAreaOfInterestUseCase {

    private final AreaOfInterestRepositoryPort repository;

    public AreaOfInterestService(AreaOfInterestRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void addTown(String townName) {
        AreaOfInterest area = repository.load().orElse(new AreaOfInterest());
        area.getTowns().add(townName);
        repository.save(area);
    }

    @Override
    public void removeTown(String townName) {
        AreaOfInterest area = repository.load().orElse(new AreaOfInterest());
        area.getTowns().remove(townName);
        repository.save(area);
    }

    @Override
    public boolean containsTown(String townName) {
        return repository.load().orElse(new AreaOfInterest()).getTowns().contains(townName);
    }

    @Override
    public boolean isEmpty() {
        return repository.load().orElse(new AreaOfInterest()).getTowns().isEmpty();
    }

    @Override
    public ArrayList<String> townList() {
        return repository.load()
                .map(aoi -> new ArrayList<>(aoi.getTowns()))
                .orElseGet(ArrayList::new);
    }
}
