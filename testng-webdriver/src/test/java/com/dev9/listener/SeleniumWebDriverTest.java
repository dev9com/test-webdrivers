package com.dev9.listener;

import com.dev9.annotation.ClassDriver;
import com.dev9.annotation.MethodDriver;
import com.dev9.driver.ThreadLocalWebDriver;
import mockit.*;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.*;

import static java.util.Collections.singletonList;
import static mockit.Deencapsulation.getField;
import static mockit.Deencapsulation.invoke;
import static mockit.Deencapsulation.setField;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:Justin.Graham@dev9.com">Justin Graham</a>
 * @since 9/8/2015
 */
@Test
@SuppressWarnings("unchecked")
public class SeleniumWebDriverTest {
    @Injectable Field field;
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
    public void testInitializeDriver(@Mocked final ThreadLocalWebDriver driver) throws Exception {
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
    public void testInitializeDriverException(@Mocked final ThreadLocalWebDriver driver) throws Exception {
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
    public void testSetDriverFieldAccessible() throws Exception {
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
                put(SeleniumWebDriverTest.class, singletonList(method));
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
            setField(seleniumWebDriver, "classListMap", new HashMap<Class, List<ITestNGMethod>>() {{
                put(SeleniumWebDriverTest.class, new ArrayList<ITestNGMethod>() {{
                    add(method);
                }});
            }});
            method.getMethodName(); result = "Name1";
            method.getInvocationCount(); result = 2;
        }};
        invoke(seleniumWebDriver, "endDriver", iTestResult);
    }

    @Test
    public void testQuitDriver(@Injectable final WebDriver driver) throws Exception {
        new Expectations() {{
            ((ThreadLocal<Field>) getField(seleniumWebDriver, "webDriverField")).set(field);
            field.get(any);
            result = driver;
        }};
        invoke(seleniumWebDriver, "quitDriver", iTestResult);
        new Verifications() {{
            driver.quit();
        }};
    }

    @Test
    public void testQuitDriverException() throws Exception {
        new Expectations() {{
            ((ThreadLocal<Field>) getField(seleniumWebDriver, "webDriverField")).set(field);
            field.get(any); result = new Exception();
        }};
        invoke(seleniumWebDriver, "quitDriver", iTestResult);
    }

    @Test
    public void testSetDriverNull() throws Exception {
        new Expectations() {{
            ((ThreadLocal<Field>) getField(seleniumWebDriver, "webDriverField")).set(field);
            field.set(any, any);
        }};
        invoke(seleniumWebDriver, "setDriverNull", iTestResult);
    }

    @Test
    public void testSetDriverNullException() throws Exception {
        new Expectations() {{
            ((ThreadLocal<Field>) getField(seleniumWebDriver, "webDriverField")).set(field);
            field.set(any, any); result = new Exception();
        }};
        invoke(seleniumWebDriver, "setDriverNull", iTestResult);
    }

    @Test
    public void testIsTestExcludedNull() throws Exception {
        new Expectations() {{
            ((ThreadLocal<List<String>>) getField(seleniumWebDriver, "excludedMethods")).set(null);
        }};
        assertThat((Boolean) invoke(seleniumWebDriver, "isTestExcluded", iTestResult)).isFalse();
    }

    @Test
    public void testIsTestExcludedNotContains() throws Exception {
        new Expectations() {{
            ((ThreadLocal<List<String>>) getField(seleniumWebDriver, "excludedMethods")).set(singletonList("Thing1"));
            iTestResult.getMethod().getMethodName(); result = "Thing2";
        }};
        assertThat((Boolean) invoke(seleniumWebDriver, "isTestExcluded", iTestResult)).isFalse();
    }

    @Test
    public void testIsTestExcluded() throws Exception {
        final String testName = "testName";
        new Expectations() {{
            ((ThreadLocal<List<String>>) getField(seleniumWebDriver, "excludedMethods")).set(singletonList(testName));
            iTestResult.getMethod().getMethodName(); result = testName;
        }};
        assertThat((Boolean) invoke(seleniumWebDriver, "isTestExcluded", iTestResult)).isTrue();
    }

    @Test
    public void testIsDriverRunningNull() throws Exception {
        new Expectations() {{
            ((ThreadLocal<Field>) getField(seleniumWebDriver, "webDriverField")).set(null);
        }};
        assertThat((Boolean) invoke(seleniumWebDriver, "isDriverRunning", iTestResult)).isFalse();
    }

    @Test
    public void testIsDriverRunning(@Injectable final WebDriver driver) throws Exception {
        new Expectations() {{
            ((ThreadLocal<Field>) getField(seleniumWebDriver, "webDriverField")).set(field);
            field.get(any); result = driver;
        }};
        assertThat((Boolean) invoke(seleniumWebDriver, "isDriverRunning", iTestResult)).isTrue();
        new Verifications() {{
            driver.getTitle();
        }};
    }

    @Test
    public void testIsDriverRunningException() throws Exception {
        new Expectations() {{
            ((ThreadLocal<Field>) getField(seleniumWebDriver, "webDriverField")).set(field);
            field.get(any); result = new Exception();
        }};
        assertThat((Boolean) invoke(seleniumWebDriver, "isDriverRunning", iTestResult)).isFalse();
    }

    @Test
    public void testGetRealTestClass() throws Exception {
        invoke(seleniumWebDriver, "getRealTestClass", iTestResult);
    }

    // Stub
    static class ClassWithNoFields {}

    @Test
    public void testCheckForWebDriverFieldEmpty() throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "getRealTestClass", iTestResult); result = ClassWithNoFields.class;
        }};
        invoke(seleniumWebDriver, "checkForWebDriverField", iTestResult);
    }

    // Stub
    static class ClassWithoutAnnotation {
        public WebDriver driver;
    }

    @Test
    public void testCheckForWebDriverFieldNoAnnotation() throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "getRealTestClass", iTestResult); result = ClassWithoutAnnotation.class;
        }};
        invoke(seleniumWebDriver, "checkForWebDriverField", iTestResult);
    }

    // Stub
    static class ClassWithClassDriver {
        @ClassDriver public WebDriver driver;
    }

    @Test
    public void testCheckForWebDriverFieldClassDriver() throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "getRealTestClass", iTestResult); result = ClassWithClassDriver.class;
            invoke(seleniumWebDriver, "setWebDriverField", new Class<?>[]{Field.class}, (Field) any);
        }};
        invoke(seleniumWebDriver, "checkForWebDriverField", iTestResult);
    }

    // Stub
    static class ClassWithMethodDriver {
        @MethodDriver public WebDriver driver;
    }

    @Test
    public void testCheckForWebDriverFieldMethodDriver() throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "getRealTestClass", iTestResult); result = ClassWithMethodDriver.class;
            invoke(seleniumWebDriver, "setWebDriverField", new Class<?>[]{Field.class}, (Field) any);
        }};
        invoke(seleniumWebDriver, "checkForWebDriverField", iTestResult);
    }

    @Test
    public void testSetWebDriverField() throws Exception {
        new Expectations(seleniumWebDriver) {{
            invoke(seleniumWebDriver, "setDriverFieldAccessible");
        }};
        invoke(seleniumWebDriver, "setWebDriverField", field);
    }

    @Test
    public void testSetClassListMapEmpty() throws Exception {
        new Expectations() {{
            iTestContext.getAllTestMethods(); result = new ITestNGMethod[0];
        }};
        invoke(seleniumWebDriver, "setClassListMap", iTestContext);
    }

    @Test
    public void testSetClassListMapNullMethods(@Injectable final ITestNGMethod method) throws Exception {
        new Expectations() {{
            iTestContext.getAllTestMethods(); result = new ITestNGMethod[]{method};
            method.getRealClass(); result = this.getClass();
            setField(seleniumWebDriver, "classListMap",
                    new HashMap<Class, List<ITestNGMethod>>(){{ put(this.getClass(), null); }});
        }};
        invoke(seleniumWebDriver, "setClassListMap", iTestContext);
    }

    @Test
    public void testSetClassListMap(@Injectable final ITestNGMethod method) throws Exception {
        new Expectations() {{
            iTestContext.getAllTestMethods(); result = new ITestNGMethod[]{method};
            method.getRealClass(); result = this.getClass();
            setField(seleniumWebDriver, "classListMap",
                    new HashMap<Class, List<ITestNGMethod>>(){{ put(this.getClass(), singletonList(method)); }});
        }};
        invoke(seleniumWebDriver, "setClassListMap", iTestContext);
    }

    @Test
    public void testGetTestDescriptionNull() throws Exception {
        new Expectations() {{
            iTestResult.getMethod().getDescription(); result = null;
        }};
        assertThat((String) invoke(seleniumWebDriver, "getTestDescription", iTestResult)).isNull();
    }

    @Test
    public void testGetTestDescriptionEmpty() throws Exception {
        new Expectations() {{
            iTestResult.getMethod().getDescription(); result = "";
        }};
        assertThat((String) invoke(seleniumWebDriver, "getTestDescription", iTestResult)).isNull();
    }

    @Test
    public void testGetTestDescription() throws Exception {
        final String description = "description";
        new Expectations() {{
            iTestResult.getMethod().getDescription(); result = description;
        }};
        assertThat((String) invoke(seleniumWebDriver, "getTestDescription", iTestResult)).isEqualTo(description);
    }

    @Test
    public void testIsClassDriverNull() throws Exception {
        new Expectations() {{
            ((ThreadLocal<Field>) getField(seleniumWebDriver, "webDriverField")).set(null);
        }};
        assertThat((Boolean) invoke(seleniumWebDriver, "isClassDriver")).isFalse();
    }

    @Test
    public void testIsClassDriverFalse() throws Exception {
        new Expectations() {{
            ((ThreadLocal<Field>) getField(seleniumWebDriver, "webDriverField")).set(field);
            field.isAnnotationPresent(ClassDriver.class); result = false;
        }};
        assertThat((Boolean) invoke(seleniumWebDriver, "isClassDriver")).isFalse();
    }

    @Test
    public void testIsClassDriver() throws Exception {
        new Expectations() {{
            ((ThreadLocal<Field>) getField(seleniumWebDriver, "webDriverField")).set(field);
            field.isAnnotationPresent(ClassDriver.class); result = true;
        }};
        assertThat((Boolean) invoke(seleniumWebDriver, "isClassDriver")).isTrue();
    }
}