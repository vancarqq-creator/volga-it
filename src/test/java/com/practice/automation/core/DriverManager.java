package com.practice.automation.core;

import com.practice.automation.config.Config;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

/** Хранит драйвер текущего потока (безопасно для параллельного запуска). */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver start() {
        quit();
        WebDriver driver = DriverFactory.create(Config.browser(), Config.headless());
        DRIVER.set(driver);
        return driver;
    }

    public static Optional<WebDriver> current() {
        return Optional.ofNullable(DRIVER.get());
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            return;
        }
        try {
            driver.quit();
        } finally {
            DRIVER.remove();
        }
    }
}
