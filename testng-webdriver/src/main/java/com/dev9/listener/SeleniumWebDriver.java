package com.dev9.listener;

import java.util.HashMap;
import java.util.Map;

import com.dev9.domain.TestClass;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import static java.util.Arrays.stream;


/**
 * The purpose of this class is to start a new WebDriver instance on an annotated WebDriver variable.
 *
 * @author <a href="mailto:Justin.Graham@dev9.com">Justin Graham</a>
 * @since 10/1/13
 */
public class SeleniumWebDriver extends TestListenerAdapter {

    private static final Map<Object, TestClass> testClasses = new HashMap<>();

    @Override
    public void onStart(ITestContext testContext) {
        super.onStart(testContext);

        stream(testContext.getAllTestMethods())
                .forEach(method -> {
                    final Object instance = method.getInstance();
                    if (testClasses.containsKey(instance)) {
                        testClasses.get(instance).addTestNGMethod(method);
                    } else {
                        final TestClass testClass = new TestClass(instance).addTestNGMethod(method);
                        testClasses.put(instance, testClass);
                    }
                });
    }

    @Override
    public void onTestStart(ITestResult tr) {
        super.onTestStart(tr);
        final TestClass currentClass = testClasses.get(tr.getInstance());
        final String methodName = tr.getMethod().getMethodName();

        if (currentClass.isEnabled(methodName)) {
            currentClass.startWebDriver(getTestDescription(tr));
        } else {
            currentClass.killDriver(methodName);
        }
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        stopDriver(tr);
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        stopDriver(tr);
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        stopDriver(tr);
    }

    private void stopDriver(ITestResult tr) {
        final TestClass testClass = testClasses.get(tr.getInstance());
        testClass.killDriver(tr.getMethod().getMethodName());
    }

    /**
     * Sets the testDescription to the description on the testMethod
     *
     * @param tr ITestResult from TestNG
     */
    private String getTestDescription(ITestResult tr) {
        String description = tr.getMethod().getDescription();
        return (description != null && !description.equals("")) ? description : null;
    }
}