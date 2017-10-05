package com.prostage.lifecard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.prostage.lifecard.Camera.CameraCustomView;

public class ShowFullImageActivity extends AppCompatActivity {

    ImageView imageView;
    TextView retakeView;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_full_image);

        imageView = (ImageView)findViewById(R.id.viewFullImage);
        retakeView = (TextView)findViewById(R.id.toolbar_retake);

        path = getIntent().getStringExtra("position");
//        final String newPath = getIntent().getStringExtra("newPath");
        final int id = getIntent().getIntExtra("id",0);
//        final int newId = getIntent().getIntExtra("id",2);

        if (id == 0){
            imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        }


        Toolbar toolbar = (Toolbar)findViewById(R.id.fullImageToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ShowFullImageActivity.this, ReviewImageActivity.class);
                intent.putExtra("id",3);
                startActivity(intent);
            }
        });

        retakeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowFullImageActivity.this, CameraCustomView.class);
                intent.putExtra("path", path);
                intent.putExtra("id", 1003);
                startActivityForResult(intent, 5);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 5 ){
            imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        Intent intent = new Intent(ShowFullImageActivity.this, ReviewImageActivity.class);
        intent.putExtra("id",3);
        startActivity(intent);
    }
}
