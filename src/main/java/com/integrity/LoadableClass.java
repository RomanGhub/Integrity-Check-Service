package com.integrity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

//This is service interface (SPI)
@Component
public interface LoadableClass {

    String check(InputStream inputStream);

    @Autowired
    default void regMe(AlgorithmWrapper algorithm){
        algorithm.register("Algorithm", this); // myType()
    }

    String myType();

}
