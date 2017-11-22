package joe2k01.xenonhd.com.browserinstaller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebActivity extends AppCompatActivity {

    private static final String DOCS = "http://www.google.com";
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        web = findViewById(R.id.webview);
        web.loadUrl(DOCS);
    }
}
