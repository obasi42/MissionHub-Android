package com.missionhub.ui.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.missionhub.Application;
import com.missionhub.R;
import com.missionhub.api.model.sql.FollowupComment;
import com.missionhub.api.model.sql.Person;
import com.missionhub.api.model.sql.Rejoicable;
import com.missionhub.helper.U;
import com.missionhub.ui.ImageManager;
import com.missionhub.ui.widget.item.ContactStatusItem;
import com.ocpsoft.pretty.time.PrettyTime;

import greendroid.widget.item.Item;
import greendroid.widget.itemview.ItemView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactStatusItemView extends LinearLayout implements ItemView {

	private ImageView mPicture;
	private TextView mName;
	private TextView mStatus;
	private TextView mComment;
	private TextView mTime;
	private View mGospel;
	private View mChrist;
	private View mConvo;

	private static ImageManager imageManager;
	private static PrettyTime prettyTime;
	private static Map<Integer, Person> commenterCache = Collections.synchronizedMap(new HashMap<Integer, Person>());

	public ContactStatusItemView(Context context) {
		this(context, null);
		setup(context);
	}

	public ContactStatusItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup(context);
	}

	private void setup(Context context) {
		if (imageManager == null) {
			imageManager = new ImageManager(context.getApplicationContext());
		}
		if (prettyTime == null) {
			prettyTime = new PrettyTime();
		}
	}

	private synchronized Person getCommenter(int commenterId) {
		Person p = commenterCache.get(commenterId);
		if (p == null) {
			p = ((Application) getContext().getApplicationContext()).getDbSession().getPersonDao().load(commenterId);
			commenterCache.put(commenterId, p);
		}
		return p;
	}

	@Override
	public void prepareItemView() {
		mPicture = (ImageView) findViewById(R.id.picture);
		mName = (TextView) findViewById(R.id.name);
		mStatus = (TextView) findViewById(R.id.status);
		mComment = (TextView) findViewById(R.id.comment);
		mTime = (TextView) findViewById(R.id.time);
		mGospel = findViewById(R.id.g_present);
		mChrist = findViewById(R.id.r_christ);
		mConvo = findViewById(R.id.s_convo);
	}

	@Override
	public void setObject(Item object) {
		final ContactStatusItem item = (ContactStatusItem) object;

		final FollowupComment comment = item.comment;
		final List<Rejoicable> rejoicables = item.rejoicables;
		final Person commenter = getCommenter(comment.getCommenter_id());

		if (comment == null || commenter == null)
			return;

		mPicture.setTag(commenter.getPicture() + "?type=square");
		imageManager.displayImage(commenter.getPicture() + "?type=square", mPicture, R.drawable.default_contact);
		mName.setText(commenter.getName());
		mStatus.setText(comment.getStatus());

		if (!U.nullOrEmpty(comment.getComment())) {
			mComment.setText(comment.getComment());
		} else {
			mComment.setText("");
		}
		
		mTime.setText(prettyTime.format(comment.getUpdated_at()));

		if (rejoicables != null) {
			List<String> what = new ArrayList<String>();
			for (Rejoicable rejoicable : rejoicables) {
				what.add(rejoicable.getWhat());
			}

			if (what.contains("spiritual_conversation")) {
				mConvo.setVisibility(View.VISIBLE);
			} else {
				mConvo.setVisibility(View.GONE);
			}

			if (what.contains("prayed_to_receive")) {
				mChrist.setVisibility(View.VISIBLE);
			} else {
				mChrist.setVisibility(View.GONE);
			}

			if (what.contains("gospel_presentation")) {
				mGospel.setVisibility(View.VISIBLE);
			} else {
				mGospel.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public Class<? extends Item> getItemClass() {
		return ContactStatusItem.class;
	}
}
