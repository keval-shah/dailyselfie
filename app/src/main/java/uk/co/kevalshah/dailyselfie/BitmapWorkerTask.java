package uk.co.kevalshah.dailyselfie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = "BitmapWorkerTask";
    private final WeakReference<ImageView> imageViewReference;
    private final int imageViewWidth;
    private final int imageViewHeight;
    private String data = null;

    BitmapWorkerTask(final ImageView imageView) {
        //Use a WeakReference to ensure the ImageView can be garbage collected
        this.imageViewReference = new WeakReference<>(imageView);
        if (imageView.getWidth() == 0 || imageView.getHeight() == 0) {
            Log.d(TAG, "Image view width or height zero");
        }
        this.imageViewWidth = imageView.getWidth() == 0 ? 50 : imageView.getWidth() ;
        this.imageViewHeight = imageView.getHeight() == 0 ? 50 : imageView.getHeight();
    }

    String getData() {
        return data;
    }

    @Override
    protected Bitmap doInBackground(final String... params) {
        data = params[0];
        return decodeFile(data);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap decodeFile(final String filePath) {
        //Get the dimensions of the bitmap
        final BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bitmapOptions);
        final int photoWidth = bitmapOptions.outWidth;
        final int photoHeight = bitmapOptions.outHeight;

        //Determine how much to scale down the image
        //TODO: Look into other inSampleSize calculation methods
        final int scaleFactor = Math.min(photoWidth/imageViewWidth, photoHeight/imageViewHeight);

        //Decode the image file into a Bitmap sized to fill the View.
        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = scaleFactor;
        return BitmapFactory.decodeFile(filePath, bitmapOptions);
    }

    private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }
}
