package net.trygve55.eratosthenes;

import com.mojang.logging.LogUtils;
import net.dries007.tfc.util.climate.Climate;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.trygve55.eratosthenes.compat.TFCRealWorld;
import net.trygve55.eratosthenes.config.ClientConfig;
import net.trygve55.eratosthenes.config.CommonConfig;
import net.trygve55.eratosthenes.config.ConfigManager;
import net.trygve55.eratosthenes.config.ServerConfig;
import org.slf4j.Logger;

@Mod(TFCEratosthenes.MODID)
public class TFCEratosthenes {
  public static final String MODID = "tfc_eratosthenes";
  public static final Logger LOGGER = LogUtils.getLogger();

  private final ModContainer modContainer;

  public TFCEratosthenes(IEventBus modEventBus, ModContainer modContainer) {
    modEventBus.addListener(this::commonSetup);
    this.modContainer = modContainer;

    // Register ourselves for server and other game events we are interested in.
    // Note that this is necessary if and only if we want *this* class (TFCEratosthenes) to respond
    // directly to events.
    // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like
    // onServerStarting() below.
    NeoForge.EVENT_BUS.register(this);
    NeoForge.EVENT_BUS.register(ConfigManager.class);
    NeoForge.EVENT_BUS.register(PlayerEventHandler.class);

    // Register our mod's ModConfigSpec so that FML can create and load the config file for us
    modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

    if (FMLEnvironment.dist == Dist.CLIENT) {
      modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
  }

  private void commonSetup(FMLCommonSetupEvent event) {
    LOGGER.info("Hmm, looks like the world is spherical.");
  }

  @SubscribeEvent
  public void onWorldLoad(LevelEvent.Load event) {
    if (event.getLevel() instanceof final Level level) {
      if (level.dimension() != Level.OVERWORLD) {
        return;
      }

      if (!level.isClientSide() || isConnectedToServer()) {
        int halfMeridian = (int) Climate.get(level).hemisphereScale();
        if (TFCRealWorld.isLoaded()) {
          halfMeridian = TFCRealWorld.getHalfMeridian();
        }
        MapProjectionHolder.set(ServerConfig.MAP_PROJECTION.get().toMapProjection(halfMeridian));
      }

      LOGGER.info(
          "Half meridian set to {}, equatorial circumference is {}, equator offset is {}",
          MapProjectionHolder.get().getHalfMeridian(),
          MapProjectionHolder.get().getHalfCircumferenceAtLatitude(0) * 2,
          MapProjectionHolder.get().getEquatorOffset());
    }
  }

  private boolean isConnectedToServer() {
    return Minecraft.getInstance().getConnection() != null;
  }
}
