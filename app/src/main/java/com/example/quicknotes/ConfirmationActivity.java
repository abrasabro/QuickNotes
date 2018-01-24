package com.example.quicknotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
    }
    public void Confirm(View view){
        Intent intent = new Intent();
        intent.putExtra("result", "confirm");
        setResult(RESULT_OK, intent);
        finish();
    }
    public void Deny(View view){
        Intent intent = new Intent();
        intent.putExtra("result", "deny");
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
