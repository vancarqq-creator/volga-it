package com.practice.automation.core;

import com.practice.automation.config.Browser;
import com.practice.automation.config.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/** Создает настроенный WebDriver для выбранного браузера. */
final class DriverFactory {

    private DriverFactory() {
    }

    static WebDriver create(Browser browser, boolean headless) {
        WebDriver driver;
        switch (browser) {
            case CHROME:
                driver = createChrome(headless);
                break;
            case FIREFOX:
                driver = createFirefox(headless);
                break;
            default:
                throw new IllegalStateException("Unhandled browser: " + browser);
        }
        driver.manage().timeouts().pageLoadTimeout(Config.pageLoadTimeout());
        return driver;
    }

    private static WebDriver createChrome(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--window-size=" + Config.windowWidth() + "," + Config.windowHeight(),
                "--disable-notifications",
                "--disable-search-engine-choice-screen",
                "--no-sandbox",
                "--disable-dev-shm-usage");
        if (headless) {
            options.addArguments("--headless=new");
        }
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefox(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--width=" + Config.windowWidth(), "--height=" + Config.windowHeight());
        if (headless) {
            options.addArguments("-headless");
        }
        return new FirefoxDriver(options);
    }
}
