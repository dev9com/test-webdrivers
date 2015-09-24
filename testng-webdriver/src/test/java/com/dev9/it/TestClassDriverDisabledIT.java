package com.dev9.it;

import com.dev9.annotation.ClassDriver;
import com.dev9.listener.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Test(groups = {"IT"})
@Listeners({SeleniumWebDriver.class})
public class TestClassDriverDisabledIT {

    @ClassDriver(enabled = false)
    public WebDriver driver;

    public void assertClassDriverUninitialized() {
        Assert.assertTrue(driver == null, "WebDriver != null");
    }
}
