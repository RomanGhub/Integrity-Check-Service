package com.integrity;

import java.io.InputStream;
import java.io.Serializable;

public class AlgorithmDeprecated implements LoadableClass, Serializable {

    public AlgorithmDeprecated(){

    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

    public String check(InputStream inputStream) { //byte[]  //Was static
       return null;
    }

    private static String bytesToHex(byte[] bytes) {
        return null;
    }
}
