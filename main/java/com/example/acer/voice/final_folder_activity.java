package com.example.acer.voice;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class final_folder_activity extends AppCompatActivity implements final_folder_adapter.ListItemClickListner {
    File parentdir;
    File final_folder;
    String LOGTAG1 = "logtag";
    String[] mNames;
    int num_list_items;
    RecyclerView mFilesList;
    final_folder_adapter fileAdapter;
    String mFoldername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_folder);
        //parentdir = this.getFilesDir();
        parentdir=Environment.getExternalStorageDirectory();
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        mFoldername = intent.getStringExtra("final_folder_intent");
        String[] strings = getStrings(mFoldername);
        num_list_items = strings.length;
        mFilesList = (RecyclerView) findViewById(R.id.final_folderlist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mFilesList.setLayoutManager(layoutManager);
        mFilesList.setHasFixedSize(false);
        fileAdapter = new final_folder_adapter(strings, num_list_items, this, mFoldername);
        mFilesList.setAdapter(fileAdapter);
    }

    @NonNull
    private String[] getStrings(String mFoldername) {
        File folder = new File(parentdir, "Voice");
        File final_folder = new File(folder, mFoldername);
        File[] files = final_folder.listFiles();
        int l = files.length;
        String[] strings = new String[l];
        for (int i = 0; i < l; i++) {
            strings[i] = files[i].getName();
        }
        return strings;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent back_intent = new Intent(this, files_activity.class);
            back_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(back_intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] s = getStrings(mFoldername);
        if (s.length == 0) {
            num_list_items = 0;
            fileAdapter = new final_folder_adapter(s, num_list_items, this, mFoldername);
            mFilesList.setAdapter(fileAdapter);
        } else {
            fileAdapter.setRecordeddata(getStrings(mFoldername));
        }

    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        File folder = new File(parentdir, "Voice");
        File final_folder = new File(folder, mFoldername);
        File[] files = final_folder.listFiles();
        File final_final = files[clickedItemIndex];
        new play().execute(final_final);
    }

    public class play extends AsyncTask<File, Void, Void> {

        @Override
        protected Void doInBackground(File... files) {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(files[0].getAbsolutePath()), "audio/*");
            startActivity(intent);
            return null;
        }
    }
// public class play extends AsyncTask<File,Void,Void> {
//       MediaPlayer mediaPlayer;
//        @Override
//        protected Void doInBackground(File... files) {
//
//            Uri uri = FileProvider.getUriForFile(final_folder_activity.this, "com.mydomain.fileprovider", files[0]);
//            mediaPlayer= new MediaPlayer();
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            try {
//                mediaPlayer.setDataSource(getApplicationContext(), uri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                mediaPlayer.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            mediaPlayer.start();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//    }
}


