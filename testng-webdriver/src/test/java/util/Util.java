package util;

import org.testng.annotations.DataProvider;

/**
 * The purpose of this class is to provide utility functions during testing.
 *
 * @author <a href="mailto:Justin.Graham@dev9.com">Justin Graham</a>
 * @since 11/4/13
 */
public final class Util {
    public static final String HTTP_PROTOCOL = "http://";
    public static final String YAHOO_DOMAIN = "www.yahoo.com/";
    public static final String GOOGLE_DOMAIN = "www.google.com/";
    public static final String GITHUB_DOMAIN = "github.com/";

    public static void sleep() throws InterruptedException {
        Thread.sleep(2000);
    }

    @DataProvider(parallel = true)
    public static Object[][] dataProvider() {
        return new Object[][] {
                {HTTP_PROTOCOL + GOOGLE_DOMAIN, 1},
                {HTTP_PROTOCOL + YAHOO_DOMAIN, 2},
                {"http://www.wikipedia.org/", 3},
                {HTTP_PROTOCOL + GITHUB_DOMAIN, 4}
        };
    }
}
