/**
 * $Id$
 *
 * Copyright (c) 2014 Steven Jardine, All Rights Reserved.
 * Copyright (c) 2014 MJN Services, Inc., All Rights Reserved.
 */
package com.gwt.ss;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to find classes in a package.
 *
 * @author Steven Jardine
 * @version $Rev$
 */
public final class ClassUtil {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * Recursive method used to find all classes in a given directory and subdirs. Adapted from
     * http://snippets.dzone.com/posts/show/4831 and extended to support use of JAR files
     *
     * @param directory The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws IOException an error occurred.
     */
    private static Set<String> findClasses(final String directory, final String packageName) throws IOException {
        Set<String> classes = new TreeSet<String>();
        if (directory.startsWith("file:") && directory.contains("!")) {
            String[] split = directory.split("!");
            URL jar = new URL(split[0]);
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            try {
                ZipEntry entry = null;
                while ((entry = zip.getNextEntry()) != null) {
                    if (entry.getName().endsWith(".class")) {
                        String className = entry.getName().replaceAll("[$].*", "").replaceAll("[.]class", "")
                            .replace('/', '.');
                        if (className.startsWith(packageName)) {
                            classes.add(className);
                        }
                    }
                }
            } finally {
                zip.close();
            }
        }
        File dir = new File(directory);
        if (!dir.exists()) {
            return classes;
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file.getAbsolutePath(), packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(packageName + '.'
                        + file.getName().substring(0, file.getName().length() - ".class".length()));
            }
        }
        return classes;
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and
     * subpackages. Adapted from http://snippets.dzone.com/posts/show/4831 and extended to support use of JAR
     * files
     *
     * @param packageName The base package
     * @return The classes
     */
    public static Class<?>[] getClasses(final String packageName) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            assert classLoader != null;
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<String> dirs = new ArrayList<String>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(resource.getFile());
            }
            Set<String> classes = new TreeSet<String>();
            for (String directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }
            List<Class<?>> classList = new ArrayList<Class<?>>();
            for (String clazz : classes) {
                classList.add(Class.forName(clazz));
            }
            return classList.toArray(new Class[classes.size()]);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return new Class[] {};
        }
    }

    /**
     * Private constructor.
     */
    private ClassUtil() {
        super();
    }

}
