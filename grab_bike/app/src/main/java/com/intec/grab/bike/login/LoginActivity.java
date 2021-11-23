package com.intec.grab.bike.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.intec.grab.bike.MainActivity;
import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.forgot_password.ForgotPasswordActivity;
import com.intec.grab.bike.register.RegisterActivity;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.api.SSLSettings;
import com.intec.grab.bike.utils.auth.HttpsTrustManager;
import com.intec.grab.bike.utils.base.SETTING;
import com.intec.grab.bike.utils.helper.CommonHelper;
import com.intec.grab.bike.utils.helper.StringHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    SETTING settings;
    SSLSettings sslSettings = new SSLSettings(false, "");

    @BindView(R.id.email) EditText txtEmail;
    @BindView(R.id.password) EditText txtPassword;
    @BindView(R.id.btnLogin) Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupHyperlink();

        ButterKnife.bind(this);
        settings = new SETTING(this);

        // trigger event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        txtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    Login();
                    return true;
                }
                return false;
            }
        });
    }

    private void setupHyperlink() {
        TextView link = findViewById(R.id.lblRegister);
        link.setTextColor(Color.BLUE);

        TextView linkForgot = findViewById(R.id.lblForgotPassword);
        linkForgot.setTextColor(Color.BLUE);

        link.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        linkForgot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Login() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if (StringHelper.isNullOrEmpty(email)) {
            CommonHelper.showToast(LoginActivity.this, "Email is empty");
            return;
        }
        if (StringHelper.isNullOrEmpty(password)) {
            CommonHelper.showToast(LoginActivity.this, "Password is empty");
            return;
        }

        //HttpsTrustManager.allowAllSSL();
        SharedService.LoginApi(Constants.API_NET, sslSettings)
            .Login(email, password)
            .enqueue(Callback.callInUI(LoginActivity.this, (json) -> {
                if (StringHelper.isNullOrEmpty(json.jwt)) {
                    CommonHelper.showToast(LoginActivity.this, "JWT Token is null or empty");
                    return;
                }
                settings.email(email);
                settings.jwtToken(json.jwt);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            }, (error) -> {
                CommonHelper.showToast(LoginActivity.this, "API cannot reach", error.body());
            }));
    }
}
