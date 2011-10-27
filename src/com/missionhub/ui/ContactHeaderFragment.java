package com.missionhub.ui;

import com.missionhub.R;
import com.missionhub.helpers.Helper;
import com.missionhub.sql.Person;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactHeaderFragment extends Fragment {

	public static final String TAG = ContactHeaderFragment.class.getSimpleName();
	
	private Person person;
	
	private ImageView mPicture;
	private TextView mName;
	private Button mPhone;
	private Button mSms;
	private Button mEmail;
	
	private ImageManager imageManager;
	private String currentProfilePicture = "";
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contact_header, container);
        
        imageManager = new ImageManager(root.getContext());
        
        mPicture = (ImageView) root.findViewById(R.id.picture);
        mPicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clickPicture(view);
            }
        });
        mName = (TextView) root.findViewById(R.id.name);
        mPhone = (Button) root.findViewById(R.id.phone);
        mPhone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clickPhone(view);
            }
        });
        mSms = (Button) root.findViewById(R.id.sms);
        mSms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clickSms(view);
            }
        });
        mEmail = (Button) root.findViewById(R.id.email);
        mEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                clickEmail(view);
            }
        });
        return root;
	}
	
	public void setPerson (Person person) {
		this.person = person;
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
				mPicture.setTag(person.getPicture() + "?type=large");
				imageManager.displayImage(person.getPicture() + "?type=large", mPicture, defaultImage);
			}
		} catch (Exception e) {	Log.w(TAG, e.getMessage(), e); }
		
		try {
			if (person.getPhone_number() != null && Helper.hasPhoneAbility(mPhone.getContext())) {
				mPhone.setVisibility(View.VISIBLE);
				mSms.setVisibility(View.VISIBLE);
			} else {
				mPhone.setVisibility(View.GONE);
				mSms.setVisibility(View.GONE);
			}
		} catch (Exception e) {	Log.w(TAG, e.getMessage(), e); }
		
		try {
			if (person.getEmail_address() != null) {
				mEmail.setVisibility(View.VISIBLE);
			} else {
				mEmail.setVisibility(View.GONE);
			}
		} catch (Exception e) {	Log.w(TAG, e.getMessage(), e); }
	}
	
	private void clickPicture(View view) {
		try {
			Helper.openFacebookProfile(view.getContext(), person.getFb_id());	
		} catch (Exception e) {	Log.w(TAG, e.getMessage(), e); }
	}
	
	private void clickPhone(View view) {
		try {
			Helper.makePhoneCall(view.getContext(), person.getPhone_number());
		} catch (Exception e) {	Log.w(TAG, e.getMessage(), e); }
	}
	
	private void clickSms(View view) {
		try {
			Helper.sendSMS(view.getContext(), person.getPhone_number());
		} catch (Exception e) {	Log.w(TAG, e.getMessage(), e); }
	}
	
	private void clickEmail(View view) {
		try {
			Helper.sendEmail(view.getContext(), person.getEmail_address());
		} catch (Exception e) {	Log.w(TAG, e.getMessage(), e); }
	}
}