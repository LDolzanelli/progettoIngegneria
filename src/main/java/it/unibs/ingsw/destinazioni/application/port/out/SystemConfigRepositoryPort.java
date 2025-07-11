package it.unibs.ingsw.destinazioni.application.port.out;


public interface SystemConfigRepositoryPort {

    public int getMaxTicketsPerUser();
    public void setMaxTicketsPerUser(int maxTicketsPerUser);
}
