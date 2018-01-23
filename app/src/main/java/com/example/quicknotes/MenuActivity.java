package com.example.quicknotes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.view.View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION;

public class MenuActivity extends AppCompatActivity {

    private SharedPreferences mSharedPref;
    Set<String> mDocNamesSet;
    GridView mGridView;
    boolean mbDeleteMode;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mbDeleteMode = false;
        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        mDocNamesSet = mSharedPref.getStringSet("filenames", new HashSet<String>());
        if(mDocNamesSet.isEmpty()) {
            mDocNamesSet.add("default");
        }
        String[] docNames = mDocNamesSet.toArray(new String[mDocNamesSet.size()]);
        mGridView = (GridView) findViewById(R.id.GridView);
        DocAdapter docAdapter = new DocAdapter(this, docNames);
        mGridView.setAdapter(docAdapter);
    }

    public class DocAdapter extends BaseAdapter {
        private String[] mDocs;
        private Context mContext;
        private LayoutInflater mInflater;

        public DocAdapter(Context c, String[] filenames) {
            mContext = c;
            mDocs = filenames;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return mDocs.length;
        }

        public Object getItem(int position) {
            return mDocs[position];
        }

        public long getItemId(int position) {
            return 0;
        }
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.grid_square, parent, false);
            }
            ((TextView)convertView.findViewById(R.id.filename)).setText(mDocs[position]);
            return convertView;
        }
    }

    public void TapGrid(View view){
        Intent intent = new Intent(this, TextActivity.class);
        intent.putExtra("docname", ((TextView) view.findViewById(R.id.filename)).getText().toString());
        startActivity(intent);
    }

    public void AddDocument(View view){
        String fileName;
        fileName = "default";
        if(mDocNamesSet.contains(fileName)){
            int c;
            for(c=1;mDocNamesSet.contains(fileName.concat(""+c));c++){
                Log.i("a", "fileName: " + fileName);
            }
            fileName = fileName.concat(""+c);
        }
        mDocNamesSet.add(fileName);
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove("filenames");
        editor.commit();
        editor.putStringSet("filenames", mDocNamesSet);
        editor.commit();
        this.recreate();
    }

    public void Delete(View view){
        View deleteButton = findViewById(R.id.deletebutton);
        if(mbDeleteMode == false) {
            ArrayList<View> views = new ArrayList<View>();
            mGridView.findViewsWithText(views, "gridmember", FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
            for (View c : views) {
                c.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TapGridDelete(view);
                    }
                });
            }
            deleteButton.setBackgroundColor(0x55FF0000);
            deleteButton.invalidate();
            mbDeleteMode = true;
        }
        else{
            ArrayList<View> views = new ArrayList<View>();
            mGridView.findViewsWithText(views, "gridmember", FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
            for (View c : views) {
                c.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TapGrid(view);
                    }
                });
            }
            deleteButton.setBackgroundColor(0xD6D7D7);
            deleteButton.invalidate();
            mbDeleteMode = false;
        }
    }

    public void TapGridDelete(View view){
        ArrayList<View> views = new ArrayList<View>();
        mGridView.findViewsWithText(views, "gridmember", FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        for(View c : views){
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TapGrid(view);
                }
            });
        }
        findViewById(R.id.deletebutton).setBackgroundColor(0xD6D7D7);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Legit?");


        mDocNamesSet.remove(((TextView) view.findViewById(R.id.filename)).getText());
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.remove("filenames");
        editor.commit();
        editor.putStringSet("filenames", mDocNamesSet);
        editor.commit();
        Toast.makeText(this, "DELETED", Toast.LENGTH_SHORT).show();

        this.recreate();
    }

}
