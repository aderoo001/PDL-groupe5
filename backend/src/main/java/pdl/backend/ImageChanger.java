package pdl.backend;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;
import static java.lang.StrictMath.pow;

import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.view.ExtendedRandomAccessibleInterval;




public class ImageChanger{

//besoin 14:
    public static void EditLuminosityRGB(Img<UnsignedByteType> input, Img<UnsignedByteType> output, int delta) {
        final Cursor<UnsignedByteType> inC = input.localizingCursor();
        final Cursor<UnsignedByteType> outC = output.localizingCursor();
        while (inC.hasNext()) {
        inC.fwd();
        outC.fwd();
        if (inC.get().get() +delta >=255) {
            outC.get().set(255);
            
        } else {
            if(inC.get().get() +delta <=0){
                outC.get().set(0);
            }
            else{
                outC.get().set(inC.get().get() + delta);
            }
        }
    }

    //new besoin
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

    //besoin 15 :
    /*
    L’utilisateur peut appliquer une égalisation d’histogramme à l’image sélectionnée.
    L’égalisation sera apliquée au choix sur le canal S ou V 
    de l’image représentée dans l’espace HSV.
     */
    public static void HistoHSV(Img<UnsignedByteType> input, String choix) {
        int SorV = 0;
        if (choix.equals("saturation")) {//convolution sur la saturation
            SorV = 0;
        }
        if (choix.equals("value")) {//convolution sur la value
            SorV = 1;
        }
        aplanir_histograme_HSV(input, SorV);
    }

    //besoin 16 :
    /*
    L’utilisateur peut choisir la teinte de tous les pixels 
    de l’image sélectionnée de façon à obtenir un effet de filtre coloré.
    td3 derneire partie
    */
    public static void Colored(Img<UnsignedByteType> img, float deg) {
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

    //besoin 17 :
    /*
    L’utilisateur peut appliquer un flou à l’image sélectionnée. 
    Il peut définir le filtre appliqué (moyen ou gaussien) et 
    choisir le niveau de flou. 
    La convolution est appliquée sur les trois canaux R, G et B.
     */
    public static void Blured(Img<UnsignedByteType> input, String choix, int size) {
        if (choix.equals("M")) {//filtre moyen.

        }
        if (choix.equals("G")) {

        }

    }


    //besoin 18 :
    /*
    L’utilisateur peut appliquer un détecteur de 
    contour à l’image sélectionnée. 
    Le résultat sera issu d’une convolution par le filtre de Sobel. 
    La convolution sera appliquée sur la version en niveaux de gris de l’image.
     */
    public static void Outline(Img<UnsignedByteType> input,
                               int depth){
        convolution_Gray(input, depth);
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

    public static void convolution_Gray(final Img<UnsignedByteType> output,
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
                );

                for (int chan = 0; chan < depth; chan++) {
                    if (depth > 1) r1.setPosition(chan, 2);
                    r1.get().set((int) tmp);
                }

            }
        }
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

    //*********************************************************************************** */
    public static void aplanir_histograme_HSV(Img<UnsignedByteType> img, int SorV) {//3.3
        final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0);
        final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(img, 2, 1);
        final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(img, 2, 2);
        float[] hsv = new float[3];
        int[] rgb = new int[3];

        int[] tab = histogrammeCumule(img, SorV);
//        float bottom = 0;
//        float top = 100;

        LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel(
                (r, g, b) -> {
                    rgbToHsv(r.get(), g.get(), b.get(), hsv);
                    for (float val: hsv
                         ) {
                        System.out.println(val);
                    }
                    if (SorV == 0) {
                        hsvToRgb(hsv[0], (float) (tab[Math.round(hsv[1])*100]*100)/100, hsv[2], rgb);
                    }
                    if (SorV == 1) {
//                        System.out.println((tab[Math.round(hsv[2])*100]*100)/100);
                        hsvToRgb(hsv[0], hsv[1], (float) (tab[Math.round(hsv[2])*100]*100)/100, rgb);
                    }

//                    float val = hsv[SorV + 1];
//
//                    if (tab[(int) val] > top) {
//                        val = top;
//                    } else {
//                        if (tab[(int) val] < bottom) {
//                            val = bottom;
//                        } else {
//                            val = tab[(int) val];
//                        }
//                    }

                    r.set(rgb[0]);
                    g.set(rgb[1]);
                    b.set(rgb[2]);
                }
        );
    }

    public static int[] LUT_histoHSV(Img<UnsignedByteType> img, int SorV) {
        int[] tab = new int[101];
        for (int i = 0; i < 101; i++) tab[i] = 0;
        int[] C = histogrammeCumule(img, SorV);

        for (int i = 0; i < 101; i++) {
            int new_color = (C[i] * 100) / 100;
            System.out.println(new_color);
            if (new_color > 100) {
                tab[i] = 100;
            } else tab[i] = Math.max(new_color, 0);
        }
        for (int val: tab
             ) {
            System.out.println(val);
        }
        return tab;
    }

    public static int[] histogrammeCumule(Img<UnsignedByteType> img, int SorV) {
        int[] tab = histogramme(img, SorV);
        int[] C = new int[101];
		for (int n = 0; n < 101; n++) C[n] = 0;

		for (int i = 0; i < 101; i++) {
			for (int j = 0; j <= i; j++) {
				C[i] = C[i] + tab[j];
			}
		}
        return C;
    }

    public static int[] histogramme(Img<UnsignedByteType> img, int SorV) {
        final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0);
        final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(img, 2, 1);
        final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(img, 2, 2);
        int[] tab = new int[101];
        for (int i = 0; i < 101; i++) tab[i] = 0;
        float[] hsv = new float[3];

        LoopBuilder.setImages(inputR, inputG, inputB).forEachPixel(
                (r, g, b) -> {
                    rgbToHsv(r.get(), g.get(), b.get(), hsv);

                    float color = hsv[SorV + 1] * 100;//devient sois le S ou le V de la valeur HSV de limage
//color=[0,100]
                    tab[Math.round(color)] = tab[Math.round(color)] + 1;

                }
        );
        return tab;
    }


    //sert pour outline
    public static void convolution_Gray(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output, int[][] kernel) {
        int size=kernel.length;
        int n= (size/2);
        final IntervalView<UnsignedByteType> expandedView = Views.expandMirrorDouble(input, size, size,3);
        final RandomAccess<UnsignedByteType> r = expandedView.randomAccess();
        final RandomAccess<UnsignedByteType> r1 = output.randomAccess();

        int K_count = kernelcount(kernel);
        final int iw = (int) input.max(0);
        final int ih = (int) input.max(1);
        r.setPosition(0, 2);
        r1.setPosition(0, 2);
    
        for (int x = 0; x <= iw; ++x) {
            for (int y = 1; y <= ih; ++y) {
                r.setPosition(x, 0);
                r.setPosition(y, 1);
                r1.setPosition(x, 0);
                r1.setPosition(y, 1);
                final UnsignedByteType val = r1.get();
                int i=0;
                int j=0;   
                int mean =0;
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
                if(mean >= 255){
                    r1.get().set(255);
                    r.setPosition(1, 2);
                    r1.setPosition(1, 2);
                    r1.get().set(255);
                    r.setPosition(2, 2);
                    r1.setPosition(2, 2);
                    r1.get().set(255);
                    r.setPosition(0, 2);
                    r1.setPosition(0, 2);
                }
                else{
                    r1.get().set(0);
                    r.setPosition(1, 2);
                    r1.setPosition(1, 2);
                    r1.get().set(0);
                    r.setPosition(2, 2);
                    r1.setPosition(2, 2);
                    r1.get().set(0);
                    r.setPosition(0, 2);
                    r1.setPosition(0, 2);
                }
            }
        }
    }

    //  fonction pour créé ou appeler les  Kernels
    public static int[][] average(int size) {
        int[][] kernel = new int[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                kernel[x][y] = 1;
            }
        }
        return kernel;
    }

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
    
    /*Besoin 17:*/
    /**
     *L’utilisateur peut appliquer un flou à l’image sélectionnée.
      Il peut définir le filtre appliqué (moyen ou gaussien) et 
      choisir le niveau de flou.
      La convolution est appliquée sur les trois canaux R, G et B.
     * / */
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


}
