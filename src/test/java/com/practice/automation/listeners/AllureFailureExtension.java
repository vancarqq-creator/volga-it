package com.practice.automation.listeners;

import com.practice.automation.core.DriverManager;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Прикладывает скриншот, URL и HTML страницы к Allure-отчету при падении теста.
 * Срабатывает ДО @AfterEach, пока браузер еще открыт (TestWatcher для этого не подходит).
 */
public class AllureFailureExtension implements TestExecutionExceptionHandler, LifecycleMethodExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        attachDiagnostics();
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        attachDiagnostics();
        throw throwable;
    }

    private void attachDiagnostics() {
        DriverManager.current().ifPresent(driver -> {
            attachScreenshot(driver);
            attachPageInfo(driver);
        });
    }

    private void attachScreenshot(WebDriver driver) {
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot on failure", "image/png", new ByteArrayInputStream(png), "png");
        } catch (WebDriverException e) {
            // Например, открыт JS-alert: скриншот недоступен, но падение теста скрывать нельзя
            Allure.addAttachment("Screenshot unavailable", "text/plain", e.getMessage());
        }
    }

    private void attachPageInfo(WebDriver driver) {
        try {
            Allure.addAttachment("Page URL", "text/plain", driver.getCurrentUrl());
            Allure.addAttachment("Page source", "text/html",
                    new ByteArrayInputStream(driver.getPageSource().getBytes(StandardCharsets.UTF_8)), "html");
        } catch (WebDriverException e) {
            Allure.addAttachment("Page info unavailable", "text/plain", e.getMessage());
        }
    }
}
