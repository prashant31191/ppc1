package com.openims.utility;

/** 
 * Utility class for LogCat.
 */
public class LogUtil {
    
    @SuppressWarnings("unchecked")
    public static String makeLogTag(Class cls) {
        return "OpenIMS";
        //return "OpenIMS_" + cls.getSimpleName();
    }
    public static String makeTag(Class cls) {
        //return "";
        return cls.getSimpleName() + "--";
    }

}