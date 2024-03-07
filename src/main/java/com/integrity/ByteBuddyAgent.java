package com.integrity;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.pool.TypePool;

import java.io.Serializable;

public class ByteBuddyAgent {

    public byte[] implementLoadableClass(byte[] originalBytecode, String name){

        // Define the new interface to implement
        Class<?> newInterface = LoadableClass.class;

        ClassFileLocator simpleClassLocator = ClassFileLocator.Simple.of(name, originalBytecode);
        byte[] instrumentedClazzBytes = new ByteBuddy()
//                .subclass(Object.class)
                .with(TypeValidation.DISABLED)//new IgnoreSuperclassStrategy()
                .redefine(TypePool.Default.of(simpleClassLocator).describe(name).resolve(), simpleClassLocator)  //of(simpleClassLocator)
                .name("com.integrity.Algorithm")  //org.kpi.Algorithm
                .implement(newInterface)
                .implement(Serializable.class)
                .make()
                .getBytes();

        return instrumentedClazzBytes;
    }

}
