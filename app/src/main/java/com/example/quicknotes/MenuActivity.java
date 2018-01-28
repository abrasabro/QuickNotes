package com.example.quicknotes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mbDeleteMode = false;
        mSharedPref = getPreferences(Context.MODE_PRIVATE);
        mDocNamesSet = mSharedPref.getStringSet("filenames", new HashSet<String>());
        if(mDocNamesSet.isEmpty()) {
            mDocNamesSet.add(getString(R.string.default_filename));
        }
        //put doc names in a List because Set is unordered
        List<String> stringList = new ArrayList<String>();
        stringList.addAll(mDocNamesSet);
        Collections.sort(stringList);
        String[] docNames = stringList.toArray(new String[stringList.size()]);
        mGridView = (GridView) findViewById(R.id.GridView);
        DocAdapter docAdapter = new DocAdapter(this, docNames);
        mGridView.setAdapter(docAdapter);
    }

    public class DocAdapter extends BaseAdapter {
        private String[] mDocs;
        private Context mContext;
        private LayoutInflater mInflater;

        DocAdapter(Context c, String[] filenames) {
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

    public void tapGrid(View view){ //load the tapped document
        Intent intent = new Intent(this, TextActivity.class);
        intent.putExtra("docname", ((TextView) view.findViewById(R.id.filename)).getText().toString());
        startActivity(intent);
    }

    public void addDocument(final View view){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(R.string.create_new_document);

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String fileName = input.getText().toString();
                        if(fileName.isEmpty())
                            fileName = getString(R.string.default_filename);
                        if(mDocNamesSet.contains(fileName)){//if there's a documen with the same name, append numbers to the end
                            int c;
                            for(c=1;mDocNamesSet.contains(fileName.concat(""+c));c++){
                            }
                            fileName = fileName.concat(""+c);
                        }
                        mDocNamesSet.add(fileName);
                        SharedPreferences.Editor editor = mSharedPref.edit();
                        editor.remove("filenames");//if I don't remove and commit first, it won't save to disk
                        editor.commit();
                        editor.putStringSet("filenames", mDocNamesSet);
                        editor.commit();
                        ((Activity)(findViewById(R.id.GridView)).getContext()).recreate();
                    }
                });

        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ((Activity)(findViewById(R.id.GridView)).getContext()).recreate();
                    }
                });

        alertDialog.show();
    }

    /*public void delete(View view){
        View deleteButton = findViewById(R.id.deletebutton);
        if(!mbDeleteMode) {//switch to delete mode
            ArrayList<View> views = new ArrayList<View>();
            mGridView.findViewsWithText(views, "gridmember", FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
            for (View c : views) {//set the onclicklistener for all documents to open up a delete dialog
                c.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tapGridDelete(view);
                    }
                });
            }
            deleteButton.setBackgroundColor(0x55FF0000);
            deleteButton.invalidate();//force redrawing
            mbDeleteMode = true;
        }
        else{//switch out of delete mode
            ArrayList<View> views = new ArrayList<View>();
            mGridView.findViewsWithText(views, "gridmember", FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
            for (View c : views) {//set the onclicklistener for all documents back to normal
                c.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tapGrid(view);
                    }
                });
            }
            deleteButton.setBackgroundColor(0xD6D7D7);
            deleteButton.invalidate();//force redrawing
            mbDeleteMode = false;
        }
    }*/

    public void tapGridDelete(final View view){
        /*ArrayList<View> views = new ArrayList<View>();
        mGridView.findViewsWithText(views, "gridmember", FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        for(View c : views){//set the onclicklistener for all documents back to normal
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tapGrid(view);
                }
            });
        }
        findViewById(R.id.deletebutton).setBackgroundColor(0xD6D7D7);*/
        //get the filename associated with the button that was clicked

        //final String fileName = ((TextView) view).getText().toString();
        final String fileName = ((TextView) ((ViewGroup)view.getParent()).getChildAt(0)).getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirmation, fileName));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                FileOutputStream outputStream;
                //empty the file then delete it
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
                editor.remove("filenames");//if I don't remove and commit first, it won't save to disk
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                addDocument(null);
                break;
            default:
                break;
        }
        return true;
    }

    public void openBottomMenu(View view){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MenuActivity.this);
        View parentView = getLayoutInflater().inflate(R.layout.menu_bottom, null);
        bottomSheetDialog.setContentView(parentView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentView.getParent());
        bottomSheetBehavior.setPeekHeight(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
        ((TextView) bottomSheetDialog.findViewById(R.id.filename)).setText(((TextView) ((ViewGroup)view.getParent()).getChildAt(0)).getText());
        bottomSheetDialog.show();
    }
}
