package com.example.mcmma.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by mcmma on 2017-12-03.
 */

public class TextEntry extends Activity {

    public String participant;
    public String phrase = "the quick brown fox jumps over the lazy dog";
    public long startTime, elapsedTime;
    public int trial = 1;
    public String inputMode;

    public final int TOTAL_TRIALS = 5;

    public BufferedWriter sd1;
    public File f1;
    public String sd1Leader;
    final String WORKING_DIRECTORY = "/ProjectData/";
    final String APP = "SwypeVSTap";
    final String SD1_HEADER = "App,Participant,Session,Block,Group,EntryMode,Gender,Handedness,ComputingHours," +
            "Age,UsedSwypeBefore,ErrorRate,Speed,Time, s1, s2, s3, s4, s5";

    private float[] errorRates, wpmScores, times;
    private

    TextView presented, trialNumber, currentEntryMode, timer;
    EditText transcribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        errorRates = new float[TOTAL_TRIALS];
        wpmScores = new float[TOTAL_TRIALS];
        times = new float[TOTAL_TRIALS];

        Bundle b = getIntent().getExtras();
        inputMode = b.getString("entryMode");

        showInstructionsDialog();

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

                    presentAndStoreResults();
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
    //and saves the results for storage
    public void presentAndStoreResults() {
        int index = trial - 1;
        String transcribedString = transcribed.getText().toString();

        StringBuilder result = new StringBuilder("Thank you!\n\n");
        result.append(String.format("Presented...\n   %s\n", phrase));
        result.append(String.format("Transcribed...\n   %s\n", transcribedString));

        float t = (elapsedTime / 1000f);
        result.append(String.format("You took %.2f seconds\n", t));
        times[index] = t;

        float w = wpm(phrase, elapsedTime);
        result.append(String.format(Locale.CANADA, "Entry speed: %.2f wpm\n", w));
        wpmScores[index] = w;

        MSD errors = new MSD(phrase, transcribedString);
        float e = (float)errors.getErrorRate();
        result.append(String.format(Locale.CANADA, "Error rate: %.2f%%\n", e));
        errorRates[index] = e;

        showResultsDialog(result.toString());
    }

    public void hideKeyboard() {
        View currentView = this.getCurrentFocus();
        if (currentView != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
        }
    }

    private void showInstructionsDialog() {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.results_dialog, (ViewGroup)findViewById(R.id.results_layout));

        // Set text
        TextView results = (TextView)layout.findViewById(R.id.resultsArea);
        StringBuilder sb = new StringBuilder();
        sb.append("ATTENTION!\n\n");
        Log.i("MYDEBUG", inputMode);

        if (inputMode.equals("Tap")) {
            sb.append("You are currently in the tap portion of the user study! You should be using "+
                    "one hand to input the text shown at the top of the screen " +
                    "using the tap (regular) method for this portion.\n\n");
            sb.append("Please note that you will be timed. The timer begins when you tap the text entry" +
                    " box and ends when you press the Done button on the bottom right of the keyboard.");
        }
        else if (inputMode.equals("Swype")) {
            sb.append("You are currently in the Swype portion of the user study! You should be using "+
                    "one hand to input the text shown at the top of the screen " +
                    "using the Swype gesture method that was shown previously for this portion.\n\n");
            sb.append("Please note that you will be timed. The timer begins when you tap the text entry" +
                    " box and ends when you press the Done button on the bottom right of the keyboard.");
        }
        results.setText(sb.toString());

        // Initialize the dialog
        AlertDialog.Builder parameters = new AlertDialog.Builder(this);
        parameters.setView(layout).setCancelable(false).setNeutralButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel(); // close this dialog
            }
        }).show();
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
                if (trial > TOTAL_TRIALS) {
                    saveResultsToFile();
                    finish();
                }
                dialog.cancel(); // close this dialog
            }
        }).show();
    }

    private void saveResultsToFile() {
        float meanErrorRate = calculateMean(errorRates);
        float meanTime = calculateMean(times);
        float meanSpeed = calculateMean(wpmScores);

        Bundle b = getIntent().getExtras();
        String pCode = b.getString("participantCode");
        String sCode = b.getString("sessionCode");
        String gCode = b.getString("groupCode");
        String mode = b.getString("entryMode");
        String gender = b.getString("gender");
        String hand = b.getString("handedness");
        String hours = b.getString("hours");
        String age = b.getString("age");
        String usedSwype = b.getString("swype");

        File dataDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "ProjectData");

        boolean success = true;
        if (!dataDirectory.exists()) {
            success = dataDirectory.mkdirs();
        }
        Log.i("MYDEBUG", "Working directory=" + dataDirectory);
        if (!success)
        {
            Log.e("MYDEBUG", "ERROR --> FAILED TO CREATE DIRECTORY: " + WORKING_DIRECTORY);
            super.onDestroy(); // cleanup
            this.finish(); // terminate
        }
        try {
            dataDirectory.createNewFile();
        } catch (IOException e) {}

        int blockNumber = 0;
        do {
            ++blockNumber;
            String blockCode = String.format(Locale.CANADA, "B%02d", blockNumber);
            String baseFileName = String.format("%s-%s-%s-%s-%s-%s", APP, pCode, sCode,
                    blockCode, gCode, mode);
            f1 = new File(dataDirectory, baseFileName + ".sd2");
            sd1Leader = String.format("%s,%s,%s,%s,%s,%s", APP, pCode, sCode,
                    blockCode, gCode, mode);
        } while (f1.exists());


        try {
            sd1 = new BufferedWriter(new FileWriter(f1));
        } catch (IOException e) {
            Log.e("MYDEBUG", "ERROR OPENING DATA FILES! e=" + e.toString());
            super.onDestroy();
            this.finish();
        }

        StringBuilder sd1Data = new StringBuilder(100);
        sd1Data.append(String.format("%s,", sd1Leader));
        sd1Data.append(String.format("%s,", gender));
        sd1Data.append(String.format("%s,", hand));
        sd1Data.append(String.format("%s,", hours));
        sd1Data.append(String.format("%s,", age));
        sd1Data.append(String.format("%s,", usedSwype));
        sd1Data.append(String.format(Locale.CANADA, "%.2f,", meanErrorRate));
        sd1Data.append(String.format(Locale.CANADA, "%.2f,", meanSpeed));
        sd1Data.append(String.format(Locale.CANADA, "%.2f,", meanTime));
        sd1Data.append(String.format(Locale.CANADA, "%.2f,", wpmScores[0]));
        sd1Data.append(String.format(Locale.CANADA, "%.2f,", wpmScores[1]));
        sd1Data.append(String.format(Locale.CANADA, "%.2f,", wpmScores[2]));
        sd1Data.append(String.format(Locale.CANADA, "%.2f,", wpmScores[3]));
        sd1Data.append(String.format(Locale.CANADA, "%.2f,", wpmScores[4]));

        try {
            sd1.write(SD1_HEADER, 0, SD1_HEADER.length());
            sd1.flush();
            sd1.write(sd1Data.toString(), 0, sd1Data.length());
            sd1.flush();
        } catch (IOException e) {
            Log.e("MYDEBUG", "ERROR WRITING TO DATA FILE!\n" + e);
            super.onDestroy();
            this.finish();
        }

        //refreshes the file in explorer
        MediaScannerConnection.scanFile(this, new String[]{f1.getAbsolutePath()}, null, null);

    }

    private float calculateMean(float[] arg) {
        float result = 0f;
        for (int i = 0; i < arg.length; i++) {
            result += arg[i];
        }

        return result / arg.length;
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
