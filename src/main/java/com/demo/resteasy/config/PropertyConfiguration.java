package com.demo.resteasy.config;

import lombok.SneakyThrows;

import javax.enterprise.context.ApplicationScoped;
import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

@ApplicationScoped
public class PropertyConfiguration {

    private static Properties properties;

    /**
     * https://www.baeldung.com/java-properties
     *
     * 多线程并发重复问题没有处理
     * @param key
     * @return
     */
    public String getValue(String key) {
        if (Objects.isNull(properties)) {
            loadProperties();
        }
        return properties.getProperty(key);
    }

    @SneakyThrows
    private void loadProperties() {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String configPath = rootPath + "config.properties";
        properties = new Properties();
        properties.load(new FileInputStream(configPath));
    }
}
