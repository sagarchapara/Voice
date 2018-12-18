package com.example.acer.voice.signal_processing;

public class istft {
    Complex [][] stft;
    int wlen;
    int hop;
    int nfft;
    int fs;

    public istft(Complex[][] stft, int wlen, int hop, int nfft, int fs) {
        this.stft = stft;
        this.wlen = wlen;
        this.hop = hop;
        this.nfft = nfft;
        this.fs = fs;
    }
    public double[] HanningWindow(double[] signal_in, int pos, int size)
    {
        for (int i = pos; i < pos + size; i++)
        {
            int j = i - pos; // j = index into Hann window function
            signal_in[i] = (double) (signal_in[i] * 0.5 * (1.0 - Math.cos(2.0 * Math.PI * j / size)));
        }
        return signal_in;
    }
    int coln;
    int xlen ;
    double[] x ;
    double[] t;
    int indx ;
    InplaceFFT ifft = new InplaceFFT();
    public double HanningWindow( int size)
    {   double d =0;
        for (int i = 0; i < size; i++)
        {
            // j = index into Hann window function
            double c=  (0.54 -0.46* Math.cos(2.0 * Math.PI * i / size));
            d+=c*c;
        }
        return d;
    }

    public double[] scalingandgenaratingtimesignal(){

        coln=stft[0].length;
        xlen = wlen + (coln-1)*hop;
        x = new double[xlen];
        t =new double[xlen];
        indx = 0;
        for(int i=0;i<xlen;i++){
            x[i]=0;
        }
        if((nfft%2)!=0){
            for(int i=0;i<coln;i++){
                Complex[] X = new Complex[2*stft.length-1];
                X[0] = stft[0][i];
                for(int j=1;j<stft.length;j++){
                    X[j] = stft[j][i];
                    X[2*stft.length-1-j] =stft[j][i];
                }
                InplaceFFT ifft = new InplaceFFT();
                double[] xprim = ifft.ifft(X);
                double[] xprim1 = new double[wlen];
                for(int ik=0;ik<wlen;ik++){
                    xprim1[ik] = (double)xprim[ik]*(0.54 -0.46* Math.cos(2.0 * Math.PI * ik / wlen));
                }
                for(int j=indx;j<indx+wlen;j++){
                    x[j]+=xprim1[j-indx];
                }
                indx+=hop;
            }
        }
        else {
            for(int i=0;i<coln;i++){
                Complex[] X = new Complex[2*stft.length-2];
                X[0] = stft[0][i];
                for(int j=1;j<stft.length;j++){
                    X[j] = stft[j][i];
                    if(j!=stft.length-1)
                        X[2*stft.length-2-j] =stft[j][i].conjugate();
                }
                FFT ifft = new FFT();
                double[] xprim = ifft.ifft(X);
                double[] xprim1 = new double[wlen];
                for(int ik=0;ik<wlen;ik++){
                    xprim1[ik] = xprim[ik]*(0.54 -0.46* Math.cos(2.0 * Math.PI * ik / wlen));
                    x[ik+indx]=x[ik+indx]+xprim1[ik];
                }

                // for(int j=indx;j<indx+wlen;j++){
                //     x[j]+=xprim1[j-indx];
                // }
                indx+=hop;
            }
        }






        double w0 = HanningWindow(wlen);
        for (int i=0;i<xlen;i++){
            x[i] =x[i]*(double)(hop/w0);
            //System.out.println(w0+"  fft");
        }
        for(int j=0;j<xlen;j++){
            t[j] = j/fs;
        }
        return x;

    }
}
