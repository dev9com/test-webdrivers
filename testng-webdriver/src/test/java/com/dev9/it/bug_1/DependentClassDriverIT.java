package com.dev9.it.bug_1;

import com.dev9.annotation.ClassDriver;
import com.dev9.listener.SeleniumWebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static util.Util.GITHUB_DOMAIN;
import static util.Util.HTTP_PROTOCOL;

/**
 * @author <a href="mailto:Justin.Graham@dev9.com">Justin Graham</a>
 * @since 9/24/2015
 */
@Test(groups = {"IT", "BUG_1"})
@Listeners({SeleniumWebDriver.class})
public class DependentClassDriverIT {

    @ClassDriver private WebDriver driver;

    @Test(priority = 0)
    public void testOne() throws Exception {
        driver.get(HTTP_PROTOCOL + GITHUB_DOMAIN);
    }

    @Test(priority = 2, dependsOnMethods = {"testOne"})
    public void testTwo() throws Exception {
        driver.findElement(By.cssSelector("a[href='/explore']")).click();
    }
}
