package pdl.ImageProcessing;

import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class Histogram {

    /**
     * public static void HistogramEqualization(Img<UnsignedByteType> img, int SorV)
     * <p>
     * take the cumulated histogram and use it to change the image (img).
     *
     * @param img    the input image from which we will take the data of each pixel.
     * @param Choice a string who can choose on what the image will be, change either the saturation or the value.
     */
    public static void HistogramEqualization(Img<UnsignedByteType> img, String Choice) {//3.3
        int SorV_ = 0;
        if (Choice.equals("value")) {
            SorV_ = 1;
        }
        final int SorV = SorV_;
        final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0);
        final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(img, 2, 1);
        final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(img, 2, 2);
        float[] hsv = new float[3];
        int[] rgb = new int[3];
        int[] tab = cumulatedHistogram(img, SorV);

        LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel(
                (r, g, b) -> {
                    ColorModelConverter.RGBToHSV(r.get(), g.get(), b.get(), hsv);
                    hsv[SorV + 1] = (float) (tab[Math.round(hsv[SorV + 1] * 100)]) / tab[100];
                    ColorModelConverter.HSVToRGB(hsv[0], hsv[1], hsv[2], rgb);

                    r.set(rgb[0]);
                    g.set(rgb[1]);
                    b.set(rgb[2]);
                }
        );
    }

    /**
     * public static int[] cumulatedHistogram(Img<UnsignedByteType> img, int SorV)
     * <p>
     * take the histogram and use it to build a cumulated histogram.
     *
     * @param img  the input image from which we will take the data of each pixel.
     * @param SorV an int who can choose on what the cumulated histograme will be, either the saturation or the value .
     * @return the array of int who represents the cumulated histogram.
     */
    public static int[] cumulatedHistogram(Img<UnsignedByteType> img, int SorV) {
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
     * public static int[] histogram(Img<UnsignedByteType> img, int SorV)
     * <p>
     * take data on all channels to build an histogram of either the value or the saturation of the data in HSV format.
     *
     * @param img  the input image from which we will take the data of each pixel.
     * @param SorV an int who can choose on what the histogram will be, either the saturation or the value.
     * @return the array of int who represent the histogram.
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
                    ColorModelConverter.RGBToHSV(r.get(), g.get(), b.get(), hsv);

                    float color = hsv[SorV + 1] * 100;
                    tab[Math.round(color)] = tab[Math.round(color)] + 1;

                }
        );
        return tab;
    }
}