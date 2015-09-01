package com.dev9.rule;

import java.util.List;
import java.util.Set;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import com.dev9.conf.SauceLabsCredentials;
import com.dev9.driver.TargetWebDriver;
import com.dev9.sauce.SauceREST;
import com.dev9.sauce.SauceUtils;

import lombok.extern.log4j.Log4j2;


/**
 * User: yurodivuie
 * Date: 5/23/13
 * Time: 9:12 AM
 */
@Log4j2
public class DriverClassRule extends ExternalResource implements WebDriver, JavascriptExecutor, HasInputDevices {

    /**
     * Stored as a threadLocal instead of local so that this can be added to a base class and create separate drivers
     * for each thread.  ClassRules are static, so a local variable would create a single driver for all test classes
     * that inherited from the base.
     */
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();
    private static ThreadLocal<Boolean> failed = new ThreadLocal<Boolean>();
    private static ThreadLocal<Class> testClass = new ThreadLocal<Class>();
    private static ThreadLocal<TargetWebDriver> targetWebDriver = new ThreadLocal<TargetWebDriver>();

    private SauceREST sauceREST;

    public DriverClassRule() {
    }

    @Override
    public Statement apply(Statement base, Description description) {
        initialize(description.getTestClass());
        return super.apply(base, description);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void before() throws Throwable {

        super.before();    //To change body of overridden methods use File | Settings | File Templates.

        setFailed(false);

        if (getDriver() == null) {
            buildDriver();
        }
    }

    @Override
    protected void after() {
        super.after();    //To change body of overridden methods use File | Settings | File Templates.

        if (getTargetWebDriver().isRemote()) {
            reportFinalStatus();
        }

        destroyDriver();
    }

    public WebDriver unwrapDriver() {
        return getDriver();
    }

    public void rebuildDriver() {
        destroyDriver();
        buildDriver();
    }

    public void markAsFailed() {
        setFailed(true);
    }

    public void logInContext(String s) {
        SauceUtils.logInContext(getDriver(), s);
    }

    public void get(String s) {
        getDriver().get(s);
    }

    public String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }

    public String getTitle() {
        return getDriver().getTitle();
    }

    public List<WebElement> findElements(By by) {
        return getDriver().findElements(by);
    }

    public WebElement findElement(By by) {
        return getDriver().findElement(by);
    }

    public String getPageSource() {
        return getDriver().getPageSource();
    }

    public void close() {
        getDriver().close();
    }

    public void quit() {
        getDriver().quit();
    }

    public Set<String> getWindowHandles() {
        return getDriver().getWindowHandles();
    }

    public String getWindowHandle() {
        return getDriver().getWindowHandle();
    }

    public TargetLocator switchTo() {
        return getDriver().switchTo();
    }

    public Navigation navigate() {
        return getDriver().navigate();
    }

    public Options manage() {
        return getDriver().manage();
    }

    public Keyboard getKeyboard() {
        return ((HasInputDevices) getDriver()).getKeyboard();
    }

    public Mouse getMouse() {
        return ((HasInputDevices) getDriver()).getMouse();
    }

    public Object executeScript(String s, Object... objects) {
        return ((JavascriptExecutor) getDriver()).executeScript(s, objects);
    }

    public Object executeAsyncScript(String s, Object... objects) {
        return ((JavascriptExecutor) getDriver()).executeAsyncScript(s, objects);
    }

    public Boolean hasFailed() {
        return failed.get();
    }

    public String getJobUrl() {
        return SauceUtils.getJobUrl(getDriver());
    }

    public String getJobId() {
        return SauceUtils.getJobId(getDriver());
    }

    public TargetWebDriver getTargetWebDriver() {
        return targetWebDriver.get();
    }

    private void initialize(Class testClass) {
        setTestClass(testClass);
        setTargetWebDriver(new TargetWebDriver(testClass));
        setSauceREST();
    }

    private void setFailed(Boolean hasFailed) {
        failed.set(hasFailed);
    }

    private WebDriver getDriver() {
        return driver.get();
    }

    private void setDriver(WebDriver newDriver) {
        driver.set(newDriver);
    }

    private void setTestClass(Class newTestClass) {
        testClass.set(newTestClass);
    }

    private Class getTestClass() {
        return testClass.get();
    }

    private void setTargetWebDriver(TargetWebDriver target) {
        targetWebDriver.set(target);
    }

    private void reportFailure() {
        failed.set(true);
    }

    private void buildDriver() {
        setDriver(getTargetWebDriver().build());
        reportURL();
    }

    private void setSauceREST() {
        if (getTargetWebDriver().isRemote()) {
            sauceREST = new SauceREST(SauceLabsCredentials.getUser(), SauceLabsCredentials.getKey());
        }
    }

    private void reportURL() {
        if (getTargetWebDriver().isRemote()) {
            log.info("Remote job url: {}", getJobUrl());
        }
    }

    private void destroyDriver() {
        try {
            getDriver().quit();
            setDriver(null);
        } catch (WebDriverException exception) {
            log.warn("Exception while quitting driver during driver rebuild.", exception);
        }
    }

    private void reportFinalStatus() {
        if (hasFailed()) {
            sauceREST.jobFailed(getJobId());
        } else {
            sauceREST.jobPassed(getJobId());
        }
    }
}
