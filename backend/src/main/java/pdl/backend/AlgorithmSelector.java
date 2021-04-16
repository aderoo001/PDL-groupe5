package pdl.backend;

import io.scif.img.SCIFIOImgPlus;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pdl.ImageProcessing.*;

import java.util.Map;

public class AlgorithmSelector {

    public static ResponseEntity<?> selector(SCIFIOImgPlus<UnsignedByteType> input, String format, Map<String, String> parameters) {
        if (parameters.containsKey("algorithm")) {
            switch (parameters.get("algorithm")) {
                case "increaseLuminosity":
                    int incLumDelta = Integer.parseInt(parameters.get("incLumDelta"), 10);
                    if (!(-255 <= incLumDelta && incLumDelta <= 255)) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }

                    try {
                        Luminosity.EditLuminosityRGB(input, input, incLumDelta);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;

                case "histogram":
                    String histAlgoType = parameters.get("histAlgoType");
                    if (!(histAlgoType.equals("value") || histAlgoType.equals("saturation"))) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    try {
                        Histogram.aplanir_histogram_HSV(input, histAlgoType);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;

                case "color":
                    float colorDelta = Float.parseFloat(parameters.get("colorDelta"));
                    if (!(0 <= colorDelta)) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    try {
                        Color.Colored(input, colorDelta);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;

                case "blur":
                    int blurIntensity = Integer.parseInt(parameters.get("blurIntensity"), 10);
                    String blurAlgoType = parameters.get("blurAlgoType");
                    if (!(0 <= blurIntensity) || !(blurAlgoType.equals("M") || blurAlgoType.equals("G"))) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    try {
                        int[][] kernel = Blur.gaussien();
                        if (blurAlgoType.equals("M")) {
                            kernel = Blur.average(blurIntensity);
                        }
                        switch (format) {
                            case "jpeg":
                                Blur.blured(input, input, kernel, 3);
                                break;
                            case "tif":
                                Blur.blured(input, input, kernel, 1);
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;

                case "outline":
                    try {
                        switch (format) {
                            case "jpeg":
                                assert input != null;
                                Outline.Process(input, 3);
                                break;
                            case "tif":
                                assert input != null;
                                Outline.Process(input, 1);
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;

                case "grayLevel":
                    try {
                        Outline.FromRGBtoG(input);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;
                default:
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}