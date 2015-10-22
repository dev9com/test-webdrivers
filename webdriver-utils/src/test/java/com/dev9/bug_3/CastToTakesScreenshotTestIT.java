package com.dev9.bug_3;

import com.dev9.driver.ThreadLocalWebDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@Test(groups = {"IT"})
public class CastToTakesScreenshotTestIT {

    private WebDriver driver;

    @BeforeMethod
    public void setUp() throws Exception {
        driver = new ThreadLocalWebDriver(this.getClass());
    }

    @Test
    public void testCast() throws Exception {
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        assertThat(scrFile).isNotNull();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        driver.quit();
    }
}
