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
import java.util.Optional;


@RestController
public class ImageController {

    private final ImageDao imageDao;
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Autowired
private ObjectMapper mapper;

    @Autowired
    public ImageController(ImageDao imageDao) {
        this.imageDao = imageDao;
    }


    @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getImage(
            @PathVariable("id") long id,
            @RequestParam(value = "algorithm", defaultValue = "") String algorithm,
            @RequestParam(value = "opt1", defaultValue = "null") String opt1,
            @RequestParam(value = "opt2", defaultValue = "null") String opt2
    ) {

//System.out.println(algorithm.compareTo("increaseLuminosity") );
//System.out.println("increaseLuminosity".getClass());
//System.out.println(algorithm);

        Optional<Image> image = this.imageDao.retrieve(id);

        SCIFIOImgPlus<UnsignedByteType> input = null;
        byte[] tab = null;
        if (image.isPresent()) {

//Conversion byte[] -> SCIFIOImgPlus<UnsignedByteType>

            try {
                input = ImageConverter.imageFromJPEGBytes(image.get().getData());
///System.out.println("no error 1 ");
            } catch (Exception e) {
//System.out.println("error 1 catch");
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }

        switch (algorithm) {
            case "increaseLuminosity":
//?algorithm=increaseLuminosity&opt1=[0,255]
//System.out.println(Integer.parseInt(opt1, 10));
                if (!(-255 <= Integer.parseInt(opt1, 10) && Integer.parseInt(opt1, 10) <= 255)) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                try {
                    assert input != null;
                    ImageChanger.EditLuminosityRGB(input, input, Integer.parseInt(opt1, 10));
//System.out.println("no error editLuminosityRGB ");
                } catch (Exception e) {
//System.out.println("error editLuminosityRGB  catch");

                }
                break;

            case "histogram":
//?algorithm=histogram&opt1=[value,saturation]

                System.out.println(opt1);
                if (!(opt1.equals("value") || opt1.equals("saturation"))) {
                    System.out.println("errooooooorrrr");
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                try {
                    ImageChanger.HistoHSV(input, opt1);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }

                break;

            case "color":
//?algorithm=color&opt1=[red,green,blue]
                if (!(0 <= Float.parseFloat(opt1))) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                try {
                    ImageChanger.Colored(input, Float.parseFloat(opt1));
//System.out.println("no error color ");
                } catch (Exception e) {
//System.out.println("error color catch");
                }

                break;

            case "blur":
//?algorithm=blur&opt1=["M","G"]&opt2=[0,+∞[
                if (!(0 <= Integer.parseInt(opt2, 10))
                        || !(opt1.equals("M")
                        || opt1.equals("G"))) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                try {
                    int size = Integer.parseInt(opt2, 10);
                    int[][] kernel = ImageChanger.gaussien();
                    if (opt1.equals("M")) {
                        System.out.println("lul");
                        kernel = ImageChanger.average(size);
                    }
                    switch (image.get().getFormat()) {
                        case "jpeg":
                            assert input != null;
                            ImageChanger.blured(input, input, kernel, 3);
                            break;
                        case "tif":
                            assert input != null;
                            ImageChanger.blured(input, input, kernel, 1);
                            break;
                    }
                    System.out.println("no error blur ");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case "outline":
//?algorithm=outline
                try {
                    switch (image.get().getFormat()) {
                        case "jpeg":
                            assert input != null;
                            ImageChanger.convolution_Gray(input, 3);
                            break;
                        case "tif":
                            assert input != null;
                            ImageChanger.convolution_Gray(input, 1);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case "grayLevel":
//?algorithm=grayLevel

                try {
                    ImageChanger.FromRGBtoG(input);
                    System.out.println("no error GrayLevel");
                } catch (Exception e) {
                    System.out.println("error GrayLevel  catch");
                }
                break;

            case "":

                break;

            default:

                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);


        }
        try {
            assert input != null;
            tab = ImageConverter.imageToJPEGBytes(input);
        } catch (Exception e) {
            System.out.println("error 2 catch");
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(tab);
//return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {
        Optional<Image> image = this.imageDao.retrieve(id);
        if (image.isPresent()) {
            this.imageDao.delete(image.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file,
                                      RedirectAttributes redirectAttributes) throws IOException {
        Image image = new Image(file.getOriginalFilename(), file.getBytes(), FilenameUtils.getExtension(file.getOriginalFilename()));
        this.imageDao.create(image);
        redirectAttributes.addAttribute("id", image.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ArrayNode getImageList() {

        ArrayNode nodes = mapper.createArrayNode();

        this.imageDao.retrieveAll().forEach(img -> {
            ObjectNode n = mapper.createObjectNode();

//Identifiant
            n.put("id", img.getId());

//Nom de fichier
            n.put("name", img.getName());

//taille de l'image
            int height = 0;
            int width = 0;
            InputStream in = new ByteArrayInputStream(img.getData());
            BufferedImage buf;

            try {
                buf = ImageIO.read(in);
                height = buf.getHeight();
                width = buf.getWidth();
            } catch (IOException e) {
                e.printStackTrace();
            }

            n.put("size", height * width);

//format
            n.put("format", img.getFormat());

//url
            n.put("url", "http://localhost:8080/images/" + img.getId());

            nodes.add(n);
        });

        return nodes;
    }
}