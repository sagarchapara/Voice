package com.example.acer.voice.signal_processing;

public class InplaceFFT {

    // compute the FFT of x[], assuming its length is a power of 2
    public  Complex[] fft(Complex[] x) {

        // check that length is a power of 2
        int n = x.length;
        if (Integer.highestOneBit(n) != n) {
            System.out.println(n);
            throw new RuntimeException("n is not a power of 2");
        }

        // bit reversal permutation
        int shift = 1 + Integer.numberOfLeadingZeros(n);
        for (int k = 0; k < n; k++) {
            int j = Integer.reverse(k) >>> shift;
            if (j > k) {
                Complex temp = x[j];
                x[j] = x[k];
                x[k] = temp;
            }
        }

        // butterfly updates
        for (int L = 2; L <= n; L = L + L) {
            for (int k = 0; k < L / 2; k++) {
                double kth = -2 * k * Math.PI / L;
                Complex w = new Complex(Math.cos(kth), Math.sin(kth));
                for (int j = 0; j < n / L; j++) {
                    Complex tao = w.times(x[j * L + k + L / 2]);
                    x[j * L + k + L / 2] = x[j * L + k].minus(tao);
                    x[j * L + k] = x[j * L + k].plus(tao);
                }
            }

        }
        return x;
    }
    public  double[] ifft(Complex[] x) {
        int N = x.length;
        Complex[] y = new Complex[N];
        double[] z = new  double[N];

        // take conjugate
        for (int i = 0; i < N; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < N; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by N
        for (int i = 0; i < N; i++) {
            y[i] = y[i].times(new Complex(1.0 / N,0));
            z[i] = y[i].re();
            //System.out.println(z[i]+"f");

        }

        return z;

    }

}