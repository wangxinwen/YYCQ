package com.example.yycq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class LogoActivity extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 6000; // 延迟六秒  
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_logo);
        
        new Handler().postDelayed(new Runnable() {  
            public void run() {  
                Intent mainIntent = new Intent(LogoActivity.this,MainActivity.class);  
                LogoActivity.this.startActivity(mainIntent);  
                LogoActivity.this.finish();  
            }  
        }, SPLASH_DISPLAY_LENGHT);  
    }
    
}
