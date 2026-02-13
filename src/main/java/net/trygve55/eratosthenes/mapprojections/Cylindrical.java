package net.trygve55.eratosthenes.mapprojections;

public abstract class Cylindrical implements MapProjection {

    @Override
    public int getHalfCircumferenceAtLatitude(float latitude) {
        return getHalfMeridian();
    }

    @Override
    public boolean havePolarArea() {
        return false;
    }
}
