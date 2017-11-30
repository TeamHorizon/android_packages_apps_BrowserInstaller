/*
 * Copyright (C) 2017 XenonHD
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xenonhd.browserinstaller;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class Activity extends AppCompatActivity {

    private static final int DONE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final String chromeUrl = "";
    private final String chromiumUrl = "";
    private final String dolphinUrl = "";
    private final String firefoxUrl = "";
    private final String operaUrl = "";
    private final String viaUrl = "";
    private Button buttons[] = new Button[6];
    private String browser;
    private String url = null;
    private String appPackageName = null;
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/" + browser + ".apk")), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(install);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        buttons[0] = findViewById(R.id.CAFChromiumButton);
        buttons[1] = findViewById(R.id.ChromeButton);
        buttons[2] = findViewById(R.id.DolphinButton);
        buttons[3] = findViewById(R.id.FirefoxButton);
        buttons[4] = findViewById(R.id.OperaButton);
        buttons[5] = findViewById(R.id.ViaButton);

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

        for (int n = 0; n < 6; n++) {

            buttons[n].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performInstall(v);
                }
            });

        }
    }

    private boolean isPlayStoreThere() {
        try {
            getApplicationContext().getPackageManager().getPackageInfo("com.android.vending", 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    private void showUninstall() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(R.string.alert)
                .setMessage(R.string.help)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent freeze = new Intent();
                        freeze.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri browserInstaller = Uri.fromParts("package", getPackageName(), null);
                        freeze.setData(browserInstaller);
                        startActivity(freeze);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Activity.this, R.string.bye, Toast.LENGTH_LONG).show();
                    }
                }).show();
    }


    private void performInstall(View v) {

        switch (v.getId()) {

            case R.id.ChromeButton:
                browser = "Chrome";
                url = chromeUrl;
                appPackageName = getString(R.string.chromePackage);
                break;

            case R.id.CAFChromiumButton:
                browser = "Chromium";
                url = chromiumUrl;
                appPackageName = null;
                break;

            case R.id.DolphinButton:
                browser = "Dolphin";
                url = dolphinUrl;
                appPackageName = getString(R.string.dolphinPackage);
                break;

            case R.id.FirefoxButton:
                browser = "Firefox";
                url = firefoxUrl;
                appPackageName = getString(R.string.firefoxPackage);
                break;

            case R.id.OperaButton:
                browser = "Opera";
                url = operaUrl;
                appPackageName = getString(R.string.operaPackage);
                break;

            case R.id.ViaButton:
                browser = "Via";
                url = viaUrl;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DONE) {
            try {
                if (appPackageName != null)
                    getPackageManager().getPackageInfo(appPackageName, PackageManager.GET_ACTIVITIES);
                else
                    getPackageManager().getPackageInfo("to fill later", PackageManager.GET_ACTIVITIES);

                showUninstall();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
