package com.amirz.queryz;

import java.io.File;

import com.amirz.queryz.R;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateTheme extends Activity {
	
	int content;	
	Intent intent;
	SQLiteDatabase db;
	String path;
	
	File sdcardfiledb = new File(Environment.getExternalStorageDirectory(), 
			File.separator + "queryz" + 
			File.separator + "databases");
	File sdcardfileh = new File(Environment.getExternalStorageDirectory(), 
			File.separator + "queryz" + 
			File.separator + "history");	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_theme_create);
		
		path = getIntent().getStringExtra("dbname");

		Button themeCreate = (Button) findViewById(R.id.buttonTheme_create);		
		themeCreate.setOnClickListener(new View.OnClickListener() {
			EditText editTextCreate = (EditText) findViewById(R.id.editTextCreate);
			Editable text = editTextCreate.getText();			
			@Override
			public void onClick(View arg0) {			
				String dbname = text.toString();
				//if(testname(dbname)){
					try{												
						db = SQLiteDatabase.openOrCreateDatabase(path+File.separator+dbname, null);
						
						db.close();					
						db = SQLiteDatabase.openOrCreateDatabase(sdcardfileh.toString()+File.separator+"History", null);
						
						db.close();    				
						Log.v("MyApp", "MyApp: "+dbname+" Opended/Created successfully!");
						Toast.makeText(CreateTheme.this, dbname+" Opended/Created successfully!", 
								Toast.LENGTH_LONG).show();
						//db = openOrCreateDatabase(dbname, SQLiteDatabase.CREATE_IF_NECESSARY, null);						
						db = SQLiteDatabase.openDatabase(
								sdcardfileh.toString()+File.separator+"History", 
								null, SQLiteDatabase.CREATE_IF_NECESSARY);						
						db.execSQL("CREATE TABLE IF NOT EXISTS History(id INTEGER PRIMARY KEY, hist TINYTEXT)");
						db.close();					
						gotoDataManager(path+File.separator+dbname);
					} catch(SQLiteException e){
						Log.v("DB", "DB: "+dbname+" NOT Opended/Created!");
						Toast.makeText(CreateTheme.this, "Error: "+e.getLocalizedMessage(), 
								Toast.LENGTH_LONG).show();
					}					
				//}
				//else{
				//	Toast.makeText(CreateTheme.this, "Error: invalid name", 
				//		Toast.LENGTH_LONG).show();
				//}
			}
		});	
		
	}		
	
	public void gotoDataManager(String dbname){		
		intent = new Intent(this, DataManager.class);
		intent.putExtra("dbname", dbname);
		startActivity(intent);	
		finish();
	}
	
	public void onBackPressed(){		
		intent = new Intent(CreateTheme.this, ListDatabases.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("dbname", path);
		startActivity(intent);
		finish();
	}
	
	public boolean testname(String name){
		if(name.contains("."))return false;
		if(name.contains("-"))return false;
		if(name.contains("+"))return false;
		if(name.contains(","))return false;
		if(name.contains("!"))return false;
		if(name.contains("@"))return false;
		if(name.contains("#"))return false;
		if(name.contains("$"))return false;
		if(name.contains("%"))return false;
		if(name.contains("&"))return false;
		if(name.contains("*"))return false;
		if(name.contains("("))return false;
		if(name.contains(")"))return false;
		if(name.contains("_"))return false;
		if(name.contains("/"))return false;
		if(name.contains("\\"))return false;
		if(name.contains("|"))return false;
		if(name.contains("\""))return false;
		if(name.contains("'"))return false;
		if(name.contains("?"))return false;
		
		return true;
	}
	
}

