package com.integrity;

import com.sun.tools.javac.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
public class JarLoader {


    @Autowired
    BeanRegistrator beanRegistrator;

    @Autowired
    AlgorithmWrapper wrapper;


//, Path jarLocation, String className
    public LoadableClass loadJar(String fileName, String customClassName) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        DynamicClassLoader loader = new DynamicClassLoader(Main.class.getClassLoader());
//        loader.setJarSearchSpace(jarLocation);
        String className = "com.integrity.Algorithm";
        Class<?> pl = loader.loadClass(className); // load, not find!!

        reg();
        System.out.println("after reg and before return");


//        return wrapper.algorithmMap.get("Algorithm");
        return (LoadableClass) pl.getConstructor().newInstance();
    }


    public void reg(){
        beanRegistrator.registerBean("Algorithm");
    }

/*    //Even newer version with use of SPI (figured out that it doesn't work like that)
    public LoadableClass loadJar(String fileName, String customClassName) throws Exception { // was Object
        // Specify the path to the JAR file
        final String jarFolderPath = "./uploads";
        final String className;
        if (Objects.equals(customClassName, "")){
            className = "com.integrity.Algorithm";
        } else {
            className = customClassName;
        }

        // Load the service
        ServiceLoader<LoadableClass> serviceLoader = ServiceLoader.load(LoadableClass.class);

        // List the found service implementations
        System.out.println("Found service implementations:");
        for (LoadableClass service : serviceLoader) {
            System.out.println("- " + service.getClass().getName());
        }

        // Use the first service implementation found
        LoadableClass loadedClass = serviceLoader.iterator().next();

        return loadedClass;
    }*/

    //old but mostly working (actually not)
/*    public LoadableClass loadJar(String fileName, String customClassName) throws Exception { // was Object
        // Specify the path to the JAR file
        final String jarFolderPath = "./uploads";
        final String className;
        if (Objects.equals(customClassName, "")){
            className = "com.integrity.Algorithm";
        } else {
            className = customClassName;
        }

        // Create a URLClassLoader // TODO try-catch with resources
        // URL classloader was used but another
        URLClassLoader classLoader = new URLClassLoader(new URL[]{new URL("file:" + jarFolderPath + "/" + fileName)});

        // Load a class from the JAR
        Class<?> loadedClass = classLoader.loadClass(className);

        // custom class loader
//        LoadableClass instance = (LoadableClass) ClassLoader.getSystemClassLoader();

        // Create and return an instance of the class
        return (LoadableClass) loadedClass.getDeclaredConstructor().newInstance(); //wasnt loadableClass
    }*/


    //new
//    public LoadableClass loadJar(String fileName, String customClassName) throws Exception { // was Object
//        // Specify the path to the JAR file
//        final String jarFolderPath = "./uploads";
//        final String className;
//        if (Objects.equals(customClassName, "")){
//            className = "com.integrity.Algorithm";
//        } else {
//            className = customClassName;
//        }
//
//        // Use the system class loader as the parent
//        DynamicClassLoader parentClassLoader = (DynamicClassLoader)ClassLoader.getSystemClassLoader();
//
//        parentClassLoader.add(new URL("file:" + jarFolderPath + "/" + fileName));
//        Class<?> loadedClass = parentClassLoader.loadClass(className);
//
//        // Create and return an instance of the class
//        return (LoadableClass) loadedClass.getDeclaredConstructor().newInstance();
//    }
}
