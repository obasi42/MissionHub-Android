package com.missionhub.model.gson;

import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.Address;
import com.missionhub.model.AddressDao;

public class GAddress {

	public long id;
	public String address1;
	public String address2;
	public String city;
	public String state;
	public String country;
	public String zip;

	public static final Object lock = new Object();

	/**
	 * Saves the current address to the SQLite database.
	 * 
	 * @param inTx
	 * @return saved current address
	 * @throws Exception
	 */
	public Address save(final long personId, final boolean inTx) throws Exception {
		final Callable<Address> callable = new Callable<Address>() {
			@Override
			public Address call() throws Exception {
				synchronized (lock) {
					final AddressDao dao = Application.getDb().getAddressDao();
					// delete old addresses
					dao.queryBuilder().where(AddressDao.Properties.Person_id.eq(personId)).buildDelete().executeDeleteWithoutDetachingEntities();

					// add new address
					final Address address = new Address();
					address.setId(id);
					address.setPerson_id(personId);
					address.setAddress1(address1);
					address.setAddress2(address2);
					address.setCity(city);
					address.setState(state);
					address.setCountry(country);
					address.setZip(zip);

					dao.insert(address);
					return address;
				}
			}
		};
		if (inTx) {
			return callable.call();
		} else {
			return Application.getDb().callInTx(callable);
		}
	}

}