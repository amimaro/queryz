package com.amirz.queryz;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amirz.queryz.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListDatabases extends ListActivity{
	
	SQLiteDatabase db;
	Intent intent;
	Cursor cursor;
	String txt, dpt, dptp, dpt2;			
	String[] files2;	
	List<String> files;
	List<String> items;
	File[] filesBuff;
	File sdcardfiledb = new File(Environment.getExternalStorageDirectory(), 
			File.separator + "queryz" + 
			File.separator + "databases");
	File sdcardfileh = new File(Environment.getExternalStorageDirectory(), 
			File.separator + "queryz" + 
			File.separator + "history");	
	TextView dirPathText;
	String path;
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_listdatabases);	
		
		path = getIntent().getStringExtra("dbname");
		
		dirPathText = (TextView) findViewById(R.id.textView2);
		dirPathText.setText(path);		
    	getFiles(new File(path).listFiles());
		
		/*files2 = sdcardfiledb.list();		
		files = new ArrayList<String>();
		for(String n : files2){
			files.add(n);
			if(n.endsWith("-journal")){
				files.remove(n);
				//delFile(n);
			}
		}*/
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_list_item1, fileListAdpt);	
		//setListAdapter(adapter);
		
    	Button createButton = (Button) findViewById(R.id.buttonCreate);
        createButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v("Myapp", "Create");				
				intent = new Intent(ListDatabases.this, CreateTheme.class);  
				intent.putExtra("dbname", dirPathText.getText());
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    	startActivity(intent);		    	
			}
		});
    	
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				//txt = getListAdapter().getItem(arg2).toString();
				dpt2 = items.get((int) arg3);
				if(!dpt2.equals("..")){
					txt = dpt2.toString().substring(dpt2.lastIndexOf("/"));
					AlertDialog.Builder alertBuilder= new AlertDialog.Builder(ListDatabases.this);
					alertBuilder.setMessage("Do you want to DROP "+txt+"?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface arg0, int arg1) {						
							/*db = SQLiteDatabase.openDatabase(
									sdcardfiledb.toString()+File.separator+txt, 
									null, SQLiteDatabase.OPEN_READWRITE);
							
							List<String> tname = viewsName();				
							for(String n : tname){
								db.execSQL("DROP VIEW "+n);
							}
							tname = tablesName();
							for(String n : tname){
								db.execSQL("DROP TABLE "+n);
							}
							try{
								db.close();
								db = SQLiteDatabase.openDatabase(
										sdcardfileh.toString()+File.separator+"History", 
										null, SQLiteDatabase.CREATE_IF_NECESSARY);
								
								db.execSQL("DROP TABLE "+txt);
							} catch(SQLException e){
								
							}*/							
							if(checkDataBase(dpt2)){							
								delFile(dpt2);
								if(items.contains(dpt2+"-journal"))
									delFile(dpt2+"-journal");
								Toast.makeText(ListDatabases.this, txt + " Dropped!", Toast.LENGTH_LONG).show();
								//db.close();
								//finish();								
								//startActivity(getIntent());
								getFiles(new File(dpt2.substring(0,dpt2.lastIndexOf("/"))).listFiles());
							} else {								
								Toast.makeText(ListDatabases.this, "Error: "+ txt + " Not Dropped!", Toast.LENGTH_LONG).show();
							}
							
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					AlertDialog alertDialog = alertBuilder.create();
					alertDialog.show();
				}				
				return false;
			}
		    });
	}
	
	public void onListItemClick(ListView lv, View v, int position, long id){
		String item = (String) getListAdapter().getItem(position);
		File file = new File(items.get((int) id));
		dpt = dirPathText.getText().toString();
		if((int) id == 0){
			if(!dpt.equals("/sdcard")){
				dptp = new File(dpt).getParent();							
				dirPathText.setText(dptp);
				//showToast(new File(dpt).getParent());				
				getFiles(new File(dptp).listFiles());
			}
		} else {			
			if(file.isDirectory()){
				dirPathText.setText(file.toString());
				//showToast(file.toString());
				getFiles(file.listFiles());
			} else {				
				if(checkDataBase(item) == true){
					try{
						showToast(checkDataBase(item)+"");
						gotoDBManager(item);
					} catch (SQLException e){
						Toast.makeText(ListDatabases.this, 
								"Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(ListDatabases.this, 
							"Error: "+item.substring(item.lastIndexOf("/"))+" is not a database!", 
							Toast.LENGTH_LONG).show();
					showToast(checkDataBase(item)+"");
				}
			}
		}
				
	}
	
	public void gotoDBManager(String dbname){
				
		db = SQLiteDatabase.openDatabase(		
			sdcardfileh.toString()+File.separator+"History", 
			null, SQLiteDatabase.CREATE_IF_NECESSARY);
		db.execSQL("CREATE TABLE IF NOT EXISTS History(id INTEGER PRIMARY KEY, hist TINYTEXT)");
		db.close();
		intent = new Intent(this, DataManager.class);
		intent.putExtra("dbname", dbname);		
		startActivity(intent);
		finish();
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
	
	public void delFile(String fname){		
		new File(fname).delete();
	}
	
	public void onBackPressed(){		
		intent = new Intent(ListDatabases.this, Main.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
	
	public void showToast(String s){
		Log.v("List", s);	
	}
	
	private void getFiles(File[] files){
        items = new ArrayList<String>();        
        items.add("..");
        for(File file : files){
        	items.add(file.getPath());
        }
        ArrayAdapter<String> fileList = 
        		new ArrayAdapter<String>(this,R.layout.activity_list_item1, items);
        setListAdapter(fileList);
    }
	
	private boolean checkDataBase(String dbname) {
	    SQLiteDatabase checkDB = null;
	    if(dbname.equals("..")) 
	    	return false;
	    try {
	        checkDB = SQLiteDatabase.openDatabase(dbname, null,
	                SQLiteDatabase.OPEN_READONLY);
	        checkDB.close();	    	
	    } catch (SQLiteException e) {
	        // database doesn't exist yet.	    	
	    }
	    return checkDB != null ? true : false;
	}
	
}
