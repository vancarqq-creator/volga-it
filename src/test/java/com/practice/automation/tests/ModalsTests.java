package com.practice.automation.tests;

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

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Practice Automation UI")
@Feature("Modals (/modals/)")
class ModalsTests extends BaseTest {

    private static final String VALID_NAME = "Test User";
    private static final String VALID_EMAIL = "test.user@example.com";
    private static final String VALID_MESSAGE = "Hello from automated test";

    private ModalsPage modals;

    @BeforeEach
    void openPage() {
        modals = new ModalsPage(driver);
        modals.open();
    }

    // ================================================================ POSITIVE

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("[MOD-P01] После загрузки страницы ни одно модальное окно не показано")
    void noModalIsVisibleOnPageLoad() {
        assertThat(modals.isSimpleModalDisplayed()).as("simple modal").isFalse();
        assertThat(modals.isFormModalDisplayed()).as("form modal").isFalse();
    }

    @Test
    @Tag("positive")
    @Tag("smoke")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("[MOD-P02] Кнопка 'Simple Modal' открывает простое модальное окно")
    void simpleModalOpensOnButtonClick() {
        modals.openSimpleModal();

        assertThat(modals.isSimpleModalDisplayed()).isTrue();
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[MOD-P03] Простое модальное окно содержит ожидаемый текст")
    void simpleModalContainsExpectedText() {
        modals.openSimpleModal();

        assertThat(modals.simpleModalText()).contains("simple modal");
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[MOD-P04] Кнопка × закрывает простое модальное окно")
    void simpleModalClosesWithCloseButton() {
        modals.openSimpleModal();

        modals.closeSimpleModal();

        assertThat(modals.isSimpleModalDisplayed()).isFalse();
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[MOD-P05] Простое окно можно открыть повторно после закрытия")
    void simpleModalCanBeReopenedAfterClosing() {
        modals.openSimpleModal();
        modals.closeSimpleModal();

        modals.openSimpleModal();

        assertThat(modals.isSimpleModalDisplayed()).isTrue();
    }

    @Test
    @Tag("positive")
    @Tag("smoke")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("[MOD-P06] Кнопка 'Form Modal' открывает окно с формой и заголовком")
    void formModalOpensWithHeading() {
        modals.openFormModal();

        assertThat(modals.formModalText())
                .contains("Modal Containing A Form")
                .contains("Please enter your contact info below");
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[MOD-P07] Окно с формой содержит поля Name, Email, Message и кнопку Submit")
    void formModalContainsAllFormControls() {
        modals.openFormModal();

        assertThat(modals.areFormFieldsDisplayed()).isTrue();
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[MOD-P08] Отправка корректно заполненной формы показывает подтверждение")
    @Description("Заполняем Name, Email и Message валидными данными и отправляем форму.")
    void validFormSubmissionShowsConfirmation() {
        modals.openFormModal();
        modals.fillForm(VALID_NAME, VALID_EMAIL, VALID_MESSAGE);

        modals.submitForm();

        assertThat(modals.successMessage()).contains("Thank you for your response");
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[MOD-P09] Кнопка × закрывает окно с формой")
    void formModalClosesWithCloseButton() {
        modals.openFormModal();

        modals.closeFormModal();

        assertThat(modals.isFormModalDisplayed()).isFalse();
    }

    // ================================================================ NEGATIVE

    @Test
    @Tag("negative")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[MOD-N01] Отправка пустой формы показывает ошибку обязательного поля Name")
    void emptyFormSubmissionShowsRequiredNameError() {
        modals.openFormModal();

        modals.submitForm();

        assertThat(modals.nameError()).isNotBlank();
        assertThat(modals.isSuccessMessageDisplayed()).as("success message").isFalse();
    }

    @Test
    @Tag("negative")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[MOD-N02] Некорректный email блокирует отправку формы")
    void invalidEmailBlocksFormSubmission() {
        modals.openFormModal();
        modals.fillForm(VALID_NAME, "not-an-email", VALID_MESSAGE);

        modals.submitForm();

        assertThat(modals.emailError()).isNotBlank();
        assertThat(modals.isSuccessMessageDisplayed()).as("success message").isFalse();
    }

    @Test
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[MOD-N03] Заполнение только необязательных полей не снимает требование к Name")
    void optionalFieldsOnlyStillRequireName() {
        modals.openFormModal();
        modals.fillForm("", VALID_EMAIL, VALID_MESSAGE);

        modals.submitForm();

        assertThat(modals.nameError()).isNotBlank();
        assertThat(modals.isSuccessMessageDisplayed()).as("success message").isFalse();
    }

    @Test
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[MOD-N04] Закрытие окна без отправки формы не создает сообщения об успехе")
    void closingFormModalWithoutSubmitShowsNoConfirmation() {
        modals.openFormModal();
        modals.fillForm(VALID_NAME, VALID_EMAIL, VALID_MESSAGE);
        modals.closeFormModal();

        modals.openFormModal();

        assertThat(modals.isSuccessMessageDisplayed()).as("success message").isFalse();
    }
}
