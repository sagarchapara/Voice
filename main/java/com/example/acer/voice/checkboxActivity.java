package com.example.acer.voice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.acer.voice.recordeddatabase.recordedDatadatabase;
import com.example.acer.voice.recordeddatabase.recordeddata;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class checkboxActivity extends AppCompatActivity {
    ListView lv;
    File parentdir;
    List<recordeddata> mrecordeddata;
    private recordedDatadatabase mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkbox);
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        parentdir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"Voice");

        Intent intent = getIntent();
//        String[] names = intent.getStringArrayExtra("checkboxactivity");

    }

    @Override
    protected void onResume() {
        mDb = recordedDatadatabase.getsInstance(getApplicationContext());
        super.onResume();
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                mrecordeddata =mDb.recordeddatadao().loadAlldata();
                String[] names = getNames(mrecordeddata);
                ArrayAdapter<String> adapter= new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_checked, names);
                lv= (ListView)findViewById(R.id.names_list);
                lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                lv.setAdapter(adapter);
            }
        });


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            Intent back_intent = new Intent(this,files_activity.class);
            back_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(back_intent);
        }
        if(id==R.id.checkdel){
            SparseBooleanArray checked = lv.getCheckedItemPositions();
            ArrayList<String> checkedvalues = new ArrayList<String>();
            ArrayList<String> uncheckedvalues = new ArrayList<String>();
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i) == true) {
                    String tag = String.valueOf(lv.getItemAtPosition(checked.keyAt(i)));
                    checkedvalues.add(tag);
                    Toast.makeText(checkboxActivity.this, "delete", Toast.LENGTH_SHORT).show();
                }
                else{
                    String tag = String.valueOf(lv.getItemAtPosition(checked.keyAt(i)));
                    uncheckedvalues.add(tag);
                    Log.i("xxxx", tag);
                }
            }
//            new delete_checkbox(checkedvalues,uncheckedvalues).execute(checkedvalues);
            delete(checkedvalues);


        }
        if(id==R.id.checkshr){
            SparseBooleanArray checked = lv.getCheckedItemPositions();
            ArrayList<File> checkedvalues = new ArrayList<File>();
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i) == true) {
                    String tag = String.valueOf(lv.getItemAtPosition(checked.keyAt(i)));
                    File f = new File(parentdir,tag);
                    checkedvalues.add(f);
                }
            }
            shareMultiple(checkedvalues,this);
            finish();

        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.checkboxmenu,menu);
        return true;
    }
//
//    private class delete_checkbox extends AsyncTask<ArrayList<String>,Void,Void>{
//        ArrayList<String> uncheckeditems;
//        ArrayList<String> checkeditems;
//        private delete_checkbox(ArrayList<String> checked,ArrayList<String> unchecked){
//            this.uncheckeditems =unchecked;
//            this.checkeditems = checked;
//        }
//        @Override
//        protected Void doInBackground(ArrayList<String>... arrayLists) {
////            int l = arrayLists[0].size();
////            for(int i=0;i<l;i++){
////                File temp = new File(parentdir.getAbsolutePath(),arrayLists[0].get(i));
////                Log.i("xxxx", i + " " +temp.getAbsolutePath() );
////                deleteRecursive(temp);
//////                Log.i("xxxx", i + " " + );
////            }
//        return null;
//        }
//
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
//                @Override
//                public void run() {
//                    for(int i=0;i<checkeditems.)
//                }
//            });
//            finish();
//            Context context = checkboxActivity.this;
//            Intent intent = new Intent(context,files_activity.class);
//            String[] mStringArray = new String[uncheckeditems.size()];
//            mStringArray = uncheckeditems.toArray(mStringArray);
//            intent.putExtra("recorded_data",mStringArray);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);




    public void delete(final ArrayList<String> checkeditems){
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                int l = checkeditems.size();
                for(int i=0;i<l;i++) {
                    File temp = new File(parentdir.getAbsolutePath(), checkeditems.get(i));
                    Log.i("xxxx", i + " " + temp.getAbsolutePath());
                    try {
                        FileUtils.deleteDirectory(temp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recordeddata r=mDb.recordeddatadao().getbyname(checkeditems.get(i));
                    mDb.recordeddatadao().deleteData(r);
                }
            }
        });
        finish();
    }
    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
    public static void shareMultiple(List<File> files, Context context){

        ArrayList<Uri> uris = new ArrayList<>();
        for(File file: files){
            Uri uri = FileProvider.getUriForFile(context, "com.mydomain.fileprovider", file);
            uris.add(uri);
        }
        final Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("inode/directory");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.ids_msg_share)));
    }
    public String[] getNames(List<recordeddata> recordedData){
        int l = recordedData.size();
        String[] names = new String[l];
        for(int i=0;i<l;i++){
            names[i] = recordedData.get(i).getName();
        }
        return names;
    }


}
