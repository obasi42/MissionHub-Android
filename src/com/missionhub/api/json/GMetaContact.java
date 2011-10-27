package com.missionhub.api.json;

public class GMetaContact {
	private GMeta meta;
	private GContact[] contacts;
	
	public void setMeta(GMeta x) { meta = x; }
	public GMeta getMeta() { return meta; }
	public void setContacts(GContact[] x) { contacts = x; }
	public GContact[] getContacts() { return contacts; }
}
