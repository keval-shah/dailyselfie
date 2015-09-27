package uk.co.kevalshah.dailyselfie;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DailySelfieActivity extends AppCompatActivity {

    private static final String TAG = "DailySelfieActivity";
    private static final int REQUEST_TAKE_PHOTO = 1;

    private String mCurrentPhotoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_selfie);
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
        final File picturesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        final File storageDir = new File(picturesDir, "dailyselfie");
        if (!storageDir.exists()) {
            final boolean success = storageDir.mkdirs();
            if (!success) {
                Log.e(TAG, "Failed to create storage directory");
            }
        }
        final File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
}
