package com.example.acer.voice;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class final_folder_adapter extends RecyclerView.Adapter<final_folder_adapter.fileViewHolder>{
    private int mNumberItems;
    public Context onlongclickcontext;
    String[] recordeddata;
    String foldername ;
    final private ListItemClickListner mOnclickListener;
    public final_folder_adapter(String[] recordedData,int numberofitems,ListItemClickListner listner,String foldername){
        recordeddata=recordedData;
        mNumberItems = numberofitems;
        mOnclickListener = listner;
        this.foldername=foldername;
    }
    public interface ListItemClickListner{
        void onListItemClick(int clickedItemIndex);
    }
    @Override
    public fileViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();

        int layoutidforfileitem = R.layout.final_file_list;

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldattachtoparentimmediately = false;

        View view = inflater.inflate(layoutidforfileitem,viewGroup,shouldattachtoparentimmediately);
        fileViewHolder viewHolder = new fileViewHolder(view);


        return viewHolder;
    }

    public void setRecordeddata(String[] recordeddata) {
        this.recordeddata = recordeddata;
        notifyDataSetChanged();
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
            listFileViewHolder =(TextView) itemView.findViewById(R.id.file_item_number);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int p=getLayoutPosition();
                    Context context = view.getContext();
                    onlongclickcontext = context;
                    Intent last_file_intent = new Intent(context,last_file_activity.class);
//                    checkboxintent.putExtra("checkboxactivity",recordeddata);
//                    context.startActivity(checkboxintent);
                    new final_folder_adapter.sendcheckbox().execute(last_file_intent);

                    Log.d("onclick","LongClick: " + p);
                    return true;// returning true instead of false, works for me
                }
            });
        }
        void bind(int listindex){
            listFileViewHolder.setText(recordeddata[listindex]);
        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnclickListener.onListItemClick(clickedPosition);
        }


    }
    private class sendcheckbox extends AsyncTask<Intent, Void, Void> {
        Intent intent;

        @Override
        protected Void doInBackground(Intent... intents) {
            intent = intents[0];
            intent.putExtra("last_folder", foldername);
            intent.putExtra("last_folder_files",recordeddata);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onlongclickcontext.startActivity(intent);

        }
    }
}