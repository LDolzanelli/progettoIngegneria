package it.unibs.ingsw.destinazioni.application.service;

import it.unibs.ingsw.destinazioni.application.port.in.ManageAreaOfInterestUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.QueryAreaOfInterestUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.AreaOfInterestRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.dto.TownProvinceDTO;
import it.unibs.ingsw.destinazioni.domain.model.AreaOfInterest;
import org.springframework.stereotype.Service;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AreaOfInterestService implements ManageAreaOfInterestUseCase, QueryAreaOfInterestUseCase {

    private final AreaOfInterestRepositoryPort repository;

    public AreaOfInterestService(AreaOfInterestRepositoryPort repository) {
        this.repository = repository;
    }

	@Override
	public void addArea(String townName, String provinceName) {
		AreaOfInterest area = repository.load().orElse(new AreaOfInterest());
		area.addArea(new TownProvinceDTO(townName, provinceName));
		repository.save(area);
	}

	@Override
	public boolean isEmpty() {
		return repository.load().orElse(new AreaOfInterest()).getAreas().isEmpty();
	}

	@Override
	public ArrayList<String> townList() {
		return repository.load()
				.map(aoi -> aoi.getAreas().stream() //
						.map(TownProvinceDTO::town) //
						.collect(Collectors.toCollection(ArrayList::new)))
				.orElseGet(ArrayList::new);
	}

	@Override
	public Map<String, String> townProvinceMap() {
		AreaOfInterest aoi = repository.load().orElse(new AreaOfInterest());

		return aoi.getAreas().stream()
				.collect(Collectors.toMap(
						TownProvinceDTO::town,
						TownProvinceDTO::province
				));
	}
}
