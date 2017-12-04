package com.example.mcmma.project;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class Setup extends Activity implements View.OnClickListener {

    String[] participantCode = {"P01", "P02", "P03", "P04", "P05", "P06", "P07", "P08", "P09", "P10", "P11",
            "P12", "P13", "P14", "P15", "P16", "P17", "P18", "P19", "P20", "P21", "P22", "P23", "P24", "P25"};
    String[] sessionCode = {"S01", "S02", "S03", "S04", "S05", "S06", "S07", "S08", "S09", "S10", "S11", "S12",
            "S13", "S14", "S15", "S16", "S17", "S18", "S19", "S20", "S21", "S22", "S23", "S24", "S25"};
    String[] blockCode = {"(auto)"};
    String[] groupCode = {"G01", "G02", "G03", "G04", "G05", "G06", "G07", "G08", "G09", "G10", "G11", "G12",
            "G13", "G14", "G15", "G16", "G17", "G18", "G19", "G20", "G21", "G22", "G23", "G24", "G25"};
    String[] numberOfPhrases = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    String[] phrasesFileArray = {"phrases2", "quickbrownfox", "phrases100", "alphabet"};
    String[] entryModeArray = {"Tap", "Swype"};

    Button ok, save, exit;

    SharedPreferences sp;
    SharedPreferences.Editor spe;

    private Spinner spinParticipantCode;
    private Spinner spinSessionCode, spinGroupCode, spinBlockCode;
    private Spinner spinNumberOfPhrases, spinPhrasesFile, spinEntryMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        ok = (Button)findViewById(R.id.ok);
        save = (Button)findViewById(R.id.save);
        exit = (Button)findViewById(R.id.exit);

        sp = this.getPreferences(MODE_PRIVATE);

        participantCode[0] = sp.getString("participantCode", participantCode[0]);
        sessionCode[0] = sp.getString("sessionCode", sessionCode[0]);
        // block code initialized in main activity (based on existing filenames)
        groupCode[0] = sp.getString("groupCode", groupCode[0]);
        numberOfPhrases[0] = sp.getString("numberOfPhrases", numberOfPhrases[0]);
        phrasesFileArray[0] = sp.getString("phrasesFile", phrasesFileArray[0]);
        entryModeArray[0] = sp.getString("entryMode", entryModeArray[0]);

        spinParticipantCode = (Spinner)findViewById(R.id.spinParticipantCode);
        spinSessionCode = (Spinner)findViewById(R.id.spinSessionCode);
        spinGroupCode = (Spinner)findViewById(R.id.spinGroupCode);
        spinBlockCode = (Spinner)findViewById(R.id.spinBlockCode);
        spinNumberOfPhrases = (Spinner)findViewById(R.id.numberOfPhrases);
        spinPhrasesFile = (Spinner)findViewById(R.id.phrasesFile);
        spinEntryMode = (Spinner)findViewById(R.id.entryMode);

        // initialise spinner adapters
        ArrayAdapter<CharSequence> adapterPC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                participantCode);
        spinParticipantCode.setAdapter(adapterPC);

        ArrayAdapter<CharSequence> adapterSC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                sessionCode);
        spinSessionCode.setAdapter(adapterSC);

        ArrayAdapter<CharSequence> adapterBC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                blockCode);
        spinBlockCode.setAdapter(adapterBC);

        ArrayAdapter<CharSequence> adapterGC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                groupCode);
        spinGroupCode.setAdapter(adapterGC);

        ArrayAdapter<CharSequence> adapterNOP = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                numberOfPhrases);
        spinNumberOfPhrases.setAdapter(adapterNOP);

        ArrayAdapter<CharSequence> adapterPF = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                phrasesFileArray);
        spinPhrasesFile.setAdapter(adapterPF);

        ArrayAdapter<CharSequence> adapterEM = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                entryModeArray);
        spinEntryMode.setAdapter(adapterEM);

    }

    @Override
    public void onClick(View v) {

        if (v == ok) {
            Bundle b = new Bundle();
            b.putString("participantCode", participantCode[spinParticipantCode.getSelectedItemPosition()]);
            b.putString("sessionCode", sessionCode[spinSessionCode.getSelectedItemPosition()]);
            b.putString("groupCode", groupCode[spinGroupCode.getSelectedItemPosition()]);
            b.putInt("numberOfPhrases", Integer.parseInt(numberOfPhrases[spinNumberOfPhrases.getSelectedItemPosition()]));
            b.putString("phrasesFile", phrasesFileArray[spinPhrasesFile.getSelectedItemPosition()]);
            b.putString("entryMode", entryModeArray[spinEntryMode.getSelectedItemPosition()]);

            Intent i = new Intent(getApplicationContext(), TextEntry.class);
            i.putExtras(b);
            startActivity(i);
            //finish();
        }
        else if (v == save) {
            spe = sp.edit();
            spe.putString("participantCode", participantCode[spinParticipantCode.getSelectedItemPosition()]);
            spe.putString("sessionCode", sessionCode[spinSessionCode.getSelectedItemPosition()]);
            spe.putString("groupCode", groupCode[spinGroupCode.getSelectedItemPosition()]);
            spe.putString("numberOfPhrases", numberOfPhrases[spinNumberOfPhrases.getSelectedItemPosition()]);
            spe.putString("phrasesFile", phrasesFileArray[spinPhrasesFile.getSelectedItemPosition()]);
            spe.putString("entryMode", entryModeArray[spinEntryMode.getSelectedItemPosition()]);
            spe.apply();
            Toast.makeText(this, "Preferences saved!", Toast.LENGTH_SHORT).show();
        }
        else if (v == exit) {
            this.finish();
        }

    }

}
