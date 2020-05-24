package com.amirz.queryz;

import java.io.File;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

import java.util.List;

import com.amirz.queryz.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

public class HelpScript extends Activity{
		
	String dbname;
	List<String> tnames;	
	SQLiteDatabase db;
	Cursor cursor;
	Intent intent;

	String KEY1 = "GROUP";  
	String KEY2 = "CHILD";
	String str;
	
	List<Map<String, String>> groupList;
	Map<String, String> auxGroupMap;
	List<List<Map<String, String>>> childList;
	List<Map<String, String>> auxChildMap1;
	Map<String, String> auxChildMap2;
	
	ExpandableListView expandableListView;
	ExpandableListAdapter expandableListAdapter;
	
	File sdcardfiledb = new File(Environment.getExternalStorageDirectory(), 
			File.separator + "queryz" + 
			File.separator + "databases");
	File sdcardfileh = new File(Environment.getExternalStorageDirectory(), 
			File.separator + "queryz" + 
			File.separator + "history");
	
	public void onCreate(Bundle savedInstanceState){
		try{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_theme_helpscript);				
			
			dbname = getIntent().getStringExtra("dbname");	
			db = SQLiteDatabase.openDatabase(
					dbname, 
					null, SQLiteDatabase.OPEN_READWRITE);
			
			expandableListView = (ExpandableListView) findViewById(R.id.expandableListView1);
												
			groupList = new ArrayList<Map<String, String>>();  
			childList = new ArrayList<List<Map<String, String>>>();
						
			getGroupInfo();
			getChildInfo();
									
			expandableListAdapter = new SimpleExpandableListAdapter(
					this, 
					groupList, 
					android.R.layout.simple_expandable_list_item_1, 
					new String[]{KEY1, KEY2}, 
					new int[] {android.R.id.text1, android.R.id.text2},
					childList,
					android.R.layout.simple_expandable_list_item_2, 
					new String[]{KEY1, KEY2}, 
					new int[] {android.R.id.text1, android.R.id.text2});									
			expandableListView.setAdapter(expandableListAdapter);
								
			expandableListView.setOnGroupClickListener(new OnGroupClickListener() {
				
				@Override
				public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2,
						long arg3) {
					
					return false;
				}
			});
			
			expandableListView.setOnChildClickListener(new OnChildClickListener() {
				
				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
					Object[] column = childList.get(groupPosition).get(childPosition).values().toArray();
					x((String) column[1]);					
					intent = new Intent();
					intent.putExtra("dbname", dbname);
					intent.putExtra("column", (String) column[1]);
					intent.putExtra("b", 2);
					setResult(RESULT_OK, intent);
					db.close();					
					finish();
					return false;
				}
			});
			
		} catch (Exception e){
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		db.close();
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
			
	public void x(String s){
		Log.println(Log.ASSERT, "teste", s);
	}
	
	public void getGroupInfo(){
		tnames = tablesName();
		//tnames.remove("android_metadata");
		for(String n : tnames){
			auxGroupMap = new HashMap<String, String>();
			groupList.add(auxGroupMap);
			auxGroupMap.put(KEY1, n+"(table)");
			auxGroupMap.put(KEY2, "");
		}
		tnames = viewsName();
		//tnames.remove("android_metadata");
		for(String n : tnames){
			auxGroupMap = new HashMap<String, String>();
			groupList.add(auxGroupMap);
			auxGroupMap.put(KEY1, n+"(view)");
			auxGroupMap.put(KEY2, "");
		}
	}
	
	public void getChildInfo(){		
		tnames = tablesName();
		//tnames.remove("android_metadata");
		tnames.addAll(viewsName());
		//tnames.remove("android_metadata");		
		for(String n : tnames){			;				
			cursor = db.rawQuery("PRAGMA TABLE_INFO(" + n + ")", null);
			auxChildMap1 = new ArrayList<Map<String,String>>();
			while(cursor.moveToNext()){
				auxChildMap2 = new HashMap<String, String>();
				auxChildMap1.add(auxChildMap2);
				String cname = cursor.getString(cursor.getColumnIndex("name"));
				str = cursor.getString(cursor.getColumnIndex("type"));
				if(cursor.getString(cursor.getColumnIndex("notnull")).equals("0")){
					str += " NULL ";
				} else 
					str += " NOTNULL ";
				if(!(cursor.getString(cursor.getColumnIndex("pk")).equals("0"))){
					str += "PK";
				}
				auxChildMap2.put(KEY1, cname);
				auxChildMap2.put(KEY2, str);
			}
			childList.add(auxChildMap1);
		}		
	}
	
	public void onBackPressed(){		
		db.close();
		finish();
	}
	
}

