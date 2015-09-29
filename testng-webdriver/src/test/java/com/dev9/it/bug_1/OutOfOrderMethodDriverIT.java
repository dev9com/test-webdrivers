package com.dev9.it.bug_1;

import com.dev9.annotation.MethodDriver;
import com.dev9.listener.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static util.Util.GOOGLE_DOMAIN;
import static util.Util.HTTP_PROTOCOL;

/**
 * @author <a href="mailto:Justin.Graham@dev9.com">Justin Graham</a>
 * @since 9/24/2015
 */
@Test(groups = {"IT", "BUG_1"})
@Listeners({SeleniumWebDriver.class})
public class OutOfOrderMethodDriverIT {

    @MethodDriver private WebDriver driver;

    @Test(priority = 1)
    public void testBreaker() throws Exception {
        System.out.println("OutOfOrderMethodDriverIT:Break: " + driver.getWindowHandle() + ", " + driver);
        driver.get(HTTP_PROTOCOL + GOOGLE_DOMAIN);
    }
}
