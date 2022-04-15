package com.intec.grab.bike.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.intec.grab.bike.MainActivity;
import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.forgot_password.ForgotPasswordActivity;
import com.intec.grab.bike.guest_map.GuestMapActivity;
import com.intec.grab.bike.register.RegisterActivity;
import com.intec.grab.bike.shared.PushPositionService;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.base.BaseActivity;
import com.intec.grab.bike.utils.helper.MyEventCallback;
import com.intec.grab.bike.utils.helper.MyStringCallback;
import com.intec.grab.bike.utils.helper.StringHelper;
import com.intec.grab.bike.utils.log.Log;

import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
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
                if (StringHelper.isNullOrEmpty(message)) {
                    message = error.getCause() == null ? null : error.getCause().toString();
                }
                this.HandleException("Login", message);
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
