package com.missionhub.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.missionhub.R;
import com.missionhub.api.PeopleListOptions;
import com.missionhub.application.Application;
import com.missionhub.event.ChangeHostFragmentEvent;
import com.missionhub.event.OnHostedListOptionsChangedEvent;
import com.missionhub.event.OnSidebarItemClickedEvent;
import com.missionhub.exception.ExceptionHelper;
import com.missionhub.model.Label;
import com.missionhub.model.Permission;
import com.missionhub.model.Person;
import com.missionhub.people.ApiPeopleListProvider;
import com.missionhub.people.DynamicPeopleListProvider;
import com.missionhub.people.PeopleListView;
import com.missionhub.people.PersonAdapterViewProvider;
import com.missionhub.ui.ObjectArrayAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TextView;

public class HostedPeopleListFragment extends HostedFragment implements AdapterView.OnItemSelectedListener, PeopleListView.OnPersonClickListener, DynamicPeopleListProvider.OnExceptionListener {

    public static final String TAG = HostedPeopleListFragment.class.getSimpleName();

    private PeopleListView mList;
    private SelectableApiPeopleListProvider mProvider;

    private ImageView mCheckmark;
    private TextView mCheckmarkText;
    private Spinner mDisplaySpinner;
    private ObjectArrayAdapter mDisplaySpinnerAdapter;
    private int mDisplayPosition;
    private Spinner mOrderSpinner;
    private ObjectArrayAdapter mOrderSpinnerAdapter;
    private int mOrderPosition;

    public HostedPeopleListFragment() {
        // empty fragment constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application.registerEventSubscriber(this, OnSidebarItemClickedEvent.class);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(OnSidebarItemClickedEvent event) {
        if (mProvider == null) return;

        Object item = event.getItem();
        synchronized (mList.getProvider().getLock()) {
            PeopleListOptions options = mProvider.getRawPeopleListOptions();

            if (item instanceof Person) {
                options.toggle("assigned_to", ((Person) item).getId());
            }
            if (item instanceof Label) {
                options.toggle("labels", ((Label) item).getId());
            }
            if (item instanceof Permission) {
                options.toggle("permissions", ((Permission) item).getId());
            }
            mProvider.setPeopleListOptions(options);
        }
        Application.getEventBus().postSticky(new OnHostedListOptionsChangedEvent(mProvider.getPeopleListOptions()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_list, parent, false);

        // set up the person list and adapter
        mList = (PeopleListView) view.findViewById(android.R.id.list);
        if (mProvider == null) {
            mProvider = new SelectableApiPeopleListProvider(inflater.getContext());
        } else {
            mProvider.setContext(inflater.getContext());
        }
        mProvider.setOnExceptionListener(this);
        Application.getEventBus().postSticky(new OnHostedListOptionsChangedEvent(mProvider.getPeopleListOptions()));

        mList.setProvider(mProvider);
        mList.setOnPersonClickListener(this);
        mList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));

        // set up the list controller
        mCheckmark = (ImageView) view.findViewById(R.id.checkmark);
        mCheckmarkText = (TextView) view.findViewById(R.id.checkmark_text);
        mDisplaySpinner = (Spinner) view.findViewById(R.id.display);
        mDisplaySpinner.setOnItemSelectedListener(this);
        mOrderSpinner = (Spinner) view.findViewById(R.id.order);
        mOrderSpinner.setOnItemSelectedListener(this);
        mOrderSpinner.setVisibility(View.GONE);

        if (mDisplaySpinnerAdapter == null) {
            mDisplaySpinnerAdapter = buildDisplaySpinner(inflater.getContext());
        } else {
            mDisplaySpinnerAdapter.setContext(inflater.getContext());
        }
        mDisplaySpinner.setAdapter(mDisplaySpinnerAdapter);

        if (mOrderSpinnerAdapter == null) {
            mOrderSpinnerAdapter = buildOrderSpinner(inflater.getContext());
        } else {
            mOrderSpinnerAdapter.setContext(inflater.getContext());
        }
        mOrderSpinner.setAdapter(mOrderSpinnerAdapter);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        StringRunnableItem item = (StringRunnableItem) parent.getItemAtPosition(position);
        if (parent == mDisplaySpinner) {
            if (mDisplayPosition == position) return;
            mDisplayPosition = position;
        } else if (parent == mOrderSpinner) {
            if (mOrderPosition == position) return;
            mOrderPosition = position;
        }
        try {
            item.run();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPersonClick(PeopleListView list, Person person, int position, long id) {
        ChangeHostFragmentEvent event = new ChangeHostFragmentEvent(HostedProfileFragment.class);
        event.setAddToBackstack(true);
        event.setInAnimation(R.anim.slide_in_right, R.anim.slide_in_left);
        event.setOutAnimation(R.anim.slide_out_left, R.anim.slide_out_right);

        Bundle bundle = new Bundle();
        bundle.putLong("personId", person.getId());
        event.setNewInstance(true, bundle);

        Application.postEvent(event);
    }

    @Override
    public void onException(Throwable t) {
        if (getSupportActivity() == null) return;
        ExceptionHelper eh = new ExceptionHelper(getSupportActivity(), t);
        eh.setPositiveButton(new ExceptionHelper.DialogButton() {
            @Override
            public String getTitle() {
                return "Retry";
            }

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if (mProvider != null) {
                    mProvider.setPaused(false);
                    mProvider.load();
                }
                dialog.dismiss();
            }
        });
        eh.setNeutralButton(new ExceptionHelper.DialogButton() {
            @Override
            public String getTitle() {
                return "Cancel";
            }

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        eh.show();
    }

    public static class StringRunnableItem {
        private int mText;
        private Runnable mRunnable;

        public StringRunnableItem(int text, Runnable runnable) {
            mText = text;
            mRunnable = runnable;
        }

        public void run() {
            if (mRunnable != null) {
                mRunnable.run();
            }
        }
    }

    public static class SelectableApiPeopleListProvider extends ApiPeopleListProvider {

        public SelectableApiPeopleListProvider(Context context) {
            super(context);
        }

        public void setDisplay(PersonAdapterViewProvider.Display display) {
            ((PersonAdapterViewProvider) getAdapterViewProvider()).setLine2(display);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    public static class SimpleSpinnerAdapter extends ObjectArrayAdapter<StringRunnableItem> {

        public SimpleSpinnerAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return getView(i, view, viewGroup, R.layout.simple_spinner_item);
        }

        @Override
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            return getView(i, view, viewGroup, R.layout.simple_spinner_dropdown_item);
        }

        public View getView(int i, View view, ViewGroup viewGroup, int layout) {
            StringRunnableItem item = getItem(i);
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = getLayoutInflater().inflate(layout, viewGroup, false);
                holder.text1 = (android.widget.TextView) view.findViewById(android.R.id.text1);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.text1.setText(Html.fromHtml(getContext().getString(item.mText)));

            return view;
        }

        private static class ViewHolder {
            android.widget.TextView text1;
        }
    }

    private SimpleSpinnerAdapter buildDisplaySpinner(Context context) {
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(context);
        adapter.add(new StringRunnableItem(R.string.display_gender, new Runnable() {
            @Override
            public void run() {
                mProvider.setDisplay(PersonAdapterViewProvider.Display.GENDER);
            }
        }));
        adapter.add(new StringRunnableItem(R.string.display_status, new Runnable() {
            @Override
            public void run() {
                mProvider.setDisplay(PersonAdapterViewProvider.Display.STATUS);
            }
        }));
        adapter.add(new StringRunnableItem(R.string.display_permission, new Runnable() {
            @Override
            public void run() {
                mProvider.setDisplay(PersonAdapterViewProvider.Display.PERMISSION);
            }
        }));
        adapter.add(new StringRunnableItem(R.string.display_phone, new Runnable() {
            @Override
            public void run() {
                mProvider.setDisplay(PersonAdapterViewProvider.Display.PHONE);
            }
        }));
        adapter.add(new StringRunnableItem(R.string.display_email, new Runnable() {
            @Override
            public void run() {
                mProvider.setDisplay(PersonAdapterViewProvider.Display.EMAIL);
            }
        }));
        return adapter;
    }

    private SimpleSpinnerAdapter buildOrderSpinner(Context context) {
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(context);
        adapter.add(new StringRunnableItem(R.string.sort_off, new Runnable() {
            @Override
            public void run() {

            }
        }));
        adapter.add(new StringRunnableItem(R.string.sort_asc, new Runnable() {
            @Override
            public void run() {

            }
        }));
        adapter.add(new StringRunnableItem(R.string.sort_desc, new Runnable() {
            @Override
            public void run() {

            }
        }));
        return adapter;
    }
}