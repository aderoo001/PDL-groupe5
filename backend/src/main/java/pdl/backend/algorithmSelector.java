package pdl.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.scif.img.SCIFIOImgPlus;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public class algorithmSelector{

    public static ResponseEntity<?> selector(SCIFIOImgPlus<UnsignedByteType> input, Optional<Image> image, Map<String, String> parameters){
        if(parameters.containsKey("algorithm")==true){
            switch (parameters.get("algorithm")) {
                case "increaseLuminosity":
                    int incLumDelta = Integer.parseInt(parameters.get("incLumDelta"), 10);
                    if (!(-255 <= incLumDelta && incLumDelta <= 255)) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
    
                    try {
                        ImageChanger.EditLuminosityRGB(input, input, incLumDelta);
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
                        ImageChanger.HistoHSV(input, histAlgoType);
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
                        ImageChanger.Colored(input, colorDelta);
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
                        int size = blurIntensity;
                        int[][] kernel = ImageChanger.gaussien();
                        if (blurAlgoType.equals("M")) {
                            kernel = ImageChanger.average(size);
                        }
                        switch (image.get().getFormat()) {
                            case "jpeg":
                                ImageChanger.blured(input, input, kernel, 3);
                                break;
                            case "tif":
                                ImageChanger.blured(input, input, kernel, 1);
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;
    
                case "outline":
                    try {
                        switch (image.get().getFormat()) {
                            case "jpeg":
                                assert input != null;
                                ImageChanger.Outline(input, 3);
                                break;
                            case "tif":
                                assert input != null;
                                ImageChanger.Outline(input, 1);
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;
    
                case "grayLevel":
                    try {
                        ImageChanger.FromRGBtoG(input);
    
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    break;
                    
                case "":
                    break;
                default:
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}