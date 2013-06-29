package com.missionhub.model.gson;

import com.missionhub.application.Application;
import com.missionhub.model.PhoneNumber;
import com.missionhub.model.PhoneNumberDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GPhoneNumber {

    public long id;
    public long person_id;
    public String number;
    public String location;
    public Boolean primary;
    public String txt_to_email;
    public String email_updated_at;
    public String created_at;
    public String updated_at;

    public static final Object lock = new Object();

    /**
     * Saves the phone number to the SQLite database.
     *
     * @param inTx
     * @return saved phone number
     * @throws Exception
     */
    public PhoneNumber save(final boolean inTx) throws Exception {
        final Callable<PhoneNumber> callable = new Callable<PhoneNumber>() {
            @Override
            public PhoneNumber call() throws Exception {
                synchronized (lock) {
                    final PhoneNumberDao dao = Application.getDb().getPhoneNumberDao();

                    final PhoneNumber num = new PhoneNumber();
                    num.setId(id);
                    num.setPerson_id(person_id);
                    num.setNumber(number);
                    num.setLocation(location);
                    num.setPrimary(primary);
                    num.setTxt_to_email(txt_to_email);
                    num.setEmail_updated_at(email_updated_at);
                    num.setCreated_at(created_at);
                    num.setUpdated_at(updated_at);
                    dao.insertOrReplace(num);

                    return num;
                }
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

    /**
     * Replaces a person's phone numbers with the passed ones
     *
     * @param numbers
     * @param personId
     * @param inTx
     * @return list of saved phone numbers
     * @throws Exception
     */
    public static List<PhoneNumber> replaceAll(final GPhoneNumber[] numbers, final long personId, final boolean inTx) throws Exception {
        final Callable<List<PhoneNumber>> callable = new Callable<List<PhoneNumber>>() {
            @Override
            public List<PhoneNumber> call() throws Exception {
                synchronized (lock) {
                    final PhoneNumberDao dao = Application.getDb().getPhoneNumberDao();

                    // delete current number
                    List<Long> keys = dao.queryBuilder().where(PhoneNumberDao.Properties.Person_id.eq(personId)).listKeys();
                    for (Long key : keys) {
                        dao.deleteByKey(key);
                    }

                    // save the new number
                    final List<PhoneNumber> nums = new ArrayList<PhoneNumber>();
                    for (final GPhoneNumber number : numbers) {
                        final PhoneNumber num = number.save(true);
                        if (num != null) {
                            nums.add(num);
                        }
                    }

                    return nums;
                }
            }
        };
        if (inTx) {
            return callable.call();
        } else {
            return Application.getDb().callInTx(callable);
        }
    }

    public static GPhoneNumber createFromPhoneNumber(final PhoneNumber number) {
        final GPhoneNumber phone = new GPhoneNumber();
        phone.id = number.getId();
        phone.number = number.getNumber();
        phone.location = number.getLocation();
        phone.primary = number.getPrimary();
        return phone;
    }

}