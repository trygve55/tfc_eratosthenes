package net.trygve55.eratosthenes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.trygve55.eratosthenes.config.ClientConfig;
import net.trygve55.eratosthenes.config.ServerConfig;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.abs;

public class PlayerEventHandler {
  @SubscribeEvent
  public static void onLivingMove(EntityTickEvent.Post event) {
    if (event.getEntity() instanceof Player player) {
      if (!isOverworld(player)) {
        return;
      }
      if (ServerConfig.CROSSING_180_MERIDIAN_TELEPORT.isFalse()) {
        return;
      }

      Vec3 currentPos = player.position();

      showCoordinates(player);

      if (isNorthPole(currentPos)) {
        sendMessage(player, "You have reached The North Pole.", true);
        northPolarPushback(player);
        eastWestPolarPushback(player);
        return;
      }

      if (isSouthPole(currentPos)) {
        sendMessage(player, "You have reached The South Pole.", true);
        southPolarPushBack(player);
        eastWestPolarPushback(player);
        return;
      }

      if (event.getEntity().tickCount % 20 == 0) {
        return;
      }

      final float equatorDistance = MapProjectionHolder.get().getDistanceFromEquator(currentPos);
      final float latitude = MapProjectionHolder.get().getLatitude(equatorDistance);
      final int currentHalfCircumference =
          MapProjectionHolder.get().getHalfCircumferenceAtLatitude(latitude);

      if (isWayOutsideTheWorld(currentPos, currentHalfCircumference)) {
        handleWayOutsideTheWorld(player, currentPos, currentHalfCircumference);
        return;
      }

      if (havePassedMeridian(currentPos, currentHalfCircumference)) {
        handlePassingTheMeridian(player, currentHalfCircumference, currentPos, latitude);
      }
    }
  }

  private static void showCoordinates(Player player) {
    if (ClientConfig.SHOW_COORDINATES.isTrue()) {
      Vec3 currentPos = player.position();

      float equatorDistance2 = MapProjectionHolder.get().getDistanceFromEquator(currentPos);
      sendMessage(
          player,
          "Lat: %.3f Long: %.3f"
              .formatted(
                  MapProjectionHolder.get().getLatitude(equatorDistance2),
                  MapProjectionHolder.get().getLongitude(currentPos)),
          true);
    }
  }

  private static boolean isWayOutsideTheWorld(Vec3 currentPos, int currentHalfCircumference) {
    return abs(currentPos.x)
        > currentHalfCircumference + ServerConfig.MERIDIAN_CROSSING_GRACE_DISTANCE.getAsInt() * 2;
  }

  private static void handleWayOutsideTheWorld(
      Player player, Vec3 currentPos, int currentHalfCircumference) {
    player.moveTo(
        isWest(currentPos) ? -currentHalfCircumference : currentHalfCircumference,
        currentPos.y,
        currentPos.z);

    String message1 = "You're way outside the world, teleporting you back in.";
    TFCEratosthenes.LOGGER.info(
        "{} : {} Was at {}", player.getName().getString(), message1, currentPos);
    sendMessage(player, message1, false);
  }

  private static String getHemisphere(float latitude) {
    if (abs(latitude) < 1) {
      return "";
    } else if (latitude > 0) {
      return " north";
    } else {
      return " south";
    }
  }

  private static void northPolarPushback(Player player) {
    Vec3 currentPos = player.position();

    if (isServerSide(player)
        && currentPos.z
            <= -MapProjectionHolder.get().getHalfMeridian()
                + MapProjectionHolder.get().getEquatorOffset()
                - ServerConfig.POLAR_AREA_SIZE.getAsInt()) {
      player.moveTo(
          currentPos.x,
          currentPos.y,
          -MapProjectionHolder.get().getHalfMeridian()
              + MapProjectionHolder.get().getEquatorOffset()
              - ServerConfig.POLAR_AREA_SIZE.getAsInt());
    }

    if (!isServerSide(player)
        && currentPos.z
            <= -MapProjectionHolder.get().getHalfMeridian()
                + MapProjectionHolder.get().getEquatorOffset()
                - (ServerConfig.POLAR_AREA_SIZE.getAsInt() - 1)) {
      player.addDeltaMovement(new Vec3(0, 0, 0.06));
    }
  }

  private static void southPolarPushBack(Player player) {
    Vec3 currentPos = player.position();

    if (isServerSide(player)
        && currentPos.z
            >= MapProjectionHolder.get().getHalfMeridian()
                + MapProjectionHolder.get().getEquatorOffset()
                + ServerConfig.POLAR_AREA_SIZE.getAsInt()) {
      player.moveTo(
          currentPos.x,
          currentPos.y,
          MapProjectionHolder.get().getHalfMeridian()
              + MapProjectionHolder.get().getEquatorOffset()
              + ServerConfig.POLAR_AREA_SIZE.getAsInt());
    }

    if (!isServerSide(player)
        && currentPos.z
            >= MapProjectionHolder.get().getHalfMeridian()
                + MapProjectionHolder.get().getEquatorOffset()
                + (ServerConfig.POLAR_AREA_SIZE.getAsInt() - 1)) {
      player.addDeltaMovement(new Vec3(0, 0, -0.06));
    }
  }

  private static void eastWestPolarPushback(Player player) {
    if (!MapProjectionHolder.get().havePolarArea()) {
      return;
    }

    Vec3 currentPos = player.position();

    if (isServerSide(player) && abs(currentPos.x) > ServerConfig.POLAR_AREA_SIZE.getAsInt()) {
      player.moveTo(
          isWest(currentPos)
              ? -ServerConfig.POLAR_AREA_SIZE.getAsInt()
              : ServerConfig.POLAR_AREA_SIZE.getAsInt(),
          currentPos.y,
          currentPos.z);
    }

    if (!isServerSide(player) && abs(currentPos.x) > ServerConfig.POLAR_AREA_SIZE.getAsInt() - 1) {
      player.addDeltaMovement(new Vec3(isWest(currentPos) ? 0.06 : -0.06, 0, 0));
    }
  }

  private static boolean havePassedMeridian(Vec3 currentPos, int halfCircumference) {
    return abs(currentPos.x)
        > halfCircumference + ServerConfig.MERIDIAN_CROSSING_GRACE_DISTANCE.getAsInt();
  }

  private static void handlePassingTheMeridian(
      Player player, int currentHalfCircumference, Vec3 currentPos, float latitude) {
    if (player.getRootVehicle() == player) {
      passThe180Meridian(player, currentHalfCircumference);
    } else if (isServerSide(player)) {
      passThe180Meridian(player.getRootVehicle(), currentHalfCircumference);
    }

    String message =
        String.format(
            "Crossing the %s %s at a latitude of %.0f°%s.",
            getCrossingMeridianFormated(currentPos),
            isWest(currentPos) ? "westwards" : "eastwards",
            abs(latitude),
            getHemisphere(latitude));
    TFCEratosthenes.LOGGER.info("{} : {}", player.getName().getString(), message);
    sendMessage(player, message, false);
  }

  private static @NotNull String getCrossingMeridianFormated(Vec3 currentPos) {
    int crossingMeridianLongitude =
        Math.round(
            MapProjectionHolder.get()
                .getLongitude(
                    new Vec3(MapProjectionHolder.get().getHalfCircumferenceAtLatitude(0), 0, MapProjectionHolder.get().getEquatorOffset())));

    if (crossingMeridianLongitude == 180) {
      return "180° meridian";
    }

    if (crossingMeridianLongitude > 180) {
      crossingMeridianLongitude -= 360;
    }

    if (crossingMeridianLongitude < -180) {
      crossingMeridianLongitude += 360;
    }

    if (crossingMeridianLongitude < 0) {
      return -crossingMeridianLongitude + "° meridian west";
    } else {
      return crossingMeridianLongitude + "° meridian east";
    }
  }

  private static void passThe180Meridian(Entity entity, int currentHalfCircumference) {
    Vec3 entityPos = entity.position();
    double meridianOvershoot = abs(entityPos.x) - currentHalfCircumference;
    entity.moveTo(
        -entityPos.x + (isWest(entityPos) ? meridianOvershoot * -2 : meridianOvershoot * 2),
        entityPos.y,
        entityPos.z);
  }

  private static void sendMessage(Player player, String message, boolean actionBar) {
    if (isServerSide(player)) {
      player.displayClientMessage(Component.literal(message), actionBar);
    }
  }

  private static boolean isServerSide(Player player) {
    return !player.level().isClientSide();
  }

  private static boolean isNorthPole(Vec3 pos) {
    return pos.z
        <= -MapProjectionHolder.get().getHalfMeridian()
            + MapProjectionHolder.get().getEquatorOffset()
            + 20;
  }

  private static boolean isSouthPole(Vec3 pos) {
    return pos.z
        >= MapProjectionHolder.get().getHalfMeridian()
            + MapProjectionHolder.get().getEquatorOffset()
            - 20;
  }

  private static boolean isWest(Vec3 pos) {
    return pos.x < 0;
  }

  private static boolean isNorth(Vec3 pos) {
    return pos.z < MapProjectionHolder.get().getEquatorOffset();
  }

  private static boolean isOverworld(Level level) {
    return level.dimension() == Level.OVERWORLD;
  }

  private static boolean isOverworld(Player player) {
    return isOverworld(player.level());
  }
}
