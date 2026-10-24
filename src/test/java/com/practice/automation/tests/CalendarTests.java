package com.practice.automation.tests;

import com.practice.automation.pages.CalendarPage;
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
import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Practice Automation UI")
@Feature("Calendar (/calendars/)")
class CalendarTests extends BaseTest {

    private static final Duration STABILITY_PERIOD = Duration.ofSeconds(1);

    private CalendarPage calendar;

    @BeforeEach
    void openPage() {
        calendar = new CalendarPage(driver);
        calendar.open();
    }

    // ================================================================ POSITIVE

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[CAL-P01] Календарь открывается по клику на поле даты")
    @Description("Клик по полю даты раскрывает виджет календаря.")
    void calendarOpensOnFieldClick() {
        calendar.openCalendar();

        assertThat(calendar.isCalendarOpen()).as("calendar widget is visible").isTrue();
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[CAL-P02] Календарь по умолчанию показывает текущие месяц и год")
    void calendarShowsCurrentMonthAndYearByDefault() {
        calendar.openCalendar();

        assertThat(calendar.displayedMonth()).isEqualTo(YearMonth.now());
    }

    @Test
    @Tag("positive")
    @Tag("smoke")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("[CAL-P03] Выбор сегодняшней даты записывает ее в поле")
    void selectingTodayFillsTheField() {
        LocalDate today = LocalDate.now();

        calendar.selectDate(today);

        assertThat(calendar.inputValue()).isEqualTo(today.format(CalendarPage.DATE_FORMAT));
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[CAL-P04] Выбор даты в следующем месяце через пагинацию")
    void selectingDateInNextMonthFillsTheField() {
        LocalDate target = LocalDate.now().plusMonths(1).withDayOfMonth(15);

        calendar.selectDate(target);

        assertThat(calendar.inputValue()).isEqualTo(target.format(CalendarPage.DATE_FORMAT));
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[CAL-P05] Выбор даты в предыдущем месяце через пагинацию")
    void selectingDateInPreviousMonthFillsTheField() {
        LocalDate target = LocalDate.now().minusMonths(1).withDayOfMonth(10);

        calendar.selectDate(target);

        assertThat(calendar.inputValue()).isEqualTo(target.format(CalendarPage.DATE_FORMAT));
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[CAL-P06] Выбор даты в следующем году (много кликов по стрелке)")
    void selectingDateInNextYearFillsTheField() {
        LocalDate target = LocalDate.now().plusYears(1).withMonth(3).withDayOfMonth(20);

        calendar.selectDate(target);

        assertThat(calendar.inputValue()).isEqualTo(target.format(CalendarPage.DATE_FORMAT));
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[CAL-P07] Граничное значение: 29 февраля ближайшего високосного года")
    void selectingLeapDayFillsTheField() {
        LocalDate leapDay = nextLeapDay(LocalDate.now());

        calendar.selectDate(leapDay);

        assertThat(calendar.inputValue()).isEqualTo(leapDay.format(CalendarPage.DATE_FORMAT));
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[CAL-P08] Граничное значение: 31 декабря (конец года)")
    void selectingLastDayOfYearFillsTheField() {
        LocalDate target = LocalDate.of(LocalDate.now().getYear() + 1, 12, 31);

        calendar.selectDate(target);

        assertThat(calendar.inputValue()).isEqualTo(target.format(CalendarPage.DATE_FORMAT));
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[CAL-P09] Дата, введенная вручную в формате YYYY-MM-DD, подсвечивается в календаре")
    void manuallyTypedDateIsPreselectedInCalendar() {
        calendar.typeDate("2030-02-14");
        calendar.openCalendar();

        assertThat(calendar.displayedMonth()).isEqualTo(YearMonth.of(2030, 2));
        assertThat(calendar.selectedDayInCalendar()).isEqualTo("14");
    }

    @Test
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[CAL-P10] Повторный выбор даты заменяет предыдущее значение (диапазон: начало -> конец)")
    void selectingSecondDateReplacesFirstOne() {
        LocalDate rangeStart = LocalDate.now().plusDays(1);
        LocalDate rangeEnd = LocalDate.now().plusMonths(2).withDayOfMonth(5);

        calendar.selectDate(rangeStart);
        calendar.selectDate(rangeEnd);

        assertThat(calendar.inputValue()).isEqualTo(rangeEnd.format(CalendarPage.DATE_FORMAT));
        assertThat(rangeEnd).as("range end is after range start").isAfter(rangeStart);
    }

    // ================================================================ NEGATIVE

    @Test
    @Tag("negative")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("[CAL-N01] Календарь закрыт при загрузке страницы (без действий пользователя)")
    void calendarIsClosedOnPageLoad() {
        assertThat(calendar.isCalendarOpen()).as("calendar widget is visible").isFalse();
    }

    @Test
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[CAL-N02] Escape закрывает календарь без выбора даты")
    void escapeClosesCalendarWithoutSelectingDate() {
        String valueBefore = calendar.inputValue();
        calendar.openCalendar();

        calendar.pressEscapeInField();

        calendar.waitForCalendarClosed();
        assertThat(calendar.inputValueStaysUnchanged(valueBefore, STABILITY_PERIOD))
                .as("field value is unchanged").isTrue();
    }

    @Test
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[CAL-N03] Клик вне календаря закрывает его без выбора даты")
    void clickOutsideClosesCalendarWithoutSelectingDate() {
        String valueBefore = calendar.inputValue();
        calendar.openCalendar();

        calendar.clickOutsideCalendar();

        calendar.waitForCalendarClosed();
        assertThat(calendar.inputValueStaysUnchanged(valueBefore, STABILITY_PERIOD))
                .as("field value is unchanged").isTrue();
    }

    @Test
    @Tag("negative")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[CAL-N04] Листание месяцев без клика по дню не заполняет поле")
    void paginationAloneDoesNotFillTheField() {
        String valueBefore = calendar.inputValue();
        calendar.openCalendar();

        calendar.goToNextMonth();
        calendar.goToNextMonth();

        assertThat(calendar.inputValueStaysUnchanged(valueBefore, STABILITY_PERIOD))
                .as("field value is unchanged").isTrue();
    }

    // ================================================================ helpers

    private static LocalDate nextLeapDay(LocalDate from) {
        int year = from.getYear();
        while (true) {
            if (java.time.Year.isLeap(year) && LocalDate.of(year, 2, 29).isAfter(from)) {
                return LocalDate.of(year, 2, 29);
            }
            year++;
        }
    }
}
