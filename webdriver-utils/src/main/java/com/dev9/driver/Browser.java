package com.dev9.driver;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.safari.SafariDriver;
import com.opera.core.systems.OperaDriver;


public enum Browser {
    CHROME(ChromeDriver.class),
    FIREFOX(FirefoxDriver.class),
    PHANTOMJS(PhantomJSDriver.class),
    HTMLUNIT(HtmlUnitDriver.class),
    IEXPLORE(InternetExplorerDriver.class),
    OPERA(OperaDriver.class),
    SAFARI(SafariDriver.class);

    private Class driverClass;

    public Class getDriverClass() {
        return driverClass;
    }

    Browser(Class driverClass) {
        this.driverClass = driverClass;
    }

    public static Browser fromJson(String text) {
        return valueOf(text.toUpperCase());
    }
}