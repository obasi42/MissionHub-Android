package com.missionhub.fragment;

import greendroid.widget.ItemAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.missionhub.R;
import com.missionhub.ui.widget.item.CategoryListItem;

public class NavigationListFragment extends MissionHubFragment implements OnItemClickListener {

	private ListView mListView;

	private OnCategoryClickListener mOnCategoryClickListener;

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_people_my_category, container, false);
		view.setLayoutParams(new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.FILL_PARENT, getLayoutWeight()));

		mListView = (ListView) view.findViewById(R.id.listView);

		final ItemAdapter adapter = new ItemAdapter(inflater.getContext());
		adapter.add(new CategoryListItem("All"));
		adapter.add(new CategoryListItem("In Progress"));
		adapter.add(new CategoryListItem("Completed"));
		adapter.add(new CategoryListItem("Detailed"));

		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);

		mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		mListView.setItemChecked(1, true);

		return view;
	}

	public interface OnCategoryClickListener {
		public void onCategoryClick();
	}

	public void setOnCategoryClickListener(final OnCategoryClickListener onCategoryClickListener) {
		mOnCategoryClickListener = onCategoryClickListener;
	}

	@Override
	public void onItemClick(final AdapterView<?> arg0, final View arg1, final int arg2, final long arg3) {
		if (mOnCategoryClickListener != null) {
			mOnCategoryClickListener.onCategoryClick();
		}
	}
}