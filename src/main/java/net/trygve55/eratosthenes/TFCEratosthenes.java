package net.trygve55.eratosthenes;

import net.dries007.tfc.util.climate.Climate;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(TFCEratosthenes.MODID)
public class TFCEratosthenes {
  public static final String MODID = "tfc_eratosthenes";
  public static final Logger LOGGER = LogUtils.getLogger();

  public TFCEratosthenes(IEventBus modEventBus, ModContainer modContainer) {
    modEventBus.addListener(this::commonSetup);

    // Register ourselves for server and other game events we are interested in.
    // Note that this is necessary if and only if we want *this* class (TFCEratosthenes) to respond
    // directly to events.
    // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like
    // onServerStarting() below.
    NeoForge.EVENT_BUS.register(this);

    // Register our mod's ModConfigSpec so that FML can create and load the config file for us
    modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
  }

  private void commonSetup(FMLCommonSetupEvent event) {
    LOGGER.info("Hmm, looks like the world is spherical.");
  }

  @SubscribeEvent
  public void onWorldLoad(LevelEvent.Load event) {
    if (event.getLevel() instanceof final Level level) {
      if (!isOverworld(level)) {
        return;
      }

      int halfMeridian = (int) Climate.get(level).hemisphereScale();
      MapProjectionHolder.set(Config.MAP_PROJECTION.get().toMapProjection(halfMeridian));

      LOGGER.info(
          "Half meridian set to {}, equatorial circumference is {}, equator offset is {}",
          MapProjectionHolder.get().getHalfMeridian(),
          MapProjectionHolder.get().getHalfCircumferenceAtLatitude(0) * 2,
          MapProjectionHolder.get().getEquatorOffset());
    }
  }

  @SubscribeEvent
  public void onLivingMove(EntityTickEvent.Post event) {
    if (event.getEntity() instanceof Player player) {
      if (!isOverworld(player)) {
        return;
      }
      if (Config.CROSSING_180_MERIDIAN_TELEPORT.isFalse()) {
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

  private boolean isWayOutsideTheWorld(Vec3 currentPos, int currentHalfCircumference) {
    return Math.abs(currentPos.x)
        > currentHalfCircumference + Config.MERIDIAN_CROSSING_GRACE_DISTANCE.getAsInt() * 2;
  }

  private void handleWayOutsideTheWorld(
      Player player, Vec3 currentPos, int currentHalfCircumference) {
    player.moveTo(
        isWest(currentPos) ? -currentHalfCircumference : currentHalfCircumference,
        currentPos.y,
        currentPos.z);

    String message1 = "You're way outside the world, teleporting you back in.";
    LOGGER.info("{} : {} Was at {}", player.getName().getString(), message1, currentPos);
    sendMessage(player, message1, false);
  }

  private String getHemisphere(float latitude, Vec3 currentPos) {
    if (Math.abs(latitude) < 1) {
      return "";
    } else if (isNorth(currentPos)) {
      return " north";
    } else {
      return " south";
    }
  }

  private void northPolarPushback(Player player) {
    Vec3 currentPos = player.position();

    if (isServerSide(player)
        && currentPos.z
            <= -MapProjectionHolder.get().getHalfMeridian()
                + MapProjectionHolder.get().getEquatorOffset()
                - Config.POLAR_AREA_SIZE.getAsInt()) {
      player.moveTo(
          currentPos.x,
          currentPos.y,
          -MapProjectionHolder.get().getHalfMeridian()
              + MapProjectionHolder.get().getEquatorOffset()
              - Config.POLAR_AREA_SIZE.getAsInt());
    }

    if (!isServerSide(player)
        && currentPos.z
            <= -MapProjectionHolder.get().getHalfMeridian()
                + MapProjectionHolder.get().getEquatorOffset()
                - (Config.POLAR_AREA_SIZE.getAsInt() - 1)) {
      player.addDeltaMovement(new Vec3(0, 0, 0.06));
    }
  }

  private void southPolarPushBack(Player player) {
    Vec3 currentPos = player.position();

    if (isServerSide(player)
        && currentPos.z
            >= MapProjectionHolder.get().getHalfMeridian()
                + MapProjectionHolder.get().getEquatorOffset()
                + Config.POLAR_AREA_SIZE.getAsInt()) {
      player.moveTo(
          currentPos.x,
          currentPos.y,
          MapProjectionHolder.get().getHalfMeridian()
              + MapProjectionHolder.get().getEquatorOffset()
              + Config.POLAR_AREA_SIZE.getAsInt());
    }

    if (!isServerSide(player)
        && currentPos.z
            >= MapProjectionHolder.get().getHalfMeridian()
                + MapProjectionHolder.get().getEquatorOffset()
                + (Config.POLAR_AREA_SIZE.getAsInt() - 1)) {
      player.addDeltaMovement(new Vec3(0, 0, -0.06));
    }
  }

  private void eastWestPolarPushback(Player player) {
    if (!MapProjectionHolder.get().havePolarArea()) {
      return;
    }

    Vec3 currentPos = player.position();

    if (isServerSide(player) && Math.abs(currentPos.x) > Config.POLAR_AREA_SIZE.getAsInt()) {
      player.moveTo(
          isWest(currentPos)
              ? -Config.POLAR_AREA_SIZE.getAsInt()
              : Config.POLAR_AREA_SIZE.getAsInt(),
          currentPos.y,
          currentPos.z);
    }

    if (!isServerSide(player) && Math.abs(currentPos.x) > Config.POLAR_AREA_SIZE.getAsInt() - 1) {
      player.addDeltaMovement(new Vec3(isWest(currentPos) ? 0.06 : -0.06, 0, 0));
    }
  }

  private boolean havePassedMeridian(Vec3 currentPos, int halfCircumference) {
    return Math.abs(currentPos.x)
        > halfCircumference + Config.MERIDIAN_CROSSING_GRACE_DISTANCE.getAsInt();
  }

  private void handlePassingTheMeridian(
      Player player, int currentHalfCircumference, Vec3 currentPos, float latitude) {
    if (player.getRootVehicle() == player) {
      passThe180Meridian(player, currentHalfCircumference);
    } else if (isServerSide(player)) {
      passThe180Meridian(player.getRootVehicle(), currentHalfCircumference);
    }

    String message =
        String.format(
            "Crossing the 180° meridian %s at a latitude of %.0f°%s.",
            isWest(currentPos) ? "westwards" : "eastwards",
            latitude,
            getHemisphere(latitude, currentPos));
    LOGGER.info("{} : {}", player.getName().getString(), message);
    sendMessage(player, message, false);
  }

  private void passThe180Meridian(Entity entity, int currentHalfCircumference) {
    Vec3 entityPos = entity.position();
    double meridianOvershoot = Math.abs(entityPos.x) - currentHalfCircumference;
    entity.moveTo(
        -entityPos.x + (isWest(entityPos) ? meridianOvershoot * -2 : meridianOvershoot * 2),
        entityPos.y,
        entityPos.z);
  }

  private void sendMessage(Player player, String message, boolean actionBar) {
    if (isServerSide(player)) {
      player.displayClientMessage(Component.literal(message), actionBar);
    }
  }

  private static boolean isServerSide(Player player) {
    return !player.level().isClientSide();
  }

  private boolean isNorthPole(Vec3 pos) {
    return pos.z
        <= -MapProjectionHolder.get().getHalfMeridian()
            + MapProjectionHolder.get().getEquatorOffset()
            + 20;
  }

  private boolean isSouthPole(Vec3 pos) {
    return pos.z
        >= MapProjectionHolder.get().getHalfMeridian()
            + MapProjectionHolder.get().getEquatorOffset()
            - 20;
  }

  private boolean isWest(Vec3 pos) {
    return pos.x < 0;
  }

  private boolean isNorth(Vec3 pos) {
    return pos.z < MapProjectionHolder.get().getEquatorOffset();
  }

  private boolean isOverworld(Level level) {
    return level.dimension() == Level.OVERWORLD;
  }

  private boolean isOverworld(Player player) {
    return isOverworld(player.level());
  }
}
