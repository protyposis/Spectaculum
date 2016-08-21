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

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import net.protyposis.android.spectaculum.ImageView;
import net.protyposis.android.spectaculum.PipelineResolution;

import java.io.IOException;

public class ImageViewActivity extends SpectaculumDemoBaseActivity {

    private static final int REQUEST_LOAD_IMAGE = 1;

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_imageview);
        super.onCreate(savedInstanceState);

        mImageView = (ImageView) findViewById(R.id.spectaculum);
        mImageView.setPipelineResolution(PipelineResolution.VIEW);
        mImageView.setOnFrameCapturedCallback(new Utils.OnFrameCapturedCallback(this, "spectaculum-image"));
        mImageView.setTouchEnabled(true); // enable zoom&pan

        // Load previous image or open image picker
        if(savedInstanceState != null) {
            // Load an already selected image (after configuration change)
            // uri is stored in the parent activity
            loadImage((Uri)savedInstanceState.getParcelable("uri"));
        } else if(getIntent().getData() != null) {
            // The intent-filter probably caught an url, open it...
            loadImage(getIntent().getData());
        } else {
            // Show image selection dialog
            Intent intent;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
            } else {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
            }
            startActivityForResult(intent, REQUEST_LOAD_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK) {
            // An image has been picked, load it
            loadImage(data.getData());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loadImage(Uri imageUri) {
        try {
            setMediaUri(imageUri);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            mImageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            Log.e("ImageViewActivity", "error loading image", e);
        }
    }
}
