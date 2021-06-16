package com.udacity.webcrawler.json;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A static utility class that loads a JSON configuration file.
 */
public final class ConfigurationLoader {

  private final Path path;

  /**
   * Create a {@link ConfigurationLoader} that loads configuration from the given {@link Path}.
   */
  public ConfigurationLoader(Path path) {
    this.path = Objects.requireNonNull(path);
  }

  /**
   * Loads configuration from this {@link ConfigurationLoader}'s path
   *
   * @return the loaded {@link CrawlerConfiguration}.
   */
  public CrawlerConfiguration load() {
    CrawlerConfiguration crawlerConfiguration = null;
    try (Reader reader = Files.newBufferedReader(path)) {
      crawlerConfiguration = read(reader);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return crawlerConfiguration;
  }

  /**
   * Loads crawler configuration from the given reader.
   *
   * @param reader a Reader pointing to a JSON string that contains crawler configuration.
   * @return a crawler configuration
   */
  public static CrawlerConfiguration read(Reader reader) {
    Objects.requireNonNull(reader);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE);
    CrawlerConfiguration config = null;
    try {
      config = objectMapper.readValue(reader, CrawlerConfiguration.class);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return config;
  }
}
