package com.intec.grab.bike_driver.utils.base;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.intec.grab.bike_driver.R;
import com.intec.grab.bike_driver.configs.Constants;
import com.intec.grab.bike_driver.login.LoginActivity;
import com.intec.grab.bike_driver.shared.SharedService;
import com.intec.grab.bike_driver.utils.api.Callback;
import com.intec.grab.bike_driver.utils.api.SSLSettings;
import com.intec.grab.bike_driver.utils.helper.CommonHelper;
import com.intec.grab.bike_driver.utils.helper.GPSTracker;
import com.intec.grab.bike_driver.utils.helper.MyEventCallback;
import com.intec.grab.bike_driver.utils.helper.MyStringCallback;
import com.intec.grab.bike_driver.utils.helper.StringHelper;
import com.intec.grab.bike_driver.utils.log.Log;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {
    public SETTING settings;
    public SSLSettings sslSettings = new SSLSettings(false, null);
    public Activity activity;
    public FrameLayout loading;
    public Map<String, String> header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void Initialization(Activity activity) {
        this.activity = activity;

        settings = new SETTING(activity);
        Log.init(this);

        header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        header.put("Authorization", settings.jwtToken());

        int loadingResId = getResources().getIdentifier("loading", "id", getPackageName());
        if (loadingResId > 0) {
            this.loading = this.activity.findViewById(loadingResId);
        } else {
            this.loading = null;
        }
    }

    // ============================================================
    // EditText
    // ============================================================
    public void EnableEditText(int rId) {
        EditText editText = this.activity.findViewById(rId);
        editText.setBackgroundResource(R.drawable.tp_edittext_bg);
        editText.setEnabled(true);
    }

    public void DisableEditText(int rId) {
        EditText editText = this.activity.findViewById(rId);
        editText.setBackgroundResource(R.drawable.tp_edittext_bg_disable);
        editText.setEnabled(false);
    }

    public String EditText(int rId) {
        EditText editText = this.activity.findViewById(rId);
        return editText.getText().toString();
    }

    public void EditText(int rId, String value) {
        EditText editText = this.activity.findViewById(rId);
        editText.setText(value);
    }

    public void EditTextOnKeyPress(int rId, MyEventCallback callback) {
        EditText txt = this.activity.findViewById(rId);
        txt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    callback.execute(txt);
                    return true;
                }
                return false;
            }
        });
    }

    // ============================================================
    // Button
    // ============================================================
    public void EnableButton(int rId) {
        Button button = this.activity.findViewById(rId);
        button.setBackgroundResource(R.drawable.tp_button_bg);
        button.setEnabled(true);
    }

    public void DisableButton(int rId) {
        Button button = this.activity.findViewById(rId);
        button.setBackgroundResource(R.drawable.tp_button_bg_disable);
        button.setEnabled(false);
    }

    public void ButtonClickEvent(int rId, MyEventCallback callback) {
        Button btn = this.activity.findViewById(rId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.execute(btn);
            }
        });
    }

    public void Button(int rId, boolean isEnable) {
        Button btn = this.activity.findViewById(rId);
        btn.setVisibility(isEnable ? View.VISIBLE : View.GONE);
    }
    // ============================================================
    // Image Button
    // ============================================================
    public void ImageButtonClickEvent(int rId, MyEventCallback callback) {
        ImageButton imgButton = this.activity.findViewById(rId);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.execute(imgButton);
            }
        });
    }

    public void ImageButton(int rId, boolean isEnable) {
        ImageButton imgButton = this.activity.findViewById(rId);
        imgButton.setVisibility(isEnable ? View.VISIBLE : View.GONE);
    }
    // ============================================================
    // TextView
    // ============================================================
    public void SetTextView(int rId, int rString) {
        TextView textView = this.activity.findViewById(rId);
        textView.setText(rString);
    }

    public void SetTextView(int rId, String rString) {
        if (StringHelper.isNullOrEmpty(rString)) rString = "";

        TextView textView = this.activity.findViewById(rId);
        textView.setText(Html.fromHtml(rString));
    }

    public String GetTextView(int rId) {
        TextView textView = this.activity.findViewById(rId);
        return textView.getText().toString();
    }

    public void TextViewClickEvent(int rId, MyEventCallback callback) {
        TextView lbl = this.activity.findViewById(rId);
        lbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.execute(lbl);
            }
        });
    }

    // ============================================================
    // Switch
    // ============================================================
    public boolean SwitchItem(int rId) {
        Switch switch1 = this.activity.findViewById(rId);
        return switch1.isChecked();
    }
    public void SwitchItem(int rId, boolean checked) {
        Switch switch1 = this.activity.findViewById(rId);
        switch1.setEnabled(checked);
    }

    // ============================================================
    // AutoCompleteTextView
    // ============================================================



    // ============================================================
    // Toast
    // ============================================================
    public void Toast(String message, String... errors) {
        if (errors.length > 0 && errors[0] != null) {
            Log.e(errors[0]);
        }
        Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show();
        this.Loading(false);
    }

    // INVISIBLE: This view is invisible, but it still takes up space for layout purposes.
    // GONE: This view is invisible, and it doesn't take any space for layout purposes.
    public void Loading(boolean isShow) {
        if (this.loading != null) {
            this.loading.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        }
    }
    public void Loading(int rId, boolean isShow) {
        FrameLayout loading = this.activity.findViewById(rId);
        loading.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void Redirect(Class destinationActivity) {
        Intent intent = new Intent(this, destinationActivity);
        startActivity(intent);
    }

    public Boolean IsNullOrEmpty(String value, String... name) {
        if (StringHelper.isNullOrEmpty(value)) {
            String message = name.length > 0 ? name[0] : "Value Input";
            CommonHelper.showToast(this, message + " is null or empty");
            this.Loading(false);

            return true;
        }
        return false;
    }
    public Boolean IsDate(String value, String... name) {
        boolean result = true;
        if (StringHelper.isNullOrEmpty(value)) {
            result = false;
        } else if (value.length() != 10) {
            result = false;
        } else {
            //2022-03-11
            String year = value.substring(0, 4);
            String month = value.substring(5, 7);
            String day = value.substring(8, 10);
            if (!isNumeric(year) || !isNumeric(month) || !isNumeric(day)) {
                result = false;
            }
        }
        if (result == false) {
            String message = name.length > 0 ? name[0] : "Value Input";
            CommonHelper.showToast(this, message + " is null or empty");
            this.Loading(false);
        }
        return result;
    }
    private static boolean isNumeric(String str){
        return str != null && str.matches("[0-9.]+");
    }
    // ============================================================
    // Goecode & Location
    // ============================================================

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void RequestPermissionLocation(MyStringCallback callback) {
        // Request location permission
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {

                    Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                GPSTracker gps = new GPSTracker(this);
                                if (gps.canGetLocation()) {
                                    double lat = gps.getLatitude();
                                    double lng = gps.getLongitude();
                                    settings.currentLat(lat + "");
                                    settings.currentLng(lng + "");
                                    callback.execute("");
                                }
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                Toast("Only approximate location access granted");
                            } else {
                                // No location access granted.
                                Toast("No location access granted");
                            }
                        }
                );

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void PushPosition() {
        this.RequestPermissionLocation((result) -> {
            String[] items = result.split("@");
            String fromLat = items[0];
            String fromLng = items[1];

            // Push current guest position
            Map<String, String> pushPositionParam = new HashMap<>();
            pushPositionParam.put("Content-Type", "application/x-www-form-urlencoded");
            pushPositionParam.put("Authorization", settings.jwtToken());

            SharedService.MapApi(Constants.API_NET, sslSettings)
                    .PushPosition(pushPositionParam, fromLat, fromLng)
                    .enqueue(Callback.call((res) -> {
                        // TODO: nothing to do here
                        Log.i("Guest position has been pushed - successfully");
                    }, (error) -> {
                        HandleException("Push Position", error.body());
                    }));
        });
    }

    // ============================================================
    // GLOBAL EXCEPTION
    // ============================================================
    public void HandleException(String messageDefault, String... messages) {
        if (messages == null) {
            Toast("API (" + messageDefault + ") cannot reach.");
            return;
        }

        String body = messages[0];
        if (body.indexOf("user is null in JwtMiddleware") > 0
                || body.indexOf("Unauthorized") >= 0)
        {
            Toast("Session User is expired", body);
            this.Redirect(LoginActivity.class);
        }
        else if (body.indexOf("Password is invalid") > 0)
        {
            Toast("Password is invalid", body);
        }
        else if (body.indexOf("This account is not exists") > 0)
        {
            Toast("This account is not exists", body);
        }
        else {
            Toast("API (" + messageDefault + ") cannot reach.", body);
        }
    }


}
