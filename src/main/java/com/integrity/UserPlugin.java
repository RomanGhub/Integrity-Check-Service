package com.integrity;

import java.io.InputStream;

public class UserPlugin implements LoadableClass {
    Object pluginInstance;

    public UserPlugin (Object pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    public UserPlugin() {

    }

    public String check(InputStream inputStream){
        try {
//            pluginInstance.check();
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


}
