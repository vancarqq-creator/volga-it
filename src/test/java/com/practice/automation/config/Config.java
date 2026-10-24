package com.practice.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.Properties;

/**
 * Единая точка конфигурации. Приоритет: системное свойство (-Dkey=value) -> config.properties -> значение по умолчанию.
 */
public final class Config {

    private static final Properties FILE_PROPERTIES = load();

    private Config() {
    }

    public static String baseUrl() {
        String url = get("base.url", "https://practice-automation.com").trim();
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public static Browser browser() {
        return Browser.from(get("browser", "chrome"));
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless", "true"));
    }

    public static int windowWidth() {
        return Integer.parseInt(get("window.width", "1920"));
    }

    public static int windowHeight() {
        return Integer.parseInt(get("window.height", "1080"));
    }

    public static Duration explicitWait() {
        return seconds("timeout.explicit.seconds", 10);
    }

    public static Duration pageLoadTimeout() {
        return seconds("timeout.page.load.seconds", 30);
    }

    public static Duration adTimeout() {
        return seconds("timeout.ad.seconds", 20);
    }

    private static Duration seconds(String key, int defaultValue) {
        return Duration.ofSeconds(Long.parseLong(get(key, String.valueOf(defaultValue))));
    }

    private static String get(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = FILE_PROPERTIES.getProperty(key);
        }
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read config.properties", e);
        }
        return properties;
    }
}
