package com.intec.grab.bike_driver.about;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.intec.grab.bike_driver.MainActivity;
import com.intec.grab.bike_driver.R;
import com.intec.grab.bike_driver.histories.HistoriesActivity;
import com.intec.grab.bike_driver.login.LoginActivity;
import com.intec.grab.bike_driver.messages.MessagesActivity;
import com.intec.grab.bike_driver.settings.SettingsActivity;
import com.intec.grab.bike_driver.utils.base.BaseActivity;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Initialization(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_settings, menu);

        MenuItem item = menu.findItem(R.id.toolbar_about);
        item.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.toolbar_home:
                this.Redirect(MainActivity.class);
                return true;
            case R.id.toolbar_messages:
                this.Redirect(MessagesActivity.class);
                return true;
            case R.id.toolbar_histories:
                this.Redirect(HistoriesActivity.class);
                return true;
            case R.id.toolbar_settings:
                this.Redirect(SettingsActivity.class);
                return true;
            case R.id.toolbar_about:
                this.Redirect(AboutActivity.class);
                return true;
            case R.id.toolbar_logout:
                settings.clear();
                this.Redirect(LoginActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
