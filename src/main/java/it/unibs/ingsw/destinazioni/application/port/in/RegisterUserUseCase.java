package it.unibs.ingsw.destinazioni.application.port.in;

import it.unibs.ingsw.destinazioni.domain.model.User;


public interface RegisterUserUseCase {

    void registerNewUser(User user);
    
} 
