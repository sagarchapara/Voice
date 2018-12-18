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

import com.example.acer.voice.recordeddatabase.recordeddata;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class fileAdapterclass extends RecyclerView.Adapter<fileAdapterclass.fileViewHolder> {
    private int mNumberItems;
    public Context onlongclickcontext;
    public Context mContext;
   // String[] recordeddata;
    List<recordeddata> mrecordeddata;
    final private ListItemClickListner mOnclickListener;
    public interface ListItemClickListner{
        void onListItemClick(int clickedItemIndex);
    }

    public fileAdapterclass(Context context,ListItemClickListner listner){
        mOnclickListener = listner;
        mContext=context;
    }

    public void setRecordeddata(List<recordeddata> recordeddata) {
        this.mrecordeddata = recordeddata;
        mNumberItems = mrecordeddata.size();
        notifyDataSetChanged();
    }

    @Override
    public fileViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutidforfileitem = R.layout.file_list_item;
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
            listFileViewHolder =(TextView) itemView.findViewById(R.id.file_item_number);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int p=getLayoutPosition();
                    Context context = view.getContext();
                    onlongclickcontext = context;
                    Intent checkboxintent = new Intent(context,checkboxActivity.class);
//                    checkboxintent.putExtra("checkboxactivity",recordeddata);
//                    context.startActivity(checkboxintent);
                    new sendcheckbox().execute(checkboxintent);

                    Log.d("onclick","LongClick: " + p);
                    return true;// returning true instead of false, works for me
                }
            });
        }
        void bind(int listindex){
            listFileViewHolder.setText(mrecordeddata.get(listindex).getName());
            Log.d("onclick","LongClick: " +mrecordeddata.get(listindex).getName() );
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

//            intent.putExtra("checkboxactivity", mrecordeddata);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onlongclickcontext.startActivity(intent);

        }
    }
}
