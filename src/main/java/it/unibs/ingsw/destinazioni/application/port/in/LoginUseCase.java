package it.unibs.ingsw.destinazioni.application.port.in;

import it.unibs.ingsw.destinazioni.domain.dto.LoginRequestDTO;
import it.unibs.ingsw.destinazioni.domain.dto.LoginResponseDTO;


public interface LoginUseCase {

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

}
