package com.dev9.domain;

import com.dev9.annotation.ClassDriver;
import com.dev9.annotation.MethodDriver;
import com.dev9.driver.ThreadLocalWebDriver;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.WebDriver;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.lang.reflect.Field;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * @author <a href="mailto:Justin.Graham@dev9.com">Justin Graham</a>
 * @since 9/24/2015
 */
public class TestClass {

    @NotNull private final Object instance;
    @NotNull private final List<ITestNGMethod> methods;
    @Nullable private final Field webdriverField;

    public TestClass(@NotNull Object instance) {
        this.instance = instance;
        this.webdriverField = initWebDriverField(instance.getClass());
        this.methods = new ArrayList<>();
    }

    @NotNull
    @Contract("null -> fail")
    public TestClass addTestNGMethod(@NotNull ITestNGMethod method) {
        this.methods.add(method);
        return this;
    }

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
        field.setAccessible(true);
        return field;
    }

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
