package com.example.alarmclock;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;


public class ExpandableListViewTest extends Activity{
	String time;
	SQLiteDatabase db;
	Intent intent;
	String LineName;
	int frequency[]=new int[]{0,0,0,0,0,0,0};
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expandablelistview);	
		db=SQLiteDatabase.openOrCreateDatabase(ExpandableListViewTest.this.getFilesDir().getPath()+"/myclock.db", null);
		intent=getIntent();
		Bundle data=intent.getExtras();
		time=data.getString("time");
	
		if(time.length()==7){
			for(int i=0;i<frequency.length;i++)
				frequency[i]=(time.charAt(i)-'0');
		}
		
		else if(time.length()==5){
			Cursor cursor=db.rawQuery("select * from clockdata where time=?", new String[]{time});
			cursor.moveToNext();
			for(int i=0;i<frequency.length;i++)		
				frequency[i]=cursor.getInt(i+3);
		}
		
		Button back=(Button) findViewById(R.id.QuitInExpandableListView);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) { 
				intent.putExtra("repeat",frequency);
				ExpandableListViewTest.this.setResult(0,intent);
				ExpandableListViewTest.this.finish();			
			}
			
		});
		
		ExpandableListAdapter adapter = new BaseExpandableListAdapter(){
 			private String[] DayOfTheWeek = new String[]
				{ "Monday", "Tuesday", "Wednesday","Thursday","Friday","Saturday","Sunday"};
			

			@Override
			public Object getChild(int groupPosition, int childPosition){
				return new RadioGroup(ExpandableListViewTest.this);
			}

			@Override
			public long getChildId(int groupPosition, int childPosition){
				return childPosition;
			}

			@Override
			public int getChildrenCount(int groupPosition){
				return 1;
			}

			@Override
			public View getChildView(final int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent){
				RadioGroup rg=new RadioGroup(ExpandableListViewTest.this);					
			//	rg.setPadding(30, 10, 30, 10);
				RadioButton rb1=new RadioButton(ExpandableListViewTest.this);
				rb1.setText("No repeat");
				rb1.setId(0);
				RadioButton rb2=new RadioButton(ExpandableListViewTest.this);
				rb2.setText("Repeat on single week");
				rb2.setId(1);
				RadioButton rb3=new RadioButton(ExpandableListViewTest.this);
				rb3.setText("Repeat on dual week");
				rb3.setId(2);
				RadioButton rb4=new RadioButton(ExpandableListViewTest.this);
				rb4.setText("Repeat on every week");
				rb4.setId(3);
				rg.addView(rb1,0);
				rg.addView(rb2,1);
				rg.addView(rb3,2);
				rg.addView(rb4,3);
	
				rg.check(frequency[groupPosition]);
								
				rg.setOnCheckedChangeListener(new OnCheckedChangeListener(){
					@Override
					public void onCheckedChanged(RadioGroup arg0, int id) {
						frequency[groupPosition]=id;
					}
					
				});
							
				return rg;
			}

			@Override
			public Object getGroup(int groupPosition){
				return DayOfTheWeek[groupPosition];
			}

			@Override
			public int getGroupCount(){
				return DayOfTheWeek.length;
			}

			@Override
			public long getGroupId(int groupPosition){
				return groupPosition;
			}

			@Override
			public View getGroupView(int groupPosition, boolean isExpanded,
					View convertView, ViewGroup parent){
				TextView tv = new TextView(ExpandableListViewTest.this);
				tv.setText(getGroup(groupPosition).toString());
				tv.setTextSize(30);
				return tv;
			}

			@Override
			public boolean isChildSelectable(int groupPosition,
					int childPosition){
				return true;
			}

			@Override
			public boolean hasStableIds(){
				return true;
			}
		};
		ExpandableListView expandListView = (ExpandableListView) findViewById(R.id.ExpandableListView);
		expandListView.setAdapter(adapter);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(db!=null)
			db.close();
	}
}