package pdl.backend;

import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.ImgSaver;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

import java.io.File;

public class GrayLevelProcessing {

    public static void threshold(Img<UnsignedByteType> img, int t) {
        final RandomAccess<UnsignedByteType> r = img.randomAccess();

        final int iw = (int) img.max(0);
        final int ih = (int) img.max(1);

        for (int x = 0; x <= iw; ++x) {
            for (int y = 0; y <= ih; ++y) {
                r.setPosition(x, 0);
                r.setPosition(y, 1);
                final UnsignedByteType val = r.get();
                if (val.get() < t)
                    val.set(50);
                else
                    val.set(70);
            }
        }

    }

    public static int max(Img<UnsignedByteType> img) {
        final RandomAccess<UnsignedByteType> r = img.randomAccess();

        final int iw = (int) img.max(0);
        final int ih = (int) img.max(1);
        int mymax = 0;
        for (int x = 0; x <= iw; ++x) {
            for (int y = 0; y <= ih; ++y) {
                r.setPosition(x, 0);
                r.setPosition(y, 1);
                final UnsignedByteType val = r.get();
                if (val.get() > mymax)
                    mymax = val.get();
            }
        }
        return mymax;
    }

    public static int min(Img<UnsignedByteType> img) {
        final RandomAccess<UnsignedByteType> r = img.randomAccess();

        final int iw = (int) img.max(0);
        final int ih = (int) img.max(1);
        int mymin = 255;
        for (int x = 0; x <= iw; ++x) {
            for (int y = 0; y <= ih; ++y) {
                r.setPosition(x, 0);
                r.setPosition(y, 1);
                final UnsignedByteType val = r.get();
                if (val.get() < mymin)
                    mymin = val.get();
            }
        }
        return mymin;
    }

    public static void luminosité(Img<UnsignedByteType> img, int t) {//2.1  Augmenter / diminuer la luminosité
        final RandomAccess<UnsignedByteType> r = img.randomAccess();

        final int iw = (int) img.max(0);
        final int ih = (int) img.max(1);

        for (int x = 0; x <= iw; ++x) {
            for (int y = 0; y <= ih; ++y) {
                r.setPosition(x, 0);
                r.setPosition(y, 1);
                final UnsignedByteType val = r.get();
                if (val.get() + t > 255)
                    val.set(255);
                else {
                    if (val.get() + t < 0)
                        val.set(0);
                    else
                        val.set(val.get() + t);
                }
            }
        }

    }

    public static void luminosité2(Img<UnsignedByteType> img, int t) {//2.2 Réécrire la méthode en parcourant l'image avec l'itérateur Cursor
        final Cursor<UnsignedByteType> cursor = img.cursor();

        final int iw = (int) img.max(0);
        final int ih = (int) img.max(1);
        while (cursor.hasNext()) {
            cursor.fwd();
            final UnsignedByteType val = cursor.get();
            if (val.get() + t < 255)
                val.set(val.get() + t);
            else
                val.set(255);
        }
    }

    public static void contrast(Img<UnsignedByteType> img) {//3.1 Réglage du contraste : dynamique de l'image
        final RandomAccess<UnsignedByteType> r = img.randomAccess();

        final int iw = (int) img.max(0);
        final int ih = (int) img.max(1);
        int max = max(img);
        int min = min(img);
        int constante = 255 / (max - min);

        for (int x = 0; x <= iw; ++x) {
            for (int y = 0; y <= ih; ++y) {
                r.setPosition(x, 0);
                r.setPosition(y, 1);
                final UnsignedByteType val = r.get();
                val.set(constante * (val.get() - min));
            }
        }

    }

    public static void contrast2(Img<UnsignedByteType> img, int bottom, int top) {//3.2 Réglage du contraste : dynamique de l'image
        final RandomAccess<UnsignedByteType> r = img.randomAccess();

        final int iw = (int) img.max(0);
        final int ih = (int) img.max(1);
        int max = max(img);
        int min = min(img);
        int constante = 255 / (max - min);

        for (int x = 0; x <= iw; ++x) {
            for (int y = 0; y <= ih; ++y) {
                r.setPosition(x, 0);
                r.setPosition(y, 1);
                final UnsignedByteType val = r.get();
                if ((constante * (val.get() - min)) < bottom) {
                    val.set(bottom);
                } else {
                    if ((constante * (val.get() - min)) > top) {
                        val.set(top);
                    } else {
                        val.set(constante * (val.get() - min));
                    }
                }
            }
        }

    }

    public static int[] LUT(int min, int max) {
        int[] tab;
        tab = new int[256];

        for (int i = 0; i <= 255; i++) {
            if ((255 * (i - min) / (max - min)) > 255) {
                tab[i] = 255;
            } else {
                if ((255 * (i - min) / (max - min)) < 0) {
                    tab[i] = 0;
                } else {
                    tab[i] = (255 * (i - min) / (max - min));
                }
            }
        }

        return tab;
    }

    public static void contrast3(Img<UnsignedByteType> img, int bottom, int top) {//3.3
        final RandomAccess<UnsignedByteType> r = img.randomAccess();

        final int iw = (int) img.max(0);
        final int ih = (int) img.max(1);
        int max = max(img);
        int min = min(img);
        int constante = 255 / (max - min);
        int[] tab = LUT(min, max);

        for (int x = 0; x <= iw; ++x) {
            for (int y = 0; y <= ih; ++y) {
                r.setPosition(x, 0);
                r.setPosition(y, 1);
                final UnsignedByteType val = r.get();
                if (tab[val.get()] > top) {
                    val.set(top);
                } else {
                    if (tab[val.get()] < bottom) {
                        val.set(bottom);
                    } else {
                        val.set(tab[val.get()]);
                    }
                }

            }
        }


    }

    public static void aplanir_histograme(Img<UnsignedByteType> img) {//3.3
        final RandomAccess<UnsignedByteType> r = img.randomAccess();

        final int iw = (int) img.max(0);
        final int ih = (int) img.max(1);
        int max = max(img);
        int min = min(img);
        int constante = 255 / (max - min);
        int[] tab = LUT_histo(img);
        int bottom = 0;
        int top = 255;

        for (int x = 0; x <= iw; ++x) {
            for (int y = 0; y <= ih; ++y) {
                r.setPosition(x, 0);
                r.setPosition(y, 1);
                final UnsignedByteType val = r.get();
                if (tab[val.get()] > top) {
                    val.set(top);
                } else {
                    if (tab[val.get()] < bottom) {
                        val.set(bottom);
                    } else {
                        val.set(tab[val.get()]);
                    }
                }

            }
        }


    }


    public static int[] LUT_histo(Img<UnsignedByteType> img) {
        int[] tab = new int[256];
        final int w = (int) img.max(0);
        final int h = (int) img.max(1);
        int n = w * h;
        for (int i = 0; i < 256; i++) {
            int new_color = ((histogrammeCumule(img, i) * 255) / n);
            if (new_color > 255) {
                tab[i] = 255;
            } else if (new_color < 0) {
                tab[i] = 0;
            } else {
                tab[i] = new_color;
            }
        }
        return tab;
    }

    public static int histogrammeCumule(Img<UnsignedByteType> img, int k) {
        int[] tab = histogramme(img);
        int sum = 0;
        for (int i = 0; i < k; i++) {
            sum = sum + tab[i];
        }
        return sum;
    }

    public static int[] histogramme(Img<UnsignedByteType> img) {
        final RandomAccess<UnsignedByteType> r = img.randomAccess();

        final int w = (int) img.max(0);
        final int h = (int) img.max(1);
        int[] tab = new int[256];
        for (int x = 0; x <= w; ++x) {
            for (int y = 0; y <= h; ++y) {
                r.setPosition(x, 0);
                r.setPosition(y, 1);
                final UnsignedByteType val = r.get();
                int color = val.getInteger();
                tab[color] = tab[color] + 1;
            }
        }
        return tab;
    }


    public static void fromRGBtoG(Img<UnsignedByteType> input) {
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

    public static void RGBtoHSV_Final(Img<UnsignedByteType> img, float deg) {
        final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0);
        final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(img, 2, 1);
        final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(img, 2, 2);
        float hsv[] = new float[3];
        int rgb[] = new int[3];

        LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel(
                (r, g, b) -> {
                    rgbToHsv(r.get(), g.get(), b.get(), hsv);
                    hsvToRgb(deg, hsv[1], hsv[2], rgb);
                    r.set((int) rgb[0]);
                    g.set((int) rgb[1]);
                    b.set((int) rgb[2]);
                }
        );
    }


    public static void main(final String[] args) throws ImgIOException, IncompatibleTypeException {
        // load image
        if (args.length < 2) {
            System.out.println("missing input and/or output image filenames");
            System.exit(-1);
        }
        final String filename = args[0];
        if (!new File(filename).exists()) {
            System.err.println("File '" + filename + "' does not exist");
            System.exit(-1);
        }

        final ArrayImgFactory<UnsignedByteType> factory = new ArrayImgFactory<>(new UnsignedByteType());
        final ImgOpener imgOpener = new ImgOpener();
        final Img<UnsignedByteType> input = (Img<UnsignedByteType>) imgOpener.openImgs(filename, factory).get(0);
        imgOpener.context().dispose();

        long begin = System.nanoTime();
        // process image
        //threshold(input, 128);
        //luminosité(input,50);
        //luminosité2(input,50);
        //contrast(input);
        //aplanir_histograme(input);
        //fromRGBtoG(input);
        RGBtoHSV_Final(input, 270);

        // On prend une mesure de temps après.
        long end = System.nanoTime();
        double duration = (end - begin) * Math.pow(10, -9);
        System.out.printf(" duration is %f seconds\n", duration);

        // save output image
        final String outPath = args[1];
        File path = new File(outPath);
        // clear the file if it already exists.
        if (path.exists()) {
            path.delete();
        }
        ImgSaver imgSaver = new ImgSaver();
        imgSaver.saveImg(outPath, input);
        imgSaver.context().dispose();
        System.out.println("Image test saved in: " + outPath);
    }

}
