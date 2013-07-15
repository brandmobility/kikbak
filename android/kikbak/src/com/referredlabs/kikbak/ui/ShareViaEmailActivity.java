
package com.referredlabs.kikbak.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.referredlabs.kikbak.R;
import com.referredlabs.kikbak.data.ShareExperienceRequest;
import com.referredlabs.kikbak.data.ShareExperienceResponse;
import com.referredlabs.kikbak.data.SharedType;
import com.referredlabs.kikbak.http.Http;
import com.referredlabs.kikbak.utils.Register;

import java.io.IOException;
import java.util.ArrayList;

public class ShareViaEmailActivity extends FragmentActivity
    implements LoaderCallbacks<Cursor>, OnClickListener {

  private static final int LOADER_ID = 0;

  private static final String CONTACT_NAME = Contacts.DISPLAY_NAME;
  private static final String CONTACT_EMAIL = CommonDataKinds.Email.DATA;

  public static final String ARG_COMMENT = "comment";
  public static final String ARG_LOCATION_ID = "location_id";
  public static final String ARG_MERCHANT_ID = "merchant_id";
  public static final String ARG_OFFER_ID = "offer_id";

  private ListView mList;
  private Button mShareButton;
  private SimpleCursorAdapter mAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_share_gift_via_email);
    mList = (ListView) findViewById(R.id.list);
    mAdapter = new SimpleCursorAdapter(this, R.layout.checkable_contact_row, null,
        new String[] {
            CONTACT_NAME, CONTACT_EMAIL
        }, new int[] {
            R.id.name, R.id.email
        }, 0);
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
    return new ContactsWithEmailsLoader(this);
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
    ArrayList<String> emails = getSelectedEmails();
    if (!emails.isEmpty()) {
      String subject = "Please accept this gift";
      String message = "I'm sharing this gift with you.";

      Intent intent = new Intent(Intent.ACTION_SENDTO);
      intent.setData(Uri.parse("mailto:"));
      intent.putExtra(Intent.EXTRA_EMAIL, emails.toArray(new String[emails.size()]));
      intent.putExtra(Intent.EXTRA_SUBJECT, subject);
      intent.putExtra(Intent.EXTRA_TEXT, message);
      startActivity(intent);
      registerSharing();
    }
  }

  private ArrayList<String> getSelectedEmails() {
    ArrayList<String> emails = new ArrayList<String>();
    SparseBooleanArray selectedPos = mList.getCheckedItemPositions();
    int size = selectedPos.size();
    for (int pos = 0; pos < size; pos++) {
      if (selectedPos.valueAt(pos)) {
        Cursor c = (Cursor) mList.getItemAtPosition(selectedPos.keyAt(pos));
        String email = c.getString(ContactsWithEmailsLoader.EMAIL_COLUMN);
        emails.add(email);
      }
    }
    return emails;
  }

  private void registerSharing() {
    Bundle args = getIntent().getExtras();
    final ShareExperienceRequest req = new ShareExperienceRequest();
    req.experience = new SharedType();
    req.experience.caption = args.getString(ARG_COMMENT);
    req.experience.fbImageId = 0;
    req.experience.locationId = args.getLong(ARG_LOCATION_ID);
    req.experience.merchantId = args.getLong(ARG_MERCHANT_ID);
    req.experience.offerId = args.getLong(ARG_OFFER_ID);
    req.experience.type = SharedType.SHARE_MODE_EMAIL;
    final long userId = Register.getInstance().getUserId();
    new ShareTask(req, userId).execute();
  }

  class ShareTask extends AsyncTask<Void, Void, Void> {
    private ShareExperienceRequest mRequest;
    private long mUserId;

    ShareTask(ShareExperienceRequest request, long userId) {
      mRequest = request;
      mUserId = userId;
    }

    @Override
    protected Void doInBackground(Void... params) {
      String uri = Http.getUri(ShareExperienceRequest.PATH + mUserId);
      try {
        Http.execute(uri, mRequest, ShareExperienceResponse.class);
      } catch (IOException e) {
        android.util.Log.d("MMM", "Exception: " + e);
      }
      return null;
    }
  }

  private static class ContactsWithEmailsLoader extends CursorLoader {
    public static final int EMAIL_COLUMN = 2;
    public static final Uri URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    public static final String[] PROJECTION = new String[] {
        ContactsContract.RawContacts._ID, CONTACT_NAME, CONTACT_EMAIL
    };
    public static final String SELECTION =
        ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
    public static final String ORDER = ContactsContract.Contacts.DISPLAY_NAME;

    public ContactsWithEmailsLoader(Context context) {
      super(context, URI, PROJECTION, SELECTION, null, ORDER);
    }
  }

}
