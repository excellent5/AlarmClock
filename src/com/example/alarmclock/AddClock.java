package com.example.alarmclock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
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

public class AddClock extends Activity{
	Button quit,save;
	TimePicker tp;
	String timestring=null;
	SQLiteDatabase db;
	ListView lv;
	int[] number=new int[]{0,0,0,0,0,0,0};
	String repeat="";

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


	public void ShowFrequency(int[] array){
		String repeat1="Repeat on single week: ";
		String repeat2="Repeat on dual week: ";
		String repeat3="Repeat on every week: ";
		int count1=0,count2=0,count3=0;
		for(int i=0;i<array.length;i++){
			switch(array[i]){
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
			
		
		
		String[] arr={"Frequency","Ring"};
		String[] arr2=new String[2];
		arr2[0]=repeat;
		arr2[1]="Guitar";
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
		setContentView(R.layout.addclock);
		quit=(Button) findViewById(R.id.QuitInAddClock);
		save=(Button) findViewById(R.id.SaveInAddClock);
		tp=(TimePicker) findViewById(R.id.TimePickerInAddClock);
		tp.setIs24HourView(true);
		Calendar cl=Calendar.getInstance();
		tp.setCurrentHour(cl.get(Calendar.HOUR_OF_DAY));
		tp.setCurrentMinute(cl.get(Calendar.MINUTE));
		db=SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().getPath()+"/myclock.db", null);
		lv=(ListView) findViewById(R.id.ListViewInAddClock);

		String[] arr={"Frequency","Ring"};
		String[] arr2={"No repeat","Guitar"};
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
		
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if(position==0){
					Intent intent4=new Intent(AddClock.this,ExpandableListViewTest.class);
					Bundle data=new Bundle();
					if(Arrays.equals(number,new int[]{0,0,0,0,0,0,0}))
						data.putString("time", "");
					else {
						String tmp="";
						for(int i=0;i<number.length;i++){
							tmp=tmp+String.valueOf(number[i]);
						}
						data.putString("time", tmp);
					}
					intent4.putExtras(data);
					startActivityForResult(intent4,0);
				}
				
				else if(position==1){
					//��ת������ѡ��
				}
			}
			
		});
		
		quit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View source){
				Intent intent1=new Intent(AddClock.this,MainActivity.class);
				setTitle("Alarmclock");
				startActivity(intent1);
				finish();
			}
		});
		
		save.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View source){
				ContentValues cv=new ContentValues();
				if(timestring!=null){
					cv.put("time", timestring);
					cv.put("condition", 1);
					cv.put("Monday", number[0]);
					cv.put("Tuesday", number[1]);
					cv.put("Wednesday", number[2]);
					cv.put("Thursday", number[3]);
					cv.put("Friday", number[4]);
					cv.put("Saturday", number[5]);
					cv.put("Sunday", number[6]);
					db.insert("clockdata",null,cv);								
				}
				
				else{
					Calendar c=Calendar.getInstance();
					if(repeat==""){
						cv.put("time",format(c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE)));
						cv.put("condition",1);
						cv.put("Monday", number[0]);
						cv.put("Tuesday", number[1]);
						cv.put("Wednesday", number[2]);
						cv.put("Thursday", number[3]);
						cv.put("Friday", number[4]);
						cv.put("Saturday", number[5]);
						cv.put("Sunday", number[6]);
						db.insert("clockdata",null,cv);
					}
									
				}			
				Intent intent2=new Intent(AddClock.this,MainActivity.class);
				setTitle("Alarmclock");
				startActivity(intent2);
				finish();
			}
		});
		
		tp.setOnTimeChangedListener(new OnTimeChangedListener(){
			@Override
			public void onTimeChanged(TimePicker view,int hourOfDay,int minute){
				timestring=format(hourOfDay,minute);
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
