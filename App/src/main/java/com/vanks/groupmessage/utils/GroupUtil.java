package com.vanks.groupmessage.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.vanks.groupmessage.models.unsaved.Contact;

import java.util.ArrayList;

/**
 * Created by vaneyck on 2/5/16.
 */
public class GroupUtil {

	static String TAG = "GroupUtil";
	/**
	 * Returns a List of {@link Contact}s
	 * @param context {@link Context} needed so as to query the content resolver
	 * @param groupId The id of the group as stored in the android database
	 * @return
	 */
	public static ArrayList<Contact> getContactsInGroup (Context context, Long groupId) {
		ArrayList<Contact> contactArrayList = new ArrayList<>();
		ArrayList<String> phoneNumberList = new ArrayList<>();
		String[] cProjection = { ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID };

		Cursor groupCursor = context.getContentResolver().query(
				ContactsContract.Data.CONTENT_URI,
				cProjection,
				ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "= ?" + " AND "
						+ ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE + "='"
						+ ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "'",
				new String[] { String.valueOf(groupId) }, null);
		if (groupCursor != null && groupCursor.moveToFirst()) {
			do {
				int nameCoumnIndex = groupCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
				String name = groupCursor.getString(nameCoumnIndex);
				long contactId = groupCursor.getLong(groupCursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID));
				Cursor numberCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER }, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
				if (numberCursor.moveToFirst()) {
					int numberColumnIndex = numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					do
					{
						String phoneNumber = numberCursor.getString(numberColumnIndex);
						phoneNumber = phoneNumber.replace(" ", "").trim();
						phoneNumber = PhoneNumberUtils.getInternationalPhoneNumber(context, phoneNumber, false);
						if(!phoneNumberList.contains(phoneNumber)) {
							Log.i(TAG, "contact " + name + ":" + phoneNumber);
							contactArrayList.add(new Contact(name, phoneNumber));
							phoneNumberList.add(phoneNumber);
						}
					} while (numberCursor.moveToNext());
					numberCursor.close();
				}
			} while (groupCursor.moveToNext());
			groupCursor.close();
		}
		return contactArrayList;
	}

	/**
	 * Returns the count of members in group given groupId
	 * @param context {@link Context} needed so as to query the content resolver
	 * @param groupId The id of the group as stored in the android database
	 * @return
	 */
	public static Long getGroupCount (Context context, Long groupId) {
		long groupCount = 0;
		String[] cProjection = { ContactsContract.Groups._ID, ContactsContract.Groups.SUMMARY_COUNT };
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.Groups.CONTENT_SUMMARY_URI,
				cProjection,
				ContactsContract.Groups._ID + "= ?",
				new String[] { String.valueOf(groupId) }, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				groupCount = cursor.getLong(cursor.getColumnIndex(ContactsContract.Groups.SUMMARY_COUNT));
				Log.i(TAG, "group count for " +  groupId + " is " + groupCount);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return groupCount;
	}
}
