package org.framework.config;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Properties;

public final class DatabaseProperties {

    private static DatabaseProperties INSTANCE;
    private static java.util.ResourceBundle RESOURCE_BUNDLE_SERVER;

    public static String get(final String key) {
        DatabaseProperties.verifyInstance();
        if (DatabaseProperties.RESOURCE_BUNDLE_SERVER.containsKey(key)) {
            return DatabaseProperties.RESOURCE_BUNDLE_SERVER.getString(key);
        }
        return key;
    }

    public static String get(final String key, final Object... args) {
        DatabaseProperties.verifyInstance();
        if (DatabaseProperties.RESOURCE_BUNDLE_SERVER.containsKey(key)) {
            return MessageFormat.format(DatabaseProperties.RESOURCE_BUNDLE_SERVER.getString(key), args);
        }
        return key;
    }

    public static synchronized Properties getProperties() {
        DatabaseProperties.verifyInstance();
        final Properties properties = new Properties();
        {
            final Enumeration<String> elements = DatabaseProperties.RESOURCE_BUNDLE_SERVER.getKeys();
            while (elements.hasMoreElements()) {
                final String element = elements.nextElement();
                properties.put(element, DatabaseProperties.RESOURCE_BUNDLE_SERVER.getObject(element));
            }
        }
        return properties;
    }

    private static synchronized void verifyInstance() {
        if (null == DatabaseProperties.INSTANCE) {
            DatabaseProperties.INSTANCE = new DatabaseProperties();
        }
    }

    private DatabaseProperties() {
        DatabaseProperties.RESOURCE_BUNDLE_SERVER = java.util.ResourceBundle.getBundle("persistence");
    }
}
