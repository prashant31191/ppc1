package com.openims.view.pcAssistant;

import com.openims.model.pushService.PushContentDB;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class AssistantActivity extends Activity {

	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		listView = new ListView(this);
		PushContentDB pcDB = new PushContentDB(this);
//		Cursor cursor = pcDB.queryItems();
//		startManagingCursor(cursor);
//		
//		ListAdapter listAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_expandable_list_item_1, 
//				cursor,
//				new String[]{"content"}, 
//				new int[]{android.R.id.text1});
		
		Cursor cursor = getContentResolver().query(People.CONTENT_URI, null, null, null, null);
		startManagingCursor(cursor);
		
		ListAdapter listAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_expandable_list_item_1, 
				cursor,
				new String[]{People.NAME}, 
				new int[]{android.R.id.text1});
		listView.setAdapter(listAdapter);
		setContentView(listView);
	}
}
