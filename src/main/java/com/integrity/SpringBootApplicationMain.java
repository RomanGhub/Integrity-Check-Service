package com.integrity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.MalformedURLException;

@SpringBootApplication
public class SpringBootApplicationMain {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, MalformedURLException {
//        DynamicClassLoader parentClassLoader = (DynamicClassLoader) ClassLoader.getSystemClassLoader();//new DynamicClassLoader();
//        Class<?> reloadedClass = parentClassLoader.loadClass("com.integrity.LoadableClass", null);
//        System.out.println("(main) Class LoadableClass loaded by: " + reloadedClass.getClassLoader());

        SpringApplication.run(SpringBootApplicationMain.class, args);

/*
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

        // Get the name of the system class loader class
        String systemClassLoaderName = systemClassLoader.getClass().getName();

        // Print the name of the system class loader class
        System.out.println("System Class Loader: " + systemClassLoaderName);*/




/*        // Load a class
        Class<?> loadedClass = Class.forName("com.integrity.LoadableClass");
        // Get the class loader
        ClassLoader classLoader = loadedClass.getClassLoader();
        // Print the class loader
        System.out.println("Class LoadableClass loaded by: " + classLoader);*/

//        // Load a class
//        Class<?> loadedClass2 = Class.forName("com.integrity.IntegrityExecutor");
//        // Get the class loader
//        ClassLoader classLoader2 = loadedClass2.getClassLoader();
//        // Print the class loader
//        System.out.println("Class IntegrityExecutor loaded by: " + classLoader2);


        //----------------------
        // try to reload class
//        DynamicClassLoader parentClassLoader = new DynamicClassLoader();
//        Class<?> reloadedClass = parentClassLoader.loadClass("com.integrity.LoadableClass", null); //



/*        ClassLoader classLoader = reloadedClass.getClassLoader();
        System.out.println("Class LoadableClass (after reloading) loaded by: " + classLoader);
        System.out.println("Now for the CLASS: " + LoadableClass.class.getClassLoader());*/
    }
}
