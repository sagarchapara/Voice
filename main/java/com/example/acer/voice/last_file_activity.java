package com.example.acer.voice;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class last_file_activity extends AppCompatActivity {
    File parentdir;
    String LOGTAG1 ="logtag";
    ListView lv;
    File final_folder;
    File final_folder1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_file);
        parentdir = Environment.getExternalStorageDirectory();
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        String mFoldername = intent.getStringExtra("last_folder");
        File folder = new File(parentdir,"Voice");
        final_folder = new File(folder,mFoldername);
        final_folder1=new File(final_folder,mFoldername);
        File[] files = final_folder.listFiles();
        int l = files.length;
        String[] strings = new String[l];
        for(int i=0;i<l;i++){
            strings[i] = files[i].getName();
        }
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked,strings);
        lv= (ListView)findViewById(R.id.last_file_id);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.final_folder_menu,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            Intent back_intent = new Intent(this,files_activity.class);
            back_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(back_intent);
        }
        if(id==R.id.finaldel){
            SparseBooleanArray checked = lv.getCheckedItemPositions();
            ArrayList<String> checkedvalues = new ArrayList<String>();
            ArrayList<String> uncheckedvalues = new ArrayList<String>();
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i) == true) {
                    String tag = String.valueOf(lv.getItemAtPosition(checked.keyAt(i)));
                    checkedvalues.add(tag);
                    Toast.makeText(last_file_activity.this, "delete", Toast.LENGTH_SHORT).show();
                }
                else{
                    String tag = String.valueOf(lv.getItemAtPosition(checked.keyAt(i)));
                    uncheckedvalues.add(tag);
                    Log.i("xxxx", tag);

                }
            }
            new last_file_activity.delete_checkbox(checkedvalues,uncheckedvalues).execute(checkedvalues);

        }
        if(id==R.id.finalshr){
            SparseBooleanArray checked = lv.getCheckedItemPositions();
            ArrayList<File> checkedvalues = new ArrayList<File>();
            for (int i = 0; i < checked.size(); i++) {
                if (checked.valueAt(i) == true) {
                    String tag = String.valueOf(lv.getItemAtPosition(checked.keyAt(i)));
                    File f = new File(final_folder,tag);
                    checkedvalues.add(f);
                }
            }
            shareMultiple(checkedvalues,this);
            finish();

        }


        return super.onOptionsItemSelected(item);
    }

    private class delete_checkbox extends AsyncTask<ArrayList<String>,Void,Void> {
        ArrayList<String> uncheckeditems;
        ArrayList<String> checkeditems;
        private delete_checkbox(ArrayList<String> checked,ArrayList<String> unchecked){
            this.uncheckeditems =unchecked;
            this.checkeditems = checked;
        }
        @Override
        protected Void doInBackground(ArrayList<String>... arrayLists) {
            int l = arrayLists[0].size();
            for(int i=0;i<l;i++){
                File temp = new File( final_folder.getAbsolutePath(),arrayLists[0].get(i));
                Log.i("xxxx", i + " " +temp.getAbsolutePath() );
                temp.delete();
//                Log.i("xxxx", i + " " + );
            }
            finish();
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Context context = last_file_activity.this;
//            Intent intent = new Intent(context,final_folder_activity.class);
//            String[] mStringArray = new String[uncheckeditems.size()];
//            mStringArray = uncheckeditems.toArray(mStringArray);
//            intent.putExtra("recorded_data",mStringArray);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);


        }
    }
//    void deleteRecursive(File fileOrDirectory) {
//        if (fileOrDirectory.isDirectory())
//            for (File child : fileOrDirectory.listFiles())
//                deleteRecursive(child);
//
//        fileOrDirectory.delete();
//    }
    public static void shareMultiple(List<File> files, Context context){

        ArrayList<Uri> uris = new ArrayList<>();
        for(File file: files){
            Uri uri = FileProvider.getUriForFile(context, "com.mydomain.fileprovider", file);
            uris.add(uri);
        }
        final Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("video/mp4");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.ids_msg_share)));
    }

}
