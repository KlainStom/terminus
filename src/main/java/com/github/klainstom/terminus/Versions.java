package com.github.klainstom.terminus;

import java.util.List;

public class Versions {
    public static final String VERSION = "&version";
    public static final String MINESTOM_VERSION = "&minestomVersion";

    public static List<String> getVersionLines() {
        return List.of(
                "&Name: "+ Versions.VERSION,
                "Minestom: "+Versions.MINESTOM_VERSION
        );
    }
}
