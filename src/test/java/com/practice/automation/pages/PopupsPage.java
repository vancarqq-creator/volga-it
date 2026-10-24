package com.practice.automation.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;

/**
 * Страница /popups/ — нативные JS-диалоги (alert / confirm / prompt).
 * На самой странице /modals/ таких диалогов нет, они расположены здесь.
 */
public class PopupsPage extends BasePage {

    private static final By ALERT_BUTTON = new ByAll(By.id("alert"), By.xpath(clickableByText("Alert Popup")));
    private static final By CONFIRM_BUTTON = new ByAll(By.id("confirm"), By.xpath(clickableByText("Confirm Popup")));
    private static final By PROMPT_BUTTON = new ByAll(By.id("prompt"), By.xpath(clickableByText("Prompt Popup")));
    private static final By CONFIRM_RESULT = By.id("confirmResult");
    private static final By PROMPT_RESULT = By.id("promptResult");

    private static final Duration RESULT_SETTLE_TIMEOUT = Duration.ofSeconds(3);

    public PopupsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String path() {
        return "/popups/";
    }

    @Step("Trigger the alert and read its text")
    public String triggerAlertAndReadText() {
        click(ALERT_BUTTON);
        return waitForAlert().getText();
    }

    @Step("Accept the currently open dialog")
    public void acceptDialog() {
        waitForAlert().accept();
    }

    public boolean isDialogOpen() {
        return isAlertPresent();
    }

    /** Открывает confirm, отвечает OK/Cancel и возвращает текст результата. */
    @Step("Trigger confirm and answer {accept}")
    public String answerConfirm(boolean accept) {
        String before = textOrEmpty(CONFIRM_RESULT);
        click(CONFIRM_BUTTON);
        Alert dialog = waitForAlert();
        if (accept) {
            dialog.accept();
        } else {
            dialog.dismiss();
        }
        return resultAfterChange(CONFIRM_RESULT, before);
    }

    /** Открывает prompt, вводит текст (если не null), подтверждает или отменяет, возвращает текст результата. */
    @Step("Trigger prompt, input '{text}', accept={accept}")
    public String answerPrompt(String text, boolean accept) {
        String before = textOrEmpty(PROMPT_RESULT);
        click(PROMPT_BUTTON);
        Alert dialog = waitForAlert();
        if (text != null) {
            dialog.sendKeys(text);
        }
        if (accept) {
            dialog.accept();
        } else {
            dialog.dismiss();
        }
        return resultAfterChange(PROMPT_RESULT, before);
    }

    private String textOrEmpty(By locator) {
        return driver.findElements(locator).isEmpty() ? "" : driver.findElement(locator).getText().trim();
    }

    /** Ждет изменения текста результата; если страница оставляет его прежним — возвращает текущее значение. */
    private String resultAfterChange(By locator, String before) {
        try {
            new FluentWait<>(driver)
                    .withTimeout(RESULT_SETTLE_TIMEOUT)
                    .pollingEvery(Duration.ofMillis(100))
                    .until(d -> !textOrEmpty(locator).equals(before));
        } catch (TimeoutException ignored) {
            // результат мог не измениться — это проверит тест
        }
        return textOrEmpty(locator);
    }
}
