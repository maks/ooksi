package com.ooksi;

import java.io.DataInputStream;
import java.io.IOException;

import com.google.minijoe.sys.JsArray;

public class MIDPUtils {

	public static String readFileAsText(String path) throws IOException {
		StringBuffer text = new StringBuffer();
    	DataInputStream dis = new DataInputStream(path.getClass().getResourceAsStream(path));
    	int ch;
    	while ((ch = dis.read()) != -1) {
			text.append((char)ch);
		}
    	return text.toString();
	}
	/**
	 * Split String into parts seperated by the delimiter
	 * @param text  Text to be split by the delim
	 * @param sep default ","
	 * @param limit
	 * @return
	 */
	public static JsArray split(String text, String sep, double limit) {
		
        if (Double.isNaN(limit) || limit == 0) {
          limit = Integer.MAX_VALUE;
        }
        
        JsArray a = new JsArray();
        if (sep.length() == 0) {
          if(text.length() < limit) {
            limit = text.length();
          }
          for (int i = 0; i < limit; i++) {
            a.setObject(i, text.substring(i, i+1));
          }
        }
        else {
          int cut0 = 0;
          while(cut0 < text.length() && a.size() < limit) {
            int cut = text.indexOf(sep, cut0);
            if(cut == -1) { 
              cut = text.length();
            }
            a.setObject(a.size(), text.substring(cut0, cut));
            cut0 = cut + sep.length();
          }
        }
        return a;
	}
	
}
