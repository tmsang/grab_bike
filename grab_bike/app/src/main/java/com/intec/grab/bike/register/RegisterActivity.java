package com.intec.grab.bike.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.login.LoginActivity;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.api.SSLSettings;
import com.intec.grab.bike.utils.base.SETTING;
import com.intec.grab.bike.utils.helper.CommonHelper;
import com.intec.grab.bike.utils.helper.StringHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {
    SETTING settings;
    SSLSettings sslSettings = new SSLSettings(true, null);

    @BindView(R.id.fullName) EditText txtFullName;
    @BindView(R.id.email) EditText txtEmail;
    @BindView(R.id.phone) EditText txtPhone;
    @BindView(R.id.password) EditText txtPassword;
    @BindView(R.id.smsCode) EditText txtSMSCode;
    @BindView(R.id.btnRegister) Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_activity_login);

        ButterKnife.bind(this);
        settings = new SETTING(this);

        // trigger event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }

    private void Register() {
        String fullName = txtFullName.getText().toString();
        String email = txtEmail.getText().toString();
        String phone = txtPhone.getText().toString();
        String password = txtPassword.getText().toString();
        String code = txtSMSCode.getText().toString();

        if (StringHelper.isNullOrEmpty(fullName)) {
            CommonHelper.showToast(RegisterActivity.this, "Full Name is empty");
            return;
        }
        if (StringHelper.isNullOrEmpty(email)) {
            CommonHelper.showToast(RegisterActivity.this, "Email is empty");
            return;
        }
        if (StringHelper.isNullOrEmpty(phone)) {
            CommonHelper.showToast(RegisterActivity.this, "Phone is empty");
            return;
        }
        if (StringHelper.isNullOrEmpty(password)) {
            CommonHelper.showToast(RegisterActivity.this, "Password is empty");
            return;
        }
        if (StringHelper.isNullOrEmpty(code)) {
            CommonHelper.showToast(RegisterActivity.this, "SMS Code is empty");
            return;
        }

        SharedService.GuestRegisterApi(Constants.API_NET, sslSettings)
            .Register(new RegisterDto(fullName, email, phone, password, code))
            .enqueue(Callback.callInUI(RegisterActivity.this, (json) -> {
                if (json.MessageText.equals("0")) {
                    CommonHelper.showToast(RegisterActivity.this, "This email existed - please input another email");
                } else if(json.MessageText.equals("-1")) {
                    CommonHelper.showToast(RegisterActivity.this, "This password is invalid");
                }

                String jwtToken = json.MessageText;
                settings.jwtToken(jwtToken);

                // redirect MESSAGE
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }, (error) -> {
                CommonHelper.showToast(RegisterActivity.this, "API cannot reach", error.body());
            }));
    }
}
