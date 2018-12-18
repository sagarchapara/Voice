package com.example.acer.voice.signal_processing;

public class beamform {
    Complex[][] xm;
    int len;
    Complex[][] d90f = new Complex[2][1];
    double gamma;

    public beamform(Complex[][] xm, int len, Complex[][] d90f, double gamma) {
        this.xm = xm;
        this.len = len;
        this.d90f = d90f;
        this.gamma = gamma;
    }
    Complex[][] Rxfm_avg = new Complex[2][2];


    public void rxfm(){
        double i=0;
        for(int j=0;j<xm[0].length;j++){
            i+=xm[0][j].abs();
        }
        Rxfm_avg[0][0] = new Complex((double)i/len,0);
        i=0;
        for(int j=0;j<xm[0].length;j++){
            i+=xm[1][j].abs();
        }
        Rxfm_avg[1][1] = new Complex((double)i/len,0);
        Complex c = new Complex(0,0);
        for(int j=0;j<xm[0].length;j++){
            c=c.plus(xm[0][j].times(xm[1][j].conjugate()));
        }
        Rxfm_avg[0][1] = c.divides(new Complex(len,0));
        Rxfm_avg[1][0] = c.conjugate().divides(new Complex(len,0));
    }
    Complex[][] a =new Complex[2][2];
    Complex[][] b =new Complex[2][2];
    public void computingaandtakinginverse(){
        a[0][0] = Rxfm_avg[0][0].plus(new Complex(gamma,0));
        a[0][1] = Rxfm_avg[0][1];
        a[1][0] = Rxfm_avg[1][0];
        a[1][1] = Rxfm_avg[1][1].plus(new Complex(gamma,0));
        Complex det;
        det = a[0][0].times(a[1][1]);
        det =det.minus(a[0][1].times(a[1][0]));
        b[0][0] = a[1][1].divides(det);
        b[0][1] = (a[0][1].divides(det)).scale(-1);
        b[1][0] = (a[1][0].divides(det)).scale(-1);
        b[1][1] = a[0][0].divides(det);
    }
    Complex[][] w90f2 = new Complex[2][1];
    public void computingw90f2(){
        Complex c = d90f[0][0].times(b[0][0].plus(b[1][0]));
        Complex d = d90f[1][0].times(b[0][1].plus(b[1][1]));
        Complex e = (c.times(d90f[0][0])).plus(d.times(d90f[1][0]));
        w90f2[0][0] = (b[0][0].times(d90f[0][0])).plus((b[0][1].times(d90f[1][0])));
        w90f2[1][0] = (b[1][0].times(d90f[0][0])).plus((b[1][1].times(d90f[1][0])));
    }

    public Complex[][] computingstheta(){
        Complex[][] s_theta;
        double i=0;
        for(int j=0;j<xm[0].length;j++){
            i+=xm[0][j].abs();
        }
        Rxfm_avg[0][0] = new Complex((double)i/len,0);
        i=0;
        for(int j=0;j<xm[0].length;j++){
            i+=xm[1][j].abs();
        }
        Rxfm_avg[1][1] = new Complex((double)i/len,0);
        Complex c = new Complex(0,0);
        for(int j=0;j<xm[0].length;j++){
            c=c.plus(xm[0][j].times(xm[1][j].conjugate()));
        }
        Rxfm_avg[0][1] = c.divides(new Complex(len,0));
        Rxfm_avg[1][0] = c.conjugate().divides(new Complex(len,0));


        a[0][0] = Rxfm_avg[0][0].plus(new Complex(gamma,0));
        a[0][1] = Rxfm_avg[0][1];
        a[1][0] = Rxfm_avg[1][0];
        a[1][1] = Rxfm_avg[1][1].plus(new Complex(gamma,0));
        Complex det;
        det = a[0][0].times(a[1][1]);
        det =det.minus(a[0][1].times(a[1][0]));
        b[0][0] = a[1][1].divides(det);
        b[0][1] = (a[0][1].divides(det)).scale(-1);
        b[1][0] = (a[1][0].divides(det)).scale(-1);
        b[1][1] = a[0][0].divides(det);



        Complex c1 = (b[0][0].times(new Complex(d90f[0][0].abs(),0))).plus(d90f[0][0].times(d90f[1][0].conjugate().times(b[1][0])));
        Complex d1 = (b[1][1].times(new Complex(d90f[1][0].abs(),0))).plus(d90f[1][0].times(d90f[0][0].conjugate().times(b[0][1])));
        Complex e = c1.plus(d1);
        w90f2[0][0] = (b[0][0].times(d90f[0][0])).plus((b[0][1].times(d90f[1][0]))).divides(e);
        w90f2[1][0] = (b[1][0].times(d90f[0][0])).plus((b[1][1].times(d90f[1][0]))).divides(e);



        s_theta = new Complex[1][xm[0].length];
        for(int ik=0;ik<xm[0].length;ik++){
            s_theta[0][ik] = ((w90f2[0][0].conjugate()).times(xm[0][ik])).plus((w90f2[1][0].conjugate()).times(xm[1][ik]));
        }
        return  s_theta;
    }

}
