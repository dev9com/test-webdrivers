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

    private PageObject pageObject;

    @Test(priority = 0)
    public void testOne() throws Exception {
        System.out.println("DependentClassDriverIT:One: " + driver.getWindowHandle() + ", " + driver);
        this.pageObject = new PageObject(driver);
    }

    @Test(priority = 2, dependsOnMethods = {"testOne"})
    public void testTwo() throws Exception {
        System.out.println("DependentClassDriverIT:Two: " + driver.getWindowHandle() + ", " + driver);
        pageObject.clickExplore();
    }
}
