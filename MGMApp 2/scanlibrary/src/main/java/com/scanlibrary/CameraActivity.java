package com.scanlibrary;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CameraActivity extends Activity {

    CameraView cameraView;
    View viewRect;
    RelativeLayout relLayout;
    ImageButton capturePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraView = findViewById(R.id.camera);
        viewRect = findViewById(R.id.viewRect);
        relLayout = findViewById(R.id.relLayout);
        capturePhoto = findViewById(R.id.capturePhoto);
        cameraView.setJpegQuality(100);
        cameraView.setPlaySounds(false);
        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS);
        cameraView.addCameraListener(cameraListener);
        cameraView.setCropOutput(true);

        capturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capture();
            }
        });
    }

    private void capture(){
        if(!cameraView.isStarted()) return;
        cameraView.capturePicture();
    }

    CameraListener cameraListener = new CameraListener() {
        @Override
        public void onCameraOpened(CameraOptions options) {
            super.onCameraOpened(options);
            int w = cameraView.getWidth();
            ViewGroup.LayoutParams params = viewRect.getLayoutParams();
            params.height = 9 * w / 16;
            viewRect.setLayoutParams(params);
        }

        @Override
        public void onCameraClosed() {
            super.onCameraClosed();
        }

        @Override
        public void onCameraError(@NonNull CameraException exception) {
            super.onCameraError(exception);
        }

        @Override
        public void onPictureTaken(byte[] jpeg) {
            cameraView.stop();
            CameraUtils.decodeBitmap(jpeg, new CameraUtils.BitmapCallback() {
                @Override
                public void onBitmapReady(Bitmap bitmap) {
                    Uri fileUri = (Uri) getIntent().getExtras().get(MediaStore.EXTRA_OUTPUT);
                    OutputStream fOut = null;

                    Rect r = new Rect();
                    viewRect.getLocalVisibleRect(r);
                    int width  = bitmap.getWidth();
                    int height = bitmap.getWidth() * 9 / 16;
                    Bitmap resBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                    File file = new File(fileUri.getPath());
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            fOut = getApplicationContext().getContentResolver().openOutputStream(fileUri);
                        else
                            fOut = new FileOutputStream(file);
                        resBmp.compress(Bitmap.CompressFormat.JPEG, 100,fOut);
                        fOut.flush(); // Not really required
                        fOut.close(); // do not forget to close the stream

                        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N))
                            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });
        }

        @Override
        public void onVideoTaken(File video) {
            super.onVideoTaken(video);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            super.onOrientationChanged(orientation);
        }

        @Override
        public void onFocusStart(PointF point) {
            super.onFocusStart(point);
        }

        @Override
        public void onFocusEnd(boolean successful, PointF point) {
            super.onFocusEnd(successful, point);
        }

        @Override
        public void onZoomChanged(float newValue, float[] bounds, PointF[] fingers) {
            super.onZoomChanged(newValue, bounds, fingers);
        }

        @Override
        public void onExposureCorrectionChanged(float newValue, float[] bounds, PointF[] fingers) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers);
        }
    };

    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

}