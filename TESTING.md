# Tests

* **Crossing Meridian teleport**
    * Given:
        * Is near world border
    * When:
        * Crossing world border
            * By foot
            * On horse
            * In a boat
            * In a Small Ships vehicle
            * In a Niftyships/Firmaciv vehicle
    * Then:
        * Is teleported to inverted x coordinate with `meridianCrossingGraceDistance` taken into account.
        * Z coordinate is the same as before.
        * Y coordinate is the same as before.

* **Way outside world border teleport**

### Config handling

* **Common configs apply to new worlds**

* **Server side config is saved per world**

### Debug info

* **Coordinate display config**

* **Coordinate display coordinates are correct**

### Polar zone handling

* **Polar zone detection**

* **Polar zone pushback in singleplayer**

* **Polar zone pushback in multiplayer**

* **Polar zone teleport back inside while mod on server only**

#### World size loading

* **World size is loaded correctly in singleplayer**

* **World size is loaded correctly in multiplayer**

### TFC Real World

* **Equal earth projection is used**

* **Equator is shifted correctly**

* **World generation is limited correctly**

* **No special polar zone handling**

#### World size loading

* **World size is loaded correctly from TFC real world in singleplayer**

* **World size is loaded correctly from TFC real world in multiplayer**