package com.example.alarmclock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class EditClock extends Activity{
	Button quit,save,delete;
	TimePicker tp;
	ListView lv;
	String timestring=null;
	SQLiteDatabase db;
	Cursor cursor;
	int index;
	String time;
	int[] number=new int[]{0,0,0,0,0,0,0};
	String repeat="";
	Intent intent;
	
	public String ChangeToChinese(int i){
		switch(i){
		case 0:
			return "周一";
		case 1:
			return "周二";
		case 2:
			return "周三";
		case 3:
			return "周四";
		case 4:
			return "周五";
		case 5:
			return "周六";
		case 6:
			return "周日";
		default:
			return "error!";					
		}
	}
	
	public void ShowFrequency(int[] array){
		String repeat1="单周重复: ";
		String repeat2="双周重复: ";
		String repeat3="每周重复: ";
		int count1=0,count2=0,count3=0;
		for(int i=0;i<array.length;i++){
			switch(array[i]){
				case 1:
					count1++;
					if(count1==3){
						repeat1=repeat1+"\n";
						count1=0;
					}
					repeat1=repeat1+ChangeToChinese(i)+"、";
					break;
			
				case 2:
					count2++;
					if(count2==3){
						repeat2=repeat2+"\n";
						count2=0;
					}
					repeat2=repeat2+ChangeToChinese(i)+"、";
					break;
			
				case 3:
					count3++;
					if(count3==3){
						repeat3=repeat3+"\n";
						count3=0;
					}
					repeat3=repeat3+ChangeToChinese(i)+"、";
					break;
			
				default:
					break;
			}
		}
		repeat1=repeat1.substring(0, repeat1.length()-1);
		repeat2=repeat2.substring(0, repeat2.length()-1);
		repeat3=repeat3.substring(0, repeat3.length()-1);
		
		if(repeat1.equals("单周重复:")){
			if(repeat2.equals("双周重复:")){
				if(repeat3.equals("每周重复:"))
					repeat="无";
				else repeat=repeat3;
			}
			else{
				if(repeat3.equals("每周重复:"))
					repeat=repeat2;
				else repeat=repeat2+"\n"+repeat3;
			}
		}
		else{
			if(repeat2.equals("双周重复:")){
				if(repeat3.equals("每周重复:"))
					repeat=repeat1;
				else repeat=repeat1+"\n"+repeat3;
			}
			else{
				if(repeat3.equals("每周重复:"))
					repeat=repeat1+"\n"+repeat2;
				else repeat=repeat1+"\n"+repeat2+"\n"+repeat3;
			}
		}
			
		
		
		String[] arr={"重复","铃声"};
		String[] arr2=new String[2];
		arr2[0]=repeat;
		arr2[1]="吉他扫弦";
		List<Map<String,String>> listItems=new ArrayList<Map<String,String>>();
		for(int i=0;i<arr.length;i++){
			Map<String,String> listItem=new HashMap<String,String>();
			listItem.put("option", arr[i]);
			listItem.put("result", arr2[i]);
			listItems.add(listItem);
		}
	
		SimpleAdapter adapter=new SimpleAdapter(this,listItems,R.layout.perference,new String[]
			{"option","result"},new int[]{R.id.TextView1InEditClock,R.id.TextView2InEditClock});
		lv.setAdapter(adapter);
		lv.invalidate();	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		if (requestCode == 0 && resultCode == 0){
			Bundle data = intent.getExtras();
			number = data.getIntArray("repeat");
			ShowFrequency(number);
		}
	}
						
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editclock);
		intent=getIntent();
		index=intent.getIntExtra("index",-1);
		quit=(Button) findViewById(R.id.QuitInEditClock);
		save=(Button) findViewById(R.id.SaveInEditClock);
		delete=(Button) findViewById(R.id.DeleteInEditClock);
		lv=(ListView) findViewById(R.id.ListViewInEditClock);
		tp=(TimePicker) findViewById(R.id.TimePickerInEditClock);
		tp.setIs24HourView(true);
		db=SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().getPath()+"/myclock.db", null);
		cursor=db.rawQuery("Select * from clockdata", null);
		cursor.moveToPosition(index);
		time=cursor.getString(1);
		String [] tmp=time.split(":");
		tp.setCurrentHour(Integer.parseInt(tmp[0]));
		tp.setCurrentMinute(Integer.parseInt(tmp[1]));
		
		int[] intf=new int[7];
		for(int i=0;i<7;i++)
			intf[i]=cursor.getInt(i+3);
		ShowFrequency(intf);
		
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if(position==0){
					int count=-1;
					for(int i=3;i<10;i++){
						if(cursor.getInt(i)!=0)
							count++;
					}
					Intent intent2=new Intent(MainActivity.context,AlarmActivity.class);
					AlarmManager am2=(AlarmManager) getSystemService(Service.ALARM_SERVICE);
					if(count==-1){
						PendingIntent pi2=PendingIntent.getActivity(MainActivity.context,-cursor.getInt(0),intent2,0);
						am2.cancel(pi2);
					}
					while(count>=0){
						PendingIntent pi2=PendingIntent.getActivity(MainActivity.context,7*cursor.getInt(0)+count,intent2,0);
						am2.cancel(pi2);
						count--;
					}
					
					Intent intent4=new Intent(EditClock.this,ExpandableListViewTest.class);
					Bundle data=new Bundle();
					data.putString("time", time);
					intent4.putExtras(data);
					startActivityForResult(intent4,0);
				}
				
				else if(position==1){
					//跳转到铃声选择
				}
			}
			
		});
		
		quit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View source){
				Intent intent1=new Intent(EditClock.this,MainActivity.class);
				setTitle("闹钟");
				startActivity(intent1);
				finish();
			}
		});
		
		save.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View source){
				ContentValues cv=new ContentValues();
				Cursor cursor=db.rawQuery("select * from clockdata",null);
				if(timestring!=null){	
					cv.put("time",timestring);
					cv.put("Monday", number[0]);
					cv.put("Tuesday", number[1]);
					cv.put("Wednesday", number[2]);
					cv.put("Thursday", number[3]);
					cv.put("Friday", number[4]);
					cv.put("Saturday", number[5]);
					cv.put("Sunday", number[6]);
					cursor.moveToPosition(index);
					db.update("clockdata", cv, "time=?", new String[]{cursor.getString(1)});
				}
				
				else{
					cv.put("Monday", number[0]);
					cv.put("Tuesday", number[1]);
					cv.put("Wednesday", number[2]);
					cv.put("Thursday", number[3]);
					cv.put("Friday", number[4]);
					cv.put("Saturday", number[5]);
					cv.put("Sunday", number[6]);
					cursor.moveToPosition(index);
					db.update("clockdata", cv, "time=?", new String[]{cursor.getString(1)});
				}
				
				Intent intent1=new Intent(EditClock.this,MainActivity.class);
				setTitle("闹钟");
				startActivity(intent1);
				finish();
			}
		});
		
		tp.setOnTimeChangedListener(new OnTimeChangedListener(){
			@Override
			public void onTimeChanged(TimePicker view,int hourOfDay,int minute){
				timestring=format(hourOfDay,minute);
			}
		});
		
		delete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				cursor.moveToPosition(index);
				int count=-1;
				for(int i=3;i<10;i++){
					if(cursor.getInt(i)!=0)
						count++;
				}
				Intent intent2=new Intent(MainActivity.context,AlarmActivity.class);
				AlarmManager am2=(AlarmManager) getSystemService(Service.ALARM_SERVICE);
				if(count==-1){
					PendingIntent pi2=PendingIntent.getActivity(MainActivity.context,-cursor.getInt(0),intent2,0);
					am2.cancel(pi2);
				}
				while(count>=0){
					PendingIntent pi2=PendingIntent.getActivity(MainActivity.context,7*cursor.getInt(0)+count,intent2,0);
					am2.cancel(pi2);
					count--;
				}
				db.delete("clockdata", "time=?", new String[]{cursor.getString(1)});
				Intent intent1=new Intent(EditClock.this,MainActivity.class);
				setTitle("闹钟");
				startActivity(intent1);
				finish();
			}		
		});
		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(db!=null)
			db.close();
	}
	
	public String format(int hour,int minute){
		String hourstr = String.valueOf(hour),minutestr = String.valueOf(minute);
		if(hour<10)
			hourstr="0"+hourstr;
		if(minute<10)
			minutestr="0"+minutestr;
		return (hourstr+":"+minutestr);
	}
	

}
