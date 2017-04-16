package com.cmlab.servicetest;

import android.app.Fragment;

public class TaskListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new TaskListFragment();
	}

}
