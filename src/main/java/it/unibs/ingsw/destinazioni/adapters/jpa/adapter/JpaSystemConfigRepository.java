package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.BlockedDatesEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.entity.ConfigEntity;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.BlockedDatesRepository;
import it.unibs.ingsw.destinazioni.adapters.jpa.repository.ConfigRepository;
import it.unibs.ingsw.destinazioni.application.port.out.SystemConfigRepositoryPort;

/**
 * Adapter per le configurazioni di sistema.
 * Questo adapter si occupa di gestire le configurazioni di sistema
 * come le date bloccate e il numero massimo di biglietti per utente.
 * 
 * @version 1.0
 */
@Repository
public class JpaSystemConfigRepository implements SystemConfigRepositoryPort{

    private static final String MAX_TICKETS_PER_USER = "maxTicketsPerUser";
    private final BlockedDatesRepository blockedDatesRepository;
    private final ConfigRepository configRepository;

    public JpaSystemConfigRepository(BlockedDatesRepository blockedDatesRepository, ConfigRepository configRepository) {
        this.blockedDatesRepository = blockedDatesRepository;
        this.configRepository = configRepository;
    }

    @Override
    public int getMaxTicketsPerUser() {
        configRepository.findByName(MAX_TICKETS_PER_USER);
        return Integer.parseInt(configRepository.findByName(MAX_TICKETS_PER_USER).getValue());
    }

    @Override
    public void setMaxTicketsPerUser(int maxTicketsPerUser) {
        
        if(configRepository.findByName(MAX_TICKETS_PER_USER) == null) {
            ConfigEntity config = new ConfigEntity();
            config.setName(MAX_TICKETS_PER_USER);
            config.setValue(String.valueOf(maxTicketsPerUser));
            configRepository.save(config);
        } else {
            ConfigEntity config = configRepository.findByName(MAX_TICKETS_PER_USER);
            config.setValue(String.valueOf(maxTicketsPerUser));
            configRepository.save(config);
        }
    }

    @Override
    public List<LocalDate> getBlockedDates() {
        return blockedDatesRepository.findAll().stream()
                .map(blockedDate -> blockedDate.getDate())
                .toList();
    }

    @Override
    public void setBlockedDates(List<LocalDate> blockedDates) {
        blockedDatesRepository.deleteAll();
        for (LocalDate date : blockedDates) {
            BlockedDatesEntity blockedDatesEntity = new BlockedDatesEntity();
            blockedDatesEntity.setDate(date);
            blockedDatesRepository.save(blockedDatesEntity);
        }
    }

    @Override
    public void addBlockedDate(LocalDate date) {
        BlockedDatesEntity blockedDatesEntity = new BlockedDatesEntity();
        blockedDatesEntity.setDate(date);
        blockedDatesRepository.save(blockedDatesEntity);
    }

    @Override
    public void removeBlockedDate(LocalDate date) {
        BlockedDatesEntity blockedDatesEntity = blockedDatesRepository.findByDate(date);
        if (blockedDatesEntity != null) {
            blockedDatesRepository.delete(blockedDatesEntity);
        }
    }
    
}
