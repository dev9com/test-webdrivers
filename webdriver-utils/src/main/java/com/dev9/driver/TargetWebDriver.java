package com.dev9.driver;

import java.lang.reflect.Constructor;
import java.util.UUID;
import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import com.dev9.conf.SauceLabsCredentials;
import com.dev9.conf.WebtestConfigFactory;
import com.google.common.base.Throwables;
import com.typesafe.config.Config;

import lombok.extern.log4j.Log4j2;


/**
 * User: yurodivuie
 * Date: 6/2/13
 * Time: 4:26 PM
 */
@Log4j2
public class TargetWebDriver {

    /**
     * Unique identifier for this job run. Global value for entire suite
     * execution (i.e. corresponds to a single complete mvn clean verify)
     */
    private static final String UNIQUE_ID = String.valueOf(UUID.randomUUID());

    private static final String WEBDRIVER_BROWSER_PATH = "webdriver.browser";

    private static final String WEBDRIVER_TYPE_PATH = "webdriver.type";

    private static final String WEBDRIVER_VERSION_PATH = "webdriver.version";

    private static final String WEBDRIVER_PLATFORM_PATH = "webdriver.platform";

    private static final String WEBDRIVER_CAPABILITIES_PATH = "webdriver.capabilities";

    private static final String SAUCELABS_TIMEOUT_PATH = "saucelabs.timeout";

    private final Class testClass;

    private final Browser browser;

    private final Type type;

    private final String version;

    private final Platform platform;

    private final DesiredCapabilities capabilities;

    public TargetWebDriver(Class testClass, Browser browser, Type type, String version, Platform platform, DesiredCapabilities capabilities) {
        this.testClass = testClass;
        this.browser = browser;
        this.type = type;
        this.version = version;
        this.platform = platform;
        this.capabilities = capabilities;
    }

    public TargetWebDriver(Class testClass) {
        Config config = WebtestConfigFactory.getConfig(testClass);

        this.testClass = testClass;
        this.browser = getBrowserFrom(config);
        this.type = getTypeFrom(config);

        if (isLocal()) {
            this.version = "";
            this.platform = Platform.getCurrent();
        } else {
            this.version = getVersionFrom(config);
            this.platform = getPlatformFrom(config);
        }

        this.capabilities = getCapabilitiesFrom(config);
    }

    public Class getTestClass() {
        return testClass;
    }

    public Browser getBrowser() {
        return browser;
    }

    public Type getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public Platform getPlatform() {
        return platform;
    }

    public DesiredCapabilities getCapabilities() {
        return capabilities;
    }

    public Boolean isLocal() {
        return type.equals(Type.LOCAL);
    }

    public Boolean isRemote() {
        return type.equals(Type.REMOTE);
    }

    public WebDriver build() {
        WebDriver driver;
        if (isLocal()) {
            driver = buildLocal();
        }
        else {
            driver = buildRemote();
        }
        return driver;
    }

    private WebDriver buildLocal() {
        WebDriver driver = null;
        try {
            log.debug("building {} for {}", browser.getDriverClass(), getTestClass());
            final Class<?> driverClass = browser.getDriverClass();
            Constructor constructor = driverClass.getDeclaredConstructor(Capabilities.class);
            driver = (WebDriver) constructor.newInstance(getCapabilities());
        } catch (Exception ex) {
            Throwables.propagate(ex);
        }
        return driver;
    }

    private WebDriver buildRemote() {
        log.debug("building remote {} for {}", browser.toString(), getTestClass());
        return new RemoteWebDriver(SauceLabsCredentials.getConnectionLocation(), getCapabilities());
    }

    private Browser getBrowserFrom(Config config) {
        Browser result;
        try {
            result = Browser.fromJson(config.getString(WEBDRIVER_BROWSER_PATH));
        }
        catch (IllegalArgumentException ex) {
            log.error("Invalid browser.  Must be one of {}", ArrayUtils.toString(Browser.values()));
            throw ex;
        }
        return result;
    }

    private Type getTypeFrom(Config config) {
        Type result;
        try {
            result = Type.fromJson(config.getString(WEBDRIVER_TYPE_PATH));
        }
        catch (IllegalArgumentException ex) {
            log.error("Invalid type.  Must be one of {}", ArrayUtils.toString(Type.values()));
            throw ex;
        }
        return result;
    }

    private String getVersionFrom(Config config) {
        return config.getString(WEBDRIVER_VERSION_PATH);
    }

    private Platform getPlatformFrom(Config config) {
        Platform result;
        try {
            result = Platform.valueOf(config.getString(WEBDRIVER_PLATFORM_PATH).toUpperCase());
        }
        catch (IllegalArgumentException ex) {
            log.error("Invalid platform.  Must be one of {}", ArrayUtils.toString(Platform.values()));
            throw ex;
        }
        return result;
    }

    private DesiredCapabilities getCapabilitiesFrom(Config config) {
        DesiredCapabilities newCapabilities = buildDefaultCapabilities(config);
        if (config.hasPath(WEBDRIVER_CAPABILITIES_PATH)) {
            newCapabilities.merge(new DesiredCapabilities(config.getObject(WEBDRIVER_CAPABILITIES_PATH).unwrapped()));
        }
        return newCapabilities;
    }

    private DesiredCapabilities buildDefaultCapabilities(Config config) {
        DesiredCapabilities defaultCapabilities = new DesiredCapabilities();
        defaultCapabilities.setJavascriptEnabled(true);
        if (isRemote()) {
            defaultCapabilities.setBrowserName(getBrowser().name().toLowerCase());
            defaultCapabilities.setVersion(getVersion());
            defaultCapabilities.setPlatform(getPlatform());
            defaultCapabilities.setCapability("name", getTestClass().getSimpleName());
            defaultCapabilities.setCapability("build", UNIQUE_ID);
            defaultCapabilities.setCapability("command-timeout", config.getNumber(SAUCELABS_TIMEOUT_PATH).toString());    //default is 300 - may need to revisit.
        }
        return defaultCapabilities;
    }
}
