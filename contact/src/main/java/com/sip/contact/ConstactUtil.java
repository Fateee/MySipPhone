package com.sip.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ConstactUtil {
	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	public static Map<String,ArrayList<String>> getAllCallRecords(Context context) {
		Map<String, ArrayList<String>> temp = new HashMap<>();
		Cursor c = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI,
				null,
				null,
				null,
				ContactsContract.Contacts.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");
		if (c.moveToFirst()) {
			do {
				// 获得联系人的ID号
				String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
				// 获得联系人姓名
				String name = c.getString(c
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				// 查看该联系人有多少个电话号码。如果没有这返回值为0
				int phoneCount = c
						.getInt(c
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				String number;
				if (phoneCount > 0) {
					// 获得联系人的电话号码
					Cursor phones = context.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
//					if (phones.moveToFirst()) {
//						number = phones
//								.getString(phones
//								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//						}
					while (phones.moveToNext()) {
						number = phones
								.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						if (!temp.containsKey(name)) {
							temp.put(name, new ArrayList<>());
						}
						temp.get(name).add(number);
					}
					phones.close();
				}
//				temp.put(name, number);
			} while (c.moveToNext());
		}
		c.close();
		return temp;
	}
}
