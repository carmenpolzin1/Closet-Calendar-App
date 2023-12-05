package com.cs407.closetcalendar;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {

    // reference: https://www.youtube.com/watch?v=L482ZAno-fY&t=302s

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    Button takePicButton;
    PreviewView previewView;
    private ImageCapture imageCapture;

    int cameraFacing = CameraSelector.LENS_FACING_BACK;

    static final int REQUEST_CAMERA_PERMISSION = 1001;

//    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
//        @Override
//        public void onActivityResult(Boolean result) {
//            if (result) {
//                startCameraX(cameraFacing);
//            }
//        }
//    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // check permissions
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION); // permission at runtime
//            activityResultLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCameraX(cameraFacing);
        }

        takePicButton = findViewById(R.id.take_pic_button);
        previewView = findViewById(R.id.viewFinder);

        takePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    public void startCameraX(int cameraFacing) {
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();

                //bind preview
                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

//                cameraProvider.unbindAll();

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void takePicture() {
        Log.d("Camera", "Take picture called");

        // Get a stable reference of the modifiable image capture use case
        if (imageCapture == null) {
            return;
        }

        File photoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraX");

        if (!photoDir.exists()) {
            if (!photoDir.mkdirs()) {
                Log.e("CameraX", "Failed to create directory: " + photoDir.getAbsolutePath());
                return;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis());
        String fileName = "IMG_" + timeStamp + ".jpg";

        File photoFile = new File(photoDir, fileName);

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, Executors.newSingleThreadExecutor(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = outputFileResults.getSavedUri();
                if (savedUri != null) {
                    String msg = "Photo capture succeeded: " + savedUri.toString();
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    Log.d("CameraXSample", msg);
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("CameraXSample", "Error capturing image: " + exception.getMessage(), exception);
                Toast.makeText(CameraActivity.this, "Error capturing image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public void takePicture(ImageCapture imageCapture) {
//        final File file = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
//        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
//        imageCapture.takePicture(outputFileOptions, Executors.newSingleThreadExecutor(), new ImageCapture.OnImageSavedCallback() {
//            @Override
//            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(CameraActivity.this, "Image saved at: " + file.getPath(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//                startCameraX(cameraFacing);
//            }
//
//            @Override
//            public void onError(@NonNull ImageCaptureException exception) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(CameraActivity.this, "Failed to save at: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//                startCameraX(cameraFacing);
//            }
//        });
//    }

    private int aspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        } else {
            return AspectRatio.RATIO_16_9;
        }
    }

}

    // first implementation
//    https://www.youtube.com/watch?v=IrwhjDtpIU0

//public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
//    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
//    Button takePicButton;
//    PreviewView previewView;
//    private ImageCapture imageCapture;


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_camera);
//
//        takePicButton = findViewById(R.id.take_pic_button);
//        previewView = findViewById(R.id.viewFinder);
//
//        takePicButton.setOnClickListener(this);
//
//        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
//        cameraProviderFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//                startCameraX(cameraProvider);
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }, getExecutor());
//    }
//
//    private Executor getExecutor() {
//        return ContextCompat.getMainExecutor(this);
//    }
//
//    private void startCameraX(ProcessCameraProvider cameraProvider) {
//        // unbind previous use case bindings
//        cameraProvider.unbindAll();
//
//        // camera selector use case
//        CameraSelector cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build();
//
//        // preview use case
//        Preview preview = new Preview.Builder().build();
//
//        preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
//        // image capture use case
//        imageCapture = new ImageCapture.Builder()
//                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//                .build();
//
//        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
//    }
//
//    @Override
//    public void onClick(View view) {
//        capturePhoto();
//    }
//
//    private void capturePhoto() {
//        File photoDir = new File("/mnt/sdcard/Pictures/ClosetCalendarPhotos");
//
//        if (!photoDir.exists()) {
//            photoDir.mkdir();
//        }
//
//        Date date = new Date();
//        String timeStamp = String.valueOf(date.getTime());
//        String photoFilePath  = photoDir.getAbsolutePath() + "/" + timeStamp + ".jpg";
//
//        File photoFile = new File(photoFilePath);
//
//        imageCapture.takePicture(
//                new ImageCapture.OutputFileOptions.Builder(photoFile).build(),
//                getExecutor(),
//                new ImageCapture.OnImageSavedCallback() {
//                    @Override
//                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                        Toast.makeText(CameraActivity.this, "Photo saved successfully!", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(@NonNull ImageCaptureException exception) {
//                        Toast.makeText(CameraActivity.this, "Error saving photo " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//}