package net.trygve55.eratosthenes;

import net.trygve55.eratosthenes.mapprojections.MapProjection;

public class MapProjectionHolder {
    private static MapProjection currentMapProjection;

    private MapProjectionHolder() {}

    public static void set(MapProjection currentMapProjection) {
        MapProjectionHolder.currentMapProjection = currentMapProjection;
    }

    public static MapProjection get() {
        if (currentMapProjection == null) {
            throw new IllegalStateException("No MapProjection set for TFC Eratosthenes.");
        }

        return currentMapProjection;
    }
}
