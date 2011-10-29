package com.missionhub.ui;

import com.missionhub.R;
import com.missionhub.model.sql.Person;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactHeaderSmallFragment extends Fragment {

	public static final String TAG = ContactHeaderSmallFragment.class.getSimpleName();
	
	private Person person;
	
	private ImageView mPicture;
	private TextView mName;
	
	private ImageManager imageManager;
	private String currentProfilePicture = "";
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contact_header_small, container);
        
        imageManager = new ImageManager(root.getContext());
        
        mPicture = (ImageView) root.findViewById(R.id.picture);
        mName = (TextView) root.findViewById(R.id.name);
     
        return root;
	}
	
	public void setPerson (Person person) {
		this.person = person;
		if (person != null) 
			update();
	}
	
	private void update() {
		try {
			mName.setText(person.getName());
		} catch (Exception e) {	Log.w(TAG, e.getMessage(), e); }
		
		try {
			int defaultImage = R.drawable.facebook_question;
			if (person.getGender() != null) {
				if (person.getGender().equalsIgnoreCase("male")) {
					defaultImage = R.drawable.facebook_male;
				} else if (person.getGender().equalsIgnoreCase("female")) {
					defaultImage = R.drawable.facebook_female;
				}
			}
			if (person.getPicture() != null && !currentProfilePicture.equals(person.getPicture())) {
				currentProfilePicture = person.getPicture();
				mPicture.setTag(person.getPicture() + "?type=square");
				imageManager.displayImage(person.getPicture() + "?type=square", mPicture, defaultImage);
			}
		} catch (Exception e) {	Log.w(TAG, e.getMessage(), e); }
	}
}