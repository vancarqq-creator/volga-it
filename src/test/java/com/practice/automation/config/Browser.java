package com.practice.automation.config;

import java.util.Locale;

/** Поддерживаемые браузеры. */
public enum Browser {
    CHROME,
    FIREFOX;

    public static Browser from(String value) {
        try {
            return Browser.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "Unsupported browser '" + value + "'. Supported: chrome, firefox", e);
        }
    }
}
