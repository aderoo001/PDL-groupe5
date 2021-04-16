package pdl.ImageProcessing;

import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class Histogram {

    /**
     * public static void aplanir_histograme_HSV(Img<UnsignedByteType> img, int SorV)
     * <p>
     * take the cumulated histograme and use it to change the image (img).
     *
     * @param img    the input image from which we will take the data of each pixel.
     * @param Choice an int who can choose on what the image will be change either the saturation or the value .
     */
    public static void aplanir_histogram_HSV(Img<UnsignedByteType> img, String Choice) {//3.3
        int SorV_ = 0;
        if (Choice == "value") {
            SorV_ = 1;
        }
        final int SorV = SorV_;
        final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0);
        final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(img, 2, 1);
        final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(img, 2, 2);
        float[] hsv = new float[3];
        int[] rgb = new int[3];
        int[] tab = histogramCumule(img, SorV);

        LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel(
                (r, g, b) -> {
                    rgbToHsv(r.get(), g.get(), b.get(), hsv);
                    hsv[SorV + 1] = (float) (tab[Math.round(hsv[SorV + 1] * 100)]) / tab[100];
                    hsvToRgb(hsv[0], hsv[1], hsv[2], rgb);

                    r.set(rgb[0]);
                    g.set(rgb[1]);
                    b.set(rgb[2]);
                }
        );
    }

    /**
     * public static int[] histogramCumule(Img<UnsignedByteType> img, int SorV)
     * <p>
     * take the histograme and use it to build a cumulated histogram .
     *
     * @param img  the input image from which we will take the data of each pixel.
     * @param SorV an int who can choose on what the cumulated histograme will be either the saturation or the value .
     * @return the aray of int who represent the cumulated histograme
     */
    public static int[] histogramCumule(Img<UnsignedByteType> img, int SorV) {
        int[] tab = histogram(img, SorV);
        int[] C = new int[101];
        for (int n = 0; n < 101; n++) C[n] = 0;

        for (int i = 0; i < 101; i++) {
            for (int j = 0; j <= i; j++) {
                C[i] = C[i] + tab[j];
            }
        }
        return C;
    }

    /**
     * public static int[] histogramme(Img<UnsignedByteType> img, int SorV)
     * <p>
     * take data on all channels to build an histogram of either the value or the saturation of the data in HSV shape
     *
     * @param img  the input image from which we will take the data of each pixel.
     * @param SorV an int who can choose on what the histograme will be either the saturation or the value
     * @return the aray of int who represent the histograme
     */
    public static int[] histogram(Img<UnsignedByteType> img, int SorV) {
        final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0);
        final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(img, 2, 1);
        final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(img, 2, 2);
        int[] tab = new int[101];
        for (int i = 0; i < 101; i++) tab[i] = 0;
        float[] hsv = new float[3];

        LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel(
                (r, g, b) -> {
                    rgbToHsv(r.get(), g.get(), b.get(), hsv);

                    float color = hsv[SorV + 1] * 100;
                    tab[Math.round(color)] = tab[Math.round(color)] + 1;

                }
        );
        return tab;
    }

    /**
     * public static void hsvToRgb(float h, float s, float V, int[] rgb)
     * <p>
     * trasform the hsv data TO RGB data
     *
     * @param h   the data of the tint
     * @param s   the data of the saturation
     * @param v   the data of the Value
     * @param rgb an array in wich we will put the obtain data of the function
     */
    public static void hsvToRgb(float h, float s, float V, int[] rgb) {
        int ti = (int) (h / 60) % 6;

        float f = (h / 60) - ti;
        float L = V * (1 - s);
        float M = V * (1 - f * s);
        float N = V * (1 - (1 - f) * s);
        V = V * 255;
        N = N * 255;
        L = L * 255;
        M = M * 255;
        int v = Math.round(V);
        int n = Math.round(N);
        int l = Math.round(L);
        int m = Math.round(M);

        switch (ti) {
            case 0:
                rgb[0] = v;
                rgb[1] = n;
                rgb[2] = l;
                break;
            case 1:
                rgb[0] = m;
                rgb[1] = v;
                rgb[2] = l;
                break;
            case 2:
                rgb[0] = l;
                rgb[1] = v;
                rgb[2] = n;
                break;
            case 3:
                rgb[0] = l;
                rgb[1] = m;
                rgb[2] = v;
                break;
            case 4:
                rgb[0] = n;
                rgb[1] = l;
                rgb[2] = v;
                break;
            case 5:
                rgb[0] = v;
                rgb[1] = l;
                rgb[2] = m;
                break;
        }
    }

    /**
     * public static void rgbToHsv(int r, int g, int b, float[] hsv)
     * <p>
     * trasform the RGB data TO HSV data
     *
     * @param r   the data of the R channel
     * @param g   the data of the G channel
     * @param b   the data of the RBchannel
     * @param hsv an array in wich we will put the obtain data of the function
     */
    public static void rgbToHsv(int r, int g, int b, float[] hsv) {

        float R = r / 255f;
        float G = g / 255f;
        float B = b / 255f;

        float Max = 0;
        float Min = 0;
        float Moy = 0;
        //**********min max */
        Max = Math.max(R, Math.max(G, B));
        Min = Math.min(R, Math.min(G, B));

        Moy = Max - Min;
        float t = 0;
        //**** t *//*
        if (Min == Max) {
            t = 0;
        } else {
            if (Max == R) {
                t = (60 * ((G - B) / Moy) + 360) % 360;
            }
            if (Max == G) {
                t = (60 * ((B - R) / Moy) + 120);
            }
            if (Max == B) {
                t = (60 * ((R - G) / Moy) + 240);
            }
        }
        //S
        float S;
        if (Max == 0)
            S = 0;
        else {
            S = 1 - (Min / Max);
        }
        float v = Max;
        //v = v *100 /255;
        hsv[0] = t;
        hsv[1] = S;
        hsv[2] = v;
    }

}