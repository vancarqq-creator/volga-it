package com.practice.automation.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByAll;

import java.time.Duration;

/** Страница /modals/ — простое окно и окно с формой обратной связи (Popup Maker + Forminator). */
public class ModalsPage extends PopupMakerPage {

    /** Фрагмент тела простого окна ("Hi, I'm a simple modal."); апостроф намеренно не используется. */
    private static final String SIMPLE_MODAL = "a simple modal";
    private static final String FORM_MODAL = "Please enter your contact info below";
    private static final String AD_MODAL = "I am an ad";

    private static final By SIMPLE_MODAL_BUTTON = new ByAll(By.id("modal1"), By.xpath(clickableByText("Simple Modal")));
    private static final By FORM_MODAL_BUTTON = new ByAll(By.id("modal2"), By.xpath(clickableByText("Form Modal")));

    private static final By NAME_INPUT = new ByAll(formControl("name-"),
            inModal(FORM_MODAL, "//input[contains(@class,'forminator-input')]"));
    private static final By EMAIL_INPUT = new ByAll(formControl("email-"),
            inModal(FORM_MODAL, "//input[@type='email']"));
    private static final By MESSAGE_INPUT = new ByAll(formControl("textarea-"),
            inModal(FORM_MODAL, "//textarea"));
    private static final By SUBMIT_BUTTON = inModal(FORM_MODAL,
            "//button[@type='submit' or contains(@class,'forminator-button-submit')]");
    /**
     * Блок ответа Forminator используется и для успеха, и для общей ошибки валидации
     * ("Error: Your form is not valid..."), поэтому успех определяется по классу/тексту.
     */
    private static final By SUCCESS_MESSAGE = inModal(FORM_MODAL,
            "//*[contains(@class,'forminator-response-message') and "
                    + "(contains(@class,'forminator-success') or contains(normalize-space(.),'Thank you'))]");

    public ModalsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String path() {
        return "/modals/";
    }

    // ---------------------------------------------------------------- simple modal

    @Step("Click the 'Simple Modal' button")
    public void openSimpleModal() {
        click(SIMPLE_MODAL_BUTTON);
        waitForModalVisible(SIMPLE_MODAL, Duration.ofSeconds(10));
    }

    public boolean isSimpleModalDisplayed() {
        return isModalDisplayed(SIMPLE_MODAL);
    }

    public String simpleModalText() {
        return modalText(SIMPLE_MODAL);
    }

    @Step("Close the simple modal with the × button")
    public void closeSimpleModal() {
        clickModalClose(SIMPLE_MODAL);
        waitForModalHidden(SIMPLE_MODAL);
    }

    // ---------------------------------------------------------------- form modal

    @Step("Click the 'Form Modal' button")
    public void openFormModal() {
        click(FORM_MODAL_BUTTON);
        waitForModalVisible(FORM_MODAL, Duration.ofSeconds(10));
    }

    public boolean isFormModalDisplayed() {
        return isModalDisplayed(FORM_MODAL);
    }

    public String formModalText() {
        return modalText(FORM_MODAL);
    }

    @Step("Close the form modal with the × button")
    public void closeFormModal() {
        clickModalClose(FORM_MODAL);
        waitForModalHidden(FORM_MODAL);
    }

    public boolean areFormFieldsDisplayed() {
        return isDisplayed(NAME_INPUT) && isDisplayed(EMAIL_INPUT)
                && isDisplayed(MESSAGE_INPUT) && isDisplayed(SUBMIT_BUTTON);
    }

    @Step("Fill the form: name='{name}', email='{email}', message='{message}'")
    public void fillForm(String name, String email, String message) {
        if (!name.isEmpty()) {
            type(NAME_INPUT, name);
        }
        if (!email.isEmpty()) {
            type(EMAIL_INPUT, email);
        }
        if (!message.isEmpty()) {
            type(MESSAGE_INPUT, message);
        }
    }

    @Step("Submit the form")
    public void submitForm() {
        click(SUBMIT_BUTTON);
    }

    // ---------------------------------------------------------------- form feedback

    /** Ждет и возвращает текст подтверждения отправки формы. */
    public String successMessage() {
        return getText(SUCCESS_MESSAGE);
    }

    public boolean isSuccessMessageDisplayed() {
        return isDisplayed(SUCCESS_MESSAGE);
    }

    /** Ждет и возвращает текст ошибки валидации поля Name. */
    public String nameError() {
        return fieldError("name-");
    }

    /** Ждет и возвращает текст ошибки валидации поля Email. */
    public String emailError() {
        return fieldError("email-");
    }

    private String fieldError(String namePrefix) {
        By error = inModal(FORM_MODAL, "//*[starts-with(@name,'" + namePrefix + "')]"
                + "/ancestor::*[contains(@class,'forminator-field')][1]"
                + "//*[contains(@class,'forminator-error-message')]");
        waitUntil(d -> isDisplayed(error) && !getText(error).isEmpty());
        return getText(error);
    }

    // ---------------------------------------------------------------- misc

    /** Проверяет, что на странице нет разметки рекламного окна (используется как негативная проверка /ads/). */
    public boolean isAdPopupEmbedded() {
        return isModalInDom(AD_MODAL);
    }

    private static By formControl(String namePrefix) {
        return inModal(FORM_MODAL, "//*[(self::input or self::textarea) and starts-with(@name,'" + namePrefix + "')]");
    }
}
