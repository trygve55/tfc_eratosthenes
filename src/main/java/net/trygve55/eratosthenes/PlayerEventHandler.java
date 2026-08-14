package net.trygve55.eratosthenes;

import static java.lang.Math.abs;
import static net.minecraftforge.event.TickEvent.Phase.END;

import com.alekiponi.alekiships.common.entity.vehicle.AbstractVehicle;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.trygve55.eratosthenes.config.ClientConfig;
import net.trygve55.eratosthenes.config.ServerConfig;
import org.jetbrains.annotations.NotNull;

public class PlayerEventHandler {
  @SubscribeEvent
  public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.phase != END) {
      return;
    }

    Player player = event.player;

    if (!isOverworld(player)) {
      return;
    }

    if (!isServerSide(player)) {
      showCoordinates(player);
      return;
    }

    if (!ServerConfig.CROSSING_180_MERIDIAN_TELEPORT.get()) {
      return;
    }

    Vec3 currentPos = player.position();

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

    if (player.tickCount % 23 != 0) {
      return;
    }

    if (haveTickBeenProcessedForPlayer(player)) {
      return;
    }

    final float latitude = MapProjectionHolder.get().getLatitude(currentPos);
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

  private static void showCoordinates(Player player) {
    if (isServerSide(player)) {
      return;
    }

    if (ClientConfig.SHOW_COORDINATES.get()) {
      Vec3 currentPos = player.position();

      sendMessage(
          player,
          "Lat: %.3f Long: %.3f"
              .formatted(
                  MapProjectionHolder.get().getLatitude(currentPos),
                  MapProjectionHolder.get().getLongitude(currentPos)),
          true);
    }
  }

  private static boolean isWayOutsideTheWorld(Vec3 currentPos, int currentHalfCircumference) {
    return abs(currentPos.x)
        > currentHalfCircumference + ServerConfig.MERIDIAN_CROSSING_GRACE_DISTANCE.get() * 2;
  }

  private static void handleWayOutsideTheWorld(
      Player player, Vec3 currentPos, int currentHalfCircumference) {
    player.teleportTo(
        isWest(currentPos) ? -currentHalfCircumference : currentHalfCircumference,
        currentPos.y,
        currentPos.z);

    String message = "You're way outside the world, teleporting you back in.";
    TFCEratosthenes.LOGGER.info(
        "{} : {} Was at {}", player.getName().getString(), message, currentPos);
    sendMessage(player, message, false);
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

    if (currentPos.z
        <= -MapProjectionHolder.get().getHalfMeridian()
            + MapProjectionHolder.get().getEquatorOffset()
            - ServerConfig.POLAR_AREA_SIZE.get()) {
      player.teleportTo(
          currentPos.x,
          currentPos.y,
          -MapProjectionHolder.get().getHalfMeridian()
              + MapProjectionHolder.get().getEquatorOffset()
              - ServerConfig.POLAR_AREA_SIZE.get());
    }

    if (currentPos.z
        <= -MapProjectionHolder.get().getHalfMeridian()
            + MapProjectionHolder.get().getEquatorOffset()
            - (ServerConfig.POLAR_AREA_SIZE.get() - 1)) {
      player.addDeltaMovement(new Vec3(0, 0, 0.03));
      player.hurtMarked = true;
    }
  }

  private static void southPolarPushBack(Player player) {
    Vec3 currentPos = player.position();

    if (currentPos.z
        >= MapProjectionHolder.get().getHalfMeridian()
            + MapProjectionHolder.get().getEquatorOffset()
            + ServerConfig.POLAR_AREA_SIZE.get()) {
      player.teleportTo(
          currentPos.x,
          currentPos.y,
          MapProjectionHolder.get().getHalfMeridian()
              + MapProjectionHolder.get().getEquatorOffset()
              + ServerConfig.POLAR_AREA_SIZE.get());
    }

    if (currentPos.z
        >= MapProjectionHolder.get().getHalfMeridian()
            + MapProjectionHolder.get().getEquatorOffset()
            + (ServerConfig.POLAR_AREA_SIZE.get() - 1)) {
      player.addDeltaMovement(new Vec3(0, 0, -0.03));
      player.hurtMarked = true;
    }
  }

  private static void eastWestPolarPushback(Player player) {
    if (!MapProjectionHolder.get().havePolarArea()) {
      return;
    }

    Vec3 currentPos = player.position();

    if (abs(currentPos.x) > ServerConfig.POLAR_AREA_SIZE.get()) {
      player.teleportTo(
          isWest(currentPos)
              ? -ServerConfig.POLAR_AREA_SIZE.get()
              : ServerConfig.POLAR_AREA_SIZE.get(),
          currentPos.y,
          currentPos.z);
    }

    if (abs(currentPos.x) > ServerConfig.POLAR_AREA_SIZE.get() - 1) {
      player.addDeltaMovement(new Vec3(isWest(currentPos) ? 0.03 : -0.03, 0, 0));
    }
  }

  private static boolean havePassedMeridian(Vec3 currentPos, int halfCircumference) {
    return abs(currentPos.x)
        > halfCircumference + ServerConfig.MERIDIAN_CROSSING_GRACE_DISTANCE.get();
  }

  private static void handlePassingTheMeridian(
      Player player, int currentHalfCircumference, Vec3 currentPos, float latitude) {
    if (player.getRootVehicle() == player) {
      passThe180Meridian(player, currentHalfCircumference);
    } else if (isInAlekiShip(player)) {
      passThe180Meridian(player.getRootVehicle(), currentHalfCircumference);
    } else {
      passThe180MeridianForVehicle(player, currentHalfCircumference);
    }

    String message =
        String.format(
            "Crossing the %s in the %s direction at a latitude of %.0f°%s.",
            getCrossingMeridianFormated(),
            isWest(currentPos) ? "westwards" : "eastwards",
            abs(latitude),
            getHemisphere(latitude));
    TFCEratosthenes.LOGGER.info("{} : {}", player.getName().getString(), message);
    sendMessage(player, message, false);
  }

  private static void passThe180MeridianForVehicle(Player player, int currentHalfCircumference) {
    Entity rootVehicle = player.getRootVehicle();
    List<Entity> passengers = rootVehicle.getPassengers();
    passengers.forEach(Entity::unRide);
    passThe180Meridian(rootVehicle, currentHalfCircumference);
    passengers.forEach(passenger -> passThe180Meridian(passenger, currentHalfCircumference));
    passengers.forEach(passenger -> passenger.startRiding(rootVehicle, true));
  }

  private static @NotNull String getCrossingMeridianFormated() {
    int crossingMeridianLongitude =
        Math.round(
            MapProjectionHolder.get()
                .getLongitude(
                    new Vec3(
                        MapProjectionHolder.get().getHalfCircumferenceAtLatitude(0),
                        0,
                        MapProjectionHolder.get().getEquatorOffset())));

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

    entity.teleportTo(
        -entityPos.x + (isWest(entityPos) ? meridianOvershoot * -2 : meridianOvershoot * 2),
        entityPos.y,
        entityPos.z);
  }

  private static void sendMessage(Player player, String message, boolean actionBar) {
    player.displayClientMessage(Component.literal(message), actionBar);
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

  private static boolean isOverworld(Player player) {
    return player.level().dimension() == Level.OVERWORLD;
  }

  private static boolean haveTickBeenProcessedForPlayer(Player player) {
    if (player.getPersistentData().getInt("tfcEratosthenesLastProcessedTick") == player.tickCount) {
      return true;
    }
    player.getPersistentData().putInt("tfcEratosthenesLastProcessedTick", player.tickCount);
    return false;
  }

  private static boolean isInAlekiShip(Player player) {
    return player.getRootVehicle() instanceof AbstractVehicle;
  }
}
