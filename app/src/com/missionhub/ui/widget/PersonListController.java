//package com.missionhub.ui.widget;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Parcelable;
//import android.util.AttributeSet;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import com.google.common.collect.Lists;
//import com.missionhub.R;
//import com.missionhub.ui.ObjectArrayAdapter;
//
//import org.holoeverywhere.LayoutInflater;
//import org.holoeverywhere.widget.FrameLayout;
//import org.holoeverywhere.widget.Spinner;
//import org.holoeverywhere.widget.TextView;
//
//import java.util.ArrayList;
//
//public class PersonListController extends FrameLayout {
//
//    private ImageView mCheckmark;
//    private TextView mChecked;
//    private Spinner mDisplay;
//    private Spinner mOrder;
//
//    public enum Display {
//        STATUS, GENDER, EMAIL, PHONE, PERMISSION, DATE_CREATED, DATE_SURVEYED, LAST_INTERACTION
//    }
//
//    public enum ORDER {
//        OFF, ASC, DESC
//    }
//
//    public PersonListController(Context context) {
//        this(context, null);
//    }
//
//    public PersonListController(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public PersonListController(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//
//        LayoutInflater.from(context).inflate(R.layout.widget_person_list_controller, this, true);
//    }
//
//    public void onFinishInflate() {
//        super.onFinishInflate();
//
//        mCheckmark = (ImageView) findViewById(R.id.checkmark);
//        mChecked = (TextView) findViewById(R.id.checked);
//        mDisplay = (Spinner) findViewById(R.id.display);
//        mOrder = (Spinner) findViewById(R.id.order);
//    }
//
//    @Override
//    public Parcelable onSaveInstanceState() {
//        final Bundle bundle = new Bundle();
//        bundle.putParcelable("instanceState", super.onSaveInstanceState());
//        bundle.putIntArray("mCheckmark", mCheckmark.getDrawableState());
//        bundle.putCharSequence("mChecked", mChecked.getText());
//        bundle.putInt("mDisplay", mDisplay.getSelectedItemPosition());
//        bundle.putInt("mOrder", mOrder.getSelectedItemPosition());
//        return bundle;
//    }
//
//    @Override
//    public void onRestoreInstanceState(final Parcelable state) {
//        if (state instanceof Bundle) {
//            final Bundle bundle = (Bundle) state;
//            mCheckmark.setImageState(bundle.getIntArray("mCheckmark"), false);
//            mChecked.setText(bundle.getCharSequence("mChecked"));
//            mDisplay.setSelection(bundle.getInt("mDisplay"), false);
//            mOrder.setSelection(bundle.getInt("mOrder"), false);
//            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
//            return;
//        }
//        super.onRestoreInstanceState(state);
//    }
//
//    public static class DisplaySpinnerAdapter extends ObjectArrayAdapter {
//        public DisplaySpinnerAdapter(Context context) {
//            super(context);
//            addAll(Lists.newArrayList(context.getResources().getStringArray(R.array.person_info_display)));
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            return null;
//        }
//
//        @Override
//        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
//            return null;
//        }
//    }
//
//    public static class OrderSpinnerAdapter extends ObjectArrayAdapter {
//        public DisplaySpinnerAdapter(Context context) {
//            super(context);
//            addAll(Lists.newArrayList(context.getResources().getStringArray(R.array.person_info_display_order)));
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            return null;
//        }
//
//        @Override
//        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
//            return null;
//        }
//    }
//
//    public static class IdStringItem {
//        public String
//
//
//    }
//}
