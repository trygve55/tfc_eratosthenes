package net.trygve55.eratosthenes.mapprojections;

public class TfcRealWorldFullWorld extends EqualEarth implements TfcRealWorld {
    private final int halfMeridian;

    public TfcRealWorldFullWorld(int halfMeridian) {
        this.halfMeridian = halfMeridian;
    }

    @Override
    public float getWestEdgeLongitude() {
        return -170;
    }

    @Override
    public float getEastEdgeLongitude() {
        return 190;
    }

    @Override
    public float getWidthToHeightRatio() {
        return TfcRealWorld.super.getWidthToHeightRatio();
    }

    @Override
    public int getHalfMeridian() {
        return halfMeridian;
    }
}
