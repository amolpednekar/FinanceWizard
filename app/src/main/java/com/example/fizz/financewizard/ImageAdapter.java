package com.example.fizz.financewizard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<MyImage> {

    private final int THUMBSIZE = 96;
    /**
     * applying ViewHolder pattern to speed up ListView, smoother and faster
     * item loading by caching view in A ViewHolder object
     */
    public static class ViewHolder {
        ImageView imgIcon;
        TextView description;
        int position;
    }
    public ImageAdapter(Context context, ArrayList<MyImage> images) {
        super(context, 0, images);

    }

    @Override public View getView(int position, View convertView,
                                  ViewGroup parent) {
        // view lookup cache stored in tag
        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the
        // item view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            viewHolder.position=position;
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_image, parent, false);
            viewHolder.description =(TextView) convertView.findViewById(R.id.item_img_infor);
            viewHolder.imgIcon =(ImageView) convertView.findViewById(R.id.item_img_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the data item for this position
        MyImage image = getItem(position);
        // set description text
        viewHolder.description.setText(image.toString());
        // set image icon
        /*viewHolder.imgIcon.setImageBitmap(ThumbnailUtils
                .extractThumbnail(BitmapFactory.decodeFile(image.getPath()),
                        THUMBSIZE, THUMBSIZE));*/



        new ThumbnailsTask(position,viewHolder).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);

        // Return the completed view to render on screen
        return convertView;
    }

    /*private Bitmap decodeFile(File f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }*/

    public class ThumbnailsTask extends AsyncTask<Void,Void,MyImage> {

        private final int pos;
        private final ViewHolder holder;
        MyImage image;

        @Override
        protected  void onPreExecute(){
            image=getItem(pos);
        }

        public ThumbnailsTask(int pos1,ViewHolder holder) {
            this.pos = pos1;
            this.holder=holder;
        }


        @Override
        protected MyImage doInBackground(Void... params) {
           // Bitmap b= BitmapFactory.decodeFile(image.getPath());
            image=getItem(pos);
           return image;
        }

        protected void  onPostExecute(MyImage r) {
            if ( holder.imgIcon!=null) { // or use holder.imgcon!=null
                holder.imgIcon.setImageBitmap(decodeSampledBitmapFromFile(r.getPath(),96,96));
                //holder.description.setText(image.toString());
            }
        }

        public  int calculateInSampleSize(BitmapFactory.Options options,
                                                int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                // Calculate the largest inSampleSize value that is a power of 2
                // and keeps both height and width larger than the requested
                // height and width.
                while ((halfHeight / inSampleSize) > reqHeight &&
                        (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }
            return inSampleSize;
        }
        /**
         * Decode and sample down a bitmap from a file to the requested width and
         * height.
         *
         * @param filename  The full path of the file to decode
         * @param reqWidth  The requested width of the resulting bitmap
         * @param reqHeight The requested height of the resulting bitmap
         * @return A bitmap sampled down from the original with the same aspect
         * ratio and dimensions that are equal to or greater than the requested
         * width and height
         */
        public  Bitmap decodeSampledBitmapFromFile(String filename,
                                                         int reqWidth, int reqHeight) {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filename, options);
            // Calculate inSampleSize
            options.inSampleSize =
                    calculateInSampleSize(options, reqWidth, reqHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(filename, options);
        }
    }



}
