package it.unibs.ingsw.destinazioni.domain.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Location {

    private Integer id;
    private String name;
    private String description;
    private LocationAddress address;
    private List<VisitType> visitTypes;

    public Location(Integer id, String name, String description, LocationAddress address, List<VisitType> visitTypes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.visitTypes = visitTypes;
    }


    /**
     * Costruttore per la creazione di una Location senza ID
     * Questo costruttore viene utilizzato quando si crea una nuova Location perché la generazione dell'ID è gestita dal database.
     * Il campo ID sarà null fino a quando la Location non verrà salvata nel database.
     */
    public Location(String name, String description, LocationAddress address, List<VisitType> visits) {
        this(null, name, description, address, visits);
    }


    public void removeVisitType(VisitType visitType) {

         visitTypes = new ArrayList<>(visitTypes);

        for (VisitType vt : visitTypes) {
            if (vt.getId().equals(visitType.getId())) {
                visitTypes.remove(vt);
                break;
            }
        }
    }

}
