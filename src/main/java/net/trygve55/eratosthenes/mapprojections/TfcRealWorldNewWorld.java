package net.trygve55.eratosthenes.mapprojections;

public class TfcRealWorldNewWorld extends TfcRealWorldHalfWorld {
    private final int halfMeridian;

    public TfcRealWorldNewWorld(int halfMeridian) {
        this.halfMeridian = halfMeridian;
    }

    @Override
    public int getHalfMeridian() {
        return halfMeridian;
    }

    @Override
    public float getWestEdgeLongitude() {
        return -200;
    }

    @Override
    public float getEastEdgeLongitude() {
        return -20;
    }
}
