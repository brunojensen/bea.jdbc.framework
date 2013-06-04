package org.framework.model;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Access the configuration bundle
 * 
 * @author bruno-jensen
 */
public final class ResourceBundle {

    private static ResourceBundle RESOURCE_BUNDLE;
    private static java.util.ResourceBundle RESOURCE_BUNDLE_SERVER;

    /**
     * @param key {@link String}
     * @return {@link String}
     */
    public static String getMessage(final String key) {
        ResourceBundle.verifyInstance();
        if (ResourceBundle.RESOURCE_BUNDLE_SERVER.containsKey(key)) {
            return ResourceBundle.RESOURCE_BUNDLE_SERVER.getString(key);
        }
        return key;
    }

    /**
     * @param key {@link String}
     * @param args {@link String} array
     * @return {@link String}
     */
    public static String getMessage(final String key, final Object... args) {
        ResourceBundle.verifyInstance();
        if (ResourceBundle.RESOURCE_BUNDLE_SERVER.containsKey(key)) {
            return MessageFormat.format(ResourceBundle.RESOURCE_BUNDLE_SERVER.getString(key), args);
        }
        return key;
    }

    /**
     * @return {@link Properties}
     */
    public static synchronized Properties getProperties() {
        ResourceBundle.verifyInstance();
        final Properties properties = new Properties();
        {
            final Enumeration<String> elements = ResourceBundle.RESOURCE_BUNDLE_SERVER.getKeys();
            while (elements.hasMoreElements()) {
                final String element = elements.nextElement();
                properties.put(element, ResourceBundle.RESOURCE_BUNDLE_SERVER.getObject(element));
            }
        }
        return properties;
    }

    private static synchronized void verifyInstance() {
        if (null == ResourceBundle.RESOURCE_BUNDLE) {
            ResourceBundle.RESOURCE_BUNDLE = new ResourceBundle();
        }
    }

    private ResourceBundle() {
        ResourceBundle.RESOURCE_BUNDLE_SERVER = java.util.ResourceBundle.getBundle("validation_messages");
    }
}