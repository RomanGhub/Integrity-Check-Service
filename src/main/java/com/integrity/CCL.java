package com.integrity;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class CCL extends ClassLoader{

    @Override
    public Class<?> findClass(String classname){
        String filename = "target/classes/" + classname + ".class"; //+ classname.replace('.', File.separatorChar)
        byte[] byteCode;
        try {
            byteCode = Files.newInputStream(Path.of(filename)).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return defineClass("com.integrity.Algorithm", byteCode, 0, byteCode.length); //classname
    }
}
