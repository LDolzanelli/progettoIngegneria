package it.unibs.ingsw.destinazioni.domain.model;

public enum DaysOfWeek {
    MONDAY("Lunedì"), TUESDAY("Martedì"), WEDNESDAY("Mercoledì"), THURSDAY("Giovedì"), FRIDAY("Venerdì"), SATURDAY(
            "Sabato"), SUNDAY("Domenica");

    private final String italianName;

    DaysOfWeek(String italianName) {
        this.italianName = italianName;
    }


    public String getItalianName() {
        return italianName;
    }


    public static DaysOfWeek fromEnglishString(String day) {
        try {
            return DaysOfWeek.valueOf(day.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid day: " + day);
        }
    }


    public static DaysOfWeek fromItalianString(String day) {
        for (DaysOfWeek daysOfWeek : DaysOfWeek.values()) {
            if (daysOfWeek.getItalianName().equalsIgnoreCase(day)) {
                return daysOfWeek;
            }
        }
        throw new IllegalArgumentException("Invalid day: " + day);
    }

}
