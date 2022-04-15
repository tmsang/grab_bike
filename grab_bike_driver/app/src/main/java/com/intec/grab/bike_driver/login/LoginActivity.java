package com.intec.grab.bike_driver.login;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.intec.grab.bike_driver.MainActivity;
import com.intec.grab.bike_driver.R;
import com.intec.grab.bike_driver.configs.Constants;
import com.intec.grab.bike_driver.forgot_password.ForgotPasswordActivity;
import com.intec.grab.bike_driver.register.RegisterActivity;
import com.intec.grab.bike_driver.shared.SharedService;
import com.intec.grab.bike_driver.utils.api.Callback;
import com.intec.grab.bike_driver.utils.base.BaseActivity;
import com.intec.grab.bike_driver.utils.helper.MyEventCallback;
import com.intec.grab.bike_driver.utils.helper.MyStringCallback;

public class LoginActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Initialization(this);

        // trigger event
        this.TextViewClickEvent(R.id.lblRegister, (v) -> {
            TextView lbl = (TextView)v;
            lbl.setTextColor(Color.BLUE);
            Redirect(RegisterActivity.class);
        });

        this.TextViewClickEvent(R.id.lblForgotPassword, (v) -> {
            TextView lbl = (TextView)v;
            lbl.setTextColor(Color.BLUE);
            Redirect(ForgotPasswordActivity.class);
        });

        this.ButtonClickEvent(R.id.btnLogin, Login);

        this.EditTextOnKeyPress(R.id.password, Login);
    }

    MyEventCallback Login = (ctl) -> {
        String email = this.EditText(R.id.email);
        String password = this.EditText(R.id.password);

        if (this.IsNullOrEmpty(email, "Email")) return;
        if (this.IsNullOrEmpty(password, "Password")) return;

        //HttpsTrustManager.allowAllSSL();
        SharedService.LoginApi(Constants.API_NET, sslSettings)
            .Login(email, password)
            .enqueue(Callback.callInUI(LoginActivity.this, (json) -> {
                if (this.IsNullOrEmpty(json.jwt, "JWT Token")) return;

                FcmToken((token) -> {
                    settings.jwtToken(json.jwt);
                    settings.fcmToken(token);
                    settings.fullName(json.FullName);
                    settings.phone(json.Phone);
                    settings.email(email);
                    this.Redirect(MainActivity.class);
                });
            }, (error) -> {
                String message = error.body();
                if (message.indexOf("not exists") > 0) {
                    Toast("This account is not exists", error.body());
                    return;
                }
                String cause = error.getCause().toString();
                Toast("API - Login raise error: " + cause, error.body());
            }));
    };

    private void FcmToken(MyStringCallback callback)
    {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Toast("Fetching FCM registration token failed", task.getException().toString());
                        return;
                    }
                    // Get new FCM registration token
                    String fcmToken = task.getResult();
                    callback.execute(fcmToken);
                }
            });
    }
}
