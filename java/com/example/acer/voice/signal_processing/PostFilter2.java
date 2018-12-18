package com.example.acer.voice.signal_processing;


public class PostFilter2 {
    Complex[][] S;
    double[][] sig;

    public double[] Filter(double[] b, double[] a, double[] x) {
        int nx = x.length;
        //System.out.println("and"+ nx);
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
    public double[] getColumn(double[][] array, int index){
        double[] column = new double[array[0].length]; // Here I assume a rectangular 2D array!
        for(int i=0; i<column.length; i++){
            column[i] = array[i][index];
        }
        return column;
    }
    public PostFilter2(Complex[][] s, double[][] sig) {
        S = s;
        this.sig = sig;
    }
    double[][] SM;

    public void setSM(){
        SM = new double[S.length][S[0].length];
        for(int i=0;i<S.length;i++){
            for(int j=0;j<S[0].length;j++){
                SM[i][j] =S[i][j].abs();
            }
        }
    }
    public double[][] upsample(double[][] a,int p){
        int len = a.length;
        int bred =a[0].length;
        double[][] b = new double[(p)*len][bred];
        for(int j=0;j<bred;j++){
            for(int i=0;i<(p)*len;i++){
                if(i%p==0)
                    b[i][j] = a[i/p][j];
                else
                    b[i][j] = 0;
            }
        }
        return b;
    }


    public Complex[][] postfilter(){
        SM = new double[S.length][S[0].length];
        for(int i=0;i<S.length;i++){
            for(int j=0;j<S[0].length;j++){
                SM[i][j] =S[i][j].abs();
            }
        }
        int  L = 8;
        int W =16;
        double alpha = 0.5;
        double eps = 10^(-8);
        int  lam = 1;
        Complex Sout[][] = S;
        int Blen=0;
        int Bwdt=0;
        double[][] Bnorm=null;
        double[][] norm2=null;
        for(int i=0;i<= S.length-2*W-1;i=i+W){
            for(int j=0;j<=S[0].length-L-2;j=j+L){
                double[][] B = new double[W][L];
                double[][] sigB = new double[W][L];
                for(int a=0;a<W;a++){
                    for(int b=0;b<L;b++){
                        B[a][b] =  SM[a+i][b+j];
                        sigB[a][b] = sig[a+i][b+j];
                    }
                }
                double invNorm = 0;
                for(int n=0;n<=3;n++){
                    int len = (int)Math.pow(2,n);
                    int wdt = (int)Math.pow(2,4-n);
                    double[] ones = new double[wdt];
                    for(int c=0;c<wdt;c++){
                        ones[c] =1;
                    }
                    double[] array = new double[1];
                    array[0]=1;
                    double[][] norm = new double[W][L];
                    for(int pspk =0;pspk<L;pspk++){
                        double[] w =new double[W];
                        for(int ppp=0;ppp<W;ppp++){
                            w[ppp] = B[ppp][pspk];
                        }
                        double[] z =Filter(ones,array,w);
                        //System.out.println(B.length);
                        for(int psp=0;psp<W;psp++){
                            norm[psp][pspk] = z[psp];
                        }
                    }


                    double[][] norm1 =new double[(int)W/wdt][L];
                    int f=0;
                    for(int e=wdt-1;e<W;e=e+wdt){
                        norm1[f] = norm[e];
                        f++;
                    }
                    double[] ones1 = new double[len];
                    for(int c=0;c<len;c++){
                        ones1[c] =1;
                    }
                    // double[] g = Filter(ones,array,norm1[0]);
                    norm2 = new double[norm1[0].length][norm1.length];
                    for(int pspk =0;pspk<norm1.length;pspk++){
                        double[] z =Filter(ones1,array,norm1[pspk]);
                        for(int psp=0;psp<norm1[0].length;psp++){
                            norm2[psp][pspk] = z[psp];
                        }
                    }
                    double[][] norm3 = new double[(int)(norm1[0].length/len)][norm1.length];
                    int h=0;
                    double cNorm=0;
                    int g=0;
                    for(int o=len-1;o<L;o=o+len){
                        norm3[g] = norm2[o];
                        for(int m=0;m<norm1.length;m++){
                            cNorm+=Math.sqrt(norm2[o][m]);
                        }
                        g++;
                    }
                    // for(int o=0;o<norm1.length;o++){
                    //     double[] w =new double[norm1[0].length/len];
                    //     for(int ppp=0;ppp<g.length/len;ppp++){
                    //         w[ppp] = norm2[ppp][o];
                    //     }
                    //     double[] k = Filter(ones1,array,w);
                    //     for(int m = len-1;m<norm1[0].length;m=m+len){
                    //         norm3[m/len][o] = k[m/len];
                    //         cNorm+=Math.sqrt(norm2[m][o]);
                    //     }
                    // }
                    cNorm=1.0/cNorm;
                    if(cNorm>invNorm){
                        invNorm = cNorm;
                        Blen = len;
                        Bwdt = wdt;
                        Bnorm = norm3;
                    }



                }
                double[] ones = new double[Bwdt];
                for(int c=0;c<Bwdt;c++){
                    ones[c] =1;
                }
                double[] array = new double[1];
                array[0]=1;
                // double[] z = Filter(ones,array,getColumn(sigB,0));
                double[][] sigB1 = new double[W][L];
                for(int pspk =0;pspk<L;pspk++){
                    double[] w =new double[W];
                    for(int ppp=0;ppp<W;ppp++){
                        w[ppp] = sigB[ppp][pspk];
                    }
                    double[] k =Filter(ones,array,w);
                    for(int psp=0;psp<W;psp++){
                        sigB1[psp][pspk] = k[psp];
                    }
                }

                double[][] sigB2 =new double[(int)W/Bwdt][L];
                int f=0;
                for(int e=Bwdt-1;e<W;e=e+Bwdt){
                    sigB2[f] = sigB1[e];
                    f++;
                }
                double[] ones1 = new double[Blen];
                for(int c=0;c<Blen;c++){
                    ones1[c] =1;
                }
                // double[] g = Filter(ones,array,sigB2[0]);
                double[][] sigB3 = new double[sigB2[0].length][sigB2.length];
                // System.out.println(g.length);
                for(int pspk =0;pspk<sigB2.length;pspk++){
                    double[] g =Filter(ones1,array,sigB2[pspk]);
                    for(int psp=0;psp<sigB2[0].length;psp++){
                        sigB3[psp][pspk] = g[psp];
                    }
                }
                double[][] sigB4 = new double[(int)(sigB2[0].length/Blen)][sigB2.length];
                int h=0;
                for(int m = Blen-1;m<sigB2[0].length;m=m+Blen){
                    sigB4[h] = sigB3[m];
                    h++;
                }
                double[][] ksi = new double[Bnorm.length][Bnorm[0].length];
                double[][] a = new double[Bnorm.length+1][Bnorm[0].length];
                for(int l=0;l<Bnorm.length;l++){
                    for(int m=0;m<Bnorm[0].length;m++){
                        ksi[l][m] = Math.max((double) (1-1.0/(Bnorm[l][m]/(sigB4[l][m]+eps))),alpha/(1+(double)(sigB4[l][m]+eps)/Bnorm[l][m]));
                        a[l][m] =ksi[l][m];
                        if(l==Bnorm[0].length-1){
                            a[l+1][m] = 0;
                        }
                    }
                }
                // double[] ones1 = new double[Blen];
                //     for(int c=0;c<Blen;c++){
                //         ones1[c] =1;
                //     }
                double[][] b= upsample(a,Blen);
                double[][] c = new double[b.length][b[0].length];
                double[][] d =new double[b.length-Blen][b[0].length];
                for(int r=0;r<b[0].length;r++){
                    double[] w =new double[b.length];
                    for(int ppp=0;ppp<b.length;ppp++){
                        w[ppp] = B[ppp][r];
                    }
                    double[] sa = Filter(ones1,array,w);
                    for(int s=0;s<b.length;s++){
                        c[s][r] = sa[s];
                        if(s<b.length-Blen){
                            d[s][r] = sa[s];
                        }
                    }
                }
                double[][] e =new double[b[0].length+1][b.length-Blen];
                for(int t =0;t<b[0].length;t++){
                    double[] w =new double[b.length-Blen];
                    for(int ppp=0;ppp<b.length-Blen;ppp++){
                        e[t][ppp] = d[ppp][t];
                    }
                }
                for(int xy =0;xy<b.length-Blen;xy++){
                    e[b[0].length][xy]=0;
                }

                double[][] ef = upsample(e,Bwdt);
                double[][] ff = new double[ef.length][ef[0].length];
                for(int tt=0;tt<ef[0].length;tt++){
                    double[] w =new double[ef.length];
                    for(int ppp=0;ppp<ef.length;ppp++){
                        w[ppp] = ef[ppp][tt];
                    }
                    double[] ftf = Filter(ones,array,w);
                    for(int ts=0;ts<ef.length;ts++){
                        ff[ts][tt] = ftf[ts];
                        if(ts<ef.length-Bwdt){
                            Sout[i+ts][j+tt] = S[i+ts][j+tt].times(new Complex(ff[ts][tt],0));
                        }
                    }
                }
            }
        }
        return  Sout;
    }

}
