package com.amirz.queryz;

import java.io.File;

import com.amirz.queryz.R;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Main extends Activity {

	Intent intent;
    
	File sdcardfiledb = new File(Environment.getExternalStorageDirectory(), 
			File.separator + "queryz" + 
			File.separator + "databases");
    File sdcardfileh = new File(Environment.getExternalStorageDirectory(), 
			File.separator + "queryz" + 
			File.separator + "history");
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  
                        
        /*File sdcardfileq = new File(Environment.getExternalStorageDirectory(), 
				File.separator + "RSQLite" + 
				File.separator + "query");        
        if(!sdcardfileq.exists()){
			if(!sdcardfileq.mkdirs())
				Log.v("File", "Problem creating folder.");
			else sdcardfileq.mkdirs();
		}*/	
		if(!sdcardfiledb.exists()){
			if(!sdcardfiledb.mkdirs())
				Log.v("File", "Problem creating folder.");
			else sdcardfiledb.mkdirs();
		}
		if(!sdcardfileh.exists()){
			if(!sdcardfileh.mkdirs())
				Log.v("File", "Problem creating folder.");
			else sdcardfileh.mkdirs();
		}
		
		Button learnButton = (Button) findViewById(R.id.buttonLearn);		
        learnButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v("Myapp", "Learn");				
				//intent = new Intent(Main.this, CreateTheme.class);   
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    	//startActivity(intent);
				Log.v("Myapp", "About");								
				intent = new Intent(Main.this, AboutTheme.class);
				startActivity(intent);
				
			}
		});       
                
        Button startButton = (Button) findViewById(R.id.buttonStart);
        startButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v("Myapp", "Start");	
				intent = new Intent(Main.this, ListDatabases.class);
				intent.putExtra("dbname", "/sdcard/queryz/databases");
		    	startActivity(intent);
			}
		});
               
        /*Button exitButton = (Button) findViewById(R.id.buttonExit);
        exitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v("Myapp", "Exit");
				finish();
			}
		});*/
        
        View aboutButton = findViewById(R.id.buttonAbout);
        aboutButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v("Myapp", "About");								
				intent = new Intent(Main.this, AboutTheme.class);
				startActivity(intent);
			}
		});       
                
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}
