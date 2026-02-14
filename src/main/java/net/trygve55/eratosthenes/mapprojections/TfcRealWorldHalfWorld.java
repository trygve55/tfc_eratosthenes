package net.trygve55.eratosthenes.mapprojections;

public abstract class TfcRealWorldHalfWorld extends Cylindrical implements TfcRealWorld {
  @Override
  public float getLatitude(float equatorDistance) {
    return equatorDistance / getHalfMeridian() * 90f; // todo temp
  }
}
