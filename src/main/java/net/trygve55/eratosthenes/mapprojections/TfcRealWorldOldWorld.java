package net.trygve55.eratosthenes.mapprojections;

public class TfcRealWorldOldWorld extends TfcRealWorldHalfWorld implements TfcRealWorld {
    private final int halfMeridian;

    public TfcRealWorldOldWorld(int halfMeridian) {
        this.halfMeridian = halfMeridian;
    }

    @Override
    public int getHalfMeridian() {
        return halfMeridian;
    }

    @Override
    public float getWestEdgeLongitude() {
        return -20;
    }

    @Override
    public float getEastEdgeLongitude() {
        return 160;
    }
}
