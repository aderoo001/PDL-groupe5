package pdl.ImageProcessing;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class Luminosity {
    /**
     * public static void EditLuminosityRGB(Img<UnsignedByteType> input, Img<UnsignedByteType> output, int delta)
     * <p>
     * change the brightness of each pixel by integer delta and that on each RGB channels.
     *
     * @param input  the input image from which we will take the data of each pixel.
     * @param output the output image from which we will change the data of each pixel.
     */
    public static void EditLuminosityRGB(Img<UnsignedByteType> input, Img<UnsignedByteType> output, int delta) {
        final Cursor<UnsignedByteType> inC = input.localizingCursor();
        final Cursor<UnsignedByteType> outC = output.localizingCursor();
        while (inC.hasNext()) {
            inC.fwd();
            outC.fwd();
            if (inC.get().get() + delta >= 255) {
                outC.get().set(255);

            } else {
                if (inC.get().get() + delta <= 0) {
                    outC.get().set(0);
                } else {
                    outC.get().set(inC.get().get() + delta);
                }
            }
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
    public static void FromRGBtoG(Img<UnsignedByteType> input) {
        final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(input, 2, 0);
        final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(input, 2, 1);
        final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(input, 2, 2);

        LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel(
                (r, g, b) -> {
                    //new color
                    int sum = ((r.get() * 30) + (g.get() * 59) + (b.get() * 11)) / 100;


                    //set
                    r.set(sum);
                    g.set(sum);
                    b.set(sum);
                }
        );
    }


}