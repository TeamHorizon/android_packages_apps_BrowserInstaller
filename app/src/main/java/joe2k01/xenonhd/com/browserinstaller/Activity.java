package joe2k01.xenonhd.com.browserinstaller;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class Activity extends AppCompatActivity {

    private Button buttons[] = new Button[4];
    private String browser;
    private String url = null;
    private final String chromiumUrl = "";
    private final String fireFoxUrl = "";
    private final String operaUrl = "";
    private final String viaUrl = "";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        buttons[0] = findViewById(R.id.CAFChromiumButton);
        buttons[1] = findViewById(R.id.FireFoxButton);
        buttons[2] = findViewById(R.id.OperaButton);
        buttons[3] = findViewById(R.id.ViaButton);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        for(int n = 0; n < 4; n ++){

            buttons[n].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performInstall(v);
                    System.out.println(v.getId());
                }
            });

        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/" + browser + ".apk")), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(install);

        }
    };


    private void performInstall(View v) {

        switch (v.getId()) {

            case R.id.CAFChromiumButton:
                browser = "chromium";
                url = chromiumUrl;
                break;

            case R.id.FireFoxButton:
                browser = "firefox";
                url = fireFoxUrl;
                break;

            case R.id.OperaButton:
                browser = "opera";
                url = operaUrl;
                break;

            case R.id.ViaButton:
                browser = "via";
                url = viaUrl;
                break;
        }

        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
        req.setTitle(getString(R.string.app_name));
        req.setDescription(getString(R.string.notification_description).replace("$", browser));
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        req.setDestinationInExternalPublicDir("", browser + ".apk");
        DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        mgr.enqueue(req);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);

    }
}
