package com.missionhub.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.Interest;
import com.missionhub.model.InterestDao;
import com.missionhub.model.Location;
import com.missionhub.model.LocationDao;
import com.missionhub.model.Person;
import com.missionhub.util.U;

import de.greenrobot.dao.CloseableListIterator;
import de.greenrobot.dao.LazyList;

public class GIdNameProvider {
	public String id;
	public String name;
	public String provider;
	public String category;

	/**
	 * Saves the generic id name provider if used directly.
	 * 
	 * @param clss
	 * @param location
	 * @param p
	 * @throws Exception
	 */
	public static void save(final Class<?> clss, final GIdNameProvider provider, final Person p, final boolean inTx) throws Exception {
		if (U.isNull(clss, provider, p)) return;

		save(clss, new GIdNameProvider[] { provider }, p, inTx);
	}

	/**
	 * Saves the generic id name provider
	 * 
	 * @param clss
	 * @param interests
	 * @param p
	 * @throws Exception
	 */
	public static void save(final Class<?> clss, final GIdNameProvider[] provider, final Person p, final boolean inTx) throws Exception {
		if (clss == null || provider == null || provider.length == 0 || p == null) return;
		final Callable<Void> callable = new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				if (clss == Location.class) {
					saveLocations(provider, p);
				} else if (clss == Interest.class) {
					saveInterests(provider, p);
				} else {
					throw new RuntimeException(clss.getName() + " is not saveable");
				}
				return null;
			}
		};
		if (!inTx) {
			Application.getDb().callInTx(callable);
		} else {
			callable.call();
		}
	}

	private static void saveLocations(final GIdNameProvider[] provider, final Person p) {
		final LocationDao ld = Application.getDb().getLocationDao();

		// delete current records as we don't have stable id's currently
		// TODO: return the id's api side so we can sync data better.. this is bad.
		final LazyList<Location> delLocations = ld.queryBuilder().where(com.missionhub.model.LocationDao.Properties.Person_id.eq(p.getId())).listLazyUncached();
		final CloseableListIterator<Location> itr = delLocations.listIteratorAutoClose();
		while (itr.hasNext()) {
			final Location loc = itr.next();
			ld.delete(loc);
		}

		// insert the new records
		for (final GIdNameProvider location : provider) {
			if (location == null) continue;

			final Location l = new Location();
			if (!U.isNullEmpty(location.id)) l.setLocation_id(location.id);
			if (!U.isNullEmpty(location.name)) l.setName(location.name);
			if (!U.isNullEmptyNegative(p, p.getId())) l.setPerson_id(p.getId());
			if (!U.isNullEmpty(location.provider)) l.setProvider(location.provider);
			ld.insert(l);
		}
	}

	private static void saveInterests(final GIdNameProvider[] provider, final Person p) {
		final InterestDao id = Application.getDb().getInterestDao();

		// delete current records as we don't have stable id's currently
		final LazyList<Interest> delInterests = id.queryBuilder().where(com.missionhub.model.InterestDao.Properties.Person_id.eq(p.getId())).listLazyUncached();
		final CloseableListIterator<Interest> itr = delInterests.listIteratorAutoClose();
		while (itr.hasNext()) {
			final Interest interest = itr.next();
			id.delete(interest);
		}

		// insert the new records
		for (final GIdNameProvider interest : provider) {
			if (interest == null) continue;

			final Interest i = new Interest();
			if (!U.isNullEmpty(interest.id)) i.setInterest_id(interest.id);
			if (!U.isNullEmpty(interest.name)) i.setName(interest.name);
			if (!U.isNullEmptyNegative(p, p.getId())) i.setPerson_id(p.getId());
			if (!U.isNullEmpty(interest.provider)) i.setProvider(interest.provider);
			id.insert(i);
		}
	}
}
