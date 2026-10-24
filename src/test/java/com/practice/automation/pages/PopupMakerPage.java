package com.practice.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Общая логика модальных окон плагина Popup Maker (его использует практический сайт на /modals/ и /ads/).
 * Окно идентифицируется по фрагменту своего текста, т.к. у окон нет стабильных id в тестовых данных.
 */
public abstract class PopupMakerPage extends BasePage {

    protected PopupMakerPage(WebDriver driver) {
        super(driver);
    }

    /** XPath overlay-контейнера окна, внутри которого присутствует заданный текст. */
    protected static String modalXpath(String textFragment) {
        return "//div[contains(@class,'pum-overlay')][.//*[contains(normalize-space(.),'" + textFragment + "')]]";
    }

    protected static By modal(String textFragment) {
        return By.xpath(modalXpath(textFragment));
    }

    /** Элемент внутри окна; relativeXpath должен начинаться с "//". */
    protected static By inModal(String textFragment, String relativeXpath) {
        return By.xpath(modalXpath(textFragment) + relativeXpath);
    }

    protected boolean isModalDisplayed(String textFragment) {
        return isDisplayed(modal(textFragment));
    }

    protected boolean isModalInDom(String textFragment) {
        return isPresentInDom(modal(textFragment));
    }

    protected void waitForModalVisible(String textFragment, Duration timeout) {
        new WebDriverWait(driver, timeout)
                .ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.visibilityOfElementLocated(modal(textFragment)));
    }

    protected void waitForModalHidden(String textFragment) {
        waitInvisible(modal(textFragment));
    }

    protected void clickModalClose(String textFragment) {
        click(inModal(textFragment, "//*[contains(@class,'pum-close')]"));
    }

    protected boolean isModalCloseButtonDisplayed(String textFragment) {
        return isDisplayed(inModal(textFragment, "//*[contains(@class,'pum-close')]"));
    }

    protected String modalTitle(String textFragment) {
        return getText(inModal(textFragment, "//*[contains(@class,'pum-title')]"));
    }

    protected String modalText(String textFragment) {
        return getText(modal(textFragment));
    }
}
