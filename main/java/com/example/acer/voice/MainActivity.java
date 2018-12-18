package com.example.acer.voice;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.voice.recordeddatabase.recordedDatadatabase;
import com.example.acer.voice.recordeddatabase.recordeddata;
import com.goodiebag.protractorview.ProtractorView;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    ImageButton button_import;
    private recordedDatadatabase mDb;
    PopupWindow popUp;
    ImageButton button_record;
    ImageButton button_recordings;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_WRITE_EXTERNAL_PERMISSION =1;
    private static String mFileName = null;
    private static String outputfile = null;
    private MediaRecorder mRecorder = null;
    public static int i=1;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted1 = false;
    private boolean permissionToRecordAccepted2 = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    finish();
                    // functionality that depends on this permission.
                }
        }
    }
    //TODO(1) needs to modify the permissions

    boolean mStartRecording = true;
    RelativeLayout rl;
    File parentdir;
    OnSwipeTouchListener onswipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //This can then be added to its parent layout
        ProtractorView  protractorView = (ProtractorView) findViewById( R.id.main_text);
        // Obtain the FirebaseAnalytics instance.

        mDb =recordedDatadatabase.getsInstance(getApplicationContext());
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        SharedPreferences settings = getSharedPreferences("YOUR_PREF_NAME", 0);
        int snowDensity = settings.getInt("SNOW_DENSITY", 1);//0 is the default value
        i = snowDensity;
//        parentdir = new File(this.getFilesDir().getAbsolutePath(), "Voice");
        parentdir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"Voice");
        if(!parentdir.exists())
            parentdir.mkdirs();
        button_import = (ImageButton) findViewById(R.id.import_button);
        button_record = (ImageButton) findViewById(R.id.record_button);
        button_recordings =(ImageButton) findViewById(R.id.recordings_button);
        mStartRecording = true;
        button_record.setOnClickListener(clicker);
        button_recordings.setOnClickListener(clicker);
        button_import.setOnClickListener(clicker);
     /*   main_textview.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
                dispatchTakeAudioIntent();

            }

            public void onSwipeRight() {
                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
                dispatchTakeVideoIntent();

           }

        public void onSwipeLeft() {
        Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
                Context context = MainActivity.this;
                Intent start_files_activity = new Intent(context, files_activity.class);
                Log.d(LOGTAG, "activity started");
                new gettingRecordedData().execute(start_files_activity);
            }

            public void onSwipeBottom() {
                Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });  */
        protractorView.setOnProtractorViewChangeListener(new ProtractorView.OnProtractorViewChangeListener() {
            @Override
            public void onProgressChanged(ProtractorView pv, int progress, boolean b) {
                //protractorView's getters can be accessed using pv instance.
            }

            @Override
            public void onStartTrackingTouch(ProtractorView pv) {

            }

            @Override
            public void onStopTrackingTouch(ProtractorView pv) {

            }
        });


    }


    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        //Log.e(LOG_TAG, "prepare() faile");
        String FileName = "Recording" + i ;
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        File outputdir = new File(parentdir,"Recording" + i);
        outputdir.mkdirs();
        mFileName = outputdir.getAbsolutePath();
        Log.e(LOG_TAG,mFileName);
        File outputFile = new File(outputdir,FileName+".mp3");
        outputfile= outputFile.getAbsolutePath();
        mRecorder.setOutputFile(outputFile.getAbsolutePath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }
    boolean mSaver;
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save as");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        input.setText(outputfile);
        input.setSelectAllOnFocus(true);
        this.defaultSetup();
        builder.setCancelable(false);
        if(mSaver) {
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                    if(m_Text.equals(""))
//                        new save().execute(outputfile);
                        retrievetasks(mFileName);
                    else{
                        Log.e(LOG_TAG,"!"+m_Text);
//                        new save().execute(m_Text);
                        retrievetasks(m_Text);
                    }
                    dialog.cancel();
                    i++;
                    SharedPreferences settings = getSharedPreferences("YOUR_PREF_NAME", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("SNOW_DENSITY",i);
                    editor.commit();
                }
            });
            builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new delete().execute(mFileName);
                    dialog.cancel();
                }
            });
            builder.show();
        }
        else{
//            new save().execute(outputfile);
            retrievetasks(mFileName);
            i++;
        }




    }
    public void retrievetasks(final String strings){
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            recordeddata recordedData;
            @Override
            public void run() {
                Log.e(LOG_TAG,strings);
                if(!strings.equals((outputfile))) {
                    File from = new File(outputfile);
                    File to = new File(mFileName, strings + ".mp3");
                    from.renameTo(to);
                    File from1 = new File(mFileName);
                    File to1 = new File(parentdir, strings);
                    from1.renameTo(to1);
                    String name = to1.getName();
                    Log.e(LOG_TAG, name);
                    Date date = new Date(to1.lastModified());
                    recordedData = new recordeddata(date, name);
                    mDb.recordeddatadao().insertData(recordedData);
                }
                else{
                    File f = new File(mFileName);
                    String name = f.getName();
                    Log.e(LOG_TAG, name);
                    Date date = new Date(f.lastModified());
                    recordedData = new recordeddata(date, name);
                    mDb.recordeddatadao().insertData(recordedData);
                }
            }
        });

    }
    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int REQUEST_AUDIO_CAPTURE = 0;
    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }
    private void dispatchTakeAudioIntent() {
        Intent takeAudioIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (takeAudioIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeAudioIntent, REQUEST_AUDIO_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
//            mVideoView.setVideoURI(videoUri);
        }
        if(requestCode== REQUEST_AUDIO_CAPTURE && requestCode == RESULT_OK){
            Uri audioUri = intent.getData();
        }
    }



    private static final int READ_REQUEST_CODE = 42;
    private View.OnClickListener clicker = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.record_button: {
                    onRecord(mStartRecording);
                    if (mStartRecording) {
                        button_record.setImageResource(R.drawable.image1);
                    } else {
                        button_record.setImageResource(R.drawable.icon1);
                    }
                    mStartRecording = !mStartRecording;
                    return;
                }
                case R.id.recordings_button:{
                    Context context =v.getContext();
                    Intent start_files_activity = new Intent(context,files_activity.class);
                    Log.d(LOGTAG,"activity started");
                    new gettingRecordedData().execute(start_files_activity);
                    return;
                }
                case R.id.import_button:{
                    Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                    Log.d(LOGTAG,"activity started");
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("audio/*");
                    startActivityForResult(intent, READ_REQUEST_CODE);

               }
            }
        }

        //public RecordButton(Context ctx) {
        //    super(ctx);
        //    setText("Start recording");
        //    setOnClickListener(clicker);
        //}
        //}


    };
    String LOGTAG ="LOGTAG";

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("name_recordings_manually")){
            mSaver = sharedPreferences.getBoolean(key,true);
        }
    }


    private class delete extends AsyncTask<String, Void, Boolean> {
       @Override
       protected Boolean doInBackground(String... strings) {
            File f0 = new File(outputfile);
            File f1 = new File(mFileName);
            boolean b = f0.delete();
           try {
               FileUtils.deleteDirectory(f1);
           } catch (IOException e) {
               e.printStackTrace();
           }
            return b;
       }
    }

//    private class save extends AsyncTask<String, Void, Void> {
//        recordeddata recordedData;
//
//        @Override
//        protected Void doInBackground(String... strings) {
//            File from = new File(outputfile);
//            File to = new File(mFileName, strings[0]);
//            from.renameTo(to);
//            File from1 =new File(mFileName);
//            File to1 =  new File(parentdir,strings[0]);
//            String name = to1.getName();
//            Date date =  new Date(to1.lastModified());
//            recordedData = new recordeddata(date,name);
//
//            mDb.recordeddatadao().insertData(recordedData);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//        }
//    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.main_activity_settings){
            Intent startSettingsActivity = new Intent(this,settings_activity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void defaultSetup(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        mSaver = sharedPreferences.getBoolean("name_recordings_manually",true);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private class gettingRecordedData extends AsyncTask<Intent, Void, String[]> {
        Intent intent_transfered;
        @Override
        protected String[] doInBackground(Intent... intents) {
            int k=0;
            intent_transfered = intents[0];
            File yourDir = new File(parentdir.getAbsolutePath());
            File childfile[] = yourDir .listFiles();
            String[] recordedData = new String[childfile.length];
            for (k=0;k<childfile.length;k++) {
            recordedData[k] = childfile[k].getName();
            }
        return recordedData;
         }

         @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            intent_transfered.putExtra("recorded_data",strings);
            startActivity(intent_transfered);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        int j = i;
        outState.putInt("no_of_recordings",j);
    }
//    public  boolean isStoragePermissionGranted() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_GRANTED) {
//                //Log.v(TAG,"Permission is granted");
//                return true;
//            } else {
//
//                //Log.v(TAG,"Permission is revoked");
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_PERMISSION);
//                return false;
//            }
//        }
//        else { //permission is automatically granted on sdk<23 upon installation
//            //Log.v(TAG,"Permission is granted");
//            return true;
//        }
//    }
    


}

