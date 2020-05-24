package com.amirz.queryz;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.amirz.queryz.R;

/*import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;*/

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

public class DataManager extends Activity{

	List<String> autoList = new ArrayList<String>();
	
	SQLiteDatabase db;
	Cursor cursor;
	Intent intent;
	String dbname, dbop;	
	
	//Font colnameFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	//Font rowFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	
	String[] colnames, a;
	//File sdcardfileq;
		
	int b=0;
	
	TableRow tableRow;
	TableLayout tableLayout;
	TextView tableTextView;
	ScrollView scrollView;
	HorizontalScrollView hscrollView1, hscrollView2, hscrollView3;
	
	File sdcardfiledb = new File(Environment.getExternalStorageDirectory(), 
			File.separator + "queryz" + 
			File.separator + "databases");
	File sdcardfileh = new File(Environment.getExternalStorageDirectory(), 
			File.separator + "queryz" + 
			File.separator + "history");
	
	RelativeLayout rl;//
	RelativeLayout.LayoutParams p1 = 
			new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	RelativeLayout.LayoutParams p2 = 
			new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		rl = new RelativeLayout(this);//
		rl.setBackgroundColor(getResources().getColor(R.color.yellow));
		//setContentView(R.layout.activity_datamanager);		
		dbname = getIntent().getStringExtra("dbname");				
		b = getIntent().getIntExtra("b", 0);
		
		//db = openOrCreateDatabase(dbname, SQLiteDatabase.OPEN_READWRITE, null);
		
		//x(Integer.toString(db.getVersion()));		
		//TextView textdbname = (TextView) findViewById(R.id.textViewDbName);
		//textdbname.setText(" " + dbname);		
		TextView textdbname = new TextView(this);//
		textdbname.setText("Database: " + dbname);//		
		textdbname.setHorizontallyScrolling(true);
		hscrollView3 = new HorizontalScrollView(this);
		hscrollView3.setHorizontalScrollBarEnabled(false);
		hscrollView3.setId(1001);
		hscrollView3.addView(textdbname);
		rl.addView(hscrollView3);//
		
		hscrollView1 = new HorizontalScrollView(this);
		hscrollView2 = new HorizontalScrollView(this);
		scrollView = new ScrollView(this);
		TextView textdbop = new TextView(this);				
		/*sdcardfileq = new File(Environment.getExternalStorageDirectory(), 
				File.separator + "queryz" + 
				File.separator + "query" + File.separator + 
				dbname);
		if(!sdcardfileq.exists()){
			if(!sdcardfileq.mkdirs())
				Log.v("File", "Problem creating folder.");
			else sdcardfileq.mkdirs();
		}*/		
		
		if(b==1){
			db = SQLiteDatabase.openDatabase(
					//sdcardfiledb.toString()+File.separator+dbname,
					dbname,
					null, SQLiteDatabase.OPEN_READWRITE);			
			
			//x("Execute Button");
			dbop = getIntent().getStringExtra("dbop");
			textdbop.setText(dbop);
			hscrollView1.setPadding(2, 0, 5, 0);
			hscrollView1.setId(1002);
			hscrollView1.addView(textdbop);
			hscrollView1.setHorizontalScrollBarEnabled(false);
			hscrollView1.setVerticalScrollBarEnabled(false);
			p1.addRule(RelativeLayout.BELOW, 1001);
			rl.addView(hscrollView1, p1);//
			//addContentView(hscrollView1, 
			//		new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			autoList.clear();				
			try{
				
				if(dbop.startsWith("!")) dbop=dbop.substring(2);
				if(dbop.startsWith("S") || dbop.startsWith("s") ||
						dbop.startsWith("P") || dbop.startsWith("p")){
					cursor = db.rawQuery(dbop, null);
					Toast.makeText(
							DataManager.this, "Executed Successfully!", 
							Toast.LENGTH_SHORT).show();	
					Log.v("Query", dbop);
					createTable(cursor);						
				} else {
										
					db.execSQL(dbop);					
					Toast.makeText(
							DataManager.this, "Executed Successfully!", 
							Toast.LENGTH_SHORT).show();	
					Log.v("SQL", dbop);
				}
				db.close();
				db = SQLiteDatabase.openDatabase(
						sdcardfileh.toString()+File.separator+"History", 
						null, SQLiteDatabase.CREATE_IF_NECESSARY);				
				dbop = fixString(dbop);								
				db.execSQL("INSERT INTO History VALUES ((SELECT MAX(id) FROM History) + 1,'"+dbop+"')");
				db.close();
			} catch(SQLException e){			
				Toast.makeText(
						DataManager.this, "Error: "+e.getMessage(), 
						Toast.LENGTH_LONG).show();
				db.close();
				db = SQLiteDatabase.openDatabase(
						sdcardfileh.toString()+File.separator+"History", 
						null, SQLiteDatabase.CREATE_IF_NECESSARY);
				
				dbop = fixString(dbop);				
				db.execSQL("INSERT INTO History VALUES ((SELECT MAX(id) FROM History) + 1,'! "+dbop+"')");
				db.close();
			}
			b=0;
			db.close();
		} /*else if(b==2){
			x("Create pdf");
			dbop = getIntent().getStringExtra("dbop");			
			autoList.clear();
			try{	
				if(dbop.startsWith("S") || dbop.startsWith("s") ||
						dbop.startsWith("P") || dbop.startsWith("p")){
					cursor = db.rawQuery(dbop, null);
					Toast.makeText(
							DataManager.this, "Executed Successfully!", 
							Toast.LENGTH_LONG).show();	
					Log.v("Query", dbop);
					createPDF(cursor, dbname, dbop);
				} else {					
					Toast.makeText(
							DataManager.this, "No returned result.", 
							Toast.LENGTH_LONG).show();	
					Log.v("SQL", dbop);
				}
			} catch(SQLException e){
				Toast.makeText(
						DataManager.this, "Error: "+e.getMessage(), 
						Toast.LENGTH_LONG).show();
			}
			b=0;
		}*/			
		setContentView(rl);
		
		rl.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				openOptionsMenu();		
			}
		});
	}
	
	public void onBackPressed(){
		if(b==1) db.close();
		intent = new Intent(DataManager.this, ListDatabases.class);
		intent.putExtra("dbname", dbname.substring(0, dbname.lastIndexOf("/")));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_datamanager, menu);        
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.menu_cmd:
				intent = new Intent(this, SqlScript.class);	
				intent.putExtra("dbname", dbname);				
				startActivity(intent);				
				break;
			case R.id.menu_hist:
				intent = new Intent(this, SqlHist.class);	
				intent.putExtra("dbname", dbname);				
				startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void goScript(){
		intent = new Intent(this, SqlScript.class);
		startActivity(intent);
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
	
	/*public void createPDF(Cursor cursor, String dbname, String dbop){
		String FILEPDF = sdcardfileq + File.separator + 
				dbname  + "-" + dbop.hashCode() +".pdf";		
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(FILEPDF));
			document.open();
			generateData(cursor, document, dbop);
			document.close();			
		} catch (FileNotFoundException e) {
			Log.v("PDF", "Problem creating file. Not found.");
			e.printStackTrace();
		} catch (DocumentException e) {
			Log.v("PDF", "Problem creating file.");
			e.printStackTrace();
		}		
	}
	
	public void generateData(Cursor cursor, Document document, String dbop){				
		try {
			Paragraph paragraph = new Paragraph();
			paragraph.add(new Paragraph(dbop, new Font(
					Font.FontFamily.TIMES_ROMAN, 13, Font.BOLD)));
			paragraph.add(new Paragraph(" "));			
			document.add(paragraph);
			PdfPTable pTable = new PdfPTable(cursor.getColumnCount());
			PdfPCell pCell;
			pTable.setHorizontalAlignment(Element.ALIGN_LEFT);			
			if(cursor.getCount()>0){
				String[] names = cursor.getColumnNames();
				for(int i=0; i<cursor.getColumnCount(); i++){
					pCell = new PdfPCell(new Phrase(names[i], colnameFont));
					pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			    	pTable.addCell(pCell);				
				}			    		    
			    while(cursor.moveToNext()){
			    	for(String n: names){			    			    		
			    		pCell = new PdfPCell(new Phrase(cursor.getString(cursor.getColumnIndex(n))));
			    		pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					    pTable.addCell(pCell);			    		
			    	}			    	
			    }
			    document.add(pTable);
			} else {
				document.add(new Paragraph("No data"));
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}*/
	
	public void createTable(Cursor cursor){		
		colnames = cursor.getColumnNames();		
		tableLayout = new TableLayout(this);		
		tableLayout.setColumnStretchable(cursor.getColumnCount(), true);
		
		tableRow = new TableRow(this);
		tableRow.setPadding(0, 1, 0, 1);
		for(String n : colnames){
			tableTextView = new TextView(this);
			tableTextView.setText(n);			
			tableTextView.setPadding(5, 0, 5, 0);
			tableTextView.setGravity(Gravity.CENTER);
			tableTextView.setTextColor(getResources().getColor(R.color.white));
			tableTextView.setBackgroundColor(getResources().getColor(R.color.dkgray));
			tableRow.addView(tableTextView);
		}
		tableLayout.addView(tableRow);
		
		while(cursor.moveToNext()){
			tableRow = new TableRow(this);
			tableRow.setPadding(0, 1, 0, 1);
	    	for(String n: colnames){			    			    	
	    		tableTextView = new TextView(this);
				tableTextView.setText(cursor.getString(cursor.getColumnIndex(n)));			
				tableTextView.setPadding(5, 0, 5, 0);
				tableTextView.setGravity(Gravity.LEFT);		
				tableTextView.setBackgroundColor(getResources().getColor(R.color.ltgray));
				tableRow.addView(tableTextView);		    		
	    	}			    	
	    	tableLayout.addView(tableRow);
	    }		
		hscrollView2.addView(tableLayout);
		hscrollView2.setHorizontalScrollBarEnabled(false);
		hscrollView2.setVerticalScrollBarEnabled(false);		
		scrollView.addView(hscrollView2);
		scrollView.setPadding(2, 0, 2, 2);
		scrollView.setHorizontalScrollBarEnabled(false);
		scrollView.setVerticalScrollBarEnabled(false);
		p2.addRule(RelativeLayout.BELOW, 1002);
		rl.addView(scrollView, p2);
		//addContentView(scrollView, 
		//		new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}	
	
	public void x(String s){
		Log.println(Log.ASSERT, "teste", s);		
	}
	
	public String fixString(String dbop){				
		return dbop.replaceAll("'", "''");
	}
	
}
