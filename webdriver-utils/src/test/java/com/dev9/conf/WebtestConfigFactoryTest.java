package com.dev9.conf;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import mockit.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import static mockit.Deencapsulation.invoke;
import static mockit.Deencapsulation.setField;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:Justin.Graham@dev9.com">Justin Graham</a>
 * @since 9/1/2015
 */
@Test
public class WebtestConfigFactoryTest {

    @Injectable Config config;
    @Injectable ConcurrentHashMap<Class, Config> map;
    @Injectable File file;
    @Tested WebtestConfigFactory factory;

    @BeforeMethod
    public void setUp() throws Exception {
        setField(WebtestConfigFactory.class, "DEFAULT_CONFIG", config);
        setField(WebtestConfigFactory.class, "CLASS_CONFIG", map);
    }

    @Test
    public void testGetConfig() throws Exception {
        assertThat(WebtestConfigFactory.getConfig()).isEqualTo(config);
    }

    @Test
    public void testGetConfigDoesNotContain() throws Exception {
        new Expectations(factory) {{
            map.containsKey(WebtestConfigFactoryTest.class); result = false;
            invoke(WebtestConfigFactory.class, "buildConfigForClass",
                    new Class<?>[]{Class.class}, WebtestConfigFactoryTest.class);
            map.get(WebtestConfigFactoryTest.class); result = null;
        }};
        assertThat(WebtestConfigFactory.getConfig(WebtestConfigFactoryTest.class)).isNull();
    }

    @Test
    public void testGetConfigDoesContain() throws Exception {
        new Expectations(WebtestConfigFactory.class) {{
            map.containsKey(WebtestConfigFactoryTest.class); result = true;
            map.get(WebtestConfigFactoryTest.class); result = null;
        }};
        assertThat(WebtestConfigFactory.getConfig(WebtestConfigFactoryTest.class)).isNull();
    }

    @Test
    public void testBuildConfigForClassNotNull(@Mocked final ConfigFactory configFactory) throws Exception {
        new Expectations(factory) {{
            invoke(WebtestConfigFactory.class, "getClassConfigFile",
                    new Class<?>[]{Class.class}, WebtestConfigFactoryTest.class); result = file;
            invoke(WebtestConfigFactory.class, "addProfileToConfig",
                    new Class<?>[]{Config.class}, config); result = config;
        }};
        invoke(WebtestConfigFactory.class, "buildConfigForClass",
                new Class<?>[]{Class.class}, WebtestConfigFactoryTest.class);
        new Verifications() {{
            map.put(WebtestConfigFactoryTest.class, config);
        }};
    }

    @Test
    public void testBuildConfigForClassNull(@Mocked final ConfigFactory configFactory) throws Exception {
        new Expectations(factory) {{
            invoke(WebtestConfigFactory.class, "getClassConfigFile",
                    new Class<?>[]{Class.class}, WebtestConfigFactoryTest.class); result = null;
            WebtestConfigFactory.getConfig(); result = config;
        }};
        invoke(WebtestConfigFactory.class, "buildConfigForClass",
                new Class<?>[]{Class.class}, WebtestConfigFactoryTest.class);
        new Verifications() {{
            map.put(WebtestConfigFactoryTest.class, config);
        }};
    }

//    @Test
//    public void testGetClassConfigFileNotNull() throws Exception {
//        new Expectations() {{
//            URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
//            File file = new File(WebtestConfigFactoryTest.class.getSimpleName());
//            invoke(sysloader, "addURL", new URL("file", ));
//            setField(WebtestConfigFactory.class, "CONF_EXTENSION", "");
//        }};
//        this.getClass().getClassLoader().getResource("");
//        assertThat(invoke(WebtestConfigFactory.class, "getClassConfigFile",
//                new Class<?>[]{Class.class}, WebtestConfigFactoryTest.class)).isNotNull();
//    }
}