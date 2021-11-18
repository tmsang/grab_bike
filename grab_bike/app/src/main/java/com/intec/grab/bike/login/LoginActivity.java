package com.intec.grab.bike.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.intec.grab.bike.MainActivity;
import com.intec.grab.bike.R;
import com.intec.grab.bike.configs.Constants;
import com.intec.grab.bike.shared.SharedService;
import com.intec.grab.bike.utils.api.Callback;
import com.intec.grab.bike.utils.api.SSLSettings;
import com.intec.grab.bike.utils.base.SETTING;
import com.intec.grab.bike.utils.helper.CommonHelper;
import com.intec.grab.bike.utils.helper.StringHelper;
import com.intec.grab.bike.utils.log.Log;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    SETTING settings;
    SSLSettings sslSettings = new SSLSettings(true, null);

    @BindView(R.id.username) EditText txtUsername;
    @BindView(R.id.password) EditText txtPassword;
    @BindView(R.id.btnlogin) Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        settings = new SETTING(this);

        // trigger event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreProcess_Login();
            }
        });

        txtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    PreProcess_Login();
                    return true;
                }
                return false;
            }
        });
    }

    private void PreProcess_Login() {
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

        if (StringHelper.isNullOrEmpty(username)) {
            CommonHelper.showToast(LoginActivity.this, "Username is empty");
            return;
        }
        if (StringHelper.isNullOrEmpty(password)) {
            CommonHelper.showToast(LoginActivity.this, "Password is empty");
            return;
        }

        SharedService.loginApi(Constants.API_PHP, sslSettings)
            .CheckAccount(new UserMappingIn(username, password))
            .enqueue(Callback.callInUI(LoginActivity.this,
                    (json) -> {
                        if(json.MessageText.equals("0")) {
                            CommonHelper.showToast(LoginActivity.this, "This account does not exists");
                        } else if(json.MessageText.equals("-1")) {
                            CommonHelper.showToast(LoginActivity.this, "This password is invalid");
                        } else if(json.MessageText.equals("-2")) {
                            CommonHelper.showToast(LoginActivity.this, "User have not assigned permission on Channel");
                        } else if (json.MessageText.equals("1")) {
                            Login(username, password);
                        }
                    },
                    (error) -> {
                        CommonHelper.showToast(LoginActivity.this, "This account is invalid. Please check Admin Assistant");
                    }
            ));
    }

    private void Login(String username, String password)
    {
        // =============================================
        // Use Firebase
        // =============================================
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                String fcmToken = "";
                if (!task.isSuccessful()) {
                    String errorMessage = task.getException().getMessage();
                    CommonHelper.showToast(LoginActivity.this, "Firebase Network cannot reach", errorMessage);
                } else {
                    fcmToken = task.getResult().getToken();
                    Log.i("FCM TOKEN: " + fcmToken);

                    settings.fcmToken(fcmToken);
                    SharedService.loginApi(Constants.API_BOLTZ, sslSettings)
                            .getUserKey(new BoltzUserkeyMappingIn(Constants.PUSH_TYPE_SERVICE, fcmToken))
                            .enqueue(Callback.callInUI(LoginActivity.this,
                                    (json) -> authenticate(json.UserkeyResult),
                                    (error) -> {
                                        CommonHelper.showToast(LoginActivity.this, "Boltz API cannot reach", error.body());
                                    }
                            ));
                }
            }

            private void authenticate(String userKey) {
                settings.userKey(userKey);
                SharedService.loginApi(Constants.API_PHP, sslSettings)
                        .getToken(new LoginMappingIn(userKey, username, password))
                        .enqueue(Callback.callInUI(LoginActivity.this, (json) -> {
                            if (json.MessageError != null) {
                                CommonHelper.showToast(LoginActivity.this, "Username or Password is invalid", json.MessageError);
                                return;
                            }

                            String jwtToken = json.MessageText;
                            settings.jwtToken(jwtToken);

                            // redirect MESSAGE
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }, (error) -> {
                            CommonHelper.showToast(LoginActivity.this, "API cannot reach", error.body());
                        }));
            }
        });
    }

}
