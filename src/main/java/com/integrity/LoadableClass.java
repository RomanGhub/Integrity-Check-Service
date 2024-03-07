package com.integrity;

import java.io.InputStream;

//This is service interface (SPI)
public interface LoadableClass {

    String check(InputStream inputStream);

}
