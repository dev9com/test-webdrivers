package com.dev9.domain;

import com.dev9.annotation.ClassDriver;
import com.dev9.annotation.MethodDriver;
import com.dev9.driver.ThreadLocalWebDriver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.WebDriver;
import org.testng.ITestNGMethod;

import java.lang.reflect.Field;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * @author <a href="mailto:Justin.Graham@dev9.com">Justin Graham</a>
 * @since 9/24/2015
 */
public class TestClass {

    @NotNull private final Object instance;
    @NotNull private final List<ITestNGMethod> methods;
    @Nullable private final Field webdriverField;

    /**
     * Builds a TestClass object from the given TestNG IClass instance.
     *
     * @param instance A running instance of a test class from TestNG.
     */
    public TestClass(@NotNull Object instance) {
        this.instance = instance;
        this.webdriverField = initWebDriverField(instance.getClass());
        this.methods = new ArrayList<>();
    }

    /**
     * Adds an ITestNGMethod as included in this test class.
     *
     * @param method The ITestNGMethod that belongs to this class.
     * @return This TestClass instance with the new method added.
     */
    @NotNull
    public TestClass addTestNGMethod(@NotNull ITestNGMethod method) {
        this.methods.add(method);
        return this;
    }

    /**
     * Will initialize a new instance of a ThreadLocalWebDriver if a webdriver field was found, we are running
     * using the MethodDriver annotation, or the driver is not already running with the ClassDriver annotation.
     *
     * @param testDescription The description to print out during the test.
     */
    public void startWebDriver(@Nullable String testDescription) {
        if (webdriverField != null) {
            // Do not restart the browser if the ClassDriver is already running
            if (webdriverField.isAnnotationPresent(ClassDriver.class) && isDriverRunning()) {
                return;
            }

            try {
                webdriverField.set(instance, new ThreadLocalWebDriver(instance.getClass(), testDescription));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if the current test class or test method is enabled or excluded.
     *
     * @param methodName The currently queued method.
     * @return True if a new WebDriver should be started.
     */
    public boolean isEnabled(@NotNull String methodName) {
        if (webdriverField == null) return false;
        else if (webdriverField.isAnnotationPresent(ClassDriver.class)) {
            return webdriverField.getAnnotation(ClassDriver.class).enabled();
        } else {
            final MethodDriver annotation = webdriverField.getAnnotation(MethodDriver.class);
            return annotation.enabled() &&
                    !asList(annotation.excludeMethods()).contains(methodName);
        }
    }

    /**
     * If the current test class is a MethodDriver call quit() on the WebDriver, else if the current
     * test class is a ClassDriver it checks if all methods in the test class have finished before
     * quitting the WebDriver.
     *
     * @param methodName The recently finished method.
     */
    public void killDriver(@NotNull String methodName) {
        if (webdriverField != null) {
            if (webdriverField.isAnnotationPresent(ClassDriver.class)) {
                Optional<ITestNGMethod> iTestNGMethod = methods.stream()
                        .filter(method -> method.getMethodName().equals(methodName)).findFirst();

                if (iTestNGMethod.isPresent()) {
                    final ITestNGMethod method = iTestNGMethod.get();
                    final int invocationCount = method.getInvocationCount();
                    if (invocationCount > 1) {
                        method.setInvocationCount(invocationCount - 1);
                    } else {
                        methods.remove(method);
                    }
                }

                if (!methods.isEmpty()) {
                    return;
                }
            }

            try {
                ((WebDriver) webdriverField.get(instance)).quit();
                webdriverField.set(instance, null);
            } catch (Exception e) { /* Ignore stopping the driver messages */ }
        }
    }

    /**
     * Scans the provided class for the ClassDriver or MethodDriver annotation.
     *
     * @param clazz The test class to search for the annotation.
     * @return The field annotated with ClassDriver or MethodDriver.
     * @throws IllegalStateException If more than 1 annotation is found in the current class or the annotation exists
     * on a non-WebDriver variable
     */
    @Nullable
    private Field initWebDriverField(@NotNull Class clazz) {
        final List<Field> fields = stream(clazz.getDeclaredFields())
                .filter(field ->
                        field.isAnnotationPresent(ClassDriver.class) || field.isAnnotationPresent(MethodDriver.class))
                .collect(toList());

        if (fields.size() > 1) {
            throw new IllegalStateException("Test classes may only have a single webdriver annotation. Error in: "
                    + clazz.getCanonicalName());
        } else if (fields.isEmpty()) {
            return null;
        }

        final Field field = fields.get(0);

        if (!field.getType().isAssignableFrom(WebDriver.class)) {
            throw new IllegalStateException("WebDriver annotation may only be used on WebDriver fields. Error in: "
                    + clazz.getCanonicalName());
        }

        field.setAccessible(true);
        return field;
    }

    /**
     * Checks if the WebDriver contained in this test class is active.
     *
     * @return True if the WebDriver is running.
     */
    private boolean isDriverRunning() {
        if (webdriverField == null) return false;

        // Horrid way to check if .quit() was called on the driver.
        try {
            WebDriver driver = (WebDriver) webdriverField.get(instance);
            driver.getTitle();
            return true;
        } catch (Exception e) { return false; }
    }
}
