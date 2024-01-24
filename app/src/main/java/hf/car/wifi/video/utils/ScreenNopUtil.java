package hf.car.wifi.video.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ScreenNopUtil {
    public static String  TAG = "ScreenNopUtil";

    public static Bitmap takeScreenshot(Activity activity, int ResourceID) {
        Random r = new Random();
        int iterator=r.nextInt();
        String mPath = Environment.getExternalStorageDirectory().toString() + "/screenshots/";
        View v1 = activity.getWindow().getDecorView().findViewById(ResourceID);
        v1.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v1.layout(0, 0, v1.getMeasuredWidth(), v1.getMeasuredHeight());
        v1.setDrawingCacheEnabled(true);
        final Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        Bitmap resultBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, false);
        v1.setDrawingCacheEnabled(false);
        File imageFile = new File(mPath);
        imageFile.mkdirs();
        imageFile = new File(imageFile+"/"+iterator+"_screenshot.png");
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
