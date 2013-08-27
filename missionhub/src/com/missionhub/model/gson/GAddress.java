package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.Address;
import com.missionhub.model.AddressDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GAddress {

    public GAddress address;

    public long id;
    public String address1;
    public String address2;
    public String city;
    public String state;
    public String country;
    public String zip;
    public String address_type;

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
                // wrapped address
                if (address != null) {
                    return address.save(personId, true);
                }

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

        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

    public static GAddress createFromAddress(Address address) {
        final GAddress addr = new GAddress();
        addr.id = address.getId();
        addr.address1 = address.getAddress1();
        addr.address2 = address.getAddress2();
        addr.city = address.getCity();
        addr.state = address.getState();
        addr.country = address.getCountry();
        addr.zip = address.getZip();
        addr.address_type = address.getAddress_type();
        return addr;
    }
}