package com.example.mcmma.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by mcmma on 2017-12-03.
 */

public class TextEntry extends Activity {

    public String participant;
    public String phrase = "The quick brown fox jumps over the lazy dog";
    public long startTime, elapsedTime;
    public int trial = 1;

    public final int TOTAL_TRIALS = 5;

    TextView presented, trialNumber, currentEntryMode, timer;
    EditText transcribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Bundle b = getIntent().getExtras();

        presented = (TextView)findViewById(R.id.presented);
        transcribed = (EditText)findViewById(R.id.transcribed);
        trialNumber = (TextView)findViewById(R.id.trialNumber);
        currentEntryMode = (TextView)findViewById(R.id.currentEntryMode);
        timer = (TextView)findViewById(R.id.time);

        //phrase = getResources().getStringArray(R.array.quickbrownfox);
        presented.setText(phrase);

        //used to detect when the user presses the edit text to start the timer
        transcribed.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                startTime = System.currentTimeMillis();
            }
        });

        transcribed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    elapsedTime = (System.currentTimeMillis() - startTime);
                    timer.setText(Float.toString(elapsedTime / 1000f));
                    presentResults();
                    trial++;

                    if (trial <= TOTAL_TRIALS)
                        trialNumber.setText("Trial Number: " + trial + "/" + TOTAL_TRIALS);

                    transcribed.setText("");
                }
                return false;
            }
        });

        trialNumber.setText("Trial Number: " + trial + "/" + TOTAL_TRIALS);
        currentEntryMode.setText("Entry Mode: " + b.getString("entryMode"));
    }

    //presents the results of a single trial in an AlertDialog to the user
    public void presentResults() {
        String transcribedString = transcribed.getText().toString();

        StringBuilder result = new StringBuilder("Thank you!\n\n");
        result.append(String.format("Presented...\n   %s\n", phrase));
        result.append(String.format("Transcribed...\n   %s\n", transcribedString));
        result.append(String.format(Locale.CANADA, "Entry speed: %.2f wpm\n", wpm(phrase, elapsedTime)));

        MSD errors = new MSD(phrase, transcribedString);
        result.append(String.format(Locale.CANADA, "Error rate: %.2f%%\n", (float)errors.getErrorRate()));

        showResultsDialog(result.toString());
    }

    public void hideKeyboard() {
        View currentView = this.getCurrentFocus();
        if (currentView != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
        }
    }

    private void showResultsDialog(String text) {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.results_dialog, (ViewGroup)findViewById(R.id.results_layout));

        // Set text
        TextView results = (TextView)layout.findViewById(R.id.resultsArea);
        results.setText(text);

        // Initialize the dialog
        AlertDialog.Builder parameters = new AlertDialog.Builder(this);
        parameters.setView(layout).setCancelable(false).setNeutralButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if (trial > TOTAL_TRIALS)
                    finish();
                dialog.cancel(); // close this dialog
            }
        }).show();
    }

    private String timeString(long elapsedTime) {
        StringBuilder time = new StringBuilder();
        int minutes = (int)(elapsedTime / 1000) / 60;
        int seconds = (int)(elapsedTime / 1000) - (minutes * 60);
        int tenths = (int)(elapsedTime / 10) % 10;
        time.append(minutes + ":");
        if (seconds < 10)
            time.append("0" + seconds);
        else
            time.append(seconds);
        time.append("." + tenths);
        return time.toString();
    }

    public static float wpm(String text, long msTime) {
        float speed = text.length();
        speed = speed / (msTime / 1000.0f) * (60 / 5);
        return speed;
    }
}
