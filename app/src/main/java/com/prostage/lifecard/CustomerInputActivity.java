package com.prostage.lifecard;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.prostage.lifecard.Model.Post;
import com.prostage.lifecard.Model.Token;
import com.prostage.lifecard.Utils.ApiUtils;
import com.prostage.lifecard.remote.APIService;
import com.prostage.lifecard.remote.CustomHttpClient;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.POST;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CustomerInputActivity extends AppCompatActivity {

    private EditText mEditTextNumber;
    private TextView mTextBirthday;
    private APIService apiService;
    private Subscription subscription;
    private Button mButtonNext;
    private ProgressBar progressBar;
    private int iDay;
    private int iYear;
    private int iMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_input);

        apiService = ApiUtils.getAPIService();
        mEditTextNumber = (EditText) findViewById(R.id.edit_number_receipt_input);
        mTextBirthday = (TextView) findViewById(R.id.edit_birthday_input);
        mButtonNext = (Button) findViewById(R.id.btn_next_customer_input);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.inputCustomerToolbar);
        toolbar.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTextBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime();
            }
        });

        mEditTextNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = mEditTextNumber.getText().toString();
                int textLength = text.length();
                if (text.endsWith("-")) {
                    return;
                }
                if (textLength == 5 || textLength == 10 || textLength == 15) {
                    mEditTextNumber.setText(new StringBuilder(text).insert(text.length() - 1, "-").toString());
                    mEditTextNumber.setSelection(mEditTextNumber.getText().length());
                }
            }
        });

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputText();
            }
        });

        iDay = 8;
        iYear = 2017;
        iMonth = 3;
    }

    private void checkInputText() {
        if (mEditTextNumber.getText().toString().equals("") || mTextBirthday.getText().toString().equals("")) {
            final View dialogView = View.inflate(CustomerInputActivity.this, R.layout.custom_alert_dialog_customer_input, null);
            final AlertDialog dialog = new AlertDialog.Builder(CustomerInputActivity.this).create();
            dialogView.findViewById(R.id.alert_agree_customer_input).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.setView(dialogView);
            dialog.show();
        } else {
//            sendPost(mEditTextNumber.getText().toString(), mTextBirthday.getText().toString());
            login();
        }
    }

    private void setTime() {
        final View dialogView = View.inflate(CustomerInputActivity.this, R.layout.date_picker, null);
        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datepicker);
        datePicker.init(iYear, iMonth - 1, iDay, onDateChangedListener);
        final AlertDialog dialog = new AlertDialog.Builder(CustomerInputActivity.this).create();
        dialogView.findViewById(R.id.btn_set_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                SimpleDateFormat mSDF = new SimpleDateFormat("yyyy-MM-dd");
                String formatDate = mSDF.format(new Date(year - 1900, month, day));
                mTextBirthday.setText(formatDate);
                dialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.btn_cancel_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setView(dialogView);
        dialog.show();
    }

    public DatePicker.OnDateChangedListener onDateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
            iDay = dayOfMonth;
            iMonth = month;
            iYear = year;
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(CustomerInputActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

    private void sendPost(String entry_number, String birthday) {
        Post post = new Post(entry_number, birthday);
        Call<Post> call = apiService.savePost(post);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Toast.makeText(CustomerInputActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(CustomerInputActivity.this, SendingDocumentsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(CustomerInputActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ERROR", t.getMessage());
            }
        });
    }

    private void login() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        progressBar.setVisibility(View.VISIBLE);
        subscription = Observable.create((Observable.OnSubscribe<Token>) subscriber -> {
            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            FormBody.Builder formBuilder = new FormBody.Builder()
                    .add("entry_number", mEditTextNumber.getText().toString())
                    .add("birthday", mTextBirthday.getText().toString());
            OkHttpClient client = CustomHttpClient.getHttpClient();

            Request.Builder httpReq = new Request.Builder()
                    .url("https://lifecard-omatome-api.net:11443/customers/login")
                    .post(formBuilder.build())
                    .addHeader("Content-Type", "multipart/form-data");

            try {
                okhttp3.Response response = client.newCall(httpReq.build()).execute();

                if (response.isSuccessful()) {
                    String resultString = response.body().string();
                    JSONObject result = new JSONObject(resultString);
                    JSONObject message = result.getJSONObject("message");
                    int status = message.getInt("status");
                    if (status == 1) {
                        String token = message.getString("token");
                        subscriber.onNext(new Token(status, token));
                    } else {
                        String error = message.getString("error");
                        subscriber.onNext(new Token(status, error));
                    }
                } else {
                    subscriber.onError(new IOException(String.valueOf(response)));
                }
            } catch (Exception e) {
                subscriber.onError(e);
            }
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(token -> {
                    progressBar.setVisibility(View.GONE);
                    if (token != null && token.getStatus() == 1) {
                        MyApp.getInstance().setToken(token.getResult());
                        Intent intent = new Intent(CustomerInputActivity.this, SendingDocumentsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        assert token != null;
                        Snackbar.make(mButtonNext, token.getResult(), Snackbar.LENGTH_LONG).show();
                    }
                }, error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("MainActivity", Log.getStackTraceString(error));
                    if (!TextUtils.isEmpty(Log.getStackTraceString(error))) {
                        Snackbar.make(mButtonNext, Log.getStackTraceString(error), Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(mButtonNext, "Something is wrong with network", Snackbar.LENGTH_LONG).show();
                    }

                });
    }
}
