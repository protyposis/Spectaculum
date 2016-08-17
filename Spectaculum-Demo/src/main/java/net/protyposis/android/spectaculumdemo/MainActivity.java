/*
 * Copyright (c) 2014 Mario Guggenberger <mg@protyposis.net>
 *
 * This file is part of MediaPlayer-Extended.
 *
 * MediaPlayer-Extended is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MediaPlayer-Extended is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MediaPlayer-Extended.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.protyposis.android.spectaculumdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends Activity implements VideoURIInputDialogFragment.OnVideoURISelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_LOAD_VIDEO = 1;

    private Button mVideoSelectButton;
    private Button mVideoSelect2Button;
    private Button mAndroidVideoViewButton;
    private Button mAndroidMediaPlayerButton;
    private Button mMediaPlayerExtendedButton;
    private Button mExoPlayerButton;
    private Button mCameraButton;
    private Button mImageButton;

    private TextView mVideoUriText;
    private Uri mVideoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(BuildConfig.CRASHLYTICS_CONFIGURED) {
            Fabric.with(this, new Crashlytics());
        } else {
            Log.w(TAG, "Crashlytics not configured!");
        }

        setContentView(R.layout.activity_main);

        mVideoSelectButton = (Button) findViewById(R.id.videoselect);
        mVideoSelect2Button = (Button) findViewById(R.id.videoselect2);
        mAndroidVideoViewButton = (Button) findViewById(R.id.androidvideoview);
        mAndroidMediaPlayerButton = (Button) findViewById(R.id.androidmediaplayer);
        mMediaPlayerExtendedButton = (Button) findViewById(R.id.mediaplayerextendedvideoview);
        mExoPlayerButton = (Button) findViewById(R.id.exoplayervideoview);
        mCameraButton = (Button) findViewById(R.id.cameraview);
        mImageButton = (Button) findViewById(R.id.imageview);
        mVideoUriText = (TextView) findViewById(R.id.videouri);

        mVideoSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open the picker...
                Log.d(TAG, "opening video picker...");
                Intent intent = null;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("video/*");
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("video/*");
                }
                startActivityForResult(intent, REQUEST_LOAD_VIDEO);
            }
        });
        mVideoSelect2Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoURIInputDialogFragment dialog = new VideoURIInputDialogFragment();
                    dialog.show(getFragmentManager(), null);
                }
        });

        mAndroidVideoViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VideoViewActivity.class).setData(mVideoUri));
            }
        });
        mAndroidMediaPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MediaPlayerActivity.class).setData(mVideoUri));
            }
        });
        mMediaPlayerExtendedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MediaPlayerExtendedViewActivity.class).setData(mVideoUri));
            }
        });
        mExoPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ExoPlayerActivity.class).setData(mVideoUri));
            }
        });
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CameraViewActivity.class).setData(mVideoUri));
            }
        });
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ImageViewActivity.class));
            }
        });
        (findViewById(R.id.licenses)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebView licensesWebView = new WebView(MainActivity.this);
                licensesWebView.loadUrl("file:///android_asset/licenses.html");

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.open_source_licenses))
                        .setView(licensesWebView)
                        .create()
                        .show();
            }
        });

        Uri uri = null;

        if (getIntent().getData() != null) {
            // The intent-filter probably caught an url, open it...
            uri = getIntent().getData();
        } else {
            String savedUriString = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getString("lastUri", "");
            if(!"".equals(savedUriString)) {
                uri = Uri.parse(savedUriString);
            }
        }

        if(savedInstanceState != null) {
            uri = savedInstanceState.getParcelable("uri");
        }

        updateUri(uri);
        versionInfos();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_LOAD_VIDEO) {
            Log.d(TAG, "onActivityResult REQUEST_LOAD_VIDEO");

            if(resultCode == RESULT_OK) {
                updateUri(data.getData());
            } else {
                Log.w(TAG, "no file specified");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onVideoURISelected(Uri uri) {
        updateUri(uri);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("uri", mVideoUri);
        super.onSaveInstanceState(outState);
    }

    private void updateUri(final Uri uri) {
        if(uri == null) {
            mVideoUriText.setText(getString(R.string.uri_missing));

            mAndroidVideoViewButton.setEnabled(false);
            mAndroidMediaPlayerButton.setEnabled(false);
            mMediaPlayerExtendedButton.setEnabled(false);
            mExoPlayerButton.setEnabled(false);
        } else {
            updateUri(null); // disable buttons

            // Validate content URI
            if(uri.getScheme().equals("content")) {
                ContentResolver cr = getContentResolver();
                try {
                    cr.openInputStream(uri).close();
                } catch (Exception e) {
                    // The content URI is invalid, probably because the file has been removed
                    // or the system rebooted (which invalidates content URIs).
                    return;
                }
            }

            String text = uri.toString();
            mVideoUriText.setText(text);
            mVideoUri = uri;

            mAndroidVideoViewButton.setEnabled(true);
            mAndroidMediaPlayerButton.setEnabled(true);
            mMediaPlayerExtendedButton.setEnabled(true);
            mExoPlayerButton.setEnabled(true);

            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .edit().putString("lastUri", uri.toString()).commit();
        }
    }

    private void versionInfos() {
        String versionInfos = "";
        Map<String, Class> components = new LinkedHashMap<String, Class>();
        components.put("Spectaculum-Demo", net.protyposis.android.spectaculumdemo.BuildConfig.class);
        components.put("Spectaculum", net.protyposis.android.spectaculum.BuildConfig.class);
        components.put("Spectaculum-Camera", net.protyposis.android.spectaculum.camera.BuildConfig.class);
        components.put("Spectaculum-Image", net.protyposis.android.spectaculum.image.BuildConfig.class);
        components.put("Spectaculum-MediaPlayerExtended", net.protyposis.android.spectaculum.mediaplayerextended.BuildConfig.class);
        components.put("Spectaculum-Effect-Immersive", net.protyposis.android.spectaculum.gles.immersive.BuildConfig.class);
        components.put("Spectaculum-Effect-FlowAbs", net.protyposis.android.spectaculum.gles.flowabs.BuildConfig.class);
        components.put("Spectaculum-Effect-QrMarker", net.protyposis.android.spectaculum.gles.qrmarker.BuildConfig.class);
        components.put("MediaPlayer", net.protyposis.android.mediaplayer.BuildConfig.class);

        Iterator<String> componentIterator = components.keySet().iterator();
        while(componentIterator.hasNext()) {
            String component = componentIterator.next();
            versionInfos += component + ":" + versionInfo(components.get(component));
            if(componentIterator.hasNext()) {
                versionInfos += ", ";
            }
        }

        ((TextView) findViewById(R.id.versioninfos)).setText(versionInfos);
    }

    private String versionInfo(Class buildInfo) {
        String info = "";
        try {
            info += buildInfo.getField("VERSION_NAME").get(null).toString();
            info += "/";
            info += buildInfo.getField("VERSION_CODE").get(null).toString();
            info += "/";
            info += buildInfo.getField("BUILD_TYPE").get(null).toString();
            info += buildInfo.getField("FLAVOR").get(null).toString();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return info.length() == 0 ? "n/a" : info;
    }
}
