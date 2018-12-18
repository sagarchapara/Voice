package com.example.acer.voice.signal_processing;

public class filter {

    public double[] Filter(double[] b, double[] a, double[] x) {
        int nx = x.length;
        int na = a.length;
        int nb = b.length;

        double[] y = new double[nx];
        for (int k = 0; k < nx; k++) {
            y[k] = 0;
            for (int i = 0; i < nb; i++) {
                if (k - i >= 0 && k - i < nx) {
                    y[k] += b[i] * x[k - i];
                }
            }
            for (int i = 1; i < na; i++) {
                if (k - i >= 0 && k - i < nx) {
                    y[k] -= a[i] * y[k - i];
                }
            }
            if (Math.abs(a[0] - 1) > 1.e-9) {
                y[k] /= a[0];
            }

        }
        return y;
    }
}
