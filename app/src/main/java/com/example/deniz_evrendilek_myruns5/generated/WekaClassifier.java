package com.example.deniz_evrendilek_myruns5.generated;
// Generated with Weka 3.8.6
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Wed Nov 22 20:23:24 PST 2023
public class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N429c9e8b0(i);
        return p;
    }
    static double N429c9e8b0(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 13.390311) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 13.390311) {
            p = WekaClassifier.N5fbbd3181(i);
        }
        return p;
    }
    static double N5fbbd3181(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 1;
        } else if (((Double) i[64]).doubleValue() <= 14.534508) {
            p = WekaClassifier.N391989ba2(i);
        } else if (((Double) i[64]).doubleValue() > 14.534508) {
            p = 2;
        }
        return p;
    }
    static double N391989ba2(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 1;
        } else if (((Double) i[4]).doubleValue() <= 14.034383) {
            p = WekaClassifier.N1f40a8cf3(i);
        } else if (((Double) i[4]).doubleValue() > 14.034383) {
            p = 1;
        }
        return p;
    }
    static double N1f40a8cf3(Object []i) {
        double p = Double.NaN;
        if (i[7] == null) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() <= 4.804712) {
            p = 1;
        } else if (((Double) i[7]).doubleValue() > 4.804712) {
            p = 2;
        }
        return p;
    }
}