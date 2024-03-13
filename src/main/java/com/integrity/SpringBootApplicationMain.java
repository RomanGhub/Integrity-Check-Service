package com.integrity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootApplicationMain {


    public static void main(String[] args) throws ClassNotFoundException {
        SpringApplication.run(SpringBootApplicationMain.class, args);
      /*  ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

        // Get the name of the system class loader class
        String systemClassLoaderName = systemClassLoader.getClass().getName();

        // Print the name of the system class loader class
        System.out.println("System Class Loader: " + systemClassLoaderName);

        // Load a class
        Class<?> loadedClass = Class.forName("com.integrity.LoadableClass");
        // Get the class loader
        ClassLoader classLoader = loadedClass.getClassLoader();
        // Print the class loader
        System.out.println("Class LoadableClass loaded by: " + classLoader);

        // Load a class
        Class<?> loadedClass2 = Class.forName("com.integrity.IntegrityExecutor");
        // Get the class loader
        ClassLoader classLoader2 = loadedClass2.getClassLoader();
        // Print the class loader
        System.out.println("Class IntegrityExecutor loaded by: " + classLoader2);


        // try to reload clas

//        DynamicClassLoader parentClassLoader = (DynamicClassLoader)ClassLoader.getSystemClassLoader();

//        parentClassLoader.loadClass("com.integrity.LoadableClass");

        // Load a class
        loadedClass = Class.forName("com.integrity.LoadableClass");
        // Get the class loader
        classLoader = loadedClass.getClassLoader();
        // Print the class loader
        System.out.println("Class LoadableClass (after reloading) loaded by: " + classLoader);*/

    }



}
