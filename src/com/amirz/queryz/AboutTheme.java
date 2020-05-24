package com.amirz.queryz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AboutTheme extends Activity{

	Intent intent;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_theme_about);		
	}
	
	public void onBackPressed(){		
		intent = new Intent(AboutTheme.this, Main.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
	
}
