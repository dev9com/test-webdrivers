package com.dev9.conf;

import com.typesafe.config.Config;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static mockit.Deencapsulation.invoke;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author <a href="mailto:Justin.Graham@dev9.com">Justin Graham</a>
 * @since 8/25/2015
 */
@Test
public class SauceLabsCredentialsTest {
    private final String value = "value";

    @Mocked Config config;
    @Tested SauceLabsCredentials sauceLabsCredentials;

    @Test
    public void testGetUser() throws Exception {
        new Expectations(sauceLabsCredentials) {{
            invoke(SauceLabsCredentials.class, "safelyGetConfigString",
                    new Class[]{Config.class, String.class}, any, anyString); result = value;
        }};
        assertThat(SauceLabsCredentials.getUser()).isEqualTo(value);
    }

    @Test
    public void testGetKey() throws Exception {
        new Expectations(sauceLabsCredentials) {{
            invoke(SauceLabsCredentials.class, "safelyGetConfigString",
                    new Class[]{Config.class, String.class}, any, anyString); result = value;
        }};
        assertThat(SauceLabsCredentials.getKey()).isEqualTo(value);
    }

    @Test
    public void testGetServer() throws Exception {
        new Expectations(sauceLabsCredentials) {{
            invoke(SauceLabsCredentials.class, "safelyGetConfigString",
                    new Class[]{Config.class, String.class}, any, anyString); result = value;
        }};
        assertThat(SauceLabsCredentials.getServer()).isEqualTo(value);
    }

    @Test
    public void testGetConnectionLocation(@Mocked URL url) throws Exception {
        new Expectations(sauceLabsCredentials) {{
            invoke(SauceLabsCredentials.class, "getUser"); result = "user";
            invoke(SauceLabsCredentials.class, "getKey"); result = "key";
            invoke(SauceLabsCredentials.class, "getServer"); result = "server";
            new URL("http://user:key@server");
        }};
        assertThat(SauceLabsCredentials.getConnectionLocation()).isNotNull();
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testGetConnectionLocationException(@Mocked URL url) throws Exception {
        new Expectations(sauceLabsCredentials) {{
            invoke(SauceLabsCredentials.class, "getUser"); result = "user";
            invoke(SauceLabsCredentials.class, "getKey"); result = "key";
            invoke(SauceLabsCredentials.class, "getServer"); result = "server";
            new URL("http://user:key@server"); result = new MalformedURLException();
        }};
        SauceLabsCredentials.getConnectionLocation();
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testSafelyGetConfigStringNoPath() throws Exception {
        new Expectations() {{
            config.hasPath(anyString); result = false;
        }};
        invoke(sauceLabsCredentials, "safelyGetConfigString", config, "path");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testSafelyGetConfigStringEmptyPath() throws Exception {
        new Expectations() {{
            config.hasPath(anyString); result = true;
            config.getString(anyString); result = "";
        }};
        invoke(sauceLabsCredentials, "safelyGetConfigString", config, "path");
    }

    @Test
    public void testSafelyGetConfigString() throws Exception {
        final String path = "path";
        new Expectations() {{
            config.hasPath(path); result = true;
            config.getString(path); result = path;
        }};
        final String result = invoke(sauceLabsCredentials, "safelyGetConfigString", config, path);
        assertThat(result).isEqualTo(path);
    }
}