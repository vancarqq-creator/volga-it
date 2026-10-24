package com.practice.automation.tests;

import com.practice.automation.pages.PopupsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Нативные JS-диалоги (Alert / Confirm / Prompt). На сайте они находятся на /popups/, а не на /modals/,
 * поэтому вынесены в отдельный класс и относятся к разделу "Модальные окна" в README.
 */
@Epic("Practice Automation UI")
@Feature("Modals: native JS dialogs (/popups/)")
class JsDialogTests extends BaseTest {

    private PopupsPage popups;

    @BeforeEach
    void openPage() {
        popups = new PopupsPage(driver);
        popups.open();
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[JSD-P01] Alert показывает текст и закрывается по OK")
    void alertShowsTextAndClosesOnAccept() {
        String text = popups.triggerAlertAndReadText();
        popups.acceptDialog();

        assertThat(text).isNotBlank();
        assertThat(popups.isDialogOpen()).as("dialog still open").isFalse();
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[JSD-P02] Confirm: ответ OK обновляет результат на странице")
    void confirmAcceptUpdatesResult() {
        assertThat(popups.answerConfirm(true)).isNotBlank();
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[JSD-P03] Confirm: OK и Cancel приводят к разным результатам")
    void confirmAcceptAndCancelProduceDifferentResults() {
        String acceptResult = popups.answerConfirm(true);
        String cancelResult = popups.answerConfirm(false);

        assertThat(cancelResult).isNotBlank().isNotEqualTo(acceptResult);
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[JSD-P04] Prompt: введенный текст отображается в результате после OK")
    void promptAcceptedTextAppearsInResult() {
        assertThat(popups.answerPrompt("Alex", true)).contains("Alex");
    }

    @Test
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[JSD-N01] Prompt: при Cancel введенный текст в результат не попадает")
    void promptDismissedTextDoesNotAppearInResult() {
        assertThat(popups.answerPrompt("Alex", false)).doesNotContain("Alex");
    }
}
