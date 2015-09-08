package com.dev9.listener;

import com.dev9.driver.ThreadLocalWebDriver;
import mockit.*;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.*;

import static mockit.Deencapsulation.getField;
import static mockit.Deencapsulation.invoke;
import static mockit.Deencapsulation.setField;

/**
 * @author <a href="mailto:Justin.Graham@dev9.com">Justin Graham</a>
 * @since 9/8/2015
 */
@Test
@SuppressWarnings("unchecked")
public class SeleniumWebDriverTest {
    @Injectable ITestContext iTestContext;
    @Injectable ITestResult iTestResult;
    @Tested SeleniumWebDriver seleniumWebDriver;

    @Test
    public void testOnStart() throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "setClassListMap", iTestContext);
        }};
        seleniumWebDriver.onStart(iTestContext);
    }

    @Test
    public void testOnTestStartNotDriverTest() throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "checkForWebDriverField", iTestResult);
        }};
        seleniumWebDriver.onTestStart(iTestResult);
    }

    @Test
    public void testOnTestStart() throws Exception {
        new Expectations(seleniumWebDriver) {{
            ((ThreadLocal<Boolean>) getField(seleniumWebDriver, "isDriverTest")).set(true);
            invoke(seleniumWebDriver, "checkForWebDriverField", iTestResult);
            invoke(seleniumWebDriver, "startDriver", iTestResult);
        }};
        seleniumWebDriver.onTestStart(iTestResult);
    }

    @Test
    public void testOnTestSuccess() throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "endDriver", iTestResult);
        }};
        seleniumWebDriver.onTestSuccess(iTestResult);
    }

    @Test
    public void testOnTestFailure() throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "endDriver", iTestResult);
        }};
        seleniumWebDriver.onTestFailure(iTestResult);
    }

    @Test
    public void testOnTestSkipped() throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "endDriver", iTestResult);
        }};
        seleniumWebDriver.onTestSkipped(iTestResult);
    }

    @Test
    public void testStartDriverNotDriverTest() throws Exception {
        invoke(seleniumWebDriver, "startDriver", iTestResult);
    }

    @Test
    public void testStartDriverExcluded() throws Exception {
        new Expectations(seleniumWebDriver) {{
            ((ThreadLocal<Boolean>) getField(seleniumWebDriver, "isDriverTest")).set(true);
            invoke(seleniumWebDriver, "isTestExcluded", iTestResult); result = true;
            invoke(seleniumWebDriver, "setDriverNull", iTestResult);
        }};
        invoke(seleniumWebDriver, "startDriver", iTestResult);
    }

    @Test
    public void testStartDriverIsRunning() throws Exception {
        new Expectations(seleniumWebDriver) {{
            ((ThreadLocal<Boolean>) getField(seleniumWebDriver, "isDriverTest")).set(true);
            invoke(seleniumWebDriver, "isTestExcluded", iTestResult); result = false;
            invoke(seleniumWebDriver, "isDriverRunning", iTestResult); result = true;
        }};
        invoke(seleniumWebDriver, "startDriver", iTestResult);
    }

    @Test
    public void testStartDriver() throws Exception {
        new Expectations(seleniumWebDriver) {{
            ((ThreadLocal<Boolean>) getField(seleniumWebDriver, "isDriverTest")).set(true);
            invoke(seleniumWebDriver, "isTestExcluded", iTestResult); result = false;
            invoke(seleniumWebDriver, "isDriverRunning", iTestResult); result = false;
            invoke(seleniumWebDriver, "initializeDriver", iTestResult);
        }};
        invoke(seleniumWebDriver, "startDriver", iTestResult);
    }

    @Test
    public void testInitializeDriver(
            @Injectable final Field field, @Mocked final ThreadLocalWebDriver driver) throws Exception {
        final Object key = this;
        new Expectations(seleniumWebDriver) {{
            ((ThreadLocal<Field>) getField(seleniumWebDriver, "webDriverField")).set(field);
            iTestResult.getInstance(); result = key;
            invoke(seleniumWebDriver, "getRealTestClass", iTestResult); result = this.getClass();
            invoke(seleniumWebDriver, "getTestDescription", iTestResult); result = "description";
        }};
        invoke(seleniumWebDriver, "initializeDriver", iTestResult);
        new Verifications() {{
            field.set(key, any);
        }};
    }

    @Test
    public void testInitializeDriverException(
            @Injectable final Field field, @Mocked final ThreadLocalWebDriver driver) throws Exception {
        final Object key = this;
        new Expectations(seleniumWebDriver) {{
            ((ThreadLocal<Field>) getField(seleniumWebDriver, "webDriverField")).set(field);
            field.set(any, any); result = new IllegalAccessException();
            iTestResult.getInstance(); result = key;
            invoke(seleniumWebDriver, "getRealTestClass", iTestResult); result = this.getClass();
            invoke(seleniumWebDriver, "getTestDescription", iTestResult); result = "description";
        }};
        invoke(seleniumWebDriver, "initializeDriver", iTestResult);
    }

    @Test
    public void testSetDriverFieldAccessible(@Injectable final Field field) throws Exception {
        new Expectations(seleniumWebDriver) {{
            ((ThreadLocal<Field>) getField(seleniumWebDriver, "webDriverField")).set(field);
        }};
        invoke(seleniumWebDriver, "setDriverFieldAccessible");
        new Verifications() {{
            field.setAccessible(true);
        }};
    }

    @Test
    public void testEndDriverNotClass() throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "isClassDriver"); result = false;
            invoke(seleniumWebDriver, "quitDriver", iTestResult);
        }};
        invoke(seleniumWebDriver, "endDriver", iTestResult);
    }

    @Test
    public void testEndDriverNoRemainingMethods(@Injectable final ITestNGMethod method) throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "isClassDriver"); result = true;
            iTestResult.getMethod(); result = method;
            method.getRealClass(); result = SeleniumWebDriverTest.class;
            setField(seleniumWebDriver, "classListMap", new HashMap<Class, List<ITestNGMethod>>(){{
                put(SeleniumWebDriverTest.class, Collections.EMPTY_LIST);
            }});
            invoke(seleniumWebDriver, "quitDriver", iTestResult);
        }};
        invoke(seleniumWebDriver, "endDriver", iTestResult);
    }

    @Test
    public void testEndDriverNotMatchingMethod(@Injectable final ITestNGMethod method) throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "isClassDriver"); result = true;
            iTestResult.getMethod(); result = method;
            method.getRealClass(); result = SeleniumWebDriverTest.class;
            setField(seleniumWebDriver, "classListMap", new HashMap<Class, List<ITestNGMethod>>(){{
                put(SeleniumWebDriverTest.class, Collections.singletonList(method));
            }});
            method.getMethodName(); returns("Name1", "Name2");
        }};
        invoke(seleniumWebDriver, "endDriver", iTestResult);
    }

    @Test
    public void testEndDriverCount1(@Injectable final ITestNGMethod method) throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "isClassDriver"); result = true;
            iTestResult.getMethod(); result = method;
            method.getRealClass(); result = SeleniumWebDriverTest.class;
            setField(seleniumWebDriver, "classListMap", new HashMap<Class, List<ITestNGMethod>>(){{
                put(SeleniumWebDriverTest.class, new ArrayList<ITestNGMethod>(){{ add(method); }});
            }});
            method.getMethodName(); result = "Name1";
            method.getInvocationCount(); result = 1;
            invoke(seleniumWebDriver, "quitDriver", iTestResult);
        }};
        invoke(seleniumWebDriver, "endDriver", iTestResult);
    }

    @Test
    public void testEndDriver(@Injectable final ITestNGMethod method) throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "isClassDriver"); result = true;
            iTestResult.getMethod(); result = method;
            method.getRealClass(); result = SeleniumWebDriverTest.class;
            setField(seleniumWebDriver, "classListMap", new HashMap<Class, List<ITestNGMethod>>(){{
                put(SeleniumWebDriverTest.class, new ArrayList<ITestNGMethod>(){{ add(method); }});
            }});
            method.getMethodName(); result = "Name1";
            method.getInvocationCount(); result = 2;
        }};
        invoke(seleniumWebDriver, "endDriver", iTestResult);
    }
}