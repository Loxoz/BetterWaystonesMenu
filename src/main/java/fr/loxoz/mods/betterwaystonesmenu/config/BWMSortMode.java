package fr.loxoz.mods.betterwaystonesmenu.config;

public enum BWMSortMode {
    INDEX("index"),
    NAME("name"),
    DISTANCE("distance");

    private final String id;

    BWMSortMode(String id) {
        this.id = id;
    }

    public String getId() { return id; }
}
