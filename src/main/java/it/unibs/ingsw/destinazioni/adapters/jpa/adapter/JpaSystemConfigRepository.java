package it.unibs.ingsw.destinazioni.adapters.jpa.adapter;

import org.springframework.stereotype.Repository;

import it.unibs.ingsw.destinazioni.adapters.jpa.entity.ConfigEntity;
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

    private static final String MAX_TICKETS_PER_USER = "max_tickets_per_user_per_event";
    private final ConfigRepository configRepository;

    public JpaSystemConfigRepository(ConfigRepository configRepository) {
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

    
}
