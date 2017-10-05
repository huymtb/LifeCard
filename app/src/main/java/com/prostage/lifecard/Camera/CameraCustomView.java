package com.prostage.lifecard.Camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.prostage.lifecard.GuideToCaptureImageActivity;
import com.prostage.lifecard.R;
import com.prostage.lifecard.ReviewImageActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * Created by PHP Team on 10/25/2016.
 *
 */
public class CameraCustomView extends Activity implements SurfaceHolder.Callback {

    private Camera camera = null;
    private SurfaceHolder cameraSurfaceHolder = null;
    private boolean previewing = false;
    private static int fullWid = 0, fullHgt = 0;
    private Button cameraButton;
    private ImageView imageView;
    private View view_layout;
    private RelativeLayout relativeLayout;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    private byte[] dataImage;
    private int id1, id2, id3, id4, id5, id6;
    DrawingView drawingView;
    Camera.Face[] detectedFaces;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.camera_overlay);

        Display display = getWindowManager().getDefaultDisplay();
        fullWid = display.getWidth();
        fullHgt = display.getHeight();
        int wid = (int) (display.getWidth()*0.75);
        int hgt = (int) (display.getHeight()*0.75);

        relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);
        Button okBtn = (Button)findViewById(R.id.getImageBtn);
        Button cancelBtn = (Button)findViewById(R.id.cancelImageBtn);

        final Intent intent = getIntent();
        final String path = intent.getStringExtra("path");
        id1 = intent.getIntExtra("id", 1001);
        id2 = intent.getIntExtra("id", 1002);
        id3 = intent.getIntExtra("id", 1003);
        id4 = intent.getIntExtra("id", 1004);
        id5 = intent.getIntExtra("id", 1005);
        id6 = intent.getIntExtra("id", 1006);

        imageView = (ImageView)findViewById(R.id.previewImage);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        view_layout = findViewById(R.id.frame_layout);
        view_layout.getLayoutParams().width = wid;
        view_layout.getLayoutParams().height = hgt;
        view_layout.setVisibility(View.VISIBLE);

        CameraSurfaceView cameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        cameraSurfaceHolder = cameraSurfaceView.getHolder();
        cameraSurfaceHolder.addCallback(this);

        cameraSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        drawingView = new DrawingView(this);
        ViewGroup.LayoutParams layoutParamsDrawing
                = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
        this.addContentView(drawingView, layoutParamsDrawing);

        LayoutInflater layoutInflater = LayoutInflater.from(getBaseContext());
        WindowManager.LayoutParams layoutParamsControl = new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
        View cameraViewControl = layoutInflater.inflate(R.layout.shuttle_button_camera, null);
        this.addContentView(cameraViewControl, layoutParamsControl);
        cameraButton = (Button)findViewById(R.id.btnCamera);
        AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        manager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(null, cameraPictureCallbackRaw, cameraPictureCallbackJpeg);
                view_layout.setVisibility(View.GONE);
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (path != null){
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    File file1 = new File(path);
                    FileOutputStream outputStream = null;
                    if (file1.exists()){
                        try {
                            MemoryCacheUtils.removeFromCache(Uri.fromFile(file1).toString(), imageLoader.getMemoryCache());
                            DiskCacheUtils.removeFromCache(Uri.fromFile(file1).toString(), imageLoader.getDiskCache());
                            file1.createNewFile();

                            outputStream = new FileOutputStream(file1);
                            outputStream.write(dataImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            if (outputStream != null){{
                                try {
                                    outputStream.close();
                                    outputStream.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }}
                        }
                        dataImage = null;
                        finish();
                    }
                    if (id3 == 1003){
                        Intent intent1 = new Intent();
                        intent1.putExtra("path", path);
                        setResult(5, intent1);
//                    startActivity(intent1);
                        finish();
                    }else if (id5 == 1005){
                        Intent intent1 = new Intent(CameraCustomView.this, ReviewImageActivity.class);
                        intent1.putExtra("path", path);
                        startActivityForResult(intent1, 5);
                        finish();
                    }
                }else {
                    Intent intent = new Intent(CameraCustomView.this, ReviewImageActivity.class);
                    intent.putExtra("id",1);
                    startActivity(intent);
                    File file = new File(Environment.getExternalStorageDirectory()+ "/LifeCard");
                    if (!file.exists())
                        file.mkdir();
                    String imgCurrentTime = dateFormat.format(new Date());
                    String path = file.getPath()+ "/" + imgCurrentTime+".jpg";
                    File imgFile = new File(path);
                    FileOutputStream os = null;
                    try {
                        imgFile.createNewFile();
                        os = new FileOutputStream(imgFile);
                        os.write(dataImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        if (os != null){{
                            try {
                                os.close();
                                os.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }}
                    }
                    dataImage = null;
                    finish();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.startPreview();
                relativeLayout.setVisibility(View.GONE);
                cameraButton.setVisibility(View.VISIBLE);
                view_layout.setVisibility(View.VISIBLE);
                dataImage = null;
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);

            List<String> permissions = new ArrayList<String>();

            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.CAMERA);
            }
            if (!permissions.isEmpty()){
                requestPermissions(permissions.toArray(new String[permissions.size()]), 111);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 111: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permissions --> " + "Permission Granted: " + permissions[i]);

                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        System.out.println("Permissions --> " + "Permission Denied: " + permissions[i]);

                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public void touchFocus(final Rect tFocusRect){
        try {
            cameraButton.setEnabled(false);
            final Rect targetFocusRect = new Rect(
                    tFocusRect.left * 2000/drawingView.getWidth() - 1000,
                    tFocusRect.top * 2000/drawingView.getHeight() - 1000,
                    tFocusRect.right * 2000/drawingView.getWidth() - 1000,
                    tFocusRect.bottom * 2000/drawingView.getHeight() - 1000);

            final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
            Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
            focusList.add(focusArea);

            Camera.Parameters para = camera.getParameters();

            para.setFocusAreas(focusList);
            para.setMeteringAreas(focusList);
            camera.setParameters(para);

            camera.autoFocus(myAutoFocusCallback);

            drawingView.setHaveTouch(true,tFocusRect);
            drawingView.invalidate();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }

//    Camera.FaceDetectionListener faceDetectionListener
//            = new Camera.FaceDetectionListener(){
//
//        @Override
//        public void onFaceDetection(Camera.Face[] faces, Camera tcamera) {
//
//            if (faces.length == 0){
//                //prompt.setText(" No Face Detected! ");
//                drawingView.setHaveFace(false);
//            }else{
//                //prompt.setText(String.valueOf(faces.length) + " Face Detected :) ");
//                drawingView.setHaveFace(true);
//                detectedFaces = faces;
//
//                //Set the FocusAreas using the first detected face
//                List<Camera.Area> focusList = new ArrayList<Camera.Area>();
//                Camera.Area firstFace = new Camera.Area(faces[0].rect, 1000);
//                focusList.add(firstFace);
//
//                Camera.Parameters para = camera.getParameters();
//
//                if(para.getMaxNumFocusAreas()>0){
//                    para.setFocusAreas(focusList);
//                }
//
//                if(para.getMaxNumMeteringAreas()>0){
//                    para.setMeteringAreas(focusList);
//                }
//
//                camera.setParameters(para);
//
//                cameraButton.setEnabled(false);
//
//                //Stop further Face Detection
//                camera.stopFaceDetection();
//
//                cameraButton.setEnabled(false);
//
//				/*
//				 * Allways throw java.lang.RuntimeException: autoFocus failed
//				 * if I call autoFocus(myAutoFocusCallback) here!
//				 *
//					camera.autoFocus(myAutoFocusCallback);
//				*/
//
//                //Delay call autoFocus(myAutoFocusCallback)
//                myScheduledExecutorService = Executors.newScheduledThreadPool(1);
//                myScheduledExecutorService.schedule(new Runnable(){
//                    public void run() {
//                        camera.autoFocus(myAutoFocusCallback);
//                    }
//                }, 500, TimeUnit.MILLISECONDS);
//
//            }
//
//            drawingView.invalidate();
//
//        }};

    Camera.AutoFocusCallback myAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean b, Camera arg1) {
            if (b){
                cameraButton.setEnabled(true);
                camera.cancelAutoFocus();
            }

            float focusDistances[] = new float[3];
            arg1.getParameters().getFocusDistances(focusDistances);
        }
    };

    Camera.ShutterCallback cameraShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
//            if (camera != null){
//
//            }
        }
    };

    Camera.PictureCallback cameraPictureCallbackRaw = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

        }
    };

    Camera.PictureCallback cameraPictureCallbackJpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            dataImage = data;
            Bitmap bitmap = editImage(data);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            imageView.setRotation(90);
            relativeLayout.setVisibility(View.VISIBLE);
            drawingView.setVisibility(View.GONE);
            cameraButton.setVisibility(View.GONE);
            if (camera != null){
                camera.stopPreview();
            }
        }
    };





    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> sizesPreview = parameters.getSupportedPreviewSizes();
            if (sizesPreview.size() > 0){
                int pWidth = sizesPreview.get(0).width;
                int pWidth_n = sizesPreview.get(sizesPreview.size() - 1).width;
                int pHeight = sizesPreview.get(0).height;
                int pHeight_n = sizesPreview.get(sizesPreview.size() - 1).height;
                if (pWidth > pWidth_n){
                    parameters.setPreviewSize(pWidth, pHeight);
                }else {
                    parameters.setPreviewSize(pWidth_n, pHeight_n);
                }
            }else {
                parameters.setPreviewSize(fullHgt, fullWid);
            }
            List<Camera.Size> sizesPicture = parameters.getSupportedPictureSizes();
            if (sizesPicture.size() > 0){
                int pWidth = sizesPicture.get(0).width;
                int pWidth_n = sizesPicture.get(sizesPreview.size() - 1).width;
                int pHeight = sizesPicture.get(0).height;
                int pHeight_n = sizesPicture.get(sizesPreview.size() - 1).height;
                if (pWidth > pWidth_n){
                    parameters.setPictureSize(pWidth, pHeight);
                }else {
                    parameters.setPictureSize(pWidth_n, pHeight_n);
                }
            }else {
                parameters.setPictureSize(fullWid, fullHgt);
            }
            parameters.setJpegQuality(100);
            camera.setParameters(parameters);
//            camera.autoFocus(autoFocusCallback);

        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (previewing){
            camera.stopPreview();
            previewing = false;
        }
        if (camera != null){
            try {
                camera.setPreviewDisplay(cameraSurfaceHolder);
                camera.setDisplayOrientation(90);
                drawingView.setVisibility(View.VISIBLE);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if (id1 == 1001){
            Intent intent = new Intent(CameraCustomView.this, GuideToCaptureImageActivity.class);
            startActivity(intent);
            finish();
        }else if (id2 == 1002){
            Intent intent = new Intent(CameraCustomView.this, ReviewImageActivity.class);
            startActivity(intent);
            finish();
        }else if (id3 == 1003){
            finish();
        }else if (id5 == 1005){
            Intent intent = new Intent(CameraCustomView.this, ReviewImageActivity.class);
            startActivity(intent);
            finish();
        }else if (id6 == 1006){
            Intent intent = new Intent(CameraCustomView.this, GuideToCaptureImageActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private class DrawingView extends View{
        boolean haveFace;
        Paint drawingPaint;

        boolean haveTouch;
        Rect touchArea;

        public DrawingView(Context context){
            super(context);
            haveFace = false;
            drawingPaint = new Paint();
            drawingPaint.setColor(Color.WHITE);
            drawingPaint.setStyle(Paint.Style.STROKE);
            drawingPaint.setStrokeWidth(2);

            haveTouch = false;
        }

        public void setHaveFace(boolean h){
            haveFace = h;
        }

        public void setHaveTouch(boolean t, Rect tArea){
            haveTouch = t;
            touchArea = tArea;
        }

        public void onDraw(Canvas canvas){
            if (haveFace){
                // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
                // UI coordinates range from (0, 0) to (width, height).
                int vWidth = getWidth();
                int vHeight = getHeight();

                for (int i=0; i<detectedFaces.length; i++){

                    if (i == 0){
                        drawingPaint.setColor(Color.GREEN);
                    }else {
                        drawingPaint.setColor(Color.WHITE);
                    }

                    int l = detectedFaces[i].rect.left;
                    int t = detectedFaces[i].rect.top;
                    int r = detectedFaces[i].rect.right;
                    int b = detectedFaces[i].rect.bottom;
                    int left = (l+1000)*vWidth/2000;
                    int top = (t+1000)*vHeight/2000;
                    int right = (r+1000)*vWidth/2000;
                    int bottom = (b+10000)*vHeight/2000;
                    canvas.drawRect(left, top, right, bottom, drawingPaint);
                }
            }else {
                canvas.drawColor(Color.TRANSPARENT);
            }

            if (haveTouch){
                drawingPaint.setColor(Color.BLUE);
                canvas.drawRect(touchArea.left, touchArea.top,
                        touchArea.right, touchArea.bottom, drawingPaint);
            }
        }
    }

    public Bitmap editImage(byte[] dataImage){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(dataImage,0, dataImage.length, options);

        options.inJustDecodeBounds = false;
        options.inSampleSize = 3;
        options.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeByteArray(dataImage,0, dataImage.length, options);

        return bitmap;
    }

}
