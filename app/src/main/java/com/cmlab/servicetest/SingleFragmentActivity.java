package com.cmlab.servicetest;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.KeyEvent;

public abstract class SingleFragmentActivity extends Activity {
	
	private boolean mIsButtonClick = false;
	
	protected abstract Fragment createFragment();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
        	fragment = createFragment();
        	fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        NotificationExtend notification = new NotificationExtend(this);
        notification.cancelNotification();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mIsButtonClick) {
			NotificationExtend notification = new NotificationExtend(this);
			notification.showNotification();
			//moveTaskToBack(true);
		}		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK)
				|| (keyCode == KeyEvent.KEYCODE_HOME)
				|| (keyCode == KeyEvent.KEYCODE_APP_SWITCH)) {
			mIsButtonClick = false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean isIsButtonClick() {
		return mIsButtonClick;
	}

	public void setIsButtonClick(boolean isButtonClick) {
		mIsButtonClick = isButtonClick;
	}

}
