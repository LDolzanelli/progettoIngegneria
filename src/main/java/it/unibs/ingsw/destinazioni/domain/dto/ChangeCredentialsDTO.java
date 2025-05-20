package it.unibs.ingsw.destinazioni.domain.dto;

public record ChangeCredentialsDTO(String oldUsername, String newUsername, String oldPassword, String newPassword) {}
