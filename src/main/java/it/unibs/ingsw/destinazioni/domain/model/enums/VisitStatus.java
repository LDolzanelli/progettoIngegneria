package it.unibs.ingsw.destinazioni.domain.model.enums;

public enum VisitStatus {
    PROPOSED("Proposta"),
    COMPLETED("Completata"),
    CANCELLED("Annullata"),
    FULL("Piena"),
    CONFIRMED("Confermata");

    private final String italianName;

    VisitStatus(String italianName) {
        this.italianName = italianName;
    }

    public String getItalianName() {
        return italianName;
    }

    public static VisitStatus fromEnglishString(String status) {
        try {
            return VisitStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    public static VisitStatus fromItalianString(String status) {
        for (VisitStatus visitStatus : VisitStatus.values()) {
            if (visitStatus.getItalianName().equalsIgnoreCase(status)) {
                return visitStatus;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + status);
    }

}
