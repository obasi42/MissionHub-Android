package com.missionhub.model.gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.missionhub.application.Application;
import com.missionhub.model.EmailAddress;
import com.missionhub.model.EmailAddressDao;
import com.missionhub.util.U;

import de.greenrobot.dao.CloseableListIterator;
import de.greenrobot.dao.LazyList;

public class GEmailAddress {

	public long id;
	public String email;
	public long person_id;
	public Boolean primary;
	public String created_at;
	public String updated_at;

	public static final Object lock = new Object();
	public static final Object allLock = new Object();

	/**
	 * Saves the email address to the SQLite database.
	 * 
	 * @param inTx
	 * @return saved email address
	 * @throws Exception
	 */
	public EmailAddress save(final boolean inTx) throws Exception {
		final Callable<EmailAddress> callable = new Callable<EmailAddress>() {
			@Override
			public EmailAddress call() throws Exception {
				synchronized (lock) {
					final EmailAddressDao dao = Application.getDb().getEmailAddressDao();

					EmailAddress address = dao.load(id);
					boolean insert = false;
					if (address == null) {
						address = new EmailAddress();
						insert = true;
					}
					address.setId(id);
					address.setEmail(email);
					address.setPerson_id(person_id);
					address.setPrimary(primary);
					address.setCreated_at(U.parseISO8601(created_at));
					address.setUpdated_at(U.parseISO8601(updated_at));

					if (insert) {
						dao.insert(address);
					} else {
						dao.update(address);
					}

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

	/**
	 * Replaces a person's email address with the passed ones
	 * 
	 * @param addresses
	 * @param personId
	 * @param inTx
	 * @return list of saved email addresses
	 * @throws Exception
	 */
	public static List<EmailAddress> replaceAll(final GEmailAddress[] addresses, final long personId, final boolean inTx) throws Exception {
		final Callable<List<EmailAddress>> callable = new Callable<List<EmailAddress>>() {
			@Override
			public List<EmailAddress> call() throws Exception {
				synchronized (allLock) {
					final EmailAddressDao dao = Application.getDb().getEmailAddressDao();

					// delete current address
					final LazyList<EmailAddress> delete = dao.queryBuilder().where(EmailAddressDao.Properties.Person_id.eq(personId)).listLazyUncached();
					final CloseableListIterator<EmailAddress> itr = delete.listIteratorAutoClose();
					while (itr.hasNext()) {
						dao.delete(itr.next());
					}

					// save the new address
					final List<EmailAddress> emails = new ArrayList<EmailAddress>();
					for (final GEmailAddress address : addresses) {
						final EmailAddress ea = address.save(true);
						if (ea != null) {
							emails.add(ea);
						}
					}

					return emails;
				}
			}
		};
		if (inTx) {
			return callable.call();
		} else {
			return Application.getDb().callInTx(callable);
		}
	}

	public static GEmailAddress createFromEmailAddress(final EmailAddress address) {
		final GEmailAddress email = new GEmailAddress();
		email.id = address.getId();
		email.email = address.getEmail();
		email.primary = address.getPrimary();

		return email;
	}

}