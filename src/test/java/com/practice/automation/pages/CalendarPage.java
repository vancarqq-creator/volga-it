package com.practice.automation.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** Страница /calendars/ — поле даты с jQuery UI datepicker и постраничной навигацией по месяцам. */
public class CalendarPage extends BasePage {

    /** Формат значения в поле (подсказка на сайте: YYYY-MM-DD). Единственное место, которое нужно менять при смене формата. */
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final By DATE_INPUT = By.cssSelector("input[placeholder='YYYY-MM-DD']");
    private static final By CALENDAR = By.cssSelector("div.ui-datepicker");
    private static final By MONTH_HEADER = By.cssSelector("div.ui-datepicker .ui-datepicker-month");
    private static final By YEAR_HEADER = By.cssSelector("div.ui-datepicker .ui-datepicker-year");
    private static final By NEXT_MONTH = By.cssSelector("div.ui-datepicker .ui-datepicker-next");
    private static final By PREVIOUS_MONTH = By.cssSelector("div.ui-datepicker .ui-datepicker-prev");
    private static final By SELECTED_DAY = By.cssSelector("div.ui-datepicker td.ui-datepicker-current-day a");
    private static final By PAGE_HEADING = By.tagName("h1");

    private static final String FORM = "//form[.//input[@placeholder='YYYY-MM-DD']]";
    private static final By SUBMIT_BUTTON = By.xpath(
            FORM + "//button[@type='submit' or contains(@class,'forminator-button-submit')]");
    private static final By SUCCESS_MESSAGE = By.xpath(
            FORM + "//*[contains(@class,'forminator-response-message') and "
                    + "(contains(@class,'forminator-success') or contains(normalize-space(.),'Thank you'))]");

    public CalendarPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected String path() {
        return "/calendars/";
    }

    // ---------------------------------------------------------------- calendar widget

    @Step("Open the calendar by clicking the date field")
    public void openCalendar() {
        if (!isCalendarOpen()) {
            click(DATE_INPUT);
        }
        waitVisible(CALENDAR);
    }

    public boolean isCalendarOpen() {
        return isDisplayed(CALENDAR);
    }

    @Step("Wait until the calendar is closed")
    public void waitForCalendarClosed() {
        waitInvisible(CALENDAR);
    }

    @Step("Select date {date} using month pagination")
    public void selectDate(LocalDate date) {
        openCalendar();
        navigateTo(YearMonth.from(date));
        click(dayCell(date.getDayOfMonth()));
        waitForCalendarClosed();
    }

    @Step("Select today's date from the calendar")
    public void selectToday() {
        selectDate(LocalDate.now());
    }

    @Step("Navigate calendar to {target}")
    public void navigateTo(YearMonth target) {
        openCalendar();
        if (isDropdownNavigation()) {
            selectMonthAndYearFromDropdowns(target);
            return;
        }
        long monthsToMove = ChronoUnit.MONTHS.between(displayedMonth(), target);
        By arrow = monthsToMove > 0 ? NEXT_MONTH : PREVIOUS_MONTH;
        int direction = monthsToMove > 0 ? 1 : -1;
        for (long i = 0; i < Math.abs(monthsToMove); i++) {
            YearMonth expected = displayedMonth().plusMonths(direction);
            click(arrow);
            waitUntil(d -> displayedMonth().equals(expected));
        }
    }

    /** Если в заголовке есть выпадающие списки месяца и года, переходим к нужному месяцу без цепочки кликов. */
    private boolean isDropdownNavigation() {
        List<WebElement> months = driver.findElements(MONTH_HEADER);
        List<WebElement> years = driver.findElements(YEAR_HEADER);
        return !months.isEmpty() && !years.isEmpty()
                && "select".equalsIgnoreCase(months.get(0).getTagName())
                && "select".equalsIgnoreCase(years.get(0).getTagName());
    }

    private void selectMonthAndYearFromDropdowns(YearMonth target) {
        // Значения option в jQuery UI: год — само число, месяц — индекс 0..11
        new Select(waitVisible(YEAR_HEADER)).selectByValue(String.valueOf(target.getYear()));
        waitUntil(d -> displayedMonth().getYear() == target.getYear());
        new Select(waitVisible(MONTH_HEADER)).selectByValue(String.valueOf(target.getMonthValue() - 1));
        waitUntil(d -> displayedMonth().equals(target));
    }

    @Step("Click the next-month arrow")
    public void goToNextMonth() {
        YearMonth expected = displayedMonth().plusMonths(1);
        click(NEXT_MONTH);
        waitUntil(d -> displayedMonth().equals(expected));
    }

    /** Месяц и год, отображаемые в заголовке календаря (поддерживает как текст, так и выпадающие списки). */
    public YearMonth displayedMonth() {
        WebElement monthElement = waitVisible(MONTH_HEADER);
        WebElement yearElement = waitVisible(YEAR_HEADER);
        String monthText = "select".equalsIgnoreCase(monthElement.getTagName())
                ? new Select(monthElement).getFirstSelectedOption().getText()
                : monthElement.getText();
        String yearText = "select".equalsIgnoreCase(yearElement.getTagName())
                ? new Select(yearElement).getFirstSelectedOption().getText()
                : yearElement.getText();
        return YearMonth.of(Integer.parseInt(yearText.trim()), parseMonth(monthText));
    }

    /** Число в ячейке, отмеченной как выбранная в календаре, либо пустая строка. */
    public String selectedDayInCalendar() {
        List<WebElement> cells = driver.findElements(SELECTED_DAY);
        return cells.isEmpty() ? "" : cells.get(0).getText().trim();
    }

    // ---------------------------------------------------------------- input field

    @Step("Type date '{text}' into the field")
    public void typeDate(String text) {
        type(DATE_INPUT, text);
        pressKey(DATE_INPUT, Keys.TAB);
        if (isCalendarOpen()) {
            waitForCalendarClosed();
        }
    }

    public String inputValue() {
        return getValue(DATE_INPUT);
    }

    @Step("Press Escape in the date field")
    public void pressEscapeInField() {
        pressKey(DATE_INPUT, Keys.ESCAPE);
    }

    @Step("Click outside the calendar (page heading)")
    public void clickOutsideCalendar() {
        click(PAGE_HEADING);
    }

    /** true, если значение поля не менялось на протяжении всего периода. */
    public boolean inputValueStaysUnchanged(String expectedValue, Duration period) {
        return holdsFor(period, () -> expectedValue.equals(inputValue()));
    }

    // ---------------------------------------------------------------- submit

    @Step("Submit the form")
    public void submit() {
        click(SUBMIT_BUTTON);
    }

    public String confirmationMessage() {
        return getText(SUCCESS_MESSAGE);
    }

    // ---------------------------------------------------------------- helpers

    private static By dayCell(int day) {
        return By.xpath("//div[contains(@class,'ui-datepicker')]//table[contains(@class,'ui-datepicker-calendar')]"
                + "//td[@data-handler='selectDay' and not(contains(@class,'ui-datepicker-other-month'))]"
                + "/a[normalize-space(.)='" + day + "']");
    }

    private static Month parseMonth(String text) {
        String prefix = text.trim().substring(0, 3).toUpperCase(Locale.ENGLISH);
        for (Month month : Month.values()) {
            if (month.name().startsWith(prefix)) {
                return month;
            }
        }
        throw new IllegalStateException("Cannot parse month name from calendar header: '" + text + "'");
    }
}
