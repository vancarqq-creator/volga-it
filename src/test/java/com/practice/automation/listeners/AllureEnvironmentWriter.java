package com.practice.automation.listeners;

import com.practice.automation.config.Config;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/** Пишет environment.properties, чтобы в Allure-отчете был виден браузер, режим и базовый URL запуска. */
public final class AllureEnvironmentWriter {

    private AllureEnvironmentWriter() {
    }

    public static void write() {
        Path resultsDir = Paths.get(System.getProperty("allure.results.directory", "allure-results"));
        Properties environment = new Properties();
        environment.setProperty("Browser", Config.browser().name());
        environment.setProperty("Headless", String.valueOf(Config.headless()));
        environment.setProperty("Base.URL", Config.baseUrl());
        environment.setProperty("Java", System.getProperty("java.version"));
        environment.setProperty("OS", System.getProperty("os.name"));
        try {
            Files.createDirectories(resultsDir);
            try (OutputStream out = Files.newOutputStream(resultsDir.resolve("environment.properties"))) {
                environment.store(out, "Allure environment");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write Allure environment file", e);
        }
    }
}
