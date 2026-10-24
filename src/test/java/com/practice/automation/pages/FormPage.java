package com.practice.automation.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByAll;

import java.util.List;
import java.util.stream.Collectors;

/** Страница /form-fields/ — форма с разделом Automation tools и полем Message. */
public class FormPage extends BasePage {

    public static final String TOOLS_SEPARATOR = ", ";

    private static final String TOOLS_LABEL =
            "//*[self::label or self::legend or self::p or self::span or self::strong "
                    + "or self::h2 or self::h3 or self::h4 or self::h5][normalize-space(.)='Automation tools']";

    /** Элементы списка, следующего за заголовком "Automation tools". */
    private static final By TOOL_ITEMS = By.xpath(TOOLS_LABEL + "/following::ul[1]/li");
    /** Запасной вариант: список вложен внутрь контейнера с заголовком. */
    private static final By TOOL_ITEMS_NESTED = By.xpath(TOOLS_LABEL + "/ancestor::*[1]//ul/li");

    private static final By MESSAGE = new ByAll(
            By.id("message"),
            By.name("message"),
            By.xpath("//label[contains(normalize-space(.),'Message')]/following::textarea[1]"));

    public FormPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String path() {
        return "/form-fields/";
    }

    @Step("Read all items of the 'Automation tools' list")
    public List<String> automationTools() {
        List<WebElement> items = waitUntil(d -> {
            List<WebElement> found = d.findElements(TOOL_ITEMS);
            if (found.isEmpty()) {
                found = d.findElements(TOOL_ITEMS_NESTED);
            }
            return found.isEmpty() ? null : found;
        });
        return items.stream()
                .map(item -> item.getText().trim())
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
    }

    @Step("Fill the Message field with the comma-separated Automation tools")
    public String fillMessageWithAutomationTools() {
        String joined = String.join(TOOLS_SEPARATOR, automationTools());
        type(MESSAGE, joined);
        return joined;
    }

    public String messageValue() {
        return getValue(MESSAGE);
    }
}
