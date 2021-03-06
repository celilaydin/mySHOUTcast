package com.laramaki.adapters;

import java.util.List;

import org.orman.mapper.ModelQuery;
import org.orman.sql.C;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.laramaki.R;
import com.laramaki.model.Favorite;
import com.laramaki.model.Station;

public class StationAdapter extends BaseAdapter {

	private List<Station> listOfStations;
	private Station playingStation;
	private Context context;

	public StationAdapter(List<Station> listOfStations, Context context) {
		this.listOfStations = listOfStations;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (listOfStations == null)
			return 0;
		return listOfStations.size();
	}

	@Override
	public Object getItem(int position) {
		return listOfStations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.station_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView
					.findViewById(R.station_list_item.name);
			holder.genre = (TextView) convertView
					.findViewById(R.station_list_item.genre);
			holder.favorite = (CheckBox) convertView
					.findViewById(R.station_list_item.favorite);
			holder.flipper = (ViewFlipper) convertView
					.findViewById(R.station_list_item.viewFlipper);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Station station = listOfStations.get(position);

		holder.name.setText(station.name.trim());
		holder.favorite
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Station station = (Station) getItem(position);
						if (isChecked) {
							addToFavorites(station);
						} else {
							removeFromFavorites(station);
						}
					}

				});

		if (TextUtils.isEmpty(station.genre)) {
			holder.genre.setVisibility(View.GONE);
		} else {
			holder.genre.setVisibility(View.VISIBLE);
			holder.genre.setText(station.genre);
		}
		if (station.equals(this.playingStation)) {
			holder.flipper.setDisplayedChild(2);
		} else {
			holder.flipper.setDisplayedChild(0);
		}
		holder.favorite.setChecked(station.isFavorite());
		return convertView;
	}

	public void setPlayingStation(Station playingStation) {
		this.playingStation = playingStation;
	}

	static class ViewHolder {
		TextView name;
		TextView genre;
		CheckBox favorite;
		ViewFlipper flipper;
	}

	private void addToFavorites(Station station) {
		if (!station.isFavorite()) {
			Favorite favorite = new Favorite();
			favorite.station = station;
			favorite.insert();
		}
	}

	private void removeFromFavorites(Station station) {
		if (station.isFavorite()) {
			Favorite fav = Favorite.fetchSingle(
					ModelQuery.select().from(Favorite.class)
							.where(C.eq("station", station.stationId))
							.getQuery(), Favorite.class);
			if (fav != null) {
				fav.delete();
			}
		}
	}
}
