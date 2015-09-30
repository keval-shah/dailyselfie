package uk.co.kevalshah.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SelfieListAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater mInflater;
    private final List<File> mImages = new ArrayList<>();
    private Bitmap mPlaceHolderBitmap = null;

    public SelfieListAdapter(final Context context, final List<File> images) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mImages.addAll(images);
        this.mPlaceHolderBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_camera_alt_white_24dp);
    }

    public void add(final File file) {
        mImages.add(file);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mImages.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public Object getItem(int position) {
        return mImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.selfie_list_row_item, parent, false);
            holder = new ViewHolder();
            holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            holder.fileName = (TextView) convertView.findViewById(R.id.fileName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final File item = (File) getItem(position);
        holder.fileName.setText(item.getName());
        loadBitmap(item.getPath(), holder.thumbnail);

        return convertView;
    }

    private class ViewHolder {
        public ImageView thumbnail;
        public TextView fileName;
    }

    private void loadBitmap(final String filePath, final ImageView imageView) {
        if (cancelPotentialWork(filePath, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), mPlaceHolderBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(filePath);
        }
    }

    private boolean cancelPotentialWork(final String filePath, final ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.getData();
            //If bitmapData is not yet set or it differs from the new data
            if (bitmapData == null || !bitmapData.equals(filePath)) {
                //Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                //The same work is already in progress
                return false;
            }
        }
        //No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private BitmapWorkerTask getBitmapWorkerTask(final ImageView imageView) {
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
