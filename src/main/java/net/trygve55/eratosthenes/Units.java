/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.trygve55.eratosthenes;

public final class Units
{
    public static final int GRID_BITS = 7;

    public static int gridToBlock(int grid) { return grid << GRID_BITS; }
}
