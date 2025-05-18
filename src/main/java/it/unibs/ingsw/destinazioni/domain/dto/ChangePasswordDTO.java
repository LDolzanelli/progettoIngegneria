package it.unibs.ingsw.destinazioni.domain.dto;

public record ChangePasswordDTO(String nickname, String oldPassword, String newPassword) {}