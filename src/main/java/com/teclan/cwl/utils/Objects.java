package com.teclan.cwl.utils;

import java.util.List;

public class Objects {

    public static String join(String split, List<String> items) {
        if (items.isEmpty()) {
            return "";
        }

        if (items.size() == 1) {
            return items.get(0);
        }

        StringBuffer v = new StringBuffer();
        for(int i=0;i<items.size()-1;i++){
            v.append(items.get(i)).append(split);
        }
        v.append(items.get(items.size()-1));

        return v.toString();
    }
}
