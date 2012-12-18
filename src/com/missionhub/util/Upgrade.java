package com.missionhub.util;

/**
 * This class manages application upgrades
 * 
 * To add an upgrade:
 * 
 * 1.) add the version number to versions. 2.) created a method from the previous version to the new version in the
 * format v{oldMajor_oldMinor}to{newMajor_newMinor}
 */
public class Upgrade {

	// /** the logging tag */
	// public static final String TAG = Upgrade.class.getSimpleName();
	//
	// /** the missionhub application */
	// MHApplication app;
	//
	// /** major version */
	// private int majorVersion = -1;
	//
	// /** last major version */
	// private int lastMajorVersion = -1;
	//
	// /** minor version */
	// private int minorVersion = -1;
	//
	// /** last minor version */
	// private int lastMinorVersion = -1;
	//
	// /** stores the past versions */
	// private static final SetMultimap<Integer, Integer> versions =
	// Multimaps.synchronizedSetMultimap(HashMultimap.<Integer, Integer> create());
	// static {
	// versions.put(1, 0); // v1.0.x
	// versions.put(1, 1); // v1.1.x
	// versions.put(1, 2); // v1.2.x
	// versions.put(2, 0); // v2.0.x
	// }
	//
	// /**
	// * Creates a new Update object
	// *
	// * @param app
	// */
	// public Upgrade(final MissionHubApplication app) {
	// this.app = app;
	//
	// initVersions();
	//
	// doUpgrades();
	// }
	//
	// /**
	// * Starts upgrades
	// *
	// * @param app
	// */
	// public static void doUpgrades(final MissionHubApplication app) {
	// new Upgrade(app);
	// }
	//
	// /**
	// * Initializes version instance variables
	// */
	// private void initVersions() {
	// try {
	// final String[] versions = app.getVersion().split("\\.");
	// majorVersion = Integer.parseInt(versions[0]);
	// minorVersion = Integer.parseInt(versions[1]);
	//
	// lastMajorVersion = Preferences.getLastRunMajorVersion(app);
	// lastMinorVersion = Preferences.getLastRunMinorVersion(app);
	// } catch (final Exception e) {
	// Log.e(TAG, e.getMessage(), e);
	// }
	// }
	//
	// /**
	// * Method that calculates needed updates and runs them
	// */
	// private void doUpgrades() {
	// // determine methods that need to be run
	// final ArrayList<String> methodsToRun = new ArrayList<String>();
	//
	// if (lastMajorVersion == -1 && lastMinorVersion == -1) {
	// methodsToRun.add("v0_to_current");
	// } else {
	// final List<Integer> majorVersions = new ArrayList<Integer>(versions.keySet());
	// Collections.sort(majorVersions);
	// for (int i = 0; i < majorVersions.size(); i++) {
	// final int major = majorVersions.get(i);
	//
	// if (lastMajorVersion > major) {
	// continue;
	// }
	//
	// final List<Integer> minorVersions = new ArrayList<Integer>(versions.get(major));
	// Collections.sort(minorVersions);
	// for (int j = 0; j < minorVersions.size(); j++) {
	// final int minor = minorVersions.get(j);
	//
	// if (major == lastMajorVersion && lastMinorVersion > minor) {
	// continue;
	// }
	//
	// if (j + 1 < minorVersions.size()) {
	// methodsToRun.add("v" + major + "_" + minor + "to" + major + "_" + minorVersions.get(j + 1));
	// } else {
	// if (i + 1 < majorVersions.size()) {
	// final List<Integer> nextMinorVersions = new ArrayList<Integer>(versions.get(majorVersions.get(i + 1)));
	// Collections.sort(nextMinorVersions);
	//
	// methodsToRun.add("v" + major + "_" + minor + "to" + majorVersions.get(i + 1) + "_" + nextMinorVersions.get(0));
	// }
	// }
	// }
	// }
	// }
	//
	// boolean success = true;
	// for (final String method : methodsToRun) {
	// Log.e(TAG, method);
	// try {
	// final Method m = this.getClass().getMethod(method, new Class[0]);
	// if (!(Boolean) m.invoke(this, new Object[0])) {
	// success = false;
	// }
	// } catch (final Exception e) {
	// success = false;
	// Log.e(TAG, e.getMessage(), e);
	// }
	// }
	//
	// if (!success) {
	// v0_to_current();
	// // TODO: display error?
	// } else {
	// Log.d(TAG, "update complete");
	// }
	//
	// Preferences.setLastRunMajorVersion(app, majorVersion);
	// Preferences.setLastRunMinorVersion(app, minorVersion);
	// }
	//
	// /**
	// * Default upgrade to run when version is not set in Preferences
	// *
	// * Runs on new install or when version <= 1.1
	// *
	// * @return true if upgrade was successful
	// */
	// public boolean v0_to_current() {
	// final String accessToken = Preferences.getAccessToken(app);
	// final long userId = Preferences.getUserID(app);
	// final long organizationId = Preferences.getOrganizationID(app);
	//
	// app.reset();
	//
	// if (accessToken != null) {
	// Preferences.setAccessToken(app, accessToken);
	// if (userId > -1) {
	// Preferences.setUserID(app, userId);
	// }
	// if (organizationId > -1) {
	// Preferences.setOrganizationID(app, organizationId);
	// }
	// }
	//
	// return true;
	// }
	//
	// public boolean v1_0to1_1() {
	// return true;
	// }
	//
	// public boolean v1_1to1_2() {
	// return true;
	// }
	//
	// public boolean v1_2to2_0() {
	// return v0_to_current();
	// }
}