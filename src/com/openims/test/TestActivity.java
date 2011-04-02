package com.openims.test;

import com.openims.R;
import com.openims.model.pushService.PushInfoManager;
import com.openims.utility.LogUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class TestActivity extends Activity {

	private static final String LOGTAG = LogUtil.makeLogTag(PushInfoManager.class);
	private static final String TAG = LogUtil.makeTag(PushInfoManager.class);
	private CheckBox pushContentCheck;
	private CheckBox pushRegCheck;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("DemoAppActivity", "onCreate()...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        
        pushContentCheck =(CheckBox)findViewById(R.id.testPushContentDB);
        pushRegCheck =(CheckBox)findViewById(R.id.testPushRegester);
        // Settings
        Button okButton = (Button) findViewById(R.id.start);
        okButton.setText("start test");
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { 
            	if(pushContentCheck.isChecked()){
            		Log.d(LOGTAG,TAG+"start push content database test");
            		PushContentDBTest ptest = new PushContentDBTest(TestActivity.this);
            		//ptest.testRecreateTable();
            		ptest.testInsert(20);
            		ptest.testQuery();
            		ptest.testDelete();
            		ptest.testDelete();
            		ptest.testDelete();
            		ptest.testQuery();
            		
            	}
            	if(pushRegCheck.isChecked()){
            		Log.d(LOGTAG,TAG+"start push register database test");
            	}
            }
        });
	}
		
}
