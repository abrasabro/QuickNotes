package com.example.quicknotes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
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
        ShowAlertDialogNew(view);

    }

    public void Delete(View view){
        View deleteButton = findViewById(R.id.deletebutton);
        if(!mbDeleteMode) {
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
        showAlertDialogButtonClicked(view);
    }
    public void showAlertDialogButtonClicked(final View view) {
        final String fileName = ((TextView) view.findViewById(R.id.filename)).getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirmation, fileName));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FileOutputStream outputStream;
                try{
                    outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                    outputStream.write(0);
                    outputStream.close();
                    deleteFile(fileName);
                    Toast.makeText(view.getContext(), "DELETED", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    Toast.makeText(view.getContext(), "Error deleting", Toast.LENGTH_SHORT).show();
                }
                mDocNamesSet.remove(fileName);
                SharedPreferences.Editor editor = mSharedPref.edit();
                editor.remove("filenames");
                editor.commit();
                editor.putStringSet("filenames", mDocNamesSet);
                editor.commit();
                ((Activity)view.getContext()).recreate();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ((Activity)view.getContext()).recreate();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void ShowAlertDialogNew(final View view){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("New document name");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String fileName = input.getText().toString();
                        if(fileName.isEmpty())
                            fileName = "default";
                        if(mDocNamesSet.contains(fileName)){
                            int c;
                            for(c=1;mDocNamesSet.contains(fileName.concat(""+c));c++){
                            }
                            fileName = fileName.concat(""+c);
                        }
                        mDocNamesSet.add(fileName);
                        SharedPreferences.Editor editor = mSharedPref.edit();
                        editor.remove("filenames");
                        editor.commit();
                        editor.putStringSet("filenames", mDocNamesSet);
                        editor.commit();
                        ((Activity)view.getContext()).recreate();
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ((Activity)view.getContext()).recreate();
                    }
                });

        alertDialog.show();
    }
}
