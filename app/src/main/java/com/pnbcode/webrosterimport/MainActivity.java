package com.pnbcode.webrosterimport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private WebRosterReader rosterReader;
    private final Context myApp = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startConnection(View view) {
        if (hasNetworkConnection()) {
            Intent intent = new Intent(this, SASPortalLoginActivity.class);
            startActivity(intent);
        } else {
            Toast t = Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG);
            t.show();
        }
    }

    private boolean hasNetworkConnection() {
        boolean hasConnectedWifi = false;
        boolean hasConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                hasConnectedWifi = ni.isConnected();
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                hasConnectedMobile = ni.isConnected();
            }
        }
        return hasConnectedWifi || hasConnectedMobile;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}