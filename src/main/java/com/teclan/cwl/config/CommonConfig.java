package com.teclan.cwl.config;

import com.teclan.cwl.utils.Assert;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class CommonConfig {
    private final static String filePath = "conf/application.json";
    private final static String base = System.getProperty("user.dir");
    public  static Config config = ConfigFactory.parseFile(new File(base + File.separator + filePath));

    public  static Config env =null;
    		
    public  static Config getConfig(){
        return config;
    }
    
    public  static Config getEnvConfig(){
    	if(Assert.assertNull(env)) {
    		env = CommonConfig.getConfig().getConfig(config.getString("env"));
    	}
        return env;
    }
}
