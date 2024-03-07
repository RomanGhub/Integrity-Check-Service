package com.integrity;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;

public class IgnoreSuperclassStrategy implements AgentBuilder.TypeStrategy {


    public ByteBuddy ignore(TypeDescription typeDescription) {
        // Ignore the superclass for redefinition
        return new ByteBuddy().with(TypeValidation.DISABLED);
    }

    @Override
    public DynamicType.Builder<?> builder(TypeDescription typeDescription, ByteBuddy byteBuddy, ClassFileLocator classFileLocator, MethodNameTransformer methodNameTransformer, ClassLoader classLoader, JavaModule javaModule, ProtectionDomain protectionDomain) {
        return null;
    }
}