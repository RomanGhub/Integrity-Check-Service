package com.integrity;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;


public final class DynamicClassLoader extends URLClassLoader {  //this in VM options -Djava.system.class.loader=com.integrity.DynamicClassLoader

    private static Boolean isLoadableClassLoaded = false;

    private static final ByteBuddyAgent byteBuddyAgent = new ByteBuddyAgent();

    static {
        registerAsParallelCapable();
    }

    public DynamicClassLoader(String name, ClassLoader parent) {
        super(name, new URL[0], parent);
//        super(parent);
    }

    // * Required when this classloader is used as the system classloader
    public DynamicClassLoader(ClassLoader parent) {
        this("classpath", parent);
    }

    public DynamicClassLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, "");
    }

    public Class<?> loadClass(String name, String jarName) throws ClassNotFoundException {
        System.out.println("Class Loading Started for " + name);
        if (name.endsWith("LoadableClass") && !isLoadableClassLoaded || name.endsWith("Algorithm")) {
            System.out.println("loading in process for " + name);
            return getClass(name, jarName);
        } else if (name.endsWith("LoadableClass") && isLoadableClassLoaded){
            System.out.println("Subsequent call of classloader on LoadableClass. This loading won't proceed");
//            throw new Exception("");
            return LoadableClass.class;
        }
        System.out.println("loading was passed to parent for " + name);
        return super.loadClass(name);
    }


    /**
     * Loading of class from .class file
     * happens here You Can modify logic of
     * this method to load Class
     * from Network or any other source
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    private Class<?> getClass(String name, String jarName) throws ClassNotFoundException {
        System.out.println("*********Inside getClass*********");

        String file = name.replace('.', File.separatorChar) + ".class";
        System.out.println("Name of File " + file);
        byte[] byteArr;
        try {
            // This loads the byte code data from the file
            byteArr = loadClassData(file, jarName);

/*            if (name.endsWith("Algorithm")) {
                // Modify to implement com.integrity.LoadableClass
                byteArr = byteBuddyAgent.implementLoadableClass(byteArr, name);
            }*/

            System.out.println("Size of byte array " + byteArr.length);
            Class<?> c = defineClass(name, byteArr, 0, byteArr.length);
            resolveClass(c);
            System.out.println("C is : " + c.getName() + " " + c.getClassLoader());
            return c;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Loads a given file and converts
     * it into a Byte Array
     * @param name
     * @return
     * @throws IOException
     */
    private byte[] loadClassData(String name, String jarName) throws IOException {

        System.out.println("<<<<<<<<<Inside loadClassData>>>>>>");

//        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(name);
        InputStream stream = getResourceAsStream(name, jarName);

        assert stream != null;
        int size = stream.available();
        byte[] buff = new byte[size];
        DataInputStream in = new DataInputStream(stream);
        // Reading the binary data
        in.readFully(buff);
        in.close();

        isLoadableClassLoaded = true;
        return buff;
    }


    public InputStream getResourceAsStream(String className, String jarName) {
        Objects.requireNonNull(className);

//        URL url = getResource(name);
//        URL url = getResource("file:./uploads/IntegrityAlgorithm-1.8-SNAPSHOT-Serializable.jar");

        try {
            if (className.equals("com\\integrity\\LoadableClass.class")){
                URL url = getResource(className);
                assert url != null;
                return url.openStream();
            } else {
                URL url = getUrlFromJar(className, jarName);
                return url.openStream();
            }
        } catch (IOException e) {
            return null;
        } catch (Exception e){
            System.out.println("getUrlFromJar has failed");
            return null;
        }
    }


    public URL getUrlFromJar(String className, String jarFilePath) throws ClassNotFoundException, IOException {
        // Path to the external JAR file
//        String jarFilePath = "./uploads/IntegrityAlgorithm-1.8-SNAPSHOT-Serializable.jar";
//        String jarFilePath = "./uploads/" + jarName + ".jar";

        // Path to the .class file inside the JAR
        String classFilePath = "com/integrity/Algorithm.class";

        // Construct the URL for the .class file inside the JAR
        String jarUrl = "jar:file:" + jarFilePath + "!/" + classFilePath;
        return new URL(jarUrl);
    }


/*
    public Class<?> findClass(String name) {
        String tgt = name.replace(".", "/") + ".class";
        byte[] bytecode = readFullyFromPluginJar(tgt);
        return defineClass(name, bytecode, 0, bytecode.length);
    }
*/

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.equals("Algorithm")) {
            String tgt = name.replace(".", "/") + ".class";
            byte[] bytecode = readFullyFromPluginJar(tgt);
            return defineClass(name, bytecode, 0, bytecode.length);
        } else {
            return super.findClass(name);
        }
    }


    public byte[] readFullyFromPluginJar(String entryName) {
        try {
            JarFile pluginJar = new JarFile("./uploads/IntegrityAlgorithm-1.8-SNAPSHOT-Serializable.jar");

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




/*
    public static DynamicClassLoader findAncestor(ClassLoader cl) {
        do {

            if (cl instanceof DynamicClassLoader)
                return (DynamicClassLoader) cl;

            cl = cl.getParent();
        } while (cl != null);

        return null;
    }

*/

    // *  Required for Java Agents when this classloader is used as the system classloader

    @SuppressWarnings("unused")
    private void appendToClassPathForInstrumentation(String jarfile) throws IOException {
        add(Paths.get(jarfile).toRealPath().toUri().toURL());
    }

    void add(URL url) {
        addURL(url);
    }
}
