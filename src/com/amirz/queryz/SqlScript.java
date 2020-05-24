package com.amirz.queryz;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amirz.queryz.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

public class SqlScript extends Activity implements TextWatcher{
	
	MultiAutoCompleteTextView multiautocomplete;
	
	Cursor cursor;
	
	String dbname, txt;
	List<String> autoList;
	
	Intent intent;
	
	SQLiteDatabase db;
	
	String[] item = {"SELECT * FROM ", "SELECT ", "sqlite_master", 
			"CREATE TABLE ", "CREATE VIEW ","CREATE TRIGGER ",  
			"DROP TABLE ", "DROP VIEW ", "DROP TRIGGER ", 
			"FROM ", "WHERE ", "PRIMARY KEY()", "FOREIGN KEY()", 
			"REFERENCES ", "CONSTRAINT ", "INSERT INTO ", "INSERT ON ", "INSERT ", 
			"VALUES()", "IF NOT EXISTS ",
			"NULL", "NOT NULL", "INTEGER", "REAL", "TEXT", "BLOB", 
			"VARCHAR()", "FLOAT", "BOOLEAN", "DATE", "DATETIME", 
			"UPDATE ", "SET ", "AFTER ", "BEFORE ", "BEGIN ", "END ",			
			"DELETE ", "DELETE FROM ", "ORDER BY ", "HAVING ", 
			"GROUP BY ", "COUNT()", "SUM()", "PRAGMA ", "ALTER TABLE ", 
			"MIN()", "MAX()", "AVG()", "INDEX ", "LIKE "
			}, colnames;
		
	File sdcardfiledb = new File(Environment.getExternalStorageDirectory(), 
			File.separator + "queryz" + 
			File.separator + "databases");
	File sdcardfileh = new File(Environment.getExternalStorageDirectory(), 
			File.separator + "queryz" + 
			File.separator + "history");
	
	public void onCreate(Bundle savedInstanceState){		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_theme_sqlscript);	
				
		dbname = getIntent().getStringExtra("dbname");
		db = SQLiteDatabase.openDatabase(
				dbname, 
				null, SQLiteDatabase.OPEN_READWRITE);
		
		autoList = new ArrayList<String>();
		//Add Default
		for(int i=0; i<item.length; i++){
			autoList.add(item[i]);
		}
		autoList.addAll(tablesName()); //Add tables name
		autoList.addAll(viewsName()); //Add views name
		autoList.addAll(columnsName(tablesName())); //Add columns name for each table
		autoList.addAll(columnsName(viewsName())); //Add columns name for each table
		//autoList.remove("android_metadata");
				
		multiautocomplete = (MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView);
		multiautocomplete.setAdapter(new ArrayAdapter<String>(
				this, android.R.layout.simple_dropdown_item_1line, autoList));
		multiautocomplete.setTokenizer(new SpaceTokenizer());
		multiautocomplete.setSingleLine();
		
		int b = getIntent().getIntExtra("b", 0);
		if(b==1){
			String dbop = getIntent().getStringExtra("dbop");
			if(dbop.startsWith("!")) dbop = dbop.substring(2);
			multiautocomplete.setText(dbop);
			multiautocomplete.setSelection(multiautocomplete.getText().length());
		}		
		
		Button exeButton = (Button) findViewById(R.id.buttonExecute);
		exeButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {					
				Editable text = multiautocomplete.getText();				
				String dbop = text.toString();
				if(dbop.equals(null) || dbop.equals("")){
					Toast.makeText(SqlScript.this, "Error", Toast.LENGTH_LONG).show();
				} else {
					intent = new Intent(SqlScript.this, DataManager.class);				
					intent.putExtra("dbop", dbop);
					intent.putExtra("dbname", dbname);
					intent.putExtra("b", 1);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					db.close();
					startActivity(intent);	
					finish();
				}
			}
		});
		
		/*Button pdfButton = (Button) findViewById(R.id.buttonPdf);
		pdfButton.setOnClickListener(new View.OnClickListener() {					
			@Override
			public void onClick(View arg0) {				
				Editable text = multiautocomplete.getText();				
				String dbop = text.toString();
				intent = new Intent(SqlScript.this, DataManager.class);
				intent.putExtra("dbop", dbop);
				intent.putExtra("dbname", dbname);
				intent.putExtra("b", 2);
				startActivity(intent);
				finish();
			}
		});*/
		
		Button helpButton = (Button) findViewById(R.id.buttonHelp);
		helpButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {			
				intent = new Intent(SqlScript.this, HelpScript.class);
				intent.putExtra("dbname", dbname);
				startActivityForResult(intent, 2);				
			}
		});
		db.close();		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 2) {
		     if(resultCode == RESULT_OK){		      
		    	 String column = data.getStringExtra("column");
		    	 String cmd = multiautocomplete.getText().toString();
		    	 int s = multiautocomplete.getSelectionStart();
		    	 String cmdcolumn = cmd.substring(0, s)+column+cmd.substring(s);
		    	 multiautocomplete.setText(cmdcolumn);
		    	 multiautocomplete.setSelection((cmd.substring(0,s)+column).length());
		     }
		     if (resultCode == RESULT_CANCELED) {
		     }
		}
	}

		
	@Override
	public void afterTextChanged(Editable arg0) {
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {	
	}
	
	public List<String> tablesName(){
		cursor = db.rawQuery(
				"SELECT name FROM sqlite_master WHERE type='table'", null);
		List<String> s = new ArrayList<String>();		
		while(cursor.moveToNext()){			
			s.add(cursor.getString(cursor.getColumnIndex("name")));
		}		
		return s;
	}
	
	public List<String> viewsName(){
		cursor = db.rawQuery(
				"SELECT name FROM sqlite_master WHERE type='view'", null);
		List<String> s = new ArrayList<String>();		
		while(cursor.moveToNext()){			
			s.add(cursor.getString(cursor.getColumnIndex("name")));
		}		
		return s;
	}
	
	public List<String> columnsName(List<String> tables){		
		List<String> s = new ArrayList<String>();
		for(int i=0; i<tables.size(); i++){
			cursor = db.rawQuery("SELECT * FROM " + tables.get(i), null);
			colnames = cursor.getColumnNames();
			for(int j=0; j<colnames.length; j++){
				s.add(colnames[j]);
			}
		}
		return s;
	}
	
	public void onBackPressed(){
		db.close();
		finish();
	}
	
}
