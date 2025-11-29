package com.xxmicloxx.NoteBlockAPI.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Ensures the default custom-instrument resource packs are available without bundling
 * large binaries in the repository. If the packs are missing, they are downloaded from
 * the upstream NoteBlockAPI release assets on plugin startup.
 */
public class InstrumentPackManager {

    private static final URI JAVA_ZIP_URI = URI.create("https://github.com/koca2000/NoteBlockAPI/releases/latest/download/Instruments.zip");
    private static final URI BEDROCK_MCPACK_URI = URI.create("https://github.com/koca2000/NoteBlockAPI/releases/latest/download/InstrumentsBE.mcpack");
    private static final String JAVA_ZIP_NAME = "Instruments.zip";
    private static final String BEDROCK_MCPACK_NAME = "InstrumentsBE.mcpack";

    private final Plugin plugin;
    private final HttpClient httpClient;

    public InstrumentPackManager(Plugin plugin) {
        this.plugin = plugin;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Download the missing instrument packs asynchronously to avoid blocking the
     * server main thread. Files are placed in the plugin data directory so they
     * behave the same as the previously bundled archives.
     */
    public void ensurePacksAvailableAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Path dataDir = plugin.getDataFolder().toPath();
            try {
                Files.createDirectories(dataDir);
                downloadIfMissing(dataDir.resolve(JAVA_ZIP_NAME), JAVA_ZIP_URI);
                downloadIfMissing(dataDir.resolve(BEDROCK_MCPACK_NAME), BEDROCK_MCPACK_URI);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to prepare instrument pack directory: " + e.getMessage());
            }
        });
    }

    private void downloadIfMissing(Path destination, URI uri) {
        if (Files.exists(destination)) {
            return;
        }

        plugin.getLogger().info("Downloading default instrument pack: " + destination.getFileName());
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        try {
            HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(destination));
            if (response.statusCode() != 200) {
                plugin.getLogger().warning("Could not download instrument pack (status " + response.statusCode() + "): " + uri);
                Files.deleteIfExists(destination);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            plugin.getLogger().warning("Instrument pack download interrupted: " + uri);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to download instrument pack from " + uri + ": " + e.getMessage());
            try {
                Files.deleteIfExists(destination);
            } catch (IOException ignored) {
                // Ignored: best-effort cleanup
            }
        }
    }
}
