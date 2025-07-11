package it.unibs.ingsw.destinazioni.domain.dto;

import it.unibs.ingsw.destinazioni.domain.model.enums.Role;

public record UserInfoDTO(String nickname, Role role) {
}