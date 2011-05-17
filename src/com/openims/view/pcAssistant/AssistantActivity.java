package com.openims.view.pcAssistant;

import com.openims.R;
import com.openims.model.pushService.PushContentDB;
import com.openims.utility.Utility;

import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class AssistantActivity extends ListActivity {

	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.assistant_list);
		
		
		PushContentDB pcDB = new PushContentDB(this);
		Cursor cursor = pcDB.queryItems();
		startManagingCursor(cursor);
		
		SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(
				this,
				R.layout.assistant_col,
				cursor,
				new String[]{PushContentDB.CONTENT,
							 PushContentDB.TIME,
							 PushContentDB.SIZE},
				new int[]{R.id.as_content,R.id.as_time,R.id.as_size});
		//listAdapter.setViewBinder(viewBinder);
		getListView().setAdapter(listAdapter);
		
		
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Toast.makeText(this, "hint me", Toast.LENGTH_LONG);
	}
	
	public class ImageBlobViewBinder implements SimpleCursorAdapter.ViewBinder { 
	    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

	        /*if (columnIndex == 1) { 
	            byte[] bitmap = cursor.getBlob(cursor.getColumnIndex("image_data"));
	            Bitmap myImage = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
	            ((ImageView) view).setImageResource(myImage); 
	           
	            return true; 
	        }*/

	        return false; 
	    } 
	}
}
