package com.xenonhd.browserinstaller;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private int[] icons = {R.mipmap.ic_chrome, R.mipmap.ic_firefox, R.mipmap.ic_opera, R.mipmap.ic_via};

    private static final int DONE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String url = null;
    private String browser;
    private String appPackageName = null;
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/" + browser + ".apk")), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(install, DONE);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        StrictMode.VmPolicy.Builder strictBuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(strictBuilder.build());

        builder.setView(inflater.inflate(R.layout.activity_main, null));

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });

        dialog.show();
        Button button = dialog.findViewById(R.id.install);
        final Spinner spinner = dialog.findViewById(R.id.spinner);
        final String[] browsers = getResources().getStringArray(R.array.browsers_array);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, icons, browsers);
        spinner.setAdapter(spinnerAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                } else {
                    performInstall(icons[(int)spinner.getSelectedItemId()]);
                }
            }
        });

    }

    private boolean isPlayStoreThere() {
        try {
            getApplicationContext().getPackageManager().getPackageInfo("com.android.vending", 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    private void performInstall(int i) {

        switch (i) {

            case R.mipmap.ic_chrome:
                browser = "Chrome";
                url = getString(R.string.chromeXenonServer);
                appPackageName = getString(R.string.chromePackage);
                break;

            case R.mipmap.ic_firefox:
                browser = "Chromium";
                url = getString(R.string.firefoxXenonServer);
                appPackageName = getString(R.string.firefoxPackage);
                break;

            case R.mipmap.ic_opera:
                browser = "Dolphin";
                url = getString(R.string.operaXenonServer);
                appPackageName = getString(R.string.operaPackage);
                break;

            case R.mipmap.ic_via:
                browser = "Firefox";
                url = getString(R.string.viaXenonServer);
                appPackageName = getString(R.string.viaPackage);
                break;
        }

        if (isPlayStoreThere() && appPackageName != null) {
            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)), DONE);
        } else {
            DownloadManager.Request req = new DownloadManager.Request(Uri.parse(url));
            req.setTitle(getString(R.string.app_name));
            req.setDescription(getString(R.string.notification).replace("$", browser));
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            req.setDestinationInExternalPublicDir("", browser + ".apk");
            DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
            mgr.enqueue(req);

            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            registerReceiver(downloadReceiver, filter);
        }

    }
}
