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
                    if (parameters.size() > 2) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    if (!parameters.containsKey("incLumDelta")) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    int incLumDelta = Integer.parseInt(parameters.get("incLumDelta"), 10);
                    if (!(-255 <= incLumDelta && incLumDelta <= 255)) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }

                    try {
                        Luminosity.EditLuminosityRGB(input, input, incLumDelta);
                    } catch (Exception e) {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;

                case "histogram":
                    if (parameters.size() > 2) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    if (!parameters.containsKey("histAlgoType")) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    String histAlgoType = parameters.get("histAlgoType");
                    if (!(histAlgoType.equals("value") || histAlgoType.equals("saturation"))) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    try {
                        Histogram.aplanir_histogram_HSV(input, histAlgoType);
                    } catch (Exception e) {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;

                case "color":
                    if (parameters.size() > 2) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    if (!parameters.containsKey("colorDelta")) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    float colorDelta = Float.parseFloat(parameters.get("colorDelta"));
                    if (!(0 <= colorDelta) || !(360 > colorDelta)) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                    try {
                        Color.Colored(input, colorDelta);
                    } catch (Exception e) {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;

                case "blur":
                    if (parameters.size() > 3)
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    if (!parameters.containsKey("blurIntensity"))
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    if (!parameters.containsKey("blurAlgoType"))
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;

                case "outline":
                    if (parameters.size() > 1) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;

                case "grayLevel":
                    if (parameters.size() > 1) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    try {
                        Outline.FromRGBtoG(input);

                    } catch (Exception e) {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;
                default:
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}