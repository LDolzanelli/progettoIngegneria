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

@Service
public class AreaOfInterestService implements ManageAreaOfInterestUseCase, QueryAreaOfInterestUseCase {

	private final AreaOfInterestRepositoryPort repository;

	public AreaOfInterestService(AreaOfInterestRepositoryPort repository) {
		this.repository = repository;
	}


	@Override
	/*@ also
	  @ requires repository != null;
	  @ ensures townList().contains(townName);
	  @ ensures townProvinceMap().containsKey(townName);
	  @ ensures townProvinceMap().get(townName).equals(provinceName);
	  @ ensures !isEmpty();
	  @*/
	public void addArea(String townName, String provinceName) {
		AreaOfInterest area = repository.load().orElse(new AreaOfInterest());
		area.addArea(new TownProvinceDTO(townName, provinceName));
		repository.save(area);
	}


	@Override
	/*@ also
	  @ ensures \result <==> repository.load().orElse(new AreaOfInterest()).getAreas().isEmpty();
	  @*/
	public boolean isEmpty() {
		return repository.load().orElse(new AreaOfInterest()).getAreas().isEmpty();
	}


	@Override
	/*@ also
	  @ ensures \result != null;
	  @ ensures \result.size() == repository.load().orElse(new AreaOfInterest()).getAreas().size();
	  @ ensures (\forall String town; \result.contains(town);
	  @          (\exists TownProvinceDTO dto; repository.load().orElse(new AreaOfInterest()).getAreas().contains(dto);
	  @           dto.town().equals(town)));
	  @*/
	public ArrayList<String> townList() {
		return repository.load().map(aoi -> aoi.getAreas().stream() //
				.map(TownProvinceDTO::town) //
				.collect(Collectors.toCollection(ArrayList::new))).orElseGet(ArrayList::new);
	}


	@Override
	/*@ also
	  @ ensures \result != null;
	  @ ensures \result.size() == repository.load().orElse(new AreaOfInterest()).getAreas().size();
	  @ ensures (\forall String town; \result.keySet().contains(town);
	  @          (\exists TownProvinceDTO dto; repository.load().orElse(new AreaOfInterest()).getAreas().contains(dto);
	  @           dto.town().equals(town) && \result.get(town).equals(dto.province())));
	  @*/
	public Map<String, String> townProvinceMap() {
		AreaOfInterest aoi = repository.load().orElse(new AreaOfInterest());

		return aoi.getAreas().stream().collect(Collectors.toMap(TownProvinceDTO::town, TownProvinceDTO::province));
	}
}
