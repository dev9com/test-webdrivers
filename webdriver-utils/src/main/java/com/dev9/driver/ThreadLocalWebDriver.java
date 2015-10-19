package com.dev9.driver;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.remote.RemoteWebDriver;
import com.dev9.sauce.SauceUtils;

import lombok.extern.log4j.Log4j2;


/**
 * The purpose of this class is to provide a ThreadLocal instance of a WebDriver.
 *
 * @author <a href="mailto:Justin.Graham@dev9.com">Justin Graham</a>
 * @since 8/7/13
 */
@Log4j2
public class ThreadLocalWebDriver implements WebDriver, JavascriptExecutor, HasInputDevices, TakesScreenshot {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();
    private static ThreadLocal<Class> testClass = new ThreadLocal<Class>();
    private static ThreadLocal<TargetWebDriver> targetWebDriver = new ThreadLocal<TargetWebDriver>();
    private static ThreadLocal<String> jobId = new ThreadLocal<String>();

    public ThreadLocalWebDriver(Class clazz) {
        testClass.set(clazz);
        targetWebDriver.set(new TargetWebDriver(testClass.get()));
        init();
    }

    public ThreadLocalWebDriver(Class clazz, String testDescription) {
        testClass.set(clazz);
        TargetWebDriver targetDriver = new TargetWebDriver(clazz);

        if (testDescription != null && !testDescription.equals("")) {
            targetDriver.getCapabilities().setCapability("name", testDescription);
        }

        targetWebDriver.set(targetDriver);
        init();
    }

    private void init() {
        setDriver(targetWebDriver.get());
        setJobId();
        reportURL();
    }

    private void setDriver(TargetWebDriver d) {
        driver.set(d.build());
    }

    private void setJobId() {
        jobId.set(SauceUtils.getJobId(driver.get()));
    }

    public String getJobUrl() {
        return SauceUtils.getJobUrl(driver.get());
    }

    public String getJobId() {
        return jobId.get();
    }

    public boolean instanceOf(Class clazz) {
        return clazz.isAssignableFrom(driver.get().getClass());
    }

    public boolean isRemote() {
        return targetWebDriver.get().isRemote();
    }

    public Browser getBrowser() {
        return targetWebDriver.get().getBrowser();
    }

    public String getSessionId() {
        if (driver.get() instanceof RemoteWebDriver ) {
            return ((RemoteWebDriver) driver.get()).getSessionId().toString();
        }
        return null;
    }

    public void get(String s) {
        driver.get().get(s);
    }

    /* ==============================================================================
                                      WebDriver Interface
       ============================================================================== */

    public String getCurrentUrl() {
        return driver.get().getCurrentUrl();
    }

    public String getTitle() {
        return driver.get().getTitle();
    }

    public List<WebElement> findElements(By by) {
        return driver.get().findElements(by);
    }

    public WebElement findElement(By by) {
        return driver.get().findElement(by);
    }

    public String getPageSource() {
        return driver.get().getPageSource();
    }

    public void close() {
        driver.get().close();
    }

    public void quit() {
        driver.get().quit();
    }

    public Set<String> getWindowHandles() {
        return driver.get().getWindowHandles();
    }

    public String getWindowHandle() {
        return driver.get().getWindowHandle();
    }

    public TargetLocator switchTo() {
        return driver.get().switchTo();
    }

    public Navigation navigate() {
        return driver.get().navigate();
    }

    public Options manage() {
        return driver.get().manage();
    }

    public Object executeScript(String s, Object... objects) {
        return ((JavascriptExecutor) driver.get()).executeScript(s, objects);
    }

    /* ==============================================================================
                                JavascriptExecutor Interface
       ============================================================================== */

    public Object executeAsyncScript(String s, Object... objects) {
        return ((JavascriptExecutor) driver.get()).executeAsyncScript(s, objects);
    }

    public Keyboard getKeyboard() {
        return ((HasInputDevices) driver.get()).getKeyboard();
    }

    /* ==============================================================================
                                  HasInputDevices Interface
       ============================================================================== */

    public Mouse getMouse() {
        return ((HasInputDevices) driver.get()).getMouse();
    }

    private void reportURL() {
        if (targetWebDriver.get().isRemote()) {
            log.info("Remote job url: {}", getJobUrl());
        }
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return ((TakesScreenshot) driver).getScreenshotAs(target);
    }
}
