package com.dev9.it;

import com.dev9.annotation.MethodDriver;
import com.dev9.listener.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static util.Util.HTTP_PROTOCOL;
import static util.Util.YAHOO_DOMAIN;

@Test(groups = {"IT"})
@Listeners({SeleniumWebDriver.class})
public class TestMethodDriverAnnotationIgnoreIT {

    @MethodDriver(excludeMethods = {"assertMethodDriverUninitialized1", "assertMethodDriverUninitialized2"})
    WebDriver methodDriver;

    public void assertMethodDriverUninitialized1() throws InterruptedException {
        Assert.assertTrue(methodDriver == null, "WebDriver != null");
    }

    @Test(dependsOnMethods = {"assertMethodDriverUninitialized1"})
    public void navigateMethodDriverToSearch() {
        methodDriver.get(HTTP_PROTOCOL + YAHOO_DOMAIN);
        String url = methodDriver.getCurrentUrl();
        Assert.assertTrue(url.endsWith(YAHOO_DOMAIN), "False: " + url + " endsWith " + YAHOO_DOMAIN);
    }

    @Test(dependsOnMethods = {"navigateMethodDriverToSearch"})
    public void assertMethodDriverUninitialized2() {
        Assert.assertTrue(methodDriver == null, "WebDriver != null");
    }
}
