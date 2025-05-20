package it.unibs.ingsw.destinazioni.application.port.out;

import java.time.LocalDate;
import java.util.List;

public interface SystemConfigRepositoryPort {

    public int getMaxTicketsPerUser();
    public void setMaxTicketsPerUser(int maxTicketsPerUser);

    public List<LocalDate> getBlockedDates();
    public void setBlockedDates(List<LocalDate> blockedDates);
    public void addBlockedDate(LocalDate date);
    public void removeBlockedDate(LocalDate date);
    
}
