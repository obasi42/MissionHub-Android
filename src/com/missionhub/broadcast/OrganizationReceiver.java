package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class OrganizationReceiver extends MissionHubReceiver {

	public OrganizationReceiver(final Context context) {
		super(context);
	}

	@Override public String[] getAllActions() {
		final String[] NOTIFYs = { OrganizationBroadcast.NOTIFY_ORGANIZATION_CREATE,  OrganizationBroadcast.NOTIFY_ORGANIZATION_UPDATE, OrganizationBroadcast.NOTIFY_ORGANIZATION_DELETE, OrganizationBroadcast.NOTIFY_ORGANIZATION_ERROR};
		return NOTIFYs;
	}
	
	public void onCreate(long organizationId) {
		
	}
	
	public void onUpdate(long organizationId) {
		
	}
	
	public void onDelete(long organizationId) {
		
	}
	
	public void onError(long organizationId, Throwable t) {
		
	}

	@Override public void onReceive(final Context context, final Intent intent) {
		if (intent.getAction().equals(OrganizationBroadcast.NOTIFY_ORGANIZATION_CREATE)) {
			onCreate(intent.getLongExtra(MissionHubBroadcast.PREFIX + "organizationId", -1));
		} else if (intent.getAction().equals(OrganizationBroadcast.NOTIFY_ORGANIZATION_UPDATE)) {
			onUpdate(intent.getLongExtra(MissionHubBroadcast.PREFIX + "organizationId", -1));
		} else if (intent.getAction().equals(OrganizationBroadcast.NOTIFY_ORGANIZATION_DELETE)) {
			onDelete(intent.getLongExtra(MissionHubBroadcast.PREFIX + "organizationId", -1));
		} else if (intent.getAction().equals(OrganizationBroadcast.NOTIFY_ORGANIZATION_ERROR)) {
			onError(intent.getLongExtra(MissionHubBroadcast.PREFIX + "organizationId", -1), (Throwable) intent.getSerializableExtra(MissionHubBroadcast.PREFIX + "throwable"));
		}
	}
}