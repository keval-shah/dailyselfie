package uk.co.kevalshah.dailyselfie;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class DisplaySelfieActivity extends Activity {

    private static final String TAG = "DisplaySelfieActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_selfie);

        final ImageView imageView = (ImageView) findViewById(R.id.selfie);
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final String filePath = extras.getString(DailySelfieActivity.EXTRA_FILE_PATH);
            final ViewTreeObserver vto = imageView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    Log.d(TAG, "Entered PreDraw");
                    loadBitmap(imageView, filePath);
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
    }

    private void loadBitmap(final ImageView imageView, final String filePath) {
        final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(filePath);
    }
}
