package com.prostage.lifecard;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.prostage.lifecard.Camera.CameraCustomView;

public class GuideToCaptureImageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_capture_image);


        Button cameraBtn = (Button)findViewById(R.id.capture_image_Button);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(GuideToCaptureImageActivity.this);
                builder.setMessage(getString(R.string.capture_image_alert_camera));
                builder.setCancelable(false);
                builder.setNegativeButton(getString(R.string.capture_image_alert_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(GuideToCaptureImageActivity.this, CameraCustomView.class);
                        intent.putExtra("id", 1001);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();
            }
        });

        Toolbar toolbar = (Toolbar)findViewById(R.id.captureImageToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(GuideToCaptureImageActivity.this, SendingDocumentsActivity.class);
        startActivity(intent);
        finish();
    }

}
