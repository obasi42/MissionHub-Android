package com.missionhub.broadcast;

import android.content.Context;
import android.content.Intent;

public class OrganizationReceiver extends MissionHubReceiver {

	public OrganizationReceiver(final Context context) {
		super(context);
	}

	@Override public String[] getAllActions() {
		final String[] NOTIFYs = { OrganizationBroadcast.NOTIFY_ORGANIZATIONS_COMPLETE,
				OrganizationBroadcast.NOTIFY_ORGANIZATION_UPDATE, OrganizationBroadcast.NOTIFY_ORGANIZATION_DELETE, OrganizationBroadcast.NOTIFY_ORGANIZATIONS_ERROR };
		return NOTIFYs;
	}

	public void onComplete(final long[] organizationIds) {

	}
	
	public void onError(final long[] organizationIds, final Throwable t) {

	}

	public void onUpdate(final long organizationId) {

	}

	public void onDelete(final long organizationId) {

	}

	@Override public void onReceive(final Context context, final Intent intent) {
		if (intent.getAction().equals(OrganizationBroadcast.NOTIFY_ORGANIZATIONS_COMPLETE)) {
			onComplete(intent.getLongArrayExtra(MissionHubBroadcast.PREFIX + "organizationIds"));
		} else if (intent.getAction().equals(OrganizationBroadcast.NOTIFY_ORGANIZATION_UPDATE)) {
			onUpdate(intent.getLongExtra(MissionHubBroadcast.PREFIX + "organizationId", -1));
		} else if (intent.getAction().equals(OrganizationBroadcast.NOTIFY_ORGANIZATION_DELETE)) {
			onDelete(intent.getLongExtra(MissionHubBroadcast.PREFIX + "organizationId", -1));
		} else if (intent.getAction().equals(OrganizationBroadcast.NOTIFY_ORGANIZATIONS_ERROR)) {
			onError(intent.getLongArrayExtra(MissionHubBroadcast.PREFIX + "organizationIds"), (Throwable) intent.getSerializableExtra(MissionHubBroadcast.PREFIX + "throwable"));
		}
	}
}