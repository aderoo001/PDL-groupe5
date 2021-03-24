package pdl.backend;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import io.scif.SCIFIO;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.ImgSaver;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.Cursor;
import java.io.File;
import net.imglib2.view.Views;
import net.imglib2.view.IntervalView;
import net.imglib2.loops.LoopBuilder;

/************* */
import static java.lang.StrictMath.pow;


import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.view.ExtendedRandomAccessibleInterval;

//import java.util.ArrayList;
//import java.util.List;


public class ImageChanger{

//besoin 14:
/*

 */
    public static void EditLuminosityRGB(Img<UnsignedByteType> input, Img<UnsignedByteType> output, int delta) {
        final Cursor<UnsignedByteType> inC = input.localizingCursor();
        final Cursor<UnsignedByteType> outC = output.localizingCursor();
        while (inC.hasNext()) {
        inC.fwd();
        outC.fwd();
        if (inC.get().get() < 255 - delta) {
            outC.get().set(inC.get().get() + delta);
        } else {
            outC.get().set(255);
        }
        }
    }
//new besoin
    public static void FromRGBtoG(Img<UnsignedByteType> input) {
		final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(input, 2, 0);
        final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(input, 2, 1);
        final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(input, 2, 2);

        LoopBuilder.setImages(inputR,inputG,inputB).forEachPixel(
            (r,g,b) -> { 
                //new color
                int sum = ((r.get()*30)+(g.get()*59)+(b.get()*11))/100;
                
                
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
    public static void HistoHSV(Img<UnsignedByteType> input,String choix){
        int SorV =0;
        if(choix.equals("saturation")){//convolution sur la saturation
            SorV = 0;
        }
        if(choix.equals("value")){//convolution sur la value
            SorV = 1;
        }
        aplanir_histograme_HSV(input,SorV);
    }
    //besoin 16 :
    /*
    L’utilisateur peut choisir la teinte de tous les pixels 
    de l’image sélectionnée de façon à obtenir un effet de filtre coloré.
    td3 derneire partie
    */
    public static void Colored(Img<UnsignedByteType> img,float deg){
        final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0);
        final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(img, 2, 1);
        final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(img, 2, 2);
        float hsv[] = new float[3];
        int rgb[] = new int[3];

        LoopBuilder.setImages(inputR,inputG,inputB).forEachPixel(
            (r,g,b) -> { 
				rgbToHsv(r.get(),g.get(),b.get(),hsv);
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
    public static void Blured(Img<UnsignedByteType> input,String choix,int size){

        int[][] kernel = {
            {1, 2, 3, 2, 1},
            {2, 6, 8, 6, 2},
            {3, 8, 10, 8, 3},
            {2, 6, 8, 6, 2},
            {1, 2, 3, 2, 1}
        };
        if(choix.equals("M")){//filtre moyen.
            kernel = new int[size][size];
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    kernel[x][y] = 1;
                }
            }
        }
        convolution(input, input, kernel);




    }

    public static void convolution(final Img<UnsignedByteType> input, final Img<UnsignedByteType> output,
								   int[][] kernel) {

		System.out.println("--convolution--");
		float time = System.nanoTime();
		int size = kernel.length;
		int i = 0;
		int j = 0;
		int div = 0;
		for (int[] ints : kernel) {
			for (int y = 0; y < kernel.length; y++) {
				div += ints[y];
			}
		}

		IntervalView<UnsignedByteType> intervalIn = Views.expandMirrorDouble(input, size / 2, size / 2);
		IntervalView<UnsignedByteType> intervalOut = Views.expandMirrorDouble(output, size / 2, size / 2);
		final Cursor<UnsignedByteType> outC = Views.iterable(intervalOut).cursor();
		final RectangleShape shape = new RectangleShape(size / 2, false);

		for (Neighborhood<UnsignedByteType> localNeighborhood : shape.neighborhoods(intervalIn)) {
			int sum = 0;
			for (UnsignedByteType value : localNeighborhood) {
				sum += value.get() * kernel[i][j];
				j++;
				if (j == kernel.length) {
					j = 0;
					i++;
				}
				if (i == kernel.length) i = 0;
			}
			sum = sum / div;
			if (outC.hasNext()) {
				outC.next().set(sum);
			}
		}
		time = (float) ((System.nanoTime() - time) / pow(10, 9));
		System.out.println("Temps d'execution -> " + time + "");
		System.out.println();
	}


    //besoin 18 :
    /*
    L’utilisateur peut appliquer un détecteur de 
    contour à l’image sélectionnée. 
    Le résultat sera issu d’une convolution par le filtre de Sobel. 
    La convolution sera appliquée sur la version en niveaux de gris de l’image.
     */
    public static void Outline(Img<UnsignedByteType> input){
        FromRGBtoG(input);
        Sobel(input);
        
    }
    public static void Sobel(Img<UnsignedByteType> input){
        final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0);
        final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(img, 2, 1);
        final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(img, 2, 2);
        
        LoopBuilder.setImages(inputR,inputG,inputB).forEachPixel(
            (r,g,b) -> { 
				r.get();
                g.get();
                b.get();
                
                //r.set();
                //g.set();
                //b.set();
            }  
        );

    }
    
    public static void rgbToHsv(int r, int g, int b, float[] hsv){
		
		float R = r/255f;
        float G = g/255f;
        float B = b/255f;
		
		float Max = 0;
		float Min = 0;
		float Moy = 0;
		//**********min max */
		Max = Math.max(R, Math.max(G, B));
		Min = Math.min(R, Math.min(G, B));
		
		Moy = Max - Min;
		float t = 0 ;
		//**** t *//*
		if(Min == Max){
			t = 0;
		}else{
			if(Max == R){
				t = (60*((G-B)/Moy)+360)%360;
			}
			if(Max == G){
				t = (60*((B-R)/Moy)+120);
			}
			if(Max == B){
				t = (60*((R-G)/Moy)+240);
			}
		}
		//S
		float S;
		if(Max == 0)
			S = 0;
		else{
			S =1 - (Min/Max);
		}
		float v = Max;
		//v = v *100 /255;
		hsv[0]=t;
		hsv[1]=S;
		hsv[2]=v;

	}

	public static void hsvToRgb(float h, float s, float V, int[] rgb){
		int ti = (int) (h/60)%6;

		float f = (h/60)-ti;
		float L = V*(1-s);
		float M = V*(1-f*s);
		float N = V * (1-(1-f)*s);
		V = V*255;
		N = N*255;
		L = L*255;
		M = M*255;
		int v = Math.round(V);
		int n = Math.round(N);
		int l = Math.round(L);
		int m = Math.round(M);
		
		switch(ti) {
  			case 0:
				rgb[0]= v;
				rgb[1]= n;
				rgb[2]= l;
			break;
			case 1:
				rgb[0]= m;
				rgb[1]= v;
				rgb[2]= l;
			break;
			case 2:
				rgb[0]= l;
				rgb[1]= v;
				rgb[2]= n;
			break;
			case 3:
				rgb[0]= l;
				rgb[1]= m;
				rgb[2]= v;
			break;
			case 4:
				rgb[0]= n;
				rgb[1]= l;
				rgb[2]= v;
			break;
			case 5:
				rgb[0]= v;
				rgb[1]= l;
				rgb[2]= m;
			break;
		}
	}

    //*********************************************************************************** */
    public static void aplanir_histograme_HSV(Img<UnsignedByteType> img,int SorV) {//3.3
        final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0);
        final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(img, 2, 1);
        final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(img, 2, 2);
        float hsv[] = new float[3];
        int rgb[] = new int[3];

        final int iw = (int) img.max(0);
        final int ih = (int) img.max(1);
        float[] tab = LUT_histoHSV(img,SorV);
        float bottom = 0;
        float top = 100;

        LoopBuilder.setImages(inputR,inputG,inputB).forEachPixel(
                (r,g,b) -> { 
                    rgbToHsv(r.get(),g.get(),b.get(),hsv);

                    float val = hsv[SorV+1];

                    if(tab[(int) val] > top){
                        val = top;
                    }else{
                        if(tab[(int) val] < bottom){
                            val =bottom ;
                        }
                        else{
                            val = tab[(int) val];
                        }
                    }
                    //modification de l'image
                    hsvToRgb(hsv[0], hsv[1], hsv[2], rgb);
                    r.set((int) rgb[0]);
                    g.set((int) rgb[1]);
                    b.set((int) rgb[2]);
                }  
            );
    }







    public static float[] LUT_histoHSV(Img<UnsignedByteType> img,int SorV){
        float[] tab = new float[101];
        final int w = (int) img.max(0);
        final int h = (int) img.max(1);
        int n = w * h;
        for(int i=0; i<101; i++){
            float new_color = ((histogrammeCumule(img, i,SorV) * 100) / n);
            if(new_color > 100){
                tab[i] = 100;
            }
            else if(new_color < 0){
                tab[i] = 0;
            }
            else{
                tab[i] = new_color;
            }
        }
        return tab;
    }

    public static float histogrammeCumule(Img<UnsignedByteType> img, int k,int SorV){
        float[] tab= histogramme(img,SorV);
        float sum = 0;
        for(int i=0; i<k; i++){
            sum = sum + tab[i];
        }
        return sum;
    }

    public static float[] histogramme(Img<UnsignedByteType> img,int SorV){
        final IntervalView<UnsignedByteType> inputR = Views.hyperSlice(img, 2, 0);
        final IntervalView<UnsignedByteType> inputG = Views.hyperSlice(img, 2, 1);
        final IntervalView<UnsignedByteType> inputB = Views.hyperSlice(img, 2, 2);
        float[] tab= new float[101];
        float hsv[] = new float[3];

        LoopBuilder.setImages(inputR,inputG,inputB).forEachPixel(
            (r,g,b) -> { 
                rgbToHsv(r.get(),g.get(),b.get(),hsv);

                float color = hsv[SorV+1];//devient sois le S ou le V de la valeur HSV de limage
                //color=[0,100]
                tab[(int) color] =tab[(int) color] + 1;
                
            }  
        );
        return tab;
    }
}
