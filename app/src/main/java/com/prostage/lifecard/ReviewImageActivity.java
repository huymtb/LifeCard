package com.prostage.lifecard;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.prostage.lifecard.Camera.CameraCustomView;
import com.prostage.lifecard.Model.Token;
import com.prostage.lifecard.UploadImage.AndroidMultiPartEntry;
import com.prostage.lifecard.Utils.Common;
import com.prostage.lifecard.Utils.TouchImageView;
import com.prostage.lifecard.remote.CustomHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReviewImageActivity extends AppCompatActivity {

    //    private SimpleDateFormat dateFormat = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
    private List<String> listOfImagePaths;
    public String GridView_ImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeCard";
    private int s_width, s_full_width;
    private Button sendBtn, nextCamera, viewImage, backToGuide;
    //    private AlertDialog dialog;
//    private String filePath = null;
    private ProgressBar progressBar;
    private ProgressBar iProgressBar;
    private TextView textPercent;
    private long totalSize = 0;
    private ImageLoader imageLoader;

    public static final String FILE_UPLOAD_URL = "http://mx17.zeeksdg.net/zeeksdg/translate_api/api/v1/fileUpload.php";
    private static final String TAG = ReviewImageActivity.class.getSimpleName();
    //    private String filePaths[] = {};
    private ImageListAdapter adapter;
    private View view;
    private int id, id2, id3, id4;
    private RelativeLayout relativeLayout;
    private ImageView imageView;
    private AppBarLayout appBarLayout;
    private String pathImageShow;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_capture_image);
        GridView grid = (GridView) findViewById(R.id.grid_view_show_image);
//        GridView_ImagePath = getFilesDir()+ "/LifeCard";
        sendBtn = (Button) findViewById(R.id.send_photo);
        appBarLayout = (AppBarLayout) findViewById(R.id.toolbar_show);

        textPercent = (TextView) findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        view = findViewById(R.id.opacityUploading);
        relativeLayout = (RelativeLayout) findViewById(R.id.show_image_after_take);
        nextCamera = (Button) findViewById(R.id.button_next_camera);
        viewImage = (Button) findViewById(R.id.button_done_camera);
        backToGuide = (Button) findViewById(R.id.button_return_camera);
        imageView = (TouchImageView) findViewById(R.id.image_show_view);
        Button backBtn = (Button) findViewById(R.id.back_to_guide);
        iProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        TextView textChosen = (TextView) findViewById(R.id.textCapture2);

        String[] items = getResources().getStringArray(R.array.spinner_choose_document);
        textChosen.setText(items[Common.ID]);

        listOfImagePaths = null;
        listOfImagePaths = RetrieveCaptureImagePath();
        adapter = new ImageListAdapter(this);
        if (listOfImagePaths != null) {
            grid.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
//        f_width = displayMetrics.widthPixels;
        s_width = displayMetrics.widthPixels - 60;
        s_full_width = displayMetrics.widthPixels;
//        f_height = displayMetrics.heightPixels/2;

        int id = getIntent().getIntExtra("id", 1);
        int newId = getIntent().getIntExtra("id", 3);
        if (id == 1) {
            handleImageShow();
        }

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id3 = 2003;
                //new UploadFileToServer().execute();
                uploadFile();
            }
        });
    }

    public Bitmap editImage(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        options.inJustDecodeBounds = false;
        options.inSampleSize = 3;
        options.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

        return bitmap;
    }

    private void handleImageShow() {
        relativeLayout.setVisibility(View.VISIBLE);
        appBarLayout.setVisibility(View.GONE);
        for (int i = 0; i < listOfImagePaths.size(); i++) {
            Bitmap bitmap = editImage(listOfImagePaths.get(i));
            imageView.setImageBitmap(bitmap);
            imageView.setRotation(90);
        }
        if (listOfImagePaths.size() == 10) {
            nextCamera.setVisibility(View.GONE);
        }
        nextCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReviewImageActivity.this, CameraCustomView.class);
                intent.putExtra("id", 1002);
                id = 2000;
                startActivity(intent);
                finish();
            }
        });
        viewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayout.setVisibility(View.GONE);
                appBarLayout.setVisibility(View.VISIBLE);
            }
        });
        backToGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id4 = 2004;
                int i = listOfImagePaths.size() - 1;
                pathImageShow = listOfImagePaths.get(i);
//                deleteImage(pathDelete);
                if (i == 0) {
                    Intent intent = new Intent(ReviewImageActivity.this, CameraCustomView.class);
                    intent.putExtra("path", pathImageShow);
                    intent.putExtra("id", 1006);
                    startActivityForResult(intent, 5);
                    finish();
                } else {
                    Intent intent = new Intent(ReviewImageActivity.this, CameraCustomView.class);
                    intent.putExtra("path", pathImageShow);
                    intent.putExtra("id", 1003);
                    startActivityForResult(intent, 5);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 5) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(pathImageShow));
        }
    }


    private List<String> RetrieveCaptureImagePath() {
        List<String> fileList = new ArrayList<>();
        File file = new File(GridView_ImagePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            Arrays.sort(files);

            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (f.isDirectory())
                    continue;
                fileList.add(f.getPath());
            }
        }
        return fileList;
    }

    private void deleteImage(String path) {
        File file = new File(path);
        file.delete();
        listOfImagePaths.remove(path);
        MemoryCacheUtils.removeFromCache(Uri.fromFile(file).toString(), imageLoader.getMemoryCache());
        DiskCacheUtils.removeFromCache(Uri.fromFile(file).toString(), imageLoader.getDiskCache());
        adapter.notifyDataSetChanged();
    }

    private List<String> DeleteCaptureImagePath() {
        List<String> fileList = new ArrayList<String>();
        File file = new File(GridView_ImagePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file1 = files[i];

                if (!file1.isDirectory()) {
                    file1.delete();
                    deleteFileFromMediaStore(this.getContentResolver(), file1);
                    MemoryCacheUtils.removeFromCache(Uri.fromFile(file1).toString(), imageLoader.getMemoryCache());
                    DiskCacheUtils.removeFromCache(Uri.fromFile(file1).toString(), imageLoader.getDiskCache());
                }

                fileList.remove(file1.getPath());
            }
        }
        return fileList;
    }

    public void deleteFileFromMediaStore(final ContentResolver contentResolver, final File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");

        final int delete = contentResolver.delete(uri, MediaStore.MediaColumns.DATA + "='" + canonicalPath + "'", null);
        final Cursor result = MediaStore.Images.Media.query(contentResolver,
                uri, new String[]{MediaStore.MediaColumns.DATA, MediaStore.MediaColumns._ID},
                MediaStore.MediaColumns.DATA + "='" + canonicalPath + "'", null, null);
        if (result.moveToFirst()) {
            do {
                Log.d("ID", result.getString(1));
                Log.d("DATA", result.getString(0));
            } while (result.moveToNext());
        }
        if (delete == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                contentResolver.delete(uri,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
            }
        }
    }

//    private void showDialogAfterCapture(){
//        View dialogView = View.inflate(ReviewImageActivity.this, R.layout.custom_dialog_after_taking_picture, null);
//        dialog = new AlertDialog.Builder(ReviewImageActivity.this).create();
//        ImageView imageView = (ImageView)dialogView.findViewById(R.id.image_show_dialog);
//        for (int i = 0; i < listOfImagePaths.size(); i++){
//            imageView.setImageBitmap(BitmapFactory.decodeFile(listOfImagePaths.get(i)));
//            imageView.setRotation(90);
//        }
//        dialogView.findViewById(R.id.btn_next_camera).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ReviewImageActivity.this, CameraCustomView.class);
//                intent.putExtra("id", 1002);
//                id = 2000;
//                startActivity(intent);
//                dialog.dismiss();
//                finish();
//            }
//        });
//        dialogView.findViewById(R.id.btn_done_camera).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.cancel();
//                dialog.dismiss();
//            }
//        });
//        dialogView.findViewById(R.id.btn_return_camera).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//                dialog.dismiss();
//                id4 = 2004;
//                DeleteCaptureImagePath();
//                Intent intent = new Intent(ReviewImageActivity.this, GuideToCaptureImageActivity.class);
//                startActivity(intent);
//            }
//        });
//        dialog.setView(dialogView);
//        dialog.show();
//    }

    private class ImageListAdapter extends BaseAdapter {

        private Context context;
        DisplayImageOptions options;

        private ImageListAdapter(Context context) {
            this.context = context;
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.placeholder)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                    .cacheInMemory(true)
                    .resetViewBeforeLoading(true).cacheOnDisk(true)
                    .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        }

        public int getCount() {
            if (listOfImagePaths != null) {
                return listOfImagePaths.size();
            } else {
                return 0;
            }
        }

        //---returns the ID of an item---
        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            final ImageView imageView;
            final TextView textView;
            if (convertView == null) {
                view = new View(context);
                view = inflater.inflate(R.layout.grid_view_image_item, null);
                imageView = (ImageView) view.findViewById(R.id.image_view_item);
                FileInputStream fs = null;
                Bitmap bitmap;
                try {
                    fs = new FileInputStream(new File(listOfImagePaths.get(position)));
                    if (fs != null) {
//                        new loadImage(listOfImagePaths.get(position).toString(), imageView, getBaseContext()).execute();
                        imageView.setId(position);
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(s_width / 2, s_full_width / (2)));
                        imageView.setRotation(90);
                        imageLoader.displayImage("file://" + listOfImagePaths.get(position), imageView, options);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fs != null) {
                        try {
                            fs.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
//                button = (Button)view.findViewById(R.id.btnDelete);
//                button.setOnClickListener(new deleteButton(position, listOfImagePaths));
                textView = (TextView) view.findViewById(R.id.number_text);
                textView.setText(String.valueOf(position + 1));
//                imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String path = listOfImagePaths.get(position);
//                        id1 = 2001;
//                        Intent intent = new Intent(ReviewImageActivity.this, ShowFullImageActivity.class);
//                        intent.putExtra("position", path);
//                        intent.putExtra("id",0);
//                        startActivity(intent);
//                        finish();
//                    }
//                });
            } else {
                ViewGroup view_group = (ViewGroup) convertView;
                imageView = (ImageView) view_group.getChildAt(1);
//                imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String path = listOfImagePaths.get(position);
//                        id1 = 2001;
//                        Intent intent = new Intent(ReviewImageActivity.this, ShowFullImageActivity.class);
//                        intent.putExtra("position", path);
//                        intent.putExtra("id",0);
//                        startActivity(intent);
//                        finish();
//                    }
//                });
                RelativeLayout relativeLayout = (RelativeLayout) view_group.getChildAt(0);
//                button = (Button)relativeLayout.getChildAt(1);
//                button.setOnClickListener(new deleteButton(position, listOfImagePaths));
                textView = (TextView) relativeLayout.getChildAt(0);
                textView.setText(String.valueOf(position + 1));
                view = (View) view_group;
                FileInputStream fs = null;
                Bitmap bitmap;
                try {
                    fs = new FileInputStream(new File(listOfImagePaths.get(position)));
                    if (fs != null) {
//                        new loadImage(listOfImagePaths.get(position).toString(), imageView, getBaseContext()).execute();
                        imageView.setId(position);
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(s_width / 2, s_full_width / (2)));
                        imageView.setRotation(90);
                        imageLoader.displayImage("file://" + listOfImagePaths.get(position), imageView, options);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fs != null) {
                        try {
                            fs.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return view;
        }

    }

    public class deleteButton implements View.OnClickListener {
        List<String> image;
        int position;

        public deleteButton(int position, List<String> image) {
            this.position = position;
            this.image = image;
        }

        @Override
        public void onClick(View view) {
            deleteImage(image.get(position));
            if (adapter.getCount() == 0) {
                Intent intent = new Intent(ReviewImageActivity.this, GuideToCaptureImageActivity.class);
                startActivity(intent);
                DeleteCaptureImagePath();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }

        super.onDestroy();
        if (id != 2000 && id2 != 2002 && id3 != 2003 && id4 != 2004) {
            DeleteCaptureImagePath();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (id != 2000 && id2 != 2002 && id3 != 2003 && id4 != 2004) {
            DeleteCaptureImagePath();
            finish();
            Intent intent = new Intent(ReviewImageActivity.this, GuideToCaptureImageActivity.class);
            startActivity(intent);
        }
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            view.setVisibility(View.VISIBLE);
            sendBtn.setVisibility(View.GONE);
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress[0]);
            textPercent.setText(String.valueOf(progress[0] + "%"));
        }

        @Override
        protected String doInBackground(Void... voids) {
            return uploadFile();
        }

        private String uploadFile() {
            String responseString = null;

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(FILE_UPLOAD_URL);
            File file = new File(GridView_ImagePath);
            if (file.exists()) {
                File[] files = file.listFiles();
                Arrays.sort(files);
                for (int i = 0; i < files.length; i++) {
                    try {
                        AndroidMultiPartEntry entry = new AndroidMultiPartEntry(new AndroidMultiPartEntry.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                        File source = files[i];
                        entry.addPart("image", new FileBody(source));

                        // Extra parameters if you want to pass to server
                        entry.addPart("website",
                                new StringBody("www.androidhive.info"));
                        entry.addPart("email", new StringBody("abc@gmail.com"));

                        totalSize = entry.getContentLength();
                        httpPost.setEntity(entry);

                        totalSize = entry.getContentLength();
                        httpPost.setEntity(entry);

                        // Making server call
                        HttpResponse response = httpClient.execute(httpPost);
                        HttpEntity r_entity = response.getEntity();

                        int statusCode = response.getStatusLine().getStatusCode();
                        if (statusCode == 200) {
                            responseString = EntityUtils.toString(r_entity);
                        } else {
                            responseString = "Error occurred! Http Status Code: "
                                    + statusCode;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
//            showAlert(result);
            view.setVisibility(View.GONE);
            sendBtn.setVisibility(View.VISIBLE);
            DeleteCaptureImagePath();
            Intent intent = new Intent(ReviewImageActivity.this, ConfirmStepActivity.class);
            startActivity(intent);
            finish();
            super.onPostExecute(result);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        id2 = 2002;
        if (relativeLayout.getVisibility() == View.VISIBLE) {
            int i = listOfImagePaths.size() - 1;
            String path = listOfImagePaths.get(i);
//                deleteImage(pathDelete);
            if (i == 0) {
                Intent intent = new Intent(ReviewImageActivity.this, CameraCustomView.class);
                intent.putExtra("path", pathImageShow);
                intent.putExtra("id", 1006);
                startActivityForResult(intent, 5);
                finish();
            } else {
                Intent intent = new Intent(ReviewImageActivity.this, CameraCustomView.class);
                intent.putExtra("path", path);
                intent.putExtra("id", 1005);
                startActivityForResult(intent, 5);
            }
        } else {
            DeleteCaptureImagePath();
            Intent intent = new Intent(ReviewImageActivity.this, GuideToCaptureImageActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void uploadFile() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        view.setVisibility(View.VISIBLE);
        sendBtn.setVisibility(View.GONE);
        iProgressBar.setVisibility(View.VISIBLE);
        subscription = Observable.create((Observable.OnSubscribe<Token>) subscriber -> {

            OkHttpClient client = CustomHttpClient.getHttpClient();
            String token = MyApp.getInstance().getToken();
            if (TextUtils.isEmpty(token)) {
                subscriber.onNext(new Token(-1, "Token is empty"));
            } else {
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);
                File file = new File(GridView_ImagePath);
                if (file.exists()) {
                    File[] files = file.listFiles();
                    Arrays.sort(files);
                    for (File file1 : files) {
                        File source = compressFile(this, file1);
                        source.getPath();
                        assert source != null;
                        Log.e("TAG", "Size file: " + source.length());
                        final MediaType MEDIA_TYPE = MediaType.parse("image/png");
                        builder.addFormDataPart("files[]", source.getName(), RequestBody.create(MEDIA_TYPE, source));
                    }
                }

                RequestBody requestBody = builder.build();

                Request.Builder httpReq = new Request.Builder()
                        .url("https://lifecard-omatome-api.net:11443/customers/data/" + token)
                        .post(requestBody)
                        .addHeader("Content-Type", "multipart/form-data");

                try {
                    Response response = client.newCall(httpReq.build()).execute();
                    if (response.isSuccessful()) {
                        String resultString = response.body().string();
                        JSONObject result = new JSONObject(resultString);
                        JSONObject message = result.getJSONObject("message");
                        int status = message.getInt("status");
                        if (status == 1) {
                            subscriber.onNext(new Token(status, ""));
                        } else {
                            String error = message.getString("error");
                            subscriber.onNext(new Token(status, error));
                        }
                    } else {
                        subscriber.onError(new IOException(String.valueOf(response)));
                    }
                } catch (IOException | JSONException e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }

        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(token -> {
                    view.setVisibility(View.GONE);
                    sendBtn.setVisibility(View.VISIBLE);
                    iProgressBar.setVisibility(View.GONE);
                    if (token.getStatus() == 1) {
                        Toast.makeText(this, "Upload file is successful", Toast.LENGTH_LONG).show();
                        DeleteCaptureImagePath();
                        Intent intent = new Intent(ReviewImageActivity.this, ConfirmStepActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
//                        DeleteCaptureImagePath();
                        Snackbar.make(sendBtn, token.getResult(), Snackbar.LENGTH_LONG).show();
                    }

                }, error -> {
                    view.setVisibility(View.GONE);
                    sendBtn.setVisibility(View.VISIBLE);
                    iProgressBar.setVisibility(View.GONE);
//                    DeleteCaptureImagePath();
                    Log.e("MainActivity", Log.getStackTraceString(error));
                    Snackbar.make(sendBtn, Log.getStackTraceString(error), Snackbar.LENGTH_LONG).show();
                });
    }

    public File compressFile(Context context, File actualImageFile) {
        try {
            File file = new Compressor(context)
                    .compressToFile(actualImageFile);
            double bytes = file.length();
            double kilobytes = (bytes / 1024);
            double megabytes = (kilobytes / 1024);
            if (megabytes > 2) {
                return new Compressor(context).setCompressFormat(Bitmap.CompressFormat.PNG)
                        .compressToFile(file);
            } else {
                return file;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
