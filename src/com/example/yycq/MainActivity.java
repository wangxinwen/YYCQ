package com.example.yycq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        EditText editText = (EditText) findViewById(R.id.editText1);
        editText.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {  
            @Override  
            public void onFocusChange(View v, boolean hasFocus) {  
                if(hasFocus) {
                	  Intent mainIntent = new Intent(MainActivity.this,SearchActivity.class);  
                	  MainActivity.this.startActivity(mainIntent); 
//                	  MainActivity.this.finish();  
                	  v.clearFocus();
		        }
            }
        });
        
        GridView gridview = (GridView) findViewById(R.id.gridView1);
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
//                Toast.makeText(MainActivity.this, "你选择了" + (position + 1) + " 号图片", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    class ImageAdapter extends BaseAdapter{
    	private Context mContext;
    	private Integer[] mImageIds = {
            R.drawable.u16_normal,
            R.drawable.u16_normal,
            R.drawable.u16_normal,
            R.drawable.u16_normal};

	    public ImageAdapter(Context c)
	    {
	        mContext = c;
	    }
	    public int getCount()
	    {
	        return mImageIds.length;
	    }
	    public Object getItem(int position)
	    {
	        return position;
	    }
	    public long getItemId(int position)
	    {
	        return position;
	    }
	    public View getView(int position, View convertView, ViewGroup parent)
	    {
	        ImageView imageView;
	        if (convertView == null)
	        {
	            imageView = new ImageView(mContext);
	            //imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
	           // imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
	        }
	        else
	        {
	            imageView = (ImageView) convertView;
	        }
	        imageView.setImageResource(mImageIds[position]);
	        return imageView;
	    }
   }
}