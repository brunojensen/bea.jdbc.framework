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
