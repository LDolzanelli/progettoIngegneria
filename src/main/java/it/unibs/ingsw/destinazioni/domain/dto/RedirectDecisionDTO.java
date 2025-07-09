package it.unibs.ingsw.destinazioni.domain.dto;

public record RedirectDecisionDTO(
    String nickname,
    String role,
    boolean firstLogin,
    //null se non serve il redirect
    String redirectUrl
) {}