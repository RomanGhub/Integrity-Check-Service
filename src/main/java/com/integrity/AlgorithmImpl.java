package com.integrity;

import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class AlgorithmImpl implements LoadableClass {

    public void regMe(WrapperInterface wrapper){
        wrapper.register(myType(), this);
    }

    @Override
    public String check(InputStream inputStream) {
        return null;
    }

    @Override
    public String myType() {
        return null;
    }
}
