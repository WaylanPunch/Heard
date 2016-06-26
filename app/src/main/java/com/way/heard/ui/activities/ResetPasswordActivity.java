package com.way.heard.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.way.heard.R;

public class ResetPasswordActivity extends BaseActivity {

    private AutoCompleteTextView tvEmail;
    private TextView tvSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initView();
    }

    private void initView() {
        tvEmail = (AutoCompleteTextView) findViewById(R.id.actv_reset_password_email);
        tvSend = (TextView) findViewById(R.id.tv_reset_password_send_email);
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tvEmail.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    tvEmail.setError("Null");
                    tvEmail.requestFocus();
                    Toast.makeText(ResetPasswordActivity.this, "Null", Toast.LENGTH_SHORT).show();
                } else {
                    if (isEmailValid(email)) {
                        AVUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    Toast.makeText(ResetPasswordActivity.this, "An Email Has Been Sent.", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                } else {
                                    tvEmail.setError(e.getMessage());
                                    tvEmail.requestFocus();
                                    Toast.makeText(ResetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        tvEmail.setError("Invalid");
                        tvEmail.requestFocus();
                        Toast.makeText(ResetPasswordActivity.this, "Invalid", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
}
