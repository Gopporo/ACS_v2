package org.example.acs_v2.models.enums;

public enum AccessLevel {
    LEVEL_1(1),
    LEVEL_2(2),
    LEVEL_3(3),
    LEVEL_4(4),
    LEVEL_5(5);

    private final int rank;

    AccessLevel(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }
}
