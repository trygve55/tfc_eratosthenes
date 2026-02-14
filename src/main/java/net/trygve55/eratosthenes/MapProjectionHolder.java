package net.trygve55.eratosthenes;

import net.trygve55.eratosthenes.mapprojections.MapProjection;

public class MapProjectionHolder {
    private static MapProjection currentMapProjection;

    private MapProjectionHolder() {}

    public static void set(MapProjection mapProjection) {
        MapProjectionHolder.currentMapProjection = mapProjection;
    }

    public static MapProjection get() {
        if (currentMapProjection == null) {
            throw new IllegalStateException("No MapProjection set for TFC Eratosthenes.");
        }

        return currentMapProjection;
    }
}
