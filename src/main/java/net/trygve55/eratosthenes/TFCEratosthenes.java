package net.trygve55.eratosthenes;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.trygve55.eratosthenes.config.ClientConfig;
import net.trygve55.eratosthenes.config.CommonConfig;
import net.trygve55.eratosthenes.config.ConfigManager;
import net.trygve55.eratosthenes.config.ServerConfig;
import org.slf4j.Logger;

@Mod(TFCEratosthenes.MODID)
public class TFCEratosthenes {
  public static final String MODID = "tfc_eratosthenes";
  public static final Logger LOGGER = LogUtils.getLogger();

  public TFCEratosthenes() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    modEventBus.addListener(this::commonSetup);

    // Register ourselves for server and other game events we are interested in.
    // Note that this is necessary if and only if we want *this* class (TFCEratosthenes) to respond
    // directly to events.
    // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like
    // onServerStarting() below.
    EVENT_BUS.register(ConfigManager.class);
    EVENT_BUS.register(PlayerEventHandler.class);

    // Register our mod's ModConfigSpec so that FML can create and load the config file for us
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
  }

  private void commonSetup(FMLCommonSetupEvent event) {
    LOGGER.info("Hmm, looks like the world is spherical.");
  }
}
