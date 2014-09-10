package za.co.jethromuller.imageviewer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    private Context imageContext;
    public Uri[] uris;
    public File[] imagelist;

    public ImageUtils(Context context) {
        imageContext = context;
        populateUriArray();
    }

    private void populateUriArray() {
        File images = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera");
        Log.d("DIRECTORTY EXISTS: ", String.valueOf(images.exists()));
        Log.d("FILE PATH: ", images.getAbsolutePath());
        imagelist = images.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name)
            {
                return (name.endsWith(".jpg") || name.endsWith(".png"));
            }
        });

        uris = new Uri[imagelist.length];

        for(int i= 0 ; i< imagelist.length; i++) {
            uris[i] = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    getImageIdFromFilePath(imagelist[i].getPath(),
                            imageContext.getContentResolver()));
            Log.d("uris[i] = ", String.valueOf(uris[i]));
        }
    }

    /**
     * Adapted from: http://stackoverflow.com/a/11603837
     *
     * Gets the MediaStore video ID of a given file on external storage
     * @param filePath The path (on external storage) of the file to resolve the ID of
     * @param contentResolver The content resolver to use to perform the query.
     * @return the video ID as a long
     */
    public long getImageIdFromFilePath(String filePath, ContentResolver contentResolver) {
        long imageId;
        Uri imagesUri = MediaStore.Images.Media.getContentUri("external");
        String[] projection = {MediaStore.Images.ImageColumns._ID};
        Cursor cursor = contentResolver.query(imagesUri, projection,
                MediaStore.Images.ImageColumns.DATA + " LIKE ?", new String[] { filePath }, null);
        cursor.moveToFirst();

        imageId = cursor.getLong(cursor.getColumnIndex(projection[0]));

        cursor.close();
        return imageId;
    }

    /**
     * From: http://stackoverflow.com/a/3549021
     */
    public Bitmap decodeFile(Uri uri, int screenWidth){
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        try {
            InputStream fis = imageContext.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outWidth > screenWidth) {
                scale = (int)Math.pow(2, (int) Math.ceil(Math.log(screenWidth /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = imageContext.getContentResolver().openInputStream(uri);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return b;
    }
}
