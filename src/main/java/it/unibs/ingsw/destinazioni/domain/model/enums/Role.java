package it.unibs.ingsw.destinazioni.domain.model.enums;

import lombok.Getter;

@Getter
public enum Role {
    CONFIGURATOR("configurator"),
    VOLUNTEER("volunteer"),
    FINAL_USER("finalUser");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.name.equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + role);
    }

    @Override
    public String toString() {
        return name;
    }
}