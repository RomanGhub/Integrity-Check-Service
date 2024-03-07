package com.integrity;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class JarLoader {
    //new
    public LoadableClass loadJar(String fileName, String customClassName) throws Exception { // was Object
        // Specify the path to the JAR file
        final String jarFolderPath = "./uploads";
        final String className;
        if (Objects.equals(customClassName, "")){
            className = "com.integrity.Algorithm";
        } else {
            className = customClassName;
        }

        // Use the system class loader as the parent
//        DynamicClassLoader parentClassLoader = new DynamicClassLoader();//(DynamicClassLoader)ClassLoader.getSystemClassLoader();
//        parentClassLoader.add(new URL("file:" + jarFolderPath + "/" + fileName));

        //use system classloader
//        ClassLoader parentClassLoader = ClassLoader.getSystemClassLoader();

//        URLClassLoader urlClassLoader = new URLClassLoader(new URL[] {new URL("file:" + jarFolderPath + "/" + fileName)}, parentClassLoader);
//        Class<?> loadedClass = urlClassLoader.loadClass(className);

//        Class<?> loadedClass = Class.forName(className, true, parentClassLoader);

        ///////////////
/*        DynamicClassLoader parentClassLoader = new DynamicClassLoader();
        Class<?> reloadedClass = parentClassLoader.loadClass("com.integrity.LoadableClass", null); //
        ClassLoader classLoaderr = reloadedClass.getClassLoader();
        System.out.println("Class LoadableClass (after reloading) loaded by: " + classLoaderr);*/
        //////////////
        System.out.println("SOUT BEFORE LOADING ALGORITHM. Loader for LoadableClass is  " + LoadableClass.class.getClassLoader());

        DynamicClassLoader classLoader = new DynamicClassLoader(); //= parentClassLoader;  //

        Class<?> loadedClass = classLoader.loadClass(className, jarFolderPath + "/" + fileName);

        Object instanceOfLoadedClass = loadedClass.getDeclaredConstructor().newInstance();


        System.out.println("instance of loaded class is: " + instanceOfLoadedClass);
//
        System.out.println("it's Classloader is  " + instanceOfLoadedClass.getClass().getClassLoader());

/*        LoadableClass test = new Algorithm();
        System.out.println(test.getClass().getClassLoader());
        System.out.println(LoadableClass.class.getClassLoader());

        DynamicClassLoader dynamicClassLoader = new DynamicClassLoader();
        dynamicClassLoader.loadClass("com.integrity.LoadableClass");

        LoadableClass test2 = new Algorithm();
        System.out.println(test2.getClass().getClassLoader());
        System.out.println(LoadableClass.class.getClassLoader());*/

//        String ser = (String) loadedClass.getDeclaredMethod("serializeToJson").invoke(instanceOfLoadedClass);
//        System.out.println(ser);

//        String serializedInstance = SerializationService.serializeToJson(instanceOfLoadedClass);
//        LoadableClass deserializedInstanceOfLoadedClass = SerializationService.deserializeFromJson(serializedInstance);

//        System.out.println("instance of loaded deserialized class is: " + deserializedInstanceOfLoadedClass);

        // Create and return an instance of the class
//        return (LoadableClass) loadedClass.getDeclaredConstructor().newInstance();
//        return null;
//        System.out.println("SOUT BEFORE RETURN. Class LoadableClass (after reloading) loaded by: " + classLoaderr);
        System.out.println("SOUT BEFORE RETURN. Loader for LoadableClass is  " + LoadableClass.class.getClassLoader());
        return (LoadableClass) instanceOfLoadedClass;
    }
}
