package com.pnbcode.webrosterimport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SASPortalLoginActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private String username;
    private String password;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitysasportallogin);
        usernameField = findViewById(R.id.sas_portal_username);
        passwordField = findViewById(R.id.sas_portal_password);
        intent = new Intent(this, WebRosterLoginActivity.class);

    }


    public void getPortalLoginInfo(View view) {

        username = usernameField.getText().toString();
        password = passwordField.getText().toString();
        new PortalConnector().execute();


    }

    private class PortalConnector extends AsyncTask<Void, Void, String> {

        private static final String url = "https://adfs.sas.dk/adfs/ls?version=1.0&action=signin&realm=urn%3AAppProxy%3Acom&appRealm=4183fd8d-a629-e711-80ea-00155d415332&returnUrl=https%3A%2F%2Fwebroster.scandinavian.net%2Fwebroster-presentation-osl%2F&client-request-id=DCDCA6C5-D3E8-0000-A80C-E3DCE8D3D501";

        @Override
        protected String doInBackground(Void... voids) {
            try {

                Connection.Response loginForm = Jsoup.connect(url)
                        .method(Connection.Method.GET)
                        .timeout(100000)
                        .execute();

                Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1)")
                        .data("UserName", username)
                        .data("PassWord", password)
                        .data("AuthMethod", "FormsAuthentication")
                        .cookies(loginForm.cookies())
                        .post();

                if (doc.title().equalsIgnoreCase("WEB ROSTER")) {
                    return doc.location();
                } else Log.d("PORTAL LOGIN", "DID NOT REDIRECT"); return null;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("GET PORTAL DOCUMENT", "CONNECTION FAILED");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String url) {
            if (url != null) {
                intent.putExtra("REDIRECT_URL", url);
                Log.d("PORTAL REDIRECT URL", url);
                SASPortalLoginActivity.this.startActivity(intent);
            }
        }
    }
}