package com.codinguser.android.contactpicker;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ContactDetailsFragment extends ListFragment {
	private TextView mDisplayName;
	private OnContactSelectedListener mContactsListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contact_detail, container,
				false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		long personId = getArguments().getLong(ContactsPickerActivity.SELECTED_CONTACT_ID);// getIntent().getLongExtra("id", 0);
		Activity activity = getActivity();
		
		Uri phonesUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String[] projection = new String[] {
				Phone._ID, Phone.DISPLAY_NAME,
				Phone.TYPE, Phone.NUMBER };
		String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
		String[] selectionArgs = new String[] { Long.toString(personId) };
		
		Cursor cursor = activity.getContentResolver().query(phonesUri,
				projection, selection, selectionArgs, null);

		activity.startManagingCursor(cursor);
		
		mDisplayName = (TextView) activity.findViewById(R.id.display_name);
		if (cursor.moveToFirst()){
			mDisplayName.setText(cursor.getString(cursor
					.getColumnIndex(Phone.DISPLAY_NAME)));
		}
		
		ListAdapter adapter = new PhoneNumbersAdapter(this.getActivity(),
				R.layout.list_item_phone_number, cursor, new String[] {
						Phone.TYPE, Phone.NUMBER }, new int[] { R.id.label,
						R.id.phone_number });
		setListAdapter(adapter);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mContactsListener = (OnContactSelectedListener) activity;
		} catch (ClassCastException	e) {
			throw new ClassCastException(activity.toString() + " must implement OnContactSelectedListener");
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		TextView tv = (TextView) v.findViewById(R.id.phone_number);
		String number = tv.getText().toString();
		
		mContactsListener.onContactNumberSelected(number);
	}
	
	class PhoneNumbersAdapter extends SimpleCursorAdapter{

		public PhoneNumbersAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			super.bindView(view, context, cursor);
			
			TextView tx = (TextView) view.findViewById(R.id.label);
			int type = cursor.getInt(cursor.getColumnIndex(Phone.TYPE));
			String label = Phone.getTypeLabel(getResources(), type, "Custom").toString();
			tx.setText(label);
		}
		
	}
}