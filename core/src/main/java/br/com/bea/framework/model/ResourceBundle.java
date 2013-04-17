/*
The MIT License (MIT)
Copyright (c) 2013 B&A Tecnologia

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions 
of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED 
TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
IN THE SOFTWARE.
 */
package br.com.bea.framework.model;

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
        ResourceBundle.RESOURCE_BUNDLE_SERVER = java.util.ResourceBundle.getBundle("messages");
    }
}