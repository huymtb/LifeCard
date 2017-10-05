package com.prostage.lifecard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConfirmStepActivity extends AppCompatActivity {

    private Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_step);

        confirmBtn =  (Button)findViewById(R.id.confirm_button);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ConfirmStepActivity.this, CustomerInputActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        Intent intent = new Intent(ConfirmStepActivity.this, CustomerInputActivity.class);
        startActivity(intent);
    }
}
