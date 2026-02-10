package net.trygve55.eratosthenes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

@Mod(TFCEratosthenes.MODID)
public class TFCEratosthenes {
    public static final String MODID = "tfc_eratosthenes";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final int graceDistanceEastWest = 40;
    private static final int graceDistancePole = 100;
    private static final int HALF_MERIDIAN_NOT_SET = Integer.MIN_VALUE;

    int halfMeridian = HALF_MERIDIAN_NOT_SET;
    int equatorOffset = 0; // 0.5f * halfMeridian;

    public TFCEratosthenes() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (TFCEratosthenes) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        EVENT_BUS.register(this);


        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Hmm, looks like the world is spherical.");
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.LevelTickEvent event) {
        if (!isHalfMeridianSet()) {
            WorldScaleHolder.getTemperatureScale().ifPresent(temperatureScale -> {
                halfMeridian = temperatureScale;
                equatorOffset = halfMeridian / 2;

                LOGGER.info("Half meridian set to {}, equatorial circumference is {}.", halfMeridian, halfMeridian * 4);
            });
        }
    }

    @SubscribeEvent
    public void onLivingMove(TickEvent.PlayerTickEvent event) {
        handleWorldBorder(event.player);
    }

    private void handleWorldBorder(Player player) {
        if (!isOverworld(player) || !isHalfMeridianSet() || !Config.CROSSING_180_MERIDIAN_TELEPORT.get()) {
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

        if (player.tickCount % 20 == 0) {
            return;
        }

        final float equatorDistance = EratosthenesHelper.getDistanceFromEquator(currentPos, equatorOffset);
        final float latitude = EratosthenesHelper.getLatitude(equatorDistance, halfMeridian);
        final int currentHalfCircumference = EratosthenesHelper.getHalfCircumferenceAtLatitude(latitude, halfMeridian);

        if (isWayOutsideTheWorld(currentPos, currentHalfCircumference)) {
            handleWayOutsideTheWorld(player, currentPos, currentHalfCircumference);
            return;
        }

        if (havePassedMeridian(currentPos, currentHalfCircumference)) {
            handlePassingTheMeridian(player, currentHalfCircumference, currentPos, latitude);
        }
    }

    private boolean isWayOutsideTheWorld(Vec3 currentPos, int currentHalfCircumference) {
        return Math.abs(currentPos.x) > currentHalfCircumference + graceDistanceEastWest * 2;
    }

    private void handleWayOutsideTheWorld(Player player, Vec3 currentPos, int currentHalfCircumference) {
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
        } else if (isNorth(currentPos, equatorOffset)) {
            return " north";
        } else {
            return " south";
        }
    }

    private void northPolarPushback(Player player) {
        Vec3 currentPos = player.position();

        if (isServerSide(player) && currentPos.z <= -halfMeridian + equatorOffset - graceDistancePole) {
            player.moveTo(currentPos.x, currentPos.y, -halfMeridian + equatorOffset - graceDistancePole);
        }

        if (!isServerSide(player) && currentPos.z <= -halfMeridian + equatorOffset - (graceDistancePole - 1)) {
            player.addDeltaMovement(new Vec3(0, 0, 0.06));
        }
    }

    private void southPolarPushBack(Player player) {
        Vec3 currentPos = player.position();

        if (isServerSide(player) && currentPos.z >= halfMeridian + equatorOffset + graceDistancePole) {
            player.moveTo(currentPos.x, currentPos.y, halfMeridian + equatorOffset + graceDistancePole);
        }

        if (!isServerSide(player) && currentPos.z >= halfMeridian + equatorOffset + (graceDistancePole - 1)) {
            player.addDeltaMovement(new Vec3(0, 0, -0.06));
        }
    }

    private void eastWestPolarPushback(Player player) {
        Vec3 currentPos = player.position();

        if (isServerSide(player) && Math.abs(currentPos.x) > graceDistancePole) {
            player.moveTo(isWest(currentPos) ? -graceDistancePole : graceDistancePole, currentPos.y, currentPos.z);
        }

        if (!isServerSide(player) && Math.abs(currentPos.x) > graceDistancePole - 1) {
            player.addDeltaMovement(new Vec3(isWest(currentPos) ? 0.06 : -0.06, 0, 0));
        }
    }

    private boolean havePassedMeridian(Vec3 currentPos, int halfCircumference) {
        return Math.abs(currentPos.x) > halfCircumference + graceDistanceEastWest;
    }

    private void handlePassingTheMeridian(Player player, int currentHalfCircumference, Vec3 currentPos, float latitude) {
        if (player.getRootVehicle() == player) {
            passThe180Meridian(player, currentHalfCircumference);
        } else if (isServerSide(player)) {
            passThe180Meridian(player.getRootVehicle(), currentHalfCircumference);
        }

        String message = String.format(
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
        player.displayClientMessage(Component.literal(message), actionBar);
    }

    private static boolean isServerSide(Player player) {
        return !player.level().isClientSide();
    }

    private boolean isNorthPole(Vec3 pos) {
        return pos.z <= -halfMeridian + equatorOffset + 20 ;
    }

    private boolean isSouthPole(Vec3 pos) {
        return pos.z >= halfMeridian + equatorOffset - 20;
    }

    private boolean isWest(Vec3 pos) {
        return pos.x < 0;
    }

    private boolean isNorth(Vec3 pos, int equatorOffset) {
        return pos.z < equatorOffset;
    }

    private boolean isOverworld(Level level) {
        return level.dimension() == Level.OVERWORLD;
    }

    private boolean isOverworld(Player player) {
        return isOverworld(player.level());
    }

    private boolean isHalfMeridianSet() {
        return halfMeridian != HALF_MERIDIAN_NOT_SET;
    }
}
