package org.kpi;

import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;

@Component
public class JarLoader {
    public Object loadJar(String fileName, String customClassName) throws Exception {
        // Specify the path to the JAR file
        final String jarFolderPath = "./uploads";
        final String className;
        if (Objects.equals(customClassName, "")){
            className = "com.integrity.Algorithm";
        } else {
            className = customClassName;
        }

        // Create a URLClassLoader
        URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file:" + jarFolderPath + "/" + fileName)});

        // Load a class from the JAR
        Class<?> loadedClass = classLoader.loadClass(className);

        // Create and return an instance of the class
        return loadedClass.getDeclaredConstructor().newInstance();
    }
}
