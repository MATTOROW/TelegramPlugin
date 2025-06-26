package com.mattorow.jiraplugin.config;

import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;

import javax.inject.Named;
import java.io.InputStream;
import java.util.Properties;

@Named
public class PropertyLoader {
    private Properties properties = new Properties();

    public PropertyLoader() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("TelegramPlugin.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new RuntimeException("TelegramPlugin.properties not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load TelegramPlugin.properties", e);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}
