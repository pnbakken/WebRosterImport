package com.pnbcode.webrosterimport;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;


public class WebRosterReader {

    Document doc;

    public WebRosterReader(Document webRosterDocument) {

        this.doc = webRosterDocument;
    }

    private Element getRosterTable() {
        return doc.getElementById("frmRosterView:activePeriodData:tbody_element");
    }

    private Elements getTableRows() {
        return getRosterTable().getAllElements();

    }

    public ArrayList<String> getPeriodValues() {
        ArrayList<String> values = new ArrayList<>();
        Element menu = doc.getElementById("frmRosterView:periodCode");
        if (menu != null) {
            Elements months = menu.getAllElements();
            for (int i = 1; i < months.size(); i++) {
                Element value = months.get(i);
                //Log.d(e.val(), e.text());
                String[] items = value.text().split(" ");
                if (Integer.parseInt(items[1]) >= 2021) {
                    if (items.length == 2) {
                        values.add(value.text() + "::" + value.val());
                    }
                }
            }
            return values;
        } else return null;
    }

    public ArrayList<Shift> getShiftData() {

        ArrayList<Shift> shifts = new ArrayList<>();

        String date = "";
        String weekday = "";
        String startTime = "";
        String endTime = "";
        String function = "";
        String duration = "";
        Boolean isShift = false;
        Element dateRow = getTableRows().get(0);
        String[] dates = dateRow.text().split("Edit");
        Log.d("SIZE OF MONTH", String.valueOf(dates.length));
        for (int i = 0; i < dates.length; i++) {
            System.out.println(dates[i]);
            String[] oneDate = dates[i].split(" ");
            Log.d("DAY", String.valueOf(i + 1));
            if (oneDate[3].equalsIgnoreCase("SHIFT")) {
                date = oneDate[0];
                weekday = oneDate[1];
                startTime = oneDate[6];
                endTime = oneDate[8];
                function = oneDate[4];
                duration = oneDate[9];
                isShift = true;
            } else if (oneDate[4].equalsIgnoreCase("SHIFT")) {
                date = oneDate[1];
                weekday = oneDate[2];
                startTime = oneDate[7];
                endTime = oneDate[9];
                function = oneDate[5];
                duration = oneDate[10];
                isShift = true;
            }
            if (isShift) {
                shifts.add(ShiftBuilder.build(date, weekday, startTime, endTime, function, duration));
                Log.d("SHIFT", date + " || " + weekday + " || " + startTime + " - " + endTime + " || " + function + " || " + duration);
            }
            isShift = false;
        }
        return shifts;

    }

}


