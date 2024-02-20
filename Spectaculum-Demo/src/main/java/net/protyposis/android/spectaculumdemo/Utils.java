/*
 * Copyright 2014 Mario Guggenberger <mg@protyposis.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.protyposis.android.spectaculumdemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import net.protyposis.android.spectaculum.SpectaculumView;

/**
 * Created by maguggen on 28.08.2014.
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static void setActionBarSubtitleEllipsizeMiddle(Activity activity) {
        // http://blog.wu-man.com/2011/12/actionbar-api-provided-by-google-on.html
        int subtitleId = activity.getResources().getIdentifier("action_bar_subtitle", "id", "android");
        TextView subtitleView = (TextView) activity.findViewById(subtitleId);
        subtitleView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
    }

    /**
     * An implementation of the OnFrameCapturedCallback that saves captured frames to png files.
     */
    public static class OnFrameCapturedCallback implements SpectaculumView.OnFrameCapturedCallback {

        private Context mContext;
        private String mFileNamePrefix;

        public OnFrameCapturedCallback(Context context, String fileNamePrefix) {
            mContext = context;
            mFileNamePrefix = fileNamePrefix;
        }

        @Override
        public void onFrameCaptured(Bitmap bitmap) {
            String fileName = mFileNamePrefix + System.currentTimeMillis() + ".png";

            try {
                OutputStream fos;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver resolver = mContext.getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                    Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                } else {
                    File targetFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
                    fos = new FileOutputStream(targetFile);
                }

                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                Objects.requireNonNull(fos).close();
                Toast.makeText(mContext, "Saved to pictures directory as " + fileName, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e(TAG, "Failed saving frame", e);
                Toast.makeText(mContext, "Failed saving frame", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Iterates a hierarchy of exceptions and combines their messages. Useful for compact
     * error representation to the user during debugging/development.
     */
    public static String getExceptionMessageHistory(Throwable e) {
        String messages = "";

        String message = e.getMessage();
        if(message != null) {
            messages += message;
        }
        while((e = e.getCause()) != null) {
            message = e.getMessage();
            if(message != null) {
                messages += " <- " + message;
            }
        }

        return messages;
    }
}
