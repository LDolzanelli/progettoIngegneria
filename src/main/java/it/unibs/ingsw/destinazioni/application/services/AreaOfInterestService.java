package it.unibs.ingsw.destinazioni.application.services;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import it.unibs.ingsw.destinazioni.application.port.in.areaofinterest.ManageAreaOfInterestUseCase;
import it.unibs.ingsw.destinazioni.application.port.in.areaofinterest.QueryAreaOfInterestUseCase;
import it.unibs.ingsw.destinazioni.application.port.out.AreaOfInterestRepositoryPort;
import it.unibs.ingsw.destinazioni.domain.dto.TownProvinceDTO;
import it.unibs.ingsw.destinazioni.domain.model.AreaOfInterest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AreaOfInterestService implements ManageAreaOfInterestUseCase, QueryAreaOfInterestUseCase {

	private final AreaOfInterestRepositoryPort repository;


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
		return repository.load().map(aoi -> aoi.getAreas().stream()
				.map(TownProvinceDTO::town)
				.collect(Collectors.toCollection(ArrayList::new))).orElseGet(ArrayList::new);
	}


	@Override
	public Map<String, String> townProvinceMap() {
		AreaOfInterest aoi = repository.load().orElse(new AreaOfInterest());

		return aoi.getAreas().stream().collect(Collectors.toMap(TownProvinceDTO::town, TownProvinceDTO::province));
	}
}
