package pdl.ImageProcessing ;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;
import static java.lang.StrictMath.pow;

import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.view.ExtendedRandomAccessibleInterval;

public class Outline{
    
    /**
     * public static void Outline(final Img<UnsignedByteType> output,int depth)
     * 
     * apply two convolution to the input image with two differeny kernel
     * to obtain an image with only the Outline visible
     * 
     * @param input the input image from which we will take the data of each pixel.
     * @param depth jsp sad;

    */
    public static void Outline(final Img<UnsignedByteType> output,
                                        int depth) {
        if (depth > 1) FromRGBtoG(output);
        Img<UnsignedByteType> input = output.copy();

        int[][] kernel_H1 = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };
        int[][] kernel_H2 = {
                {-1, -2, -1},
                {0, 0, 0},
                {1, 2, 1}
        };
        int size = 3;
        int n= (size/2);

        final IntervalView<UnsignedByteType> expandedView = Views.expandMirrorDouble(input, size, size,3);
        final RandomAccess<UnsignedByteType> r = expandedView.randomAccess();
        final RandomAccess<UnsignedByteType> r1 = output.randomAccess();

        final int iw = (int) input.max(0);
        final int ih = (int) output.max(1);
    
        for (int x = 0; x <= iw; ++x) {
            for (int y = 0; y <= ih; ++y) {
                r.setPosition(x, 0);
                r.setPosition(y, 1);
                r1.setPosition(x, 0);
                r1.setPosition(y, 1);
                int i=0;
                int j = 0;
                int meanH = 0;
                int meanV = 0;
                
                for (int u= -n; u<=n; ++u){
                    for(int v= -n; v<=n; ++v){
                        r.setPosition(x+u, 0);
                        r.setPosition(y+v, 1);
                        meanH += r.get().get() * kernel_H1[i][j];
                        meanV += r.get().get() * kernel_H2[i][j];

                        if (i<size ) i++;
                        if(i==size) i=0;
                    }
                    if (j < size) j++;
                    if (j == size) j = 0;
                }



                double tmp = Math.sqrt(
                        meanH*meanH + meanV*meanV
                )%255;

                for (int chan = 0; chan < depth; chan++) {
                    if (depth > 1) r1.setPosition(chan, 2);
                    r1.get().set((int) tmp);
                }

            }
        }
    }
    /**
     * public static void FromRGBtoG(Img<UnsignedByteType> input)
     * 
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