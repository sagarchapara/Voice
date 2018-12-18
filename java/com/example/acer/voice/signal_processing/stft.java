package com.example.acer.voice.signal_processing;



import java.lang.Math;
public class stft {
    int wlen;
    double hop;
    int nfft;
    int fs;
    double[] x;
    int xlen;
    double [] t;
    double[] f;
    public stft(double[] x,int wlen,double hop ,int nfft,int fs){
        this.wlen = wlen;
        this.x =x;
        this.hop =hop;
        this.nfft =nfft;
        this.fs =fs;
        this.xlen =x.length ;

    }
    public double myRound(double val) {
        if (val < 0) {
            return Math.ceil(val);
        }
        return Math.floor(val);
    }
    public Complex[][] setting(){
        Complex[][] stft;
        double nff = (double)(1+nfft)/2 ;
        double fix =1+ (double)(xlen-wlen)/hop;
        System.out.println(fix+"b");
        int  rown = (int)Math.ceil(nff);
        int  coln = (int)myRound(fix);
        stft = new Complex[(int)rown][(int)coln];
        int indx = 0;
        for(int i=0;i< coln;i++){
            FFT fft1 = new FFT();
            double[] a =new double[wlen];
            Complex xwy[] = new Complex[wlen];
            for(int xyz =0;xyz<wlen;xyz++){
                a[xyz] =(double)x[indx+xyz]*(0.54 -0.46* Math.cos(2.0 * Math.PI * xyz / wlen));
                xwy[xyz] = new Complex(a[xyz],0);
            }
            Complex[] X = fft1.fft(xwy);
            for(int k=0;k<rown;k++){
                stft[k][i]=X[k];
            }
            indx=indx+(int)hop;
        }
        t = new double[coln];
        f = new double[rown];
        t[0] =(double)wlen/(2*fs);
        for(int i=1;i<coln;i++){
            t[i]=t[i-1]+(double)(hop/fs);
        }
        f[0] =0;
        for(int i=1;i<rown;i++){
            f[i]=f[i-1]+(double)(i*(double)fs/nfft);
        }
        return stft;
    }

    public double[] getF() {
        return f;
    }

    public double[] getT() {
        return t;
    }
}



