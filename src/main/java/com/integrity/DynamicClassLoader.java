package com.integrity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

//was URLClassLoader
public final class DynamicClassLoader extends ClassLoader {  //this in VM options -Djava.system.class.loader=com.integrity.DynamicClassLoader

    static {
        registerAsParallelCapable();
    }

    public DynamicClassLoader(String name, ClassLoader parent) {
//        super(name, new URL[0], parent);
        super(parent);
    }

    //new code
    public Class<?> findClass(String name) {
        String tgt = name.replace(".", "/") + ".class";
        byte[] bytecode = readFullyFromPluginJar(tgt);
        return defineClass(name, bytecode, 0, bytecode.length);
    }

    public byte[] readFullyFromPluginJar(String entryName) {
        try {
            JarFile pluginJar = new JarFile("./uploads/IntegrityAlgorithm-1.7-SNAPSHOT.jar");

            ZipEntry entry = pluginJar.getEntry(entryName);
            if (entry != null) {
                InputStream inputStream = pluginJar.getInputStream(entry);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                return byteArrayOutputStream.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //

    // * Required when this classloader is used as the system classloader

    public DynamicClassLoader(ClassLoader parent) {
        this("classpath", parent);
    }

    public DynamicClassLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

//    void add(URL url) {
//        addURL(url);
//    }

    public static DynamicClassLoader findAncestor(ClassLoader cl) {
        do {

            if (cl instanceof DynamicClassLoader)
                return (DynamicClassLoader) cl;

            cl = cl.getParent();
        } while (cl != null);

        return null;
    }


    // *  Required for Java Agents when this classloader is used as the system classloader

    //@SuppressWarnings("unused")
//    private void appendToClassPathForInstrumentation(String jarfile) throws IOException {
//        add(Paths.get(jarfile).toRealPath().toUri().toURL());
//    }
}
