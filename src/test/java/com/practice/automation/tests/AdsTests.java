package com.practice.automation.tests;

import com.practice.automation.pages.AdsPage;
import com.practice.automation.pages.ModalsPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Practice Automation UI")
@Feature("Ads (/ads/)")
class AdsTests extends BaseTest {

    private static final Duration STABILITY_PERIOD = Duration.ofMillis(1500);

    private AdsPage ads;

    @BeforeEach
    void openPage() {
        ads = new AdsPage(driver);
        ads.open();
    }

    // ================================================================ POSITIVE

    @Test
    @Tag("positive")
    @Tag("smoke")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("[ADS-P01] Рекламное окно появляется автоматически после загрузки страницы")
    @Description("Реклама показывается с задержкой (~5 с); ожидание — явное, с запасом по таймауту.")
    void adAppearsAutomatically() {
        ads.waitForAd();

        assertThat(ads.isAdDisplayed()).isTrue();
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[ADS-P02] Заголовок рекламы: 'Hi'")
    void adHasExpectedTitle() {
        ads.waitForAd();

        assertThat(ads.adTitle()).isEqualTo("Hi");
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[ADS-P03] Текст рекламы: 'I am an ad.'")
    void adHasExpectedText() {
        ads.waitForAd();

        assertThat(ads.adText()).contains("I am an ad");
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[ADS-P04] У рекламы есть видимая кнопка закрытия ×")
    void adHasVisibleCloseButton() {
        ads.waitForAd();

        assertThat(ads.isAdCloseButtonDisplayed()).isTrue();
    }

    @Test
    @Tag("positive")
    @Tag("smoke")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("[ADS-P05] Кнопка × закрывает рекламу, и она исчезает с экрана")
    void closeButtonDismissesAd() {
        ads.waitForAd();

        ads.closeAd();

        ads.waitForAdToDisappear();
        assertThat(ads.isAdDisplayed()).isFalse();
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[ADS-P06] После закрытия рекламы основной UI разблокирован (клик по ссылке проходит)")
    void pageIsInteractiveAfterAdIsClosed() {
        ads.waitForAd();
        ads.closeAd();
        ads.waitForAdToDisappear();

        ads.clickHomeBreadcrumb();

        assertThat(ads.currentUrl()).isEqualTo(ads.baseUrlWithSlash());
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[ADS-P07] Пока реклама открыта, элементы страницы под ней недоступны для клика")
    void adBlocksPageUnderneathWhileOpen() {
        ads.waitForAd();

        assertThat(ads.isHomeLinkCoveredByAd()).isTrue();
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("[ADS-P08] Реклама — это оверлей: заголовок страницы 'Ads' остается на месте")
    void pageHeadingIsPreservedWhileAdIsOpen() {
        ads.waitForAd();

        assertThat(ads.currentUrl()).endsWith("/ads/");
        assertThat(ads.pageHeading()).isEqualTo("Ads");
    }

    // ================================================================ NEGATIVE

    @Test
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[ADS-N01] На другой странице (/modals/) рекламного окна нет в DOM")
    void adIsNotEmbeddedOnOtherPages() {
        ModalsPage modals = new ModalsPage(driver);
        modals.open();

        assertThat(modals.isAdPopupEmbedded()).isFalse();
    }

    @Test
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[ADS-N02] Клик по телу рекламы (не по ×) не закрывает ее")
    void clickingInsideAdDoesNotCloseIt() {
        ads.waitForAd();

        ads.clickInsideAdBody();

        assertThat(ads.adStaysDisplayedFor(STABILITY_PERIOD)).as("ad stays on screen").isTrue();
    }

    @Test
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[ADS-N03] Закрытая реклама не оставляет на экране кнопку закрытия")
    void closedAdLeavesNoCloseButton() {
        ads.waitForAd();
        ads.closeAd();
        ads.waitForAdToDisappear();

        assertThat(ads.isAdCloseButtonDisplayed()).isFalse();
    }

    @Test
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[ADS-N04] Попытка кликнуть по странице под открытой рекламой не уводит со страницы")
    void clickUnderAdDoesNotNavigateAway() {
        ads.waitForAd();

        boolean clickWasBlocked = ads.isPageClickBlockedByAd();

        assertThat(clickWasBlocked).as("click intercepted by the ad overlay").isTrue();
        assertThat(ads.currentUrl()).endsWith("/ads/");
    }
}
