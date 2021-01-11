package com.pnbcode.webrosterimport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class WebRosterLoginActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private String rosterUsername;
    private String rosterPassword;
    private String url;


    private Document doc;
    private Connection.Response rosterLoginForm;
    private WebRosterReader reader;
    private ArrayList<Shift> shifts;
    private Spinner dropdownList;
    private Button periodSelectButton;
    private Button exportButton;
    private TextView selectionHeader;
    private ListView shiftList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_roster_login);
        Intent intent = getIntent();
        Log.d("INTENT HAS URL", String.valueOf(intent.hasExtra("REDIRECT_URL")));
        if (intent.hasExtra("REDIRECT_URL")) {
            Log.d("URL IS", intent.getStringExtra("REDIRECT_URL"));
            url = intent.getStringExtra("REDIRECT_URL");
        } else {
            finish();
        }


        usernameField = findViewById(R.id.webroster_username);
        passwordField = findViewById(R.id.webroster_password);


    }

    public void getRosterInfoAndLogIn(View view) {
        rosterUsername = usernameField.getText().toString();
        rosterPassword = passwordField.getText().toString();
        Log.d("WEBROSTERLOGIN", "BUTTON PRESSED");
        new RosterConnector().execute();
    }

    private void displayRosterPage() {
        setContentView(R.layout.activity_web_roster_display);
        fillPeriodSelector();
        periodSelectButton = findViewById(R.id.button_select_period);
        exportButton = findViewById(R.id.button_export_shifts_to_calendar);
        selectionHeader = findViewById(R.id.info_text);
    }

    private void fillPeriodSelector() {
        if (doc != null) {
            reader = new WebRosterReader(doc);
            dropdownList = findViewById(R.id.dropdown_list);
            ArrayList<String> menuValues = reader.getPeriodValues();
            ArrayAdapter<String> selectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, menuValues);
            selectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdownList.setAdapter(selectAdapter);

        } else Toast.makeText(this, "Something went wrong. Restart app and try again", Toast.LENGTH_LONG).show();
    }

    public void selectPeriod(View view) {
        String[] selection = dropdownList.getSelectedItem().toString().split("::");
        String code = selection[1];
        Log.d("SELECTION IS", code);
        new RosterUpdater(code).execute();
    }

    private void refreshPeriodSchedule(String header) {
        shiftList = findViewById(R.id.roster_display_view);
        reader = new WebRosterReader(doc);
        shifts = reader.getShiftData();
        if (shifts.size() > 0) {
            exportButton.setVisibility(View.VISIBLE);
        } else {
            exportButton.setVisibility(View.INVISIBLE);
        }
        selectionHeader.setText(header);
        selectionHeader.setVisibility(View.VISIBLE);

        ArrayAdapter<String> shiftDisplayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1,getShiftsAsText(shifts));
        shiftList.setAdapter(shiftDisplayAdapter);

    }

    private ArrayList<String> getShiftsAsText(ArrayList<Shift> shifts) {
        ArrayList<String> shiftText = new ArrayList<>();

        for (Shift s : shifts) {
            shiftText.add(s.toString());
        }
        return shiftText;
    }

    public void exportShiftsToCalendar(View view) {

        for (Shift s : shifts) {
            new EventExporter(this.getContentResolver()).export(s);
        }

    }


    private class RosterConnector extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Log.d("NEW URL IS", url);
                rosterLoginForm = Jsoup.connect(url)
                        .method(Connection.Method.GET)
                        .timeout(10000)
                        .execute();
                Log.d("CONNECTION STATE: ", rosterLoginForm.statusMessage());

                doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1)")
                        .data("input_for_enterkey_submit", "")
                        .data("frmLogin:username", rosterUsername)
                        .data("frmLogin:password", rosterPassword)
                        .data("frmLogin:loginButtonId", "Login")
                        .data("frmLogin_SUBMIT", "1")
                        .data("jsf_sequence", "1")
                        .cookies(rosterLoginForm.cookies())
                        .post();

                try {
                    Elements user = doc.getElementsByClass("applicationStatus");
                    if (user != null) {
                        Log.d("FROM WEBROSTER", "USER RECEIVED");
                        Log.d("USERINFO", user.text());
                        Log.d("URL", doc.location());
                        return true;
                    } else Log.d("FROM WEBROSTER", "NO CONTENT RECEIVED");
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("WEBROSTER", "NOT CONNECTED");
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("WEBROSTER CONNECT", "LOGIN ERROR");
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean connected) {
            if (connected) {
                displayRosterPage();
            }
        }
    }

    private class RosterUpdater extends AsyncTask<Void, Void, Document> {

        private String selection;

        public RosterUpdater(String selection) {
            this.selection = selection;
        }

        @Override
        protected Document doInBackground(Void... voids) {
            Log.d("UPDATING SELECTION", selection);
            try {

                doc = Jsoup.connect("https://webroster.scandinavian.net/webroster-presentation-osl/faces/rosterView.tiles").userAgent("Mozilla/5.0 (Windows NT 6.1)")
                        .data("frmRosterView:periodCode", selection)
                        .data("frmRosterView:isShowPopup", "false")
                        .data("frmRosterView_SUBMIT", "1")
                        .data("jsf_sequence", "1")
                        .data("frmRosterView:_link_hidden_", "frmRosterView:showScheduleButtonId")
                        .cookies(rosterLoginForm.cookies())
                        .post();

                Log.d("STATUS", doc.body().text());
                return doc;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Document location) {
            super.onPostExecute(location);
            String indicator = doc.getElementsByClass("bolded").text();

            Log.d("CHECKING", location.location());
            Log.d("PAGE INDICATOR", indicator);

            refreshPeriodSchedule(indicator);
        }
    }

    private class EventExporter extends AsyncQueryHandler {

        public EventExporter(ContentResolver cr) {
            super(cr);
        }

        public void export(Shift shift) {
            String[] dates = shift.getDate().split(".");
            String[] starts = shift.getStartTime().split(":");
            String[] ends = shift.getEndTime().split(":");
            Log.d("DATE ARRAY", dates[0]+dates[1]+dates[2]);
            Log.d("START ARRAY", starts[0]+starts[1]);
            Log.d("END ARRAY", ends[0]+ends[1]);


            long calID = 3;
            long startMillis = 0;
            long endMillis = 0;
            Calendar beginTime = Calendar.getInstance();
            beginTime.set(getYear(dates[0]), getMonth(dates[1]), getDay(dates[2]), getStartHour(starts[0]), getStartMinute(starts[1]));
            startMillis = beginTime.getTimeInMillis();
            Calendar endTime = Calendar.getInstance();
            endTime.set(getYear(dates[0]), getMonth(dates[1]), getDay(dates[2]), getEndHour(ends[0]), getEndMinute(ends[1]));
            endMillis = endTime.getTimeInMillis();

            ContentValues values = new ContentValues();

            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, "SHIFT");
            values.put(CalendarContract.Events.DESCRIPTION, shift.getFunction() + " - " + shift.getDuration());
            values.put(CalendarContract.Events.ALL_DAY, false);
            values.put(CalendarContract.Events.CALENDAR_ID, calID);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Oslo");

            Uri contentUri = CalendarContract.Events.CONTENT_URI;
        }

        private int getYear(String year) {

            return Integer.parseInt(year);
        }

        private int getMonth(String month) {
            return Integer.parseInt(month);
        }

        private int getDay(String day) {
            return Integer.parseInt(day);
        }

        private int getStartHour(String hour) {
            return Integer.parseInt(hour);
        }

        private int getStartMinute(String minute) {
            return Integer.parseInt(minute);
        }

        private int getEndHour(String hour) {
            return Integer.parseInt(hour);
        }

        private int getEndMinute(String minute) {
            return Integer.parseInt(minute);
        }

        private boolean isOverNight(int startHour, int endHour) {
            return endHour < startHour;
        }

    }

}