package com.example.photogallery.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.photogallery.R;
import com.example.photogallery.presenter.SearchActivityPresenter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements SearchActivityPresenter.View {
    private SearchActivityPresenter sPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        sPresenter = new SearchActivityPresenter(this);
        try {
            Calendar calendar = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd");
            Date now = calendar.getTime();
            String todayStr = new SimpleDateFormat("yyyy‐MM‐dd", Locale.getDefault()).format(now);
            Date today = format.parse((String) todayStr);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            String yesterdayStr = new SimpleDateFormat("yyyy‐MM‐dd", Locale.getDefault()).format(calendar.getTime());
            Date yesterday = format.parse((String) yesterdayStr);
            ((EditText) findViewById(R.id.etFromDateTime)).setText(new SimpleDateFormat(
                    "yyyy‐MM‐dd hh:mm", Locale.getDefault()).format(yesterday));
            ((EditText) findViewById(R.id.etToDateTime)).setText(new SimpleDateFormat(
                    "yyyy‐MM‐dd hh:mm", Locale.getDefault()).format(today));
        } catch (Exception ex) {
        }
    }

    public void cancel(final View v) {
        finish();
    }

    public void go(final View v) {
        Intent i = new Intent();
        EditText from = (EditText) findViewById(R.id.etFromDateTime);
        EditText to = (EditText) findViewById(R.id.etToDateTime);
        EditText keywords = (EditText) findViewById(R.id.etKeywords);
        EditText lat = (EditText) findViewById(R.id.etLatitude);
        EditText lng = (EditText) findViewById(R.id.etLongitude);
        EditText radius = (EditText) findViewById(R.id.etRadius);
        i.putExtra("STARTTIMESTAMP", from.getText() != null ? from.getText().toString() : "");
        i.putExtra("ENDTIMESTAMP", to.getText() != null ? to.getText().toString() : "");
        i.putExtra("KEYWORDS", keywords.getText() != null ? keywords.getText().toString() : "");
        i.putExtra("LATITUDE", !lat.getText().toString().equals("") ? Double.parseDouble(String.valueOf(lat.getText())) : "");
        i.putExtra("LONGITUDE", !lng.getText().toString().equals("") ? Double.parseDouble(String.valueOf(lng.getText())) : "");
        i.putExtra("RADIUS", !radius.getText().toString().equals("") ? Integer.parseInt(String.valueOf(radius.getText())) : "");
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void cancel() {

    }

    @Override
    public void go() {

    }
}
