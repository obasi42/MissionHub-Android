package com.missionhub.broadcast;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

abstract class MissionHubReceiver extends BroadcastReceiver {

	protected final Context context;

	/**
	 * Creats a new MissionHubReciever Object
	 * 
	 * @param context
	 */
	public MissionHubReceiver(final Context context) {
		this.context = context;
	}

	private IntentFilter buildIntentFilter(final String... actions) {
		final IntentFilter intent = new IntentFilter();
		for (final String action : actions) {
			intent.addAction(action);
		}
		return intent;
	}

	/**
	 * Returns all actions for receiver.
	 * 
	 * @return
	 */
	abstract String[] getAllActions();

	/**
	 * This method is called when the BroadcastReceiver is receiving an Intent
	 * broadcast.
	 */
	@Override abstract public void onReceive(Context context, Intent intent);

	/**
	 * Registers the MissionHubReceiver with the LocalBroadcastManager with all
	 * available actions
	 */
	public void register() {
		register(buildIntentFilter(getAllActions()));
	}

	/**
	 * Registers the MissionHubReceiver with the LocalBroadcastManger with the
	 * given IntentFilter
	 * 
	 * @param intentFilter
	 */
	public void register(final IntentFilter intentFilter) {
		LocalBroadcastManager.getInstance(context).registerReceiver(this, intentFilter);
	}

	/**
	 * Registers the MissionHubReceiver with the LocalBroadcastManager with the
	 * given actions
	 * 
	 * @param actions
	 */
	public void register(final String... actions) {
		register(buildIntentFilter(actions));
	}

	/**
	 * Registers the MissionHubReceiver with the LocalBroadcastManager with the
	 * given action and categories
	 * 
	 * @param action
	 * @param categories
	 */
	public void register(final String action, List<String> categories) {
		final IntentFilter intent = buildIntentFilter(action);
		for (final String category : categories) {
			intent.addCategory(category);
		}
		register(intent);
	}
	
	/**
	 * Unregisters the MissionHubReceiver with the LocalBroadcastManager
	 */
	public void unregister() {
		LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
	}
}