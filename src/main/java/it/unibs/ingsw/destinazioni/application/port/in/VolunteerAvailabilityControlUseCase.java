package it.unibs.ingsw.destinazioni.application.port.in;


public interface VolunteerAvailabilityControlUseCase {
    boolean canEnableAvailability();
    boolean canDisableAvailability();
    void enableAvailability();
    void disableAvailability();
    int getMonthToEnable();
    int getMonthToDisable();
}
