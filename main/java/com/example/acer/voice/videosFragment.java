package com.example.acer.voice;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.acer.voice.recordeddatabase.recordedDatadatabase;
import com.example.acer.voice.recordeddatabase.recordedvideo;

import java.util.List;

public class videosFragment extends Fragment implements videos_adapter.ListItemClickListner {
    public videosFragment() {
    }
    private recordedDatadatabase mDb;
    RecyclerView mFilesList;
    videos_adapter videoadapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View videos_view = inflater.inflate(R.layout.videos_fragment,container,false);
        mFilesList = (RecyclerView)videos_view.findViewById(R.id.videosfragment);
        mDb =recordedDatadatabase.getsInstance(this.getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(videos_view.getContext(),LinearLayoutManager.VERTICAL,false);
        mFilesList.setLayoutManager(layoutManager);
        mFilesList.setHasFixedSize(false);
        videoadapter =new videos_adapter(getContext(),this);
        mFilesList.setAdapter(videoadapter);
        final FragmentActivity activity = getActivity();
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<recordedvideo> recordedvideos = mDb.recordedvideodao().loadAlldata();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        videoadapter.setRecordeddata(recordedvideos);
                    }
                });
            }

        });
        return videos_view;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

    }
}
