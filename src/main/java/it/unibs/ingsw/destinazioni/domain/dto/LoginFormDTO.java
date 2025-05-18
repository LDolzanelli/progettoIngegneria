package it.unibs.ingsw.destinazioni.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginFormDTO {
    private String nickname;
    private String password;
}