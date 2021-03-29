package pdl.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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


import static net.imglib2.img.array.ArrayImgs.unsignedBytes;
//********** */
import io.scif.img.SCIFIOImgPlus;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/******** */
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


@RestController
public class ImageController {

  @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

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
  @RequestParam(value = "opt1",defaultValue = "null") String opt1,
  @RequestParam(value = "opt2",defaultValue = "null") String opt2
  ) {

    Optional<Image> image = this.imageDao.retrieve(id);

    SCIFIOImgPlus<UnsignedByteType> input = null;
    byte[] tab = null;
    if (image.isPresent()) {

      //Conversion byte[] -> SCIFIOImgPlus<UnsignedByteType>
      
      try{
        input = ImageConverter.imageFromJPEGBytes(image.get().getData());
      }catch (Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }else{
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    switch (algorithm) {
      case "increaseLuminosity":
      
      if(!(-255<=Integer.parseInt(opt1, 10) && Integer.parseInt(opt1, 10)<=255)){
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      try {
      ImageChanger.EditLuminosityRGB(input, input, Integer.parseInt(opt1, 10));
      } catch (Exception e) {
        e.printStackTrace(); les erreur 
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
      break;

      case "histogram":
        if(!(opt1.equals("value") ||opt1.equals("saturation") ) ){
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
          
        }
        try{
          ImageChanger.HistoHSV(input,opt1);
        }catch(Exception e){
          e.printStackTrace();
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

      break;

      case "color":
        if(!(0<= Float.parseFloat(opt1) ) ){
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try{
          ImageChanger.Colored(input,Float.parseFloat(opt1));
        }catch(Exception e){
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
            int[][] kernel = ImageChanger.gaussien();
            if (opt1.equals("M")) {
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
        try{
        ImageChanger.FromRGBtoG(input);
        
        }catch(Exception e){
          e.printStackTrace();
          return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
      break;

      case "":

      break;

      default:

      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

      
    }
      try{
        tab = ImageConverter.imageToJPEGBytes(input);
      } catch (Exception e) {
        e.printStackTrace();
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
        this.imageDao.create(image);
        redirectAttributes.addAttribute("id", image.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ArrayNode getImageList() throws IOException {

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