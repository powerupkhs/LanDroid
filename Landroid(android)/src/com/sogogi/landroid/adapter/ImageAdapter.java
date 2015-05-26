package com.sogogi.landroid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sogogi.landroid.MainActivity;
import com.sogogi.landroid.R;
import com.sogogi.landroid.model.ViewHolder;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;

	public ImageAdapter(Context c) {
		mContext = c;
	}

	@Override
	public int getCount() {
		return MainActivity.ICONS.length;
	}

	// Create a new ImageView for each item referenced by the Adapter
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("jisu", "getView 불림");
		View v = convertView;
		ViewHolder holder;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.dashboard_icon, null);

			holder = new ViewHolder();
			holder.setText((TextView) v.findViewById(R.id.dashboard_icon_text));
			holder.setIcon((ImageView) v.findViewById(R.id.dashboard_icon_img));
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		holder.getIcon().setImageResource(MainActivity.ICONS[position].getImgId());
		holder.getText().setText(MainActivity.ICONS[position].getText());
		return v;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
