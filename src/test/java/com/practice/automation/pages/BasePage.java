package com.practice.automation.pages;

import com.practice.automation.config.Config;
import io.qameta.allure.Step;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * Базовый Page Object: единственное место, где живут обертки над кликами, вводом и ожиданиями.
 * Жестких пауз (Thread.sleep) нет — только явные ожидания.
 */
public abstract class BasePage {

    private static final Duration POLLING_INTERVAL = Duration.ofMillis(250);

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Config.explicitWait(), POLLING_INTERVAL);
        this.wait.ignoring(StaleElementReferenceException.class);
    }

    /** Путь страницы относительно base.url, например "/calendars/". */
    protected abstract String path();

    @Step("Open the page")
    public void open() {
        driver.get(Config.baseUrl() + path());
        waitForPageLoad();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public String baseUrlWithSlash() {
        return Config.baseUrl() + "/";
    }

    // ---------------------------------------------------------------- locators helpers

    /** XPath кликабельного элемента (button / a / input) по видимому тексту. */
    protected static String clickableByText(String label) {
        return "//*[(self::button or self::a or self::input) and "
                + "(normalize-space(.)='" + label + "' or @value='" + label + "')]";
    }

    // ---------------------------------------------------------------- waits

    protected void waitForPageLoad() {
        wait.until(d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void waitInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected <T> T waitUntil(Function<? super WebDriver, T> condition) {
        return wait.until(condition);
    }

    /**
     * Проверяет, что условие остается истинным в течение всего периода (без Thread.sleep).
     * Нужен для негативных проверок вида "элемент НЕ закрылся / значение НЕ изменилось".
     */
    protected boolean holdsFor(Duration period, BooleanSupplier condition) {
        try {
            new FluentWait<>(driver)
                    .withTimeout(period)
                    .pollingEvery(Duration.ofMillis(100))
                    .until(d -> !condition.getAsBoolean());
            return false;
        } catch (TimeoutException expected) {
            return true;
        }
    }

    // ---------------------------------------------------------------- actions

    protected void click(By locator) {
        wait.ignoring(ElementClickInterceptedException.class).until(d -> {
            WebElement element = ExpectedConditions.elementToBeClickable(locator).apply(d);
            if (element == null) {
                return false;
            }
            element.click();
            return true;
        });
    }

    protected void type(By locator, String text) {
        WebElement element = waitVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected void pressKey(By locator, Keys key) {
        waitVisible(locator).sendKeys(key);
    }

    // ---------------------------------------------------------------- reads

    protected String getText(By locator) {
        return waitVisible(locator).getText().trim();
    }

    protected String getValue(By locator) {
        String value = waitVisible(locator).getDomProperty("value");
        return value == null ? "" : value;
    }

    protected boolean isDisplayed(By locator) {
        try {
            List<WebElement> found = driver.findElements(locator);
            return !found.isEmpty() && found.get(0).isDisplayed();
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }

    /** Первый ВИДИМЫЙ элемент по локатору (например, ссылка "Home" может дублироваться в скрытом меню). */
    protected WebElement firstVisible(By locator) {
        return wait.until(d -> d.findElements(locator).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElse(null));
    }

    /**
     * true, если в центре элемента находится другой элемент (например, оверлей рекламы).
     * Проверка не кликает по странице, поэтому не может случайно увести тест на другую страницу.
     */
    protected boolean isCoveredByAnotherElement(WebElement element) {
        Object covered = ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0];"
                        + "el.scrollIntoView({block: 'center'});"
                        + "var r = el.getBoundingClientRect();"
                        + "var top = document.elementFromPoint(r.left + r.width / 2, r.top + r.height / 2);"
                        + "return top !== null && !el.contains(top);",
                element);
        return Boolean.TRUE.equals(covered);
    }

    protected boolean isPresentInDom(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    // ---------------------------------------------------------------- JS dialogs

    protected Alert waitForAlert() {
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    protected boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }
}
