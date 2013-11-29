package com.example.yycq.config;

import android.graphics.Color;
import android.os.Environment;

public class Config {
 
	public static String RootPath =Environment.getExternalStorageDirectory().getAbsolutePath()+ "/learneasy";
	
	public static final int IconSize = 70;
	public static final int FONTCOLOR = Color.BLACK;
	
	public static final String XML_ENCODING="UTF-8";
	public static final float FONTSIZE = 20;
	public static final int SELECTED_FONTCOLOR = Color.RED;
}
