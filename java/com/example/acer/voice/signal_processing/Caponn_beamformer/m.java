package com.example.acer.voice.signal_processing.Caponn_beamformer;

import com.example.acer.voice.signal_processing.AudioSampleReader;
import com.example.acer.voice.signal_processing.AudioSampleWriter;
import com.example.acer.voice.signal_processing.Complex;
import com.example.acer.voice.signal_processing.PostFilter2;
import com.example.acer.voice.signal_processing.beamform;
import com.example.acer.voice.signal_processing.istft;
import com.example.acer.voice.signal_processing.stft;

import java.io.File;
import java.io.IOException;

import java.io.*;
import javax.sound.sampled.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

public class m {
//    audioread



    // Display information about the wav file

    public static double getMaxValue(double[] array) {
        double maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
            }
        }
        return maxValue;
    }

    // getting the miniumum value
    public static double getMinValue(double[] array) {
        double minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        return minValue;
    }

    public double[] beam() throws IOException {
        File a = new File("open_ground_2_mic.wav");
        AudioSampleReader sampleReader = null;
        try {
            sampleReader = new AudioSampleReader(a);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AudioFormat format = sampleReader.getFormat();
        long nbSamples = sampleReader.getSampleCount();
        System.out.println("nbChannel=" + format.getChannels());
        System.out.println("frameRate=" + format.getFrameRate());
        System.out.println("sampleSize=" + format.getSampleSizeInBits());
        double[] samples = new double[2*(int)nbSamples];
        sampleReader.getInterleavedSamples(0, nbSamples, samples);
        double[] y1 = new double[(int)nbSamples];
        double[] y2 = new double[(int)nbSamples];
        y1 = sampleReader.getChannelSamples(0,samples,y1);
        y2 = sampleReader.getChannelSamples(1,samples,y2);



        int fs=(int)format.getFrameRate();
        // System.out.println();
        System.out.println(fs);
        double d= 0.145;
        int wlen = 4096;
        int hop =wlen/4;
        int nfft=4096;
        stft stft1 = new stft(y1,wlen,hop,nfft,fs);
        stft stft2 = new stft(y2,wlen,hop,nfft,fs);
        Complex[][] S1 =stft1.setting();
        // for(int i=0;i<S1[1].length;i++){
        //             System.out.println(S1[0][i]);
        //          }
        double[] t1 = stft1.getT();
        double[] f1 = stft1.getF();
        Complex[][] S2 = stft2.setting();
        double[] t2 = stft2.getT();
        double[] f2 = stft2.getF();


        double[][] Rxfm_temp = new double[2][2];
        Rxfm_temp[0][0] = 0;
        Rxfm_temp[0][1] = 0;
        Rxfm_temp[1][0] = 0;
        Rxfm_temp[1][1] = 0;


        Complex[][] d90f = new Complex[2][1];
        d90f[0][0] =new Complex(1,0);
        d90f[1][0] =new Complex(1,0);

        double[][] b = new double[2][2];
        Complex[][] s_theta_tfm902=new Complex[f1.length][t1.length];
        Complex[][] xfm = new Complex[2][t1.length];
        double gamma = 0.1;
        int le = t1.length;

        for(int f=0;f<f1.length;f++){
            for(int i=0;i<2;i++){
                if(i==0) {
                    for (int j = 0; j < t1.length; j++) {
                        xfm[i][j] = S1[f][j];
                    }
                }
                else{
                    for (int j = 0; j < t1.length; j++) {
                        xfm[i][j] = S2[f][j];
                    }
                }
            }
            Complex[][] s_theta = new Complex[1][t1.length];
            s_theta = new beamform(xfm,le,d90f,gamma).computingstheta();
            for(int k=0;k<t1.length;k++){
                s_theta_tfm902[f][k] = s_theta[0][k];
            }
        }
        System.out.println("1");
        double[] x_final = new istft(s_theta_tfm902,wlen,hop,nfft,fs).scalingandgenaratingtimesignal();
        double[][ ] dinv2 = new double[f1.length][t1.length];
        for(int i=0;i<f1.length;i++){
            for(int j=0;j<t1.length;j++){
                dinv2[i][j]=(double)(1.0/2)*((S1[i][j].minus(s_theta_tfm902[i][j])).abs()+(S2[i][j].minus(s_theta_tfm902[i][j])).abs());
            }
        }
        System.out.println("2");
        Complex[][] s_final2 = new PostFilter2(s_theta_tfm902,dinv2).postfilter();
        double[][ ] F = new double[f1.length][t1.length];
        for(int i=0;i<f1.length;i++){
            for(int j=0;j<t1.length;j++){
                F[i][j]=(s_final2[i][j].abs())/(s_final2[i][j].abs()+dinv2[i][j]+0.001);
                s_final2[i][j] =s_final2[i][j].times(new Complex(F[i][j],0));
            }
        }
        System.out.println("3");
        int xlen = y1.length;
        double[] x_final2 = new istft(s_final2,wlen,hop,nfft,fs).scalingandgenaratingtimesignal();
        double[] x_final3 = new double[xlen];
        for(int i=0;i<x_final2.length;i++){
            x_final3[i] = x_final2[i];
        }
        double di =Math.max(getMaxValue(x_final3),Math.abs(getMinValue(x_final3)));
        for(int i=0;i<x_final3.length;i++){
            x_final3[i] = x_final3[i]/di;
        }
        return x_final;







    }
    public static void main(String[] args){
        m audio = new m();
        try{
            File a = new File("Demo1_received_mic_signal.wav");
            AudioSampleReader sampleReader = new AudioSampleReader(a);
            double[] samples =audio.beam();
            System.out.println(samples.length+"a");
            // FFT fft = new FFT();
            // Complex[] s = new Complex[8];
            // s[0] = new Complex(0,0);
            // s[1] = new Complex(1,0);
            // s[2] = new Complex(2,0);
            // s[3] = new Complex(3,0);
            // s[4] = new Complex(4,0);
            // s[5] = new Complex(5,0);
            // s[6] = new Complex(6,0);
            // s[7] = new Complex(7,0);
            // Complex[] sout = fft.fft(s);
            File outFile = new File("b.wav");
            AudioSampleWriter audioWriter = new AudioSampleWriter(outFile, new AudioFormat(44100, 16, 1, true, true), AudioFileFormat.Type.WAVE);
            audioWriter.writeInterleavedSamples(samples,samples.length);
            audioWriter.close();
            // for(int i=0;i<sout.length;i++){
            //   System.out.println(sout[i]);
            // }
        }
        catch(Exception e){
            // System.out.println(e);
            e.printStackTrace();
        }
    }



}
