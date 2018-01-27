package com.example.quicknotes;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextActivity extends AppCompatActivity {

    private EditText mTextBox;
    private String mFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_edit);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTextBox = (EditText)findViewById(R.id.editText);
        mFileName = getIntent().getStringExtra("docname");
        getSupportActionBar().setTitle(mFileName);
        load(null);
    }

    public void save(View view){
        FileOutputStream outputStream;
        String textBoxString = mTextBox.getText().toString();
        try{
            outputStream = openFileOutput(mFileName, Context.MODE_PRIVATE);
            outputStream.write(textBoxString.getBytes());
            outputStream.close();
        }
        catch(Exception e){
            Toast.makeText(this, "Error Saving", Toast.LENGTH_SHORT).show();
        }
    }
    public void load(View view){
        String content = "";
        if (fileExists(mFileName)) {
            try {
                InputStream in = openFileInput(mFileName);
                if ( in != null) {
                    InputStreamReader tmp = new InputStreamReader( in );
                    BufferedReader reader = new BufferedReader(tmp);
                    String str;
                    StringBuilder buf = new StringBuilder();
                    while ((str = reader.readLine()) != null) {
                        Log.i("a", ""+str);
                        buf.append(str + "\n");
                    } in .close();
                    content = buf.toString();
                    mTextBox.setText(content);
                    Toast.makeText(this, "Loaded", Toast.LENGTH_SHORT).show();
                }
            } catch (java.io.FileNotFoundException e) {} catch (Throwable t) {
                Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean fileExists(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }
    @Override
    protected void onDestroy() {
        save(null);
        super.onDestroy();
    }
}
