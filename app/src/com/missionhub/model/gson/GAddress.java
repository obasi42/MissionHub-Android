package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Address;
import com.missionhub.model.AddressDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GAddress {

    public long id;
    public String address1;
    public String address2;
    public String city;
    public String state;
    public String country;
    public String zip;
    public String address_type;

    public static final Object lock = new Object();
    public static final Object allLock = new Object();

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
                    address.setAddress_type(address_type);

                    Application.getDb().getAddressDao().insert(address);
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

    public static List<Address> replaceAll(final GAddress[] addresses, final long personId, final boolean inTx) throws Exception {
        final Callable<List<Address>> callable = new Callable<List<Address>>() {
            @Override
            public List<Address> call() throws Exception {
                synchronized (allLock) {
                    final AddressDao dao = Application.getDb().getAddressDao();

                    // delete old addresses
                    List<Long> keys = dao.queryBuilder().where(AddressDao.Properties.Person_id.eq(personId)).listKeys();
                    for (Long key : keys) {
                        dao.deleteByKey(key);
                    }

                    // save the new address
                    final List<Address> addrs = new ArrayList<Address>();
                    for (final GAddress address : addresses) {
                        final Address a = address.save(personId, true);
                        if (a != null) {
                            addrs.add(a);
                        }
                    }

                    return addrs;
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