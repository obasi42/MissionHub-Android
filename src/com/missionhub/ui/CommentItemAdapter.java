package com.missionhub.ui;

import java.util.ArrayList;

import com.missionhub.R;
import com.missionhub.api.GComment;
import com.missionhub.api.GCommenter;
import com.missionhub.api.GFollowupComment;
import com.missionhub.api.GRejoicable;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentItemAdapter extends ArrayAdapter<GFollowupComment> {
	private ArrayList<GFollowupComment> comments;
	private Activity activity;
	public ImageManager imageManager;
	private ArrayList<String> statusListTag;
	private ArrayList<Integer> statusListRes;

	public CommentItemAdapter(Activity a, int textViewResourceId, ArrayList<GFollowupComment> comments, ArrayList<String> statusListTag, ArrayList<Integer> statusListRes) {
		super(a, textViewResourceId, comments);
		this.comments = comments;
		this.statusListRes = statusListRes;
		this.statusListTag = statusListTag;
		activity = a;
		
		imageManager = new ImageManager(activity.getApplicationContext());
	}

	public static class ViewHolder{
		public ImageView picture;
		public TextView name;
		public TextView status;
		public TextView comment;
		public TextView time;
		public View gospel;
		public View christ;
		public View convo;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {		
			LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.comment_list_item, null);
			holder = new ViewHolder();
			holder.picture = (ImageView) v.findViewById(R.id.comment_picture);
			holder.name = (TextView) v.findViewById(R.id.comment_name);
			holder.status = (TextView) v.findViewById(R.id.comment_status);
			holder.comment = (TextView) v.findViewById(R.id.comment_comment);
			holder.time = (TextView) v.findViewById(R.id.comment_time);
			holder.gospel = v.findViewById(R.id.comment_g_present);
			holder.christ = v.findViewById(R.id.comment_r_christ);
			holder.convo = v.findViewById(R.id.comment_s_convo);
			v.setTag(holder);
		}
		else
			holder=(ViewHolder)v.getTag();

		try {
			final GFollowupComment commentMeta = comments.get(position);
			final GComment comment = commentMeta.getComment();
			final GCommenter commenter = comment.getCommenter();
			final GRejoicable[] rejoicables = commentMeta.getRejoicables();
			final ArrayList<String> rejoice = new ArrayList<String>();
			for (GRejoicable rejoicable : rejoicables) {
				rejoice.add(rejoicable.getWhat());
			}
			if (comment != null) {
				holder.picture.setTag(commenter.getPicture()+"?type=square");
				imageManager.displayImage(commenter.getPicture()+"?type=square", activity, holder.picture, R.drawable.default_contact);
				holder.name.setText(commenter.getName());
				holder.status.setText(statusListRes.get(statusListTag.indexOf(comment.getStatus())));
				if (comment.getComment() == null || comment.getComment().equals("")) {
					holder.comment.setText("");
				} else {
					holder.comment.setText(comment.getComment());
				}
				holder.time.setText(comment.getCreated_at_words());
				if (rejoice.contains("spiritual_conversation")) {
					holder.convo.setVisibility(View.VISIBLE);
				} else {
					holder.convo.setVisibility(View.GONE);
				}
				if (rejoice.contains("prayed_to_receive")) {
					holder.christ.setVisibility(View.VISIBLE);
				} else {
					holder.christ.setVisibility(View.GONE);
				}
				if (rejoice.contains("gospel_presentation")) {
					holder.gospel.setVisibility(View.VISIBLE);
				} else {
					holder.gospel.setVisibility(View.GONE);
				}
			}
		} catch (Exception e) {}
		return v;
	}
}