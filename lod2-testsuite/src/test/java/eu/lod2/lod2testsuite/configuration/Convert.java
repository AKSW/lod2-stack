package eu.lod2.lod2testsuite.configuration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Contains basic parsing methods.
 * 
 * @author Stefan Schurischuster
 */
public class Convert {
    
    /**
     * Parses a String to Boolean using static parseBoolean() from class Boolean.
     * 
     * @param value
     *          The String that is going to be parsed.
     * @return 
     *      the parsed boolean representation of the string if the String is: 
     *      "true".
     * 
     */
    public static boolean getBooleanFromString(String value)  {
        return Boolean.parseBoolean(value);
    }
    
    public static Calendar getCalendarFromString(String value, String format) throws Exception  {
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat(format);
        df.parse(value);
        cal.setTime(df.parse(value));
        return cal;
    }
    
    public static String getStringFromBoolean(boolean value)  {
        return Boolean.toString(value);
    }
    
    public static String[] getStringArrayFromString(String value, String splitCharacters) {
        return value.split(splitCharacters);
    }
    
    public static int[] getIntArrayFromString(String value, String splitCharacters) {
        String[] starr = getStringArrayFromString(value, splitCharacters);
        int[] inarr = new int[starr.length];
        
        for(int i=0; i<starr.length; i++)  {
            inarr[i] = Integer.parseInt(starr[i]);
        }
        return inarr;
    }
    
    public static String getStringFromStringArray(String[] values, String mergeCharacters)  {
        String val = values[0];
        
        for(int i=1; i<values.length; i++)  {
            val += mergeCharacters+values[i];
        }
        return val;
    }    
}
