package uk.co.kevalshah.dailyselfie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class DailySelfieActivity extends AppCompatActivity {

    private static final String TAG = "DailySelfieActivity";
    private static final int REQUEST_TAKE_PHOTO = 1;
    static final String EXTRA_FILE_PATH = "filePath";

    private String mCurrentPhotoPath = null;
    private SelfieListAdapter mListAdapter = null;
    private ListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_selfie);
        setupListView();
    }

    private void setupListView() {
        final File storageDir = getStorageDirectory();
        final File[] files = storageDir.listFiles();

        listView = (ListView) findViewById(R.id.selfiesListView);
        mListAdapter = new SelfieListAdapter(this, Arrays.asList(files));
        listView.setAdapter(mListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final File file = (File) parent.getAdapter().getItem(position);
                final Intent displaySelfieIntent = new Intent(DailySelfieActivity.this, DisplaySelfieActivity.class);
                displaySelfieIntent.putExtra(EXTRA_FILE_PATH, file.getPath());
                startActivity(displaySelfieIntent);
            }
        });
        registerForContextMenu(listView);
    }

    private File getStorageDirectory() {
        final File picturesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        final File storageDir = new File(picturesDir, "dailyselfie");
        if (!storageDir.exists()) {
            final boolean success = storageDir.mkdirs();
            if (!success) {
                Log.e(TAG, "Failed to create storage directory");
            }
        }
        return storageDir;
    }

    @Override
    protected void onDestroy() {
        listView.setAdapter(null);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_selfie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_camera) {
            dispatchTakePictureIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (final IOException e) {
                Log.e(TAG, "Failed to create Image File for new photo", e);
                Toast.makeText(getApplicationContext(), "Unable to take picture", Toast.LENGTH_LONG)
                        .show();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.d(TAG, "Photo location was " + mCurrentPhotoPath);
//            final Bundle extras = data.getExtras();
//            final Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap); //Sets the thumbnail to an image view.
        }
    }

    private File createImageFile() throws IOException {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String imageFileName = "selfie_" + timeStamp + "_";
        final File storageDir = getStorageDirectory();
        final File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.selfiesListView) {
            final String[] menuItems = getResources().getStringArray(R.array.selfie_list_item_context_menu_options);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int menuItemIndex = item.getItemId();

        if (menuItemIndex == 0) {
            final DeleteFileTask deleteFileTask = new DeleteFileTask();
            deleteFileTask.execute(info.position);
        }
        return true;
    }

    private class DeleteFileTask extends AsyncTask<Integer, Integer, Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(DailySelfieActivity.this,
                ProgressDialog.STYLE_SPINNER);
        private int position = -1;

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Deleting Selfie...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(final Integer... params) {
            position = params[0];
            final File file = (File) mListAdapter.getItem(position);
            return file.delete();
        }

        @Override
        protected void onPostExecute(final Boolean deleted) {
            if (dialog.isShowing()) dialog.dismiss();

            if (deleted) {
                mListAdapter.remove(position);
            } else {
                Toast.makeText(getApplicationContext(), "Failed to delete file", Toast.LENGTH_LONG).show();
            }
        }
    }
}
