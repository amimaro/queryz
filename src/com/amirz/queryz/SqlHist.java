package com.amirz.queryz;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amirz.queryz.R;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class SqlHist extends ListActivity{
	
	SQLiteDatabase db;
	Cursor cursor;
	String dbname, dbop;
	Intent intent;
	
	File sdcardfileh = new File(Environment.getExternalStorageDirectory(), 
			File.separator + "queryz" + 
			File.separator + "history");
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sqlhist);
		dbname = getIntent().getStringExtra("dbname");
		try{
			db = SQLiteDatabase.openDatabase(
					sdcardfileh.toString()+File.separator+"History", 
					null, SQLiteDatabase.CREATE_IF_NECESSARY);
			
			db.execSQL("CREATE TABLE IF NOT EXISTS History(id INTEGER PRIMARY KEY, hist TINYTEXT)");
			cursor = db.rawQuery("SELECT hist FROM History", null);
			List<String> list = new ArrayList<String>();
			while(cursor.moveToNext()){
				list.add(cursor.getString(cursor.getColumnIndex("hist")));
			}
			
			ArrayAdapter<String> adapter = new 
					ArrayAdapter<String>(
							this, R.layout.activity_list_item1, list);
			
			setListAdapter(adapter);
		} catch(SQLException e){
			Toast.makeText(this, "Erro: "+e.getMessage(), Toast.LENGTH_LONG).show();
			finish();
		}
		
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				int selected = (int) getListAdapter().getItemId(arg2);
				selected++;
				db.execSQL("DELETE FROM History WHERE id ="+selected);
				cursor = db.rawQuery("SELECT * FROM History", null);					
				db.execSQL("UPDATE History SET id = id-1 WHERE id > "+selected);
				db.close();
				finish();
				startActivity(getIntent());
				return true;
			}
		    });
		
	}
	
	public void onListItemClick(ListView lv, View v, int position, long id){
		dbop = (String) getListAdapter().getItem(position);
		try{		
			db.close();
			intent = new Intent(SqlHist.this, SqlScript.class);
			intent.putExtra("dbname", dbname);
			intent.putExtra("dbop", dbop);			
			intent.putExtra("b", 1);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		} catch (Exception e){
			Toast.makeText(SqlHist.this, 
					"Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
		}
				
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_sqlhist, menu);        
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){		
		db.execSQL("DELETE FROM History");
		db.close();
		finish();
		return super.onOptionsItemSelected(item);
	}
	
	public void onBackPressed(){
		db.close();
		finish();
	}
	
	public void x(String s){		
		Log.println(Log.ASSERT, "DEBUG", s);		
	}
	
}
