package net.trygve55.eratosthenes.config;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.trygve55.eratosthenes.TFCEratosthenes;

public class ConfigManager {

  private static final String SERVER_CONFIG_NAME = TFCEratosthenes.MODID + "-server.toml";

  private static Path getServerConfigPath(MinecraftServer server) {
    Path worldPath = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT);
    return worldPath.resolve("serverconfig").resolve(SERVER_CONFIG_NAME);
  }

  @SubscribeEvent
  public static void onServerStopped(ServerStoppedEvent event) {
    MinecraftServer server = event.getServer();

    try {
      Path serverConfigFile = getServerConfigPath(server);
      Path worldConfigDir = serverConfigFile.getParent();
      Path commonConfigFile =
          server.getServerDirectory().toPath().resolve("config").resolve(SERVER_CONFIG_NAME);

      if (!Files.exists(serverConfigFile) && Files.exists(commonConfigFile)) {
        Files.createDirectories(worldConfigDir);
        Files.move(commonConfigFile, serverConfigFile, REPLACE_EXISTING);
        TFCEratosthenes.LOGGER.info("Server config moved to world directory.");
      } else if (Files.exists(commonConfigFile)) {
        Files.deleteIfExists(commonConfigFile);
      }
    } catch (IOException e) {
      TFCEratosthenes.LOGGER.error("Error working with server config", e);
    }
  }
}
