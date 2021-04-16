package pdl.ImageProcessing;

import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class Color {

    /**
     * public static void Colored(Img<UnsignedByteType> img,float deg)
     * <p>
     * change the image tint by the deg parameter.
     *
     * @param img the input image from which we will take the data of each pixel.
     * @param deg the degree you want on your image.
     */
    public static void Colored(Img<UnsignedByteType> img, float deg) {
        final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0);
        final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(img, 2, 1);
        final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(img, 2, 2);
        float hsv[] = new float[3];
        int rgb[] = new int[3];

        LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel(
                (r, g, b) -> {
                    ColorModelConverter.RGBToHSV(r.get(), g.get(), b.get(), hsv);
                    ColorModelConverter.HSVToRGB(deg, hsv[1], hsv[2], rgb);
                    r.set((int) rgb[0]);
                    g.set((int) rgb[1]);
                    b.set((int) rgb[2]);
                }
        );
    }
}