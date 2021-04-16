package pdl.ImageProcessing;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class Luminosity {
    /**
     * public static void EditLuminosity(Img<UnsignedByteType> input, Img<UnsignedByteType> output, int delta)
     * <p>
     * change the brightness of each pixel by integer delta and that on each RGB channels.
     *
     * @param input  the input image from which we will take the data of each pixel.
     * @param output the output image from which we will change the data of each pixel.
     */
    public static void EditLuminosity(Img<UnsignedByteType> input, Img<UnsignedByteType> output, int delta) {
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
}