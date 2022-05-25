package io.sayeed.hossain.crashreporter.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import io.sayeed.hossain.crashreporter.R;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import io.sayeed.hossain.crashreporter.utils.AppUtils;
import io.sayeed.hossain.crashreporter.utils.FileUtils;

public class LogMessageActivity extends AppCompatActivity {

    String crashLog;
    private TextView appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_message);
        appInfo = (TextView) findViewById(R.id.appInfo);

        Intent intent = getIntent();
        if (intent != null) {
            String dirPath = intent.getStringExtra("LogMessage");
            File file = new File(dirPath);
            crashLog = FileUtils.readFromFile(file);
            TextView textView = (TextView) findViewById(R.id.logMessage);
            textView.setText(crashLog);
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(getString(R.string.crash_reporter));
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getAppInfo();
    }

    private void getAppInfo() {
        appInfo.setText(AppUtils.getDeviceDetails(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crash_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = getIntent();
        String filePath = null;
        if (intent != null) {
            filePath = intent.getStringExtra("LogMessage");
        }

        if (item.getItemId() == R.id.delete_log) {
            if (FileUtils.delete(filePath)) {
                finish();
            }
            return true;
        } else if (item.getItemId() == R.id.share_crash_log) {
            shareCrashReport(filePath);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }


    //TODO:
    private void shareCrashReport(String filePath) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "https://github.com/SubhamTyagi/AppLock/issues/new?title="
                            + URLEncoder.encode("App Crash", "UTF-8")
                            + "&body=" + URLEncoder.encode(crashLog + "---------END-----------\n\n"
                            + appInfo.getText().toString(), "UTF-8"))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}
