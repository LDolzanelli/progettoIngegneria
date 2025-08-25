package it.unibs.ingsw.destinazioni.domain.dto;

public record RedirectDecisionDTO(
    String nickname,
    String role,
    boolean firstLogin,
    String redirectUrl
) {}