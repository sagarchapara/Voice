////package com.example.acer.voice;
////
////import android.media.AudioManager;
////import android.media.MediaPlayer;
////import android.net.Uri;
////import android.os.AsyncTask;
////
////import java.io.File;
////import java.io.IOException;
////
////public class random {
////    public class play extends AsyncTask<File,Void,Void> {
////        MediaPlayer mediaPlayer;
////        @Override
////        protected Void doInBackground(File... files) {
////
////            Uri myUri = Uri.fromFile(new File(files[0].getAbsolutePath())); // initialize Uri here
////            mediaPlayer= new MediaPlayer();
////            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
////            try {
////                mediaPlayer.setDataSource(getApplicationContext(), myUri);
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////            try {
////                mediaPlayer.prepare();
////            } catch (IOException e) {
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
//}
