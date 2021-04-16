package pdl.ImageProcessing;

import net.imglib2.RandomAccess;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class ColorModelConverter {

    /**
     * public static void RGBToHSV(int r, int g, int b, float[] hsv)
     * <p>
     * transform the RGB data to HSV data.
     *
     * @param r   the data of the R channel.
     * @param g   the data of the G channel.
     * @param b   the data of the B channel.
     * @param hsv an array in which we will put the obtain data of the function.
     */
    public static void RGBToHSV(int r, int g, int b, float[] hsv) {

        float R = r / 255f;
        float G = g / 255f;
        float B = b / 255f;

        float Max = 0;
        float Min = 0;
        float Moy = 0;

        Max = Math.max(R, Math.max(G, B));
        Min = Math.min(R, Math.min(G, B));

        Moy = Max - Min;
        float t = 0;

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

        float S;
        if (Max == 0)
            S = 0;
        else {
            S = 1 - (Min / Max);
        }

        float v = Max;

        hsv[0] = t;
        hsv[1] = S;
        hsv[2] = v;
    }

    /**
     * public static void HSVToRGB(float h, float s, float V, int[] rgb)
     * <p>
     * transform the HSV data to RGB data.
     *
     * @param h   the data of the tint.
     * @param s   the data of the saturation.
     * @param V   the data of the value.
     * @param rgb an array in which we will put the obtain data of the function.
     */
    public static void HSVToRGB(float h, float s, float V, int[] rgb) {
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
     * public static void FromRGBtoG(Img<UnsignedByteType> input)
     * <p>
     * change the value of each pixel by taking the values ​​
     * of the RGB channels in order to transform the color image into a gray image .
     *
     * @param input the input image from which we will take the data of each pixel.
     */
    public static void RGBtoGray(Img<UnsignedByteType> input) {
        final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(input, 2, 0);
        final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(input, 2, 1);
        final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(input, 2, 2);

        LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel(
                (r, g, b) -> {
                    int sum = ((r.get() * 30) + (g.get() * 59) + (b.get() * 11)) / 100;

                    r.set(sum);
                    g.set(sum);
                    b.set(sum);
                }
        );
    }
}