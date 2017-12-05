package com.example.mcmma.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by mcmma on 2017-12-04.
 */

public class Survey extends Activity {

    String[] handedness = {"Right", "Left"};
    String[] gender = {"Male", "Female", "Other"};
    String[] swypeUse = {"Yes", "No"};

    private Spinner spinDominantHand, spinGender, spinSwype;
    private EditText editHours, editAge;
    private Button nextButton;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey);

        spinDominantHand = (Spinner)findViewById(R.id.spinDominantHand);
        spinGender = (Spinner)findViewById(R.id.spinGender);
        spinSwype = (Spinner)findViewById(R.id.spinSwype);

        spinSwype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (spinSwype.getSelectedItem().toString() == "No") {
                    Log.i("MYDEBUG", "No Selected");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        editHours = (EditText)findViewById(R.id.editHours);
        editAge = (EditText)findViewById(R.id.editAge);
        nextButton = (Button)findViewById(R.id.next);

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("gender", gender[spinGender.getSelectedItemPosition()]);
                b.putString("handedness", handedness[spinDominantHand.getSelectedItemPosition()]);
                b.putString("hours", editHours.getText().toString());
                b.putString("age", editAge.getText().toString());
                b.putString("swype", swypeUse[spinSwype.getSelectedItemPosition()]);

                Intent i = new Intent(getApplicationContext(), Setup.class);

                i.putExtras(b);
                startActivity(i);
                finish();
            }
        });

        // initialise spinner adapters
        ArrayAdapter<CharSequence> adapterDH = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                handedness);
        spinDominantHand.setAdapter(adapterDH);

        ArrayAdapter<CharSequence> adapterG = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                gender);
        spinGender.setAdapter(adapterG);

        ArrayAdapter<CharSequence> adapterS = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                swypeUse);
        spinSwype.setAdapter(adapterS);
    }
}
