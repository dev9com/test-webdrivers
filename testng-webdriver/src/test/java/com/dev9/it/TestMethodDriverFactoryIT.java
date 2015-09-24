package com.dev9.it;

import com.dev9.annotation.MethodDriver;
import com.dev9.listener.SeleniumWebDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import util.Util;

import static util.Util.sleep;

/**
 * The purpose of this class is to verify method driver is created when the test class contains a factory.
 *
 * @author <a href="mailto:Justin.Graham@dev9.com">Justin Graham</a>
 * @since 11/4/13
 */
@Test(groups = {"IT"})
@Listeners({SeleniumWebDriver.class})
public class TestMethodDriverFactoryIT {

    @MethodDriver(excludeMethods = {"dependsOnMethod"})
    public WebDriver driver;

    private int i;
    private String url;

    @Factory(dataProvider = "dataProvider", dataProviderClass = Util.class)
    public TestMethodDriverFactoryIT(String url, int i) {
        this.url = url;
        this.i = i;
    }

    @Test
    public void testFactoryValues() throws Exception {
        driver.get(url);

        String currentUrl = driver.getCurrentUrl();

        switch (i) {
            case 1:
                Assert.assertTrue(currentUrl.contains("www.google.com"));
                sleep();
                break;
            case 2:
                Assert.assertTrue(currentUrl.contains("www.yahoo.com"));
                sleep();
                break;
            case 3:
                Assert.assertTrue(currentUrl.contains("www.wikipedia.org"));
                sleep();
                break;
            case 4:
                Assert.assertTrue(currentUrl.contains("github.com/"));
                sleep();
                break;
            default:
                throw new IllegalStateException("[" + i + "] is an unknown url digit!");
        }
    }

    @Test(dependsOnMethods = {"testFactoryValues"}, alwaysRun = true)
    public void dependsOnMethod() throws Exception {
        Assert.assertTrue(driver == null);
    }
}
