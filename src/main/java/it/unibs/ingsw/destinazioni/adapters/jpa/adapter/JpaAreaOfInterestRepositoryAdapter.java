package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import it.unibs.ingsw.destinazioni.domain.dto.TownProvinceDTO;
import org.springframework.stereotype.Repository;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.AreaOfInterestEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.AreaOfInterestRepository;
import it.unibs.ingsw.destinazioni.application.port.out.AreaOfInterestRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.model.AreaOfInterest;


/**
 * Adapter per il repository di AreaOfInterest.
 * Questo adapter si occupa di convertire le entità JPA in oggetti di dominio e viceversa.
 * Utilizza il repository JPA per eseguire le operazioni di persistenza.
 * 
 * @version 1.0
 */
@Repository
public class JpaAreaOfInterestRepositoryAdapter implements AreaOfInterestRepositoryPort{

    private final AreaOfInterestRepository areaOfInterestRepository;

    public JpaAreaOfInterestRepositoryAdapter(AreaOfInterestRepository areaOfInterestRepository) {
        this.areaOfInterestRepository = areaOfInterestRepository;
    }

    @Override
    public void save(AreaOfInterest areaOfInterest) {
        for (TownProvinceDTO dto : areaOfInterest.getAreas()) {
            AreaOfInterestEntity entity = new AreaOfInterestEntity();
            entity.setTown(dto.town());
            entity.setProvince(dto.province());
            areaOfInterestRepository.save(entity);
        }
    }

    public Optional<AreaOfInterest> load() {
        List<AreaOfInterestEntity> entities = areaOfInterestRepository.findAll();

        Set<TownProvinceDTO> areaSet = entities.stream()
                .map(e -> new TownProvinceDTO(e.getTown(), e.getProvince()))
                .collect(Collectors.toSet());

        return Optional.of(new AreaOfInterest(areaSet));
    }
}
