package com.example.photogallery.presenter;

import com.example.photogallery.view.SearchActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchActivityPresenter {
    private final View view;

    public SearchActivityPresenter(SearchActivity view) {
        this.view = (View) view;
    }

    public void setupCalendarDates() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd");
        Date now = calendar.getTime();
        String todayStr = new SimpleDateFormat("yyyy‐MM‐dd", Locale.getDefault()).format(now);
        Date today = format.parse((String) todayStr);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String yesterdayStr = new SimpleDateFormat("yyyy‐MM‐dd", Locale.getDefault()).format(calendar.getTime());
        Date yesterday = format.parse((String) yesterdayStr);
        view.setFromAndToDates(yesterday,today);
    }

    public interface View {
        void setFromAndToDates(Date yesterday, Date today);
    }
}


