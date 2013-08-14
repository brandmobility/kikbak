
package com.referredlabs.kikbak.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.referredlabs.kikbak.R;

import java.util.ArrayList;

public class PickContactsActivity extends KikbakActivity
    implements LoaderCallbacks<Cursor>, OnClickListener {

  public static final String ARG_TYPE = "type";
  public static final int TYPE_EMAIL = 0;
  public static final int TYPE_PHONE = 1;

  public static final String DATA = "data";

  private static final int LOADER_ID = 0;

  private static final String CONTACT_NAME = Contacts.DISPLAY_NAME;
  private static final String CONTACT_EMAIL = CommonDataKinds.Email.DATA;

  private ListView mList;
  private Button mShareButton;
  DataChooserHelper mHelper;
  private ContactDataAdapter mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    int type = getIntent().getIntExtra(ARG_TYPE, TYPE_EMAIL);
    mHelper = type == TYPE_EMAIL ? new EmailChooserHelper() : new PhoneChooserHelper();
    setContentView(R.layout.activity_share_gift_via_email);
    mList = (ListView) findViewById(R.id.list);
    mAdapter = mHelper.getCursorAdapter(this);
    mList.setAdapter(mAdapter);
    mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    mList.setItemsCanFocus(false);

    View emptyView = getLayoutInflater().inflate(R.layout.share_gift_empty_contacts_list, null);
    mList.setEmptyView(emptyView);

    mShareButton = (Button) findViewById(R.id.share_gift);
    mShareButton.setOnClickListener(this);
    getSupportLoaderManager().initLoader(LOADER_ID, null, this);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    CursorLoader loader = mHelper.getCursorLoader(this);
    mHelper.configureLoader(loader, null);
    return loader;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    mAdapter.swapCursor(cursor);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> cursor) {
    mAdapter.swapCursor(null);
  }

  @Override
  public void onClick(View v) {
    onShareButtonClicked();
  }

  private void onShareButtonClicked() {
    ArrayList<String> data = getSelectedData();
    Intent intent = new Intent();
    if (!data.isEmpty())
      intent.putExtra(DATA, data);
    setResult(RESULT_OK, intent);
    finish();
  }

  private ArrayList<String> getSelectedData() {
    ArrayList<String> data = new ArrayList<String>();
    SparseBooleanArray selectedPos = mList.getCheckedItemPositions();
    int size = selectedPos.size();
    for (int pos = 0; pos < size; pos++) {
      if (selectedPos.valueAt(pos)) {
        String elem = mAdapter.getDataAtPosition(selectedPos.keyAt(pos));
        data.add(elem);
      }
    }
    return data;
  }

  private static abstract class DataChooserHelper {

    public static String COL_CONTACT_NAME = Contacts.DISPLAY_NAME;

    protected abstract String getColDataName();

    protected abstract Uri getEmptySearchUri();

    protected abstract Uri getFilledSearchUri();

    protected abstract String getNoContactsWithDataMsg(Context context);

    private String[] getProjection() {
      String[] projection = new String[] {
          ContactsContract.RawContacts._ID, COL_CONTACT_NAME, getColDataName()
      };
      return projection;
    }

    private String getSelection() {
      String selection = getColDataName() + " NOT LIKE ''"
          + " AND " + Contacts.IN_VISIBLE_GROUP + " = '1'";
      return selection;
    }

    private String getOrder() {
      return COL_CONTACT_NAME;
    }

    public CursorLoader getCursorLoader(Context context) {
      return new CursorLoader(context, getEmptySearchUri(),
          getProjection(), getSelection(), null, getOrder());
    }

    public void configureLoader(CursorLoader loader, String queryString) {
      Uri uri;
      if (TextUtils.isEmpty(queryString)) {
        uri = getEmptySearchUri();
      } else {
        uri = Uri.withAppendedPath(getFilledSearchUri(), Uri.encode(queryString));
      }
      loader.setUri(uri);
    }

    public ContactDataAdapter getCursorAdapter(Context context) {
      return new ContactDataAdapter(context,
          R.layout.checkable_contact_row, null,
          new String[] {
              COL_CONTACT_NAME, getColDataName()
          },
          new int[] {
              R.id.name, R.id.dataItem
          }, 0, 2);
    }
  }

  private static class EmailChooserHelper extends DataChooserHelper {
    public static final String COL_CONTACT_DATA = CommonDataKinds.Email.DATA;
    public static final Uri EMPTY_SEARCH_URI =
        ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    public static final Uri FILLED_SEARCH_URI =
        ContactsContract.CommonDataKinds.Email.CONTENT_FILTER_URI;

    @Override
    protected String getColDataName() {
      return COL_CONTACT_DATA;
    }

    @Override
    protected Uri getEmptySearchUri() {
      return EMPTY_SEARCH_URI;
    }

    @Override
    protected Uri getFilledSearchUri() {
      return FILLED_SEARCH_URI;
    }

    @Override
    public String getNoContactsWithDataMsg(Context context) {
      // TODO move this to resources
      return "You don't have any contacts with email addresses.";
    }
  }

  private static class PhoneChooserHelper extends DataChooserHelper {
    public static final String COL_CONTACT_DATA = CommonDataKinds.Phone.DATA;
    public static final Uri EMPTY_SEARCH_URI =
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    public static final Uri FILLED_SEARCH_URI =
        ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI;

    @Override
    protected String getColDataName() {
      return COL_CONTACT_DATA;
    }

    @Override
    protected Uri getEmptySearchUri() {
      return EMPTY_SEARCH_URI;
    }

    @Override
    protected Uri getFilledSearchUri() {
      return FILLED_SEARCH_URI;
    }

    @Override
    public String getNoContactsWithDataMsg(Context context) {
      // TODO move this to resources
      return "You don't have any contacts with phone numbers.";
    }
  }

  private static class ContactDataAdapter extends SimpleCursorAdapter {
    private int mDataColIndex;

    public ContactDataAdapter(Context context, int layout, Cursor c,
        String[] from, int[] to, int flags, int dataColIndex) {
      super(context, layout, c, from, to, flags);
      mDataColIndex = dataColIndex;
    }

    public String getDataAtPosition(int position) {
      Cursor cursor = (Cursor) super.getItem(position);
      String dataItem = cursor.getString(mDataColIndex);
      return dataItem;
    }
  }

}
