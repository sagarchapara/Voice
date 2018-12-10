package com.example.acer.voice;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.acer.voice.fileAdapterclass.ListItemClickListner;
import com.example.acer.voice.recordeddatabase.recordedDatadatabase;
import com.example.acer.voice.recordeddatabase.recordeddata;

import java.util.List;

public class files_activity extends AppCompatActivity implements fileAdapterclass.ListItemClickListner {

    private fileAdapterclass fileAdapter;
    private RecyclerView mFilesList;
    private static int num_list_items;
//    String[] mRecordedData;
    List<recordeddata> mRecordedData;
    private recordedDatadatabase mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
//        mRecordedData = intent.getStringArrayExtra("recorded_data");
//        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                mRecordedData =mDb.recordeddatadao().loadAlldata();
//            }
//        });

//        num_list_items = mRecordedData.size();
        mFilesList = (RecyclerView)findViewById(R.id.rv_files);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mFilesList.setLayoutManager(layoutManager);
        mFilesList.setHasFixedSize(false);
        fileAdapter =new fileAdapterclass(this,this);
        mFilesList.setAdapter(fileAdapter);
        mDb = recordedDatadatabase.getsInstance(getApplicationContext());
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<recordeddata> recordeddata=mDb.recordeddatadao().loadAlldata();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileAdapter.setRecordeddata(recordeddata);
                    }
                });

            }
        });
    }
    private Toast mToast;
    int click;
    Context onclickcontext;

    @Override
    protected void onResume() {
        super.onResume();

        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<recordeddata> recordeddata=mDb.recordeddatadao().loadAlldata();
                mRecordedData = recordeddata;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileAdapter.setRecordeddata(recordeddata);
                    }
                });

            }
        });

    }

    @Override
    public void onListItemClick(int clickeditemindex){
//        if(mToast!=null){
//            mToast.cancel();
//        }
//        String toastMessage = "Item #"+clickeditemindex+" clicked";
//       mToast = Toast.makeText(this,toastMessage,Toast.LENGTH_LONG);
//       mToast.show();
        click = clickeditemindex;
        onclickcontext = this;
        Intent final_folderintent = new Intent(this,final_folder_activity.class);
//        final_folderintent.putExtra("folderclicked",mRecordedData[clickeditemindex]);
        new final_folder().execute(final_folderintent);

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
    private class final_folder extends AsyncTask<Intent, Void, Void> {
        Intent intent;

        @Override
        protected Void doInBackground(Intent... intents) {
            intent = intents[0];
            intent.putExtra("final_folder_intent",/*mRecordedData[click]*/ mRecordedData.get(click).getName());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onclickcontext.startActivity(intent);

        }
    }
}
