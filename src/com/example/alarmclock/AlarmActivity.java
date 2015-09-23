package com.example.alarmclock;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class AlarmActivity extends Activity{
	MediaPlayer alarmMusic;
	SQLiteDatabase db;
	AlertDialog d;
	
	@Override
	 public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	 }
	
	@Override
	public void onResume(){
		super.onResume();	
		final Window win = getWindow();
		 win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
		 | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		 win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		 | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		db=SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().getPath()+"/myclock.db", null);
		Intent intent=getIntent();
		int id=intent.getIntExtra("id", 0);
		if(id<0){
			ContentValues cv=new ContentValues();
			cv.put("condition", 0);
			db.update("clockdata", cv, "_id=?", new String[]{String.valueOf(-id)});
		}
		try{
			alarmMusic = MediaPlayer.create(this, R.raw.alarm);
			alarmMusic.setLooping(true);
			alarmMusic.start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		Log.v("tag","create!");
		AlertDialog.Builder builder=new AlertDialog.Builder(AlarmActivity.this);
		builder.setTitle("Alarm").setMessage("It has been your set alarming time,Wake up!!!")
		.setPositiveButton("OK", new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which){
						alarmMusic.stop();
						alarmMusic.release();
						d.dismiss();
						AlarmActivity.this.finish();
					}
		}).show();
		
		d=builder.create();
		d.setCancelable(false);
		d.setCanceledOnTouchOutside(false);
		d.show();
		
		final Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg){
				if(msg.what==0x1233){
					if(alarmMusic!=null){
						alarmMusic.stop();
						alarmMusic.release();
					}
					if(d!=null){
						d.dismiss();
					}
					AlarmActivity.this.finish();
				}
			}
		};
		
		new Timer().schedule(new TimerTask(){
			@Override
			public void run() {
				handler.sendEmptyMessage(0x1233);		
			}
			
		}, 60000);
	}
	

	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.v("tag","destroy!");
		if(db!=null)
			db.close();
	}
	
}
