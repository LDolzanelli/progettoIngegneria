package it.unibs.ingsw.destinazioni.application.port.out;


public interface VolunteerAvailabilityStatePort {

    boolean isVolunteerAvailabilityOpen(int month, int year);
    void setVolunteerAvailabilityOpen(int month, int year, boolean enabled);

}
