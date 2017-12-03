package com.example.mcmma.project;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by mcmma on 2017-12-03.
 */

public class TextEntry extends Activity {

    public String participant;
    public String phrase;

    TextView presented;
    EditText transcribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Bundle b = getIntent().getExtras();

        presented = (TextView)findViewById(R.id.presented);
        transcribed = (EditText)findViewById(R.id.transcribed);
    }
}
