package com.example.acer.voice;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.acer.voice.recordeddatabase.recordedDatadatabase;
import com.example.acer.voice.recordeddatabase.recordeddata;

import java.util.List;

public class recordingsFragment extends Fragment implements fileAdapterclass.ListItemClickListner {
    public recordingsFragment() {
    }
    private fileAdapterclass fileAdapter;
    private RecyclerView mFilesList;
    private static int num_list_items;
    //    String[] mRecordedData;
    List<recordeddata> mRecordedData;
    private recordedDatadatabase mDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View recordings_view = inflater.inflate(R.layout.activity_files,container,false);
        mFilesList = (RecyclerView)recordings_view.findViewById(R.id.rv_files);
        LinearLayoutManager layoutManager = new LinearLayoutManager(recordings_view.getContext(),LinearLayoutManager.VERTICAL,false);
        mFilesList.setLayoutManager(layoutManager);
        mFilesList.setHasFixedSize(false);
        fileAdapter =new fileAdapterclass(recordings_view.getContext(),this);
        mFilesList.setAdapter(fileAdapter);
        final FragmentActivity activity = getActivity();
        mDb = recordedDatadatabase.getsInstance(activity.getApplicationContext());
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<recordeddata> recordeddata=mDb.recordeddatadao().loadAlldata();
                mRecordedData=recordeddata;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileAdapter.setRecordeddata(recordeddata);
                    }
                });

            }
        });
        return recordings_view;
    }
    private Toast mToast;
    int click;
    Context onclickcontext;
    @Override
    public void onListItemClick(int clickedItemIndex) {
        click = clickedItemIndex;
        onclickcontext = this.getContext();
        Intent final_folderintent = new Intent(this.getContext(),final_folder_activity.class);
//        final_folderintent.putExtra("folderclicked",mRecordedData[clickeditemindex]);
        new final_folder().execute(final_folderintent);
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

    @Override
    public void onResume() {
        super.onResume();
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<recordeddata> recordeddata=mDb.recordeddatadao().loadAlldata();
                mRecordedData=recordeddata;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileAdapter.setRecordeddata(recordeddata);
                    }
                });

            }
        });
    }
}



