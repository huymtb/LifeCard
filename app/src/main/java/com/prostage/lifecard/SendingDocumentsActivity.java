package com.prostage.lifecard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.prostage.lifecard.Utils.Common;

import java.util.ArrayList;
import java.util.Arrays;

public class SendingDocumentsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Spinner spinner;
    private ArrayList<String> arrayList;
    private LinearLayout linearLayout;
    private Button nextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_documents);
        linearLayout = (LinearLayout)findViewById(R.id.layoutSending);
        nextButton = (Button)findViewById(R.id.btn_choose_document);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(SendingDocumentsActivity.this, GuideToCaptureImageActivity.class);
                startActivity(intent);
            }
        });

        Toolbar toolbar = (Toolbar)findViewById(R.id.chooseDocumentToolbar);
        toolbar.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        spinner = (Spinner)findViewById(R.id.spinner_choose_document);
        spinner.setOnItemSelectedListener(this);

        arrayList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.spinner_choose_document)));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList){

            @Override
            public View getView(int position, View convertView, ViewGroup parent){

                View view = super.getView(position, convertView, parent);
                if (position == getCount()){
                    ((TextView)view.findViewById(android.R.id.text1)).setText("");
                    ((TextView)view.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
                }
                return view;
            }

            @Override
            public int getCount(){
                return super.getCount()-1;
            }
        };
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(arrayAdapter.getCount());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        Common.ID = position;
        if (position != 5 ){
            linearLayout.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
        }else {
            linearLayout.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            return;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        Intent intent = new Intent(SendingDocumentsActivity.this, CustomerInputActivity.class);
        startActivity(intent);
    }



}
