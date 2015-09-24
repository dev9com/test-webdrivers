package com.dev9.it;


import com.dev9.annotation.ClassDriver;
import com.dev9.listener.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static util.Util.HTTP_PROTOCOL;
import static util.Util.YAHOO_DOMAIN;


@Test(groups = {"IT"})
@Listeners({SeleniumWebDriver.class})
public class TestClassDriverAnnotationIT {

    @ClassDriver
    WebDriver classDriver;

    @Test
    public void navigateClassToSearch() throws Exception {
        classDriver.get(HTTP_PROTOCOL + YAHOO_DOMAIN);
        String url = classDriver.getCurrentUrl();
        Assert.assertTrue(url.endsWith(YAHOO_DOMAIN), "False: " + url + " endsWith " + YAHOO_DOMAIN);
    }

    @Test(dependsOnMethods = {"navigateClassToSearch"})
    public void assertClassPersistence() {
        String url = classDriver.getCurrentUrl();
        Assert.assertTrue(url.endsWith(YAHOO_DOMAIN), "False: " + url + " endsWith " + YAHOO_DOMAIN);
    }
}
