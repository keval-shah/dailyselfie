package uk.co.kevalshah.dailyselfie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by Keval on 27/09/2015.
 */
public class SelfieListAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final List<File> mImages;

    public SelfieListAdapter(final Context context, final List<File> images) {
        this.mInflater = LayoutInflater.from(context);
        this.mImages = images;
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

        return convertView;
    }

    private class ViewHolder {
        public ImageView thumbnail;
        public TextView fileName;
    }
}
