package com.example.acer.voice;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.acer.voice.recordeddatabase.recordeddata;
import com.example.acer.voice.recordeddatabase.recordedvideo;

import java.util.List;

public class videos_adapter extends RecyclerView.Adapter<videos_adapter.fileViewHolder> {
    private int mNumberItems;
    public Context onlongclickcontext;
    public Context mContext;
    List<recordedvideo> mrecordedvideo;
    final private ListItemClickListner mOnclickListener;
    public videos_adapter(Context context,ListItemClickListner listner) {
        mOnclickListener = listner;
        mContext=context;
    }
    public interface ListItemClickListner{
        void onListItemClick(int clickedItemIndex);
    }
    public void setRecordeddata(List<recordedvideo> recordeddata) {
        this.mrecordedvideo = recordeddata;
        mNumberItems = mrecordedvideo.size();
        Log.e("a",mNumberItems+"");
        notifyDataSetChanged();
    }
    @Override
    public fileViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutidforfileitem = R.layout.videos_layout_file;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldattachtoparentimmediately = false;
        View view = inflater.inflate(layoutidforfileitem,viewGroup,shouldattachtoparentimmediately);
        fileViewHolder viewHolder = new fileViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull fileViewHolder holder, int i) {
        holder.bind(i);
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }
    class fileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView listFileViewHolder;

        public fileViewHolder(@NonNull View itemView) {
            super(itemView);
            listFileViewHolder =(TextView) itemView.findViewById(R.id.video_layout);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
//                    int p=getLayoutPosition();
//                    Context context = view.getContext();
//                    onlongclickcontext = context;
//                    Intent checkboxintent = new Intent(context,checkboxActivity.class);
//                    checkboxintent.putExtra("checkboxactivity",recordeddata);
//                    context.startActivity(checkboxintent);
//                    new fileAdapterclass.sendcheckbox().execute(checkboxintent);
//
//                    Log.d("onclick","LongClick: " + p);
//                    return true;// returning true instead of false, works for me
                    return  false;
                }
            });
        }
        void bind(int listindex){
            listFileViewHolder.setText(mrecordedvideo.get(listindex).getName());
            Log.d("onclick","LongClick: " +listindex);
        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnclickListener.onListItemClick(clickedPosition);
        }


    }
}
