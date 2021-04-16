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

import pdl.ImageProcessing.*;


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

    /*
    la fonction GetImage
    prend des information dans un URL
    et modifie une image presente dans l'ImageDao
    que l'on trouve grace a son Id
    elle prend en paramettre :
    -un entier id (pour retrouver l'Image a modifier)
    -un String algorithm
    (pour trouver l'algorithme de modification d'image a appliquer sur l'image)
    -un String opt1
    (qui sert de premier paramettre a l'algorithme )
    -un String opt2
    (qui sert de deuxieme paramettre a l'algorithme si l'algorithme a besoin d'un dexieme paramettre)
    la fonction GetImage renvoie des erreur :
    si la requete n'est pas bonne elle renvoi un code d'erreur 400
    si les options ne correspondent pas au parametre de la fonction choisi
    elle renvoi aussi un code  d'erreur 400
    si la fonction choisie ne fonctionne pas elle renvoie un code d'erreur 500
    elle renvoi si l'image n'est pas trouver un code d'erreur 404
    et si la fonction compile correctement elle renvoi un code 200 .
    */
    @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getImage(
            @PathVariable("id") long id,
            @RequestParam(value = "algorithm", defaultValue = "") String algorithm,
            @RequestParam(value = "opt1", defaultValue = "null") String opt1,
            @RequestParam(value = "opt2", defaultValue = "null") String opt2
    ) {

        Optional<Image> image = this.imageDao.retrieve(id);

        SCIFIOImgPlus<UnsignedByteType> input = null;
        byte[] tab = null;
        if (image.isPresent()) {
            try {
                input = ImageConverter.imageFromJPEGBytes(image.get().getData());
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        switch (algorithm) {
            case "increaseLuminosity":

                if (!(-255 <= Integer.parseInt(opt1, 10) && Integer.parseInt(opt1, 10) <= 255)) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                try {
                    Luminosity.EditLuminosityRGB(input, input, Integer.parseInt(opt1, 10));
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;
            case "histogram":
                if (!(opt1.equals("value") || opt1.equals("saturation"))) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                try {
                    Histograme.aplanir_histograme_HSV(input, opt1);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;
            case "color":
                if (!(0 <= Float.parseFloat(opt1))) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                try {
                    Color.Colored(input, Float.parseFloat(opt1));
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                break;
            case "blur":
                if (!(0 <= Integer.parseInt(opt2, 10))
                        || !(opt1.equals("M")
                        || opt1.equals("G"))) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                try {
                    int size = Integer.parseInt(opt2, 10);
                    int[][] kernel = Blur.gaussien();
                    if (opt1.equals("M")) {
                        kernel = Blur.average(size);
                    }
                    switch (image.get().getFormat()) {
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
                    switch (image.get().getFormat()) {
                        case "jpeg":
                            assert input != null;
                            Outline.Outline(input, 3);
                            break;
                        case "tif":
                            assert input != null;
                            Outline.Outline(input, 1);
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
            case "":
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            tab = ImageConverter.imageToJPEGBytes(input);
        } catch (Exception e) {
            System.out.println("error 2 catch");
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(tab);
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
        if(image.getFormat().equals("jpeg") || image.getFormat().equals("tif")){
            this.imageDao.create(image);
            redirectAttributes.addAttribute("id", image.getId());
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        else{
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
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

            //format
            n.put("format", img.getFormat());

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

            if(img.getFormat().equals("jpeg")){
                String msg = Integer.toString(height)+'*'+Integer.toString(width)+"*3";
                n.put("size", msg);
            }
            else{
                String msg = Integer.toString(height)+'*'+Integer.toString(width)+"*1";
                n.put("size", msg);
            }

            //url
            n.put("url", "http://localhost:8080/images/" + img.getId());

            nodes.add(n);
        });

        return nodes;
    }
}
