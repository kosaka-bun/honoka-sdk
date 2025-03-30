package de.honoka.sdk.util.various;

import java.util.Locale;

public enum SystemEnum {

    WINDOWS, LINUX, MACOS, OTHER;

    public static SystemEnum getLocal() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if(osName.contains("windows")) return WINDOWS;
        if(osName.contains("linux")) return LINUX;
        if(osName.contains("mac")) return MACOS;
        return OTHER;
    }
}
