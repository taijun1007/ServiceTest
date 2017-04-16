package com.cmlab.servicetest;

import java.util.ArrayList;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WeiXinFragment extends Fragment {
	public static final String TAG = "WeiXinFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_main, container, false);
		GridView gv = (GridView)v.findViewById(R.id.MainGridView);
		ArrayList<GridItem> gridItems = WeiXinGridItemLab.get(getActivity()).getGridItems();
		GridItemAdapter giAdapter = new GridItemAdapter(gridItems);
		gv.setAdapter(giAdapter);
		//add listener
		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent;
				switch(position) 
				{
				case 0:
					intent = new Intent(getActivity(), WeiXinTextMsgActivity.class);
					startActivity(intent);
					break;
				case 1:
					intent = new Intent(getActivity(), WeiXinImageMsgActivity.class);
					startActivity(intent);
					break;
				default:
					Toast.makeText(getActivity(), R.string.main_switch_default, Toast.LENGTH_SHORT).show();
				}
			}
		});
		return v;
	}
	
	private class GridItemAdapter extends ArrayAdapter<GridItem> {
		public GridItemAdapter(ArrayList<GridItem> gridItems) {
			super(getActivity(), 0, gridItems);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater()
						.inflate(R.layout.gridview_item_applist, null);
			}
			GridItem gi = getItem(position);
			ImageView appImage = (ImageView)convertView.findViewById(R.id.appImage);
			appImage.setImageResource(gi.getIconId());
			TextView appTitle = (TextView)convertView.findViewById(R.id.appTitle);
			appTitle.setText(gi.getTitleId());
			return convertView;
		}
	}

}
