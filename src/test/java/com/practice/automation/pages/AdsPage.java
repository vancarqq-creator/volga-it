package com.practice.automation.pages;

import com.practice.automation.config.Config;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

/** Страница /ads/ — рекламное модальное окно, появляющееся автоматически примерно через 5 секунд. */
public class AdsPage extends PopupMakerPage {

    private static final String AD = "I am an ad";

    private static final By HOME_BREADCRUMB = By.xpath("//a[normalize-space(.)='Home']");
    private static final By PAGE_HEADING = By.tagName("h1");
    private static final By AD_BODY = inModal(AD, "//*[contains(@class,'pum-content')]");

    public AdsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String path() {
        return "/ads/";
    }

    @Step("Wait for the ad to appear")
    public void waitForAd() {
        waitForModalVisible(AD, Config.adTimeout());
    }

    public boolean isAdDisplayed() {
        return isModalDisplayed(AD);
    }

    public String adTitle() {
        return modalTitle(AD);
    }

    public String adText() {
        return modalText(AD);
    }

    public boolean isAdCloseButtonDisplayed() {
        return isModalCloseButtonDisplayed(AD);
    }

    @Step("Close the ad with the × button")
    public void closeAd() {
        clickModalClose(AD);
    }

    @Step("Wait until the ad disappears")
    public void waitForAdToDisappear() {
        waitForModalHidden(AD);
    }

    @Step("Click inside the ad body")
    public void clickInsideAdBody() {
        click(AD_BODY);
    }

    /** true, если реклама оставалась на экране весь период. */
    public boolean adStaysDisplayedFor(Duration period) {
        return holdsFor(period, this::isAdDisplayed);
    }

    public String pageHeading() {
        return getText(PAGE_HEADING);
    }

    /** true, если ссылка "Home" перекрыта оверлеем рекламы (проверка без клика). */
    @Step("Check that the 'Home' link is covered by the ad overlay")
    public boolean isHomeLinkCoveredByAd() {
        return isCoveredByAnotherElement(firstVisible(HOME_BREADCRUMB));
    }

    /**
     * Реально пытается кликнуть по ссылке под рекламой, без повторных попыток.
     *
     * @return true, если клик перехвачен оверлеем; false, если клик прошел (и произошла навигация)
     */
    @Step("Try to click the 'Home' link while the ad is open")
    public boolean isPageClickBlockedByAd() {
        try {
            firstVisible(HOME_BREADCRUMB).click();
            return false;
        } catch (ElementClickInterceptedException e) {
            return true;
        }
    }

    @Step("Click the 'Home' breadcrumb")
    public void clickHomeBreadcrumb() {
        firstVisible(HOME_BREADCRUMB).click();
        waitUntil(d -> d.getCurrentUrl().equals(baseUrlWithSlash()));
    }
}
