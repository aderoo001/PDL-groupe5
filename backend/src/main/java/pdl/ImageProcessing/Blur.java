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

public class Blur{

    /*Besoin 17:*/
    /**
    * void blured(
        final Img<UnsignedByteType> input,
        final Img<UnsignedByteType> output,
        int[][] kernel,
        int depth)
    * 
    *  add the values ​​of the kernel
    * 
    * @param input the input image from which we will take the data of each pixel.
    * @param output the output image from which we will change the data of each pixel.
    * @param kernel an array of int
    * @param depth number of dimention of the image 3 for an RGB Image and 1 for a Gray leveled image;
    * 

    */
    public static void blured(
            final Img<UnsignedByteType> input,
            final Img<UnsignedByteType> output,
            int[][] kernel,
            int depth) {

        int size= kernel.length;
        int n= (size/2);


        final IntervalView<UnsignedByteType> expandedView =
                Views.expandMirrorDouble(input,size, size, size);
        final RandomAccess<UnsignedByteType> r = expandedView.randomAccess();
        final RandomAccess<UnsignedByteType> r1 = output.randomAccess();

        final int iw = (int) input.max(0);
        final int ih = (int) input.max(1);
        for (int channel = 0;channel < depth;channel ++){
            if (depth > 1) {
                r.setPosition(channel, 2);
                r1.setPosition(channel, 2);
            }
            for (int x = 0; x <= iw; ++x) {
                for (int y = 0; y <= ih; ++y) {
                    r.setPosition(x, 0);
                    r.setPosition(y, 1);
                    r1.setPosition(x, 0);
                    r1.setPosition(y, 1);

                    int i=0;
                    int j=0;
                    int mean = 0;

                    for (int u= -n; u<=n; ++u){
                        for(int v= -n; v<=n; ++v){
                            r.setPosition(x+u, 0);
                            r.setPosition(y+v, 1);
                            mean += r.get().get()*kernel[i][j];

                            if (i<size) i++;
                            if(i==size) i=0;
                        }
                        if (j<size) j++;
                        if(j==size) j=0;
                    }
                    mean = mean /(kernelcount(kernel));
                    r1.get().set(mean);

                }
            }
        }
    }

    /**
     * public static int kernelcount(int[][] kernel)
     * 
     *  add the values ​​of the kernel
     * 
     * @param kernel an array of int
     * 
     * @return return the addition of all the value of the kernel

    */
    public static int kernelcount(int[][] kernel){
        int size=kernel.length;

        int count = 0;
        for(int i = 0;i<size;i++){
            for(int j = 0;j<size;j++){
                count = count + kernel[i][j];
            
            }
        }
        return count  ;
    }

    /**
     * public static int[][] average(int size)
     * 
     * creat a kernel with only 1 in it.
     * 
     * @param size the size of the kernel you whant.
     * @return an array of size * size with only 1 in it .
     */
    public static int[][] average(int size) {
        int[][] kernel = new int[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                kernel[x][y] = 1;
            }
        }
        return kernel;
    }

    /**
     * public static int[][] gaussien()
     * 
     * creat a gaussien kernel
     * 
     * @return an array of 5x5 with the gaussien kernel .
     */
    public static int[][] gaussien() {
        int[][] kernel = {
            {1, 2, 3, 2, 1},
            {2, 6, 8, 6, 2},
            {3, 8, 10, 8, 3},
            {2, 6, 8, 6, 2},
            {1, 2, 3, 2, 1}
        };
        return kernel;
    }
    
}