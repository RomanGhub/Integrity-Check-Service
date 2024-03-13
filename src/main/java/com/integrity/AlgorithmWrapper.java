package com.integrity;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AlgorithmWrapper implements WrapperInterface {

    Map<String, LoadableClass> algorithmMap = new HashMap<>();

    public void register(String type, LoadableClass algorithm) {
        algorithmMap.put(type, algorithm);
    }


}
