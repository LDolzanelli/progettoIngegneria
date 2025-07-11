package it.unibs.ingsw.destinazioni.adapters.jpa.entity;

import java.io.Serializable;
import java.util.Objects;

public class MonthCollectionStateId implements Serializable {
    private int month;
    private int year;

    // Costruttore vuoto richiesto da JPA
    public MonthCollectionStateId() {}

    public MonthCollectionStateId(int month, int year) {
        this.month = month;
        this.year = year;
    }

    // equals() e hashCode() sono obbligatori
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MonthCollectionStateId)) return false;
        MonthCollectionStateId that = (MonthCollectionStateId) o;
        return month == that.month && year == that.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, year);
    }
}
