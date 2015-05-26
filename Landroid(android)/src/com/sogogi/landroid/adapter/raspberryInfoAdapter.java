package com.sogogi.landroid.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sogogi.landroid.R;
import com.sogogi.landroid.model.RaspberryInfo;
import com.sogogi.landroid.raspberrycontrol.TextProgressBar;

public class raspberryInfoAdapter extends ArrayAdapter<RaspberryInfo> {
	Context context;
	int layoutResourceId;
	List<RaspberryInfo> data = null;

	public raspberryInfoAdapter(Context context, int layoutResourceId, List<RaspberryInfo> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		InfoHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new InfoHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
			holder.txtName = (TextView) row.findViewById(R.id.txtTitle);
			holder.txtDescription = (TextView) row.findViewById(R.id.txtDesc);
			holder.progressBar = (TextProgressBar) row.findViewById(R.id.ProgressBar);

			row.setTag(holder);
		} else {
			holder = (InfoHolder) row.getTag();
		}

		RaspberryInfo info = data.get(position);
		holder.txtName.setText(info.getName());
		holder.imgIcon.setImageResource(info.getIcon());
		holder.txtDescription.setText(info.getDescription());
		if (info.getProgressBarProgress() > -1) {
			if (holder.progressBar.getVisibility() == View.GONE) {
				holder.progressBar.setVisibility(View.VISIBLE);
			}
			if (holder.progressBar.getProgress() != info.getProgressBarProgress()) {

				holder.progressBar.setText(info.getProgressBarProgress() + "%");
				holder.progressBar.setTextSize(16);
				holder.progressBar.setProgress(info.getProgressBarProgress());
			}
		} else {
			holder.progressBar.setVisibility(View.GONE);
		}

		return row;
	}

	public void Update(List<RaspberryInfo> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	static class InfoHolder {

		ImageView imgIcon;
		TextView txtName;
		TextView txtDescription;
		TextProgressBar progressBar;
	}
}
