package com.practice.automation.tests;

import com.practice.automation.pages.FormPage;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Критический сценарий: динамическое заполнение поля Message списком Automation tools. */
@Epic("Practice Automation UI")
@Feature("Dynamic form (/form-fields/)")
class FormTests extends BaseTest {

    private static final List<String> EXPECTED_TOOLS =
            Arrays.asList("Selenium", "Playwright", "Cypress", "Appium", "Katalon Studio");

    private FormPage form;

    @BeforeEach
    void openPage() {
        form = new FormPage(driver);
        form.open();
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[FRM-01] Из раздела Automation tools извлекаются все пункты списка")
    void automationToolsAreExtractedFromTheList() {
        assertThat(form.automationTools()).containsExactlyElementsOf(EXPECTED_TOOLS);
    }

    @Test
    @Tag("positive")
    @Tag("smoke")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("[FRM-02] Поле Message заполняется инструментами через запятую")
    @Description("Список инструментов читается со страницы, склеивается через ', ' и вводится в Message.")
    void messageIsFilledWithCommaSeparatedTools() {
        String expectedMessage = String.join(", ", form.automationTools());

        String typed = form.fillMessageWithAutomationTools();

        assertThat(typed).isEqualTo(expectedMessage);
        assertThat(form.messageValue())
                .isEqualTo(expectedMessage)
                .isEqualTo("Selenium, Playwright, Cypress, Appium, Katalon Studio");
    }

    @Test
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[FRM-03] Повторное заполнение Message заменяет текст, а не дублирует его")
    void refillingMessageDoesNotDuplicateTools() {
        form.fillMessageWithAutomationTools();

        form.fillMessageWithAutomationTools();

        assertThat(form.messageValue()).isEqualTo(String.join(", ", EXPECTED_TOOLS));
    }
}
