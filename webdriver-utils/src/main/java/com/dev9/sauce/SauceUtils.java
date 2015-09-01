package com.dev9.sauce;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import lombok.extern.log4j.Log4j2;


/**
 * User: yurodivuie
 * Date: 7/18/13
 * Time: 11:23 AM
 */
@Log4j2
public class SauceUtils
{

    private SauceUtils() {
        throw new IllegalAccessError("Utility class");
    }

    public static void logInContext(WebDriver driver, String message) {
        if (driver instanceof JavascriptExecutor && driver instanceof RemoteWebDriver) {
            try {
                ((JavascriptExecutor) driver).executeScript("sauce:context=// " + message);
            } catch (WebDriverException exception) {
                log.warn("Failed to update sauce labs context: {}", exception.getMessage());
            }
        }
    }

    public static String getJobId(WebDriver driver) {
        String jobId = null;
        if (driver instanceof RemoteWebDriver) {
            final SessionId rawSessionId = ((RemoteWebDriver) driver).getSessionId();
            if (rawSessionId != null) {
                jobId = rawSessionId.toString();
            }
        }
        return jobId;
    }

    public static String getJobUrl(String jobId) {
        String jobUrl = null;
        if (jobId != null) {
            jobUrl = String.format("https://saucelabs.com/jobs/%s", jobId);
        }
        return jobUrl;
    }

    public static String getJobUrl(WebDriver driver) {
        return getJobUrl(getJobId(driver));
    }
}
