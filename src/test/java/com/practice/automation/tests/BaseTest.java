package com.practice.automation.tests;

import com.practice.automation.core.DriverManager;
import com.practice.automation.listeners.AllureEnvironmentWriter;
import com.practice.automation.listeners.AllureFailureExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

/** Управляет жизненным циклом браузера: новая чистая сессия на каждый тест, обязательный quit() в @AfterEach. */
@ExtendWith(AllureFailureExtension.class)
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeAll
    static void publishEnvironmentToAllure() {
        AllureEnvironmentWriter.write();
    }

    @BeforeEach
    void startBrowser() {
        driver = DriverManager.start();
    }

    @AfterEach
    void stopBrowser() {
        DriverManager.quit();
    }
}
