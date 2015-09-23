package com.example.alarmclock;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {
	SQLiteDatabase db;
	Button bn1,bn2;
	ListView lv;
	int i=0;
	int id;
	static Context context;
	ArrayList<Integer> multiple=new ArrayList<Integer>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context=this;
		db=SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().getPath()+"/myclock.db", null);
		
		lv=(ListView) findViewById(R.id.lv);
		bn1=(Button) findViewById(R.id.bn1);
		bn2=(Button) findViewById(R.id.bn2);
		
		bn1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View source){ 
				lv.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {						
						Intent ToEdit=new Intent(MainActivity.this,EditClock.class);
						Bundle data=new Bundle();
						data.putInt("index",position);
						ToEdit.putExtras(data);
						startActivity(ToEdit);
						finish();		
					}
							
				});
				
			}
		});
		
		bn2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View source){
				Intent intent0=new Intent(MainActivity.this,AddClock.class);
				startActivity(intent0);
				finish();
			}
		});
		
		try{
			Cursor cursor=db.rawQuery("select * from clockdata", null);
			inflateList(cursor);
		}
		catch (SQLiteException e){
			String sql="create table clockdata (_id INTEGER PRIMARY KEY autoincrement,time varchar(10),condition INTEGER," +
					"Monday INTEGER,Tuesday INTEGER,Wednesday INTEGER,Thursday INTEGER,Friday INTEGER," +
					"Saturday INTEGER,Sunday INTEGER)";	
			db.execSQL(sql);
			Cursor cursor=db.rawQuery("select * from clockdata", null);
			inflateList(cursor);
		}
		
		getAlarmTime(db.rawQuery("select * from clockdata where condition = 1",null));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@SuppressLint("NewApi")
	public void inflateList(final Cursor cursor){
		if(cursor.getCount()!=0){
			BaseAdapter adapter=new BaseAdapter(){
        		@Override
        		public int getCount(){
        			return cursor.getCount();
        		}
        		
        		@Override
        		public Object getItem(int position){
        			return null;
        		}
        		
        		@Override
        		public long getItemId(int position){
        			return position;
        		}
        		
        		@Override
        		public View getView(final int position,View convertView,ViewGroup parent){
        			cursor.moveToPosition(position);
        			TextView text=new TextView(MainActivity.this);
        			text.setText(cursor.getString(1));
        			text.setTextSize(30);
        			text.setId(1);
        			text.setFocusable(false);
        			TextView text2=new TextView(MainActivity.this);
        			text2.setText(ShowFrequency(cursor));
        			text2.setTextSize(15);
        			text2.setFocusable(false);
        			final Switch switcher=new Switch(MainActivity.this);
        			switcher.setFocusable(false);
        			switcher.setHeight(100);
        			if(cursor.getInt(2)==1)
        				switcher.setChecked(true);
        			else switcher.setChecked(false);
        			
        			RelativeLayout line=new RelativeLayout(MainActivity.this);
        			line.setPadding(20,20,20,20);
        			lv.addFooterView(line);
        			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        			line.addView(text,lp);
        		   			
        			RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        			lp2.addRule(RelativeLayout.BELOW, 1);
        			line.addView(text2,lp2);
            		
        			RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        			lp3.addRule(RelativeLayout.ALIGN_TOP,1);
        			lp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        			lp3.rightMargin=4;
        			line.addView(switcher,lp3);
        		      			       						
        			switcher.setOnCheckedChangeListener(new OnCheckedChangeListener(){
						@Override
						public void onCheckedChanged(CompoundButton arg0,boolean isChecked) {
							if(isChecked){
								ContentValues cv=new ContentValues();
								cv.put("condition", 1);
								cursor.moveToPosition(position);
								db.update("clockdata", cv, "time=?", new String[]{cursor.getString(1)});
								getAlarmTime(db.rawQuery("select * from clockdata where time=?",new String[]{cursor.getString(1)}));
							}
							
							else{
								ContentValues cv=new ContentValues();
								cv.put("condition", 0);
								cursor.moveToPosition(position);
								db.update("clockdata", cv, "time=?", new String[]{cursor.getString(1)});
								int count=-1;
								for(int i=3;i<10;i++){
									if(cursor.getInt(i)!=0)
										count++;
								}
												
								Intent intent2=new Intent(MainActivity.this,AlarmActivity.class);
								AlarmManager am2=(AlarmManager) getSystemService(Service.ALARM_SERVICE);
								if(count==-1){
									PendingIntent pi2=PendingIntent.getActivity(MainActivity.this,-cursor.getInt(0),intent2,0);
									am2.cancel(pi2);
								}
								while(count>=0){
									PendingIntent pi2=PendingIntent.getActivity(MainActivity.this,7*cursor.getInt(0)+count,intent2,0);
									am2.cancel(pi2);
									count--;
								}
							}												
						}
        				
        			});    	
        			return line;
        		}  		
        	};
        	
        	lv.setAdapter(adapter);	
			
        }
	}
		
	
	public void getAlarmTime(Cursor cur){
		while(cur.moveToNext()){
			String tmp=cur.getString(1);
			Alarm(Integer.parseInt(tmp.split(":")[0]),Integer.parseInt(tmp.split(":")[1]),cur);
		}
	}
	
	public void Alarm(int hour,int minute,Cursor cursor){	
		AlarmManager am=(AlarmManager) getSystemService(Service.ALARM_SERVICE);	
		ArrayList<Calendar> alc=getAlarmTimeCalendar(hour,minute,cursor);
		if(alc.size()==0){
			Intent intent=new Intent(MainActivity.this,AlarmActivity.class);
			intent.putExtra("id", -cursor.getInt(0));
			PendingIntent pi=PendingIntent.getActivity(MainActivity.this,-cursor.getInt(0),intent,0);
			Calendar c=Calendar.getInstance();
			c.setTimeInMillis(System.currentTimeMillis());
			c.set(Calendar.HOUR_OF_DAY,hour);
			c.set(Calendar.MINUTE, minute);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND,0);
			
			if(c.getTimeInMillis()<System.currentTimeMillis()){
				am.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis()+(24*60*60*1000),pi);
			}
			else{
				am.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pi);
			}
		}
		
		else{
			for(int i=0;i<alc.size();i++){
				Intent intent=new Intent(MainActivity.this,AlarmActivity.class);
				intent.putExtra("id", i);
				PendingIntent pi=PendingIntent.getActivity(MainActivity.this,7*cursor.getInt(0)+i,intent,0);
				am.setRepeating(AlarmManager.RTC_WAKEUP, alc.get(i).getTimeInMillis(), 
						multiple.get(i)*7*24*60*60*1000, pi);
			}
		}		
	}
	
	public ArrayList<Calendar> getAlarmTimeCalendar(int hour,int minute,Cursor cursor){
		int Today = 0,Toyear=2015;
		ArrayList<Calendar> cal=new ArrayList<Calendar>();
		multiple=new ArrayList<Integer>();
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		
		
		if(c.get(Calendar.YEAR)==Toyear){
			Today=c.get(Calendar.DAY_OF_YEAR);
		}
		
		else if(c.get(Calendar.YEAR)==Toyear+1){
			Calendar tmp=Calendar.getInstance();
			tmp.set(Calendar.YEAR, Toyear);
			Today=tmp.getActualMaximum(Calendar.DAY_OF_YEAR);
		}
		
		else {
			new AlertDialog.Builder(MainActivity.this).setTitle("ERROR!").setMessage(R.string.error_message)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.this.finish();
				}
			}).show();
		}
		
		int weekdistance=(Today-252)/7;
		
		for(int i=3;i<10;i++){
			switch(cursor.getInt(i)){
				case 1:
					if(weekdistance%2==0){
						Calendar tmp=Calendar.getInstance();
						tmp.setFirstDayOfWeek(Calendar.MONDAY);
						int dayofweek=((i==9)?1:(i-1));
						tmp.set(tmp.get(Calendar.YEAR), tmp.get(Calendar.MONTH), tmp.get(Calendar.DAY_OF_MONTH));
						tmp.get(Calendar.DAY_OF_MONTH);
						tmp.set(Calendar.DAY_OF_WEEK,dayofweek);
						tmp.set(Calendar.HOUR_OF_DAY,hour);
						tmp.set(Calendar.MINUTE, minute);
						tmp.set(Calendar.SECOND, 0);
						tmp.set(Calendar.MILLISECOND,0);
						if(tmp.getTimeInMillis()<c.getTimeInMillis())
							tmp.add(Calendar.DATE,14);
						cal.add(tmp);
						multiple.add(2);
					}
					
					else{
						Calendar tmp=Calendar.getInstance();
						tmp.setFirstDayOfWeek(Calendar.MONDAY);
						int dayofweek=((i==9)?1:(i-1));
						tmp.set(tmp.get(Calendar.YEAR), tmp.get(Calendar.MONTH), tmp.get(Calendar.DAY_OF_MONTH));
						tmp.get(Calendar.DAY_OF_MONTH);
						tmp.set(Calendar.DAY_OF_WEEK,dayofweek);
						tmp.set(Calendar.HOUR_OF_DAY,hour);
						tmp.set(Calendar.MINUTE, minute);
						tmp.set(Calendar.SECOND, 0);
						tmp.set(Calendar.MILLISECOND,0);
						tmp.add(Calendar.DATE,7);
						cal.add(tmp);
						multiple.add(2);
					}
					break;
					
				case 2:
					if(weekdistance%2==1){
						Calendar tmp=Calendar.getInstance();					
						tmp.setFirstDayOfWeek(Calendar.MONDAY);
						int dayofweek=((i==9)?1:(i-1));
						tmp.set(tmp.get(Calendar.YEAR), tmp.get(Calendar.MONTH), tmp.get(Calendar.DAY_OF_MONTH));
						tmp.get(Calendar.DAY_OF_MONTH);
						tmp.set(Calendar.DAY_OF_WEEK,dayofweek);
						tmp.set(Calendar.HOUR_OF_DAY,hour);
						tmp.set(Calendar.MINUTE, minute);
						tmp.set(Calendar.SECOND, 0);
						tmp.set(Calendar.MILLISECOND,0);
						if(tmp.getTimeInMillis()<System.currentTimeMillis())
							tmp.add(Calendar.DATE,14);
						cal.add(tmp);
						multiple.add(2);
					}
					
					else{
						Calendar tmp=Calendar.getInstance();
						tmp.setFirstDayOfWeek(Calendar.MONDAY);
						int dayofweek=((i==9)?1:(i-1));
						tmp.set(tmp.get(Calendar.YEAR), tmp.get(Calendar.MONTH), tmp.get(Calendar.DAY_OF_MONTH));
						tmp.get(Calendar.DAY_OF_MONTH);
						tmp.set(Calendar.DAY_OF_WEEK,dayofweek);
						tmp.set(Calendar.HOUR_OF_DAY,hour);
						tmp.set(Calendar.MINUTE, minute);
						tmp.set(Calendar.SECOND, 0);
						tmp.set(Calendar.MILLISECOND,0);
						tmp.add(Calendar.DATE,7);
						cal.add(tmp);
						multiple.add(2);
					}
					break;
				
				case 3:
					Calendar tmp=Calendar.getInstance();
					tmp.setFirstDayOfWeek(Calendar.MONDAY);
					int dayofweek=((i==9)?1:(i-1));
					tmp.set(tmp.get(Calendar.YEAR), tmp.get(Calendar.MONTH), tmp.get(Calendar.DAY_OF_MONTH));
					tmp.get(Calendar.DAY_OF_MONTH);
					tmp.set(Calendar.DAY_OF_WEEK,dayofweek);
					tmp.set(Calendar.HOUR_OF_DAY,hour);
					tmp.set(Calendar.MINUTE, minute);
					tmp.set(Calendar.SECOND, 0);
					tmp.set(Calendar.MILLISECOND,0);
					if(tmp.getTimeInMillis()<c.getTimeInMillis())
						tmp.add(Calendar.DATE,7);
					cal.add(tmp);
					multiple.add(1);
					break;
				
				default:
					break;				
			}
		}
		return cal;
	}
	
	public String ChangeToChinese(int i){
		switch(i){
		case 0:
			return "Monday";
		case 1:
			return "Tuesday";
		case 2:
			return "Wednesday";
		case 3:
			return "Thursday";
		case 4:
			return "Friday";
		case 5:
			return "Saturday";
		case 6:
			return "Sunday";
		default:
			return "error!";					
		}
	}
	
	public String ShowFrequency(Cursor cursor){
		String repeat="";
		String repeat1="Repeat on single week: ";
		String repeat2="Repeat on dual week: ";
		String repeat3="Repeat on every week: ";
		int count1=0,count2=0,count3=0;
		for(int i=0;i<7;i++){
			switch(cursor.getInt(i+3)){			
			case 1:
				count1++;
				if(count1==3){
					repeat1=repeat1+"\n";
					count1=0;
				}
				repeat1=repeat1+ChangeToChinese(i)+", ";
				break;
		
			case 2:
				count2++;
				if(count2==3){
					repeat2=repeat2+"\n";
					count2=0;
				}
				repeat2=repeat2+ChangeToChinese(i)+", ";
				break;
		
			case 3:
				count3++;
				if(count3==3){
					repeat3=repeat3+"\n";
					count3=0;
				}
				repeat3=repeat3+ChangeToChinese(i)+", ";
				break;
		
			default:
				break;
			}
		}
		
		repeat1=repeat1.substring(0, repeat1.length()-1);
		repeat2=repeat2.substring(0, repeat2.length()-1);
		repeat3=repeat3.substring(0, repeat3.length()-1);
		
		if(repeat1.equals("Repeat on single week:")){
			if(repeat2.equals("Repeat on dual week:")){
				if(repeat3.equals("Repeat on every week:"))
					repeat="No repeat";
				else repeat=repeat3;
			}
			else{
				if(repeat3.equals("Repeat on every week:"))
					repeat=repeat2;
				else repeat=repeat2+"\n"+repeat3;
			}
		}
		else{
			if(repeat2.equals("Repeat on dual week:")){
				if(repeat3.equals("Repeat on every week:"))
					repeat=repeat1;
				else repeat=repeat1+"\n"+repeat3;
			}
			else{
				if(repeat3.equals("Repeat on every week:"))
					repeat=repeat1+"\n"+repeat2;
				else repeat=repeat1+"\n"+repeat2+"\n"+repeat3;
			}
		}
		return repeat;		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(db!=null)
			db.close();
	}

}
