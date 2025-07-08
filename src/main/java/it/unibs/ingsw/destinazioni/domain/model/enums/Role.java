package it.unibs.ingsw.destinazioni.domain.model.enums;

public enum Role {
    CONFIGURATOR("Configurator"),
    VOLUNTEER("Volunteer"),
    FINAL_USER("FinalUser");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.name.equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + role);
    }
}