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

  
  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> getImage(
  @PathVariable("id") long id,
  @RequestParam(value = "algorithm", defaultValue = "") String algorithm,
  @RequestParam(value = "opt1",defaultValue = "null") String opt1,
  @RequestParam(value = "opt2",defaultValue = "null") String opt2
  ) {

    System.out.println(algorithm.compareTo("increaseLuminosity") );
    //System.out.println("increaseLuminosity".getClass());
    System.out.println(algorithm);

    Optional<Image> image = this.imageDao.retrieve(id);

    SCIFIOImgPlus<UnsignedByteType> input = null;
    byte[] tab = null;
    if (image.isPresent()) {

      //Conversion byte[] -> SCIFIOImgPlus<UnsignedByteType>
      
      try{
        input = ImageConverter.imageFromJPEGBytes(image.get().getData());
        System.out.println("no error 1 ");
      }catch (Exception e) {
        System.out.println("error 1 catch");
      }
    }else{
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    switch (algorithm) {
      case "increaseLuminosity":
      //?algorithm=increaseLuminosity&opt1=[0,255]
      System.out.println(Integer.parseInt(opt1, 10));
      if(!(0<Integer.parseInt(opt1, 10) && Integer.parseInt(opt1, 10)<255)){
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }

      try {
      ImageChanger.EditLuminosityRGB(input, input, Integer.parseInt(opt1, 10));
      System.out.println("no error editLuminosityRGB ");
      } catch (Exception e) {
        System.out.println("error editLuminosityRGB  catch");
        
      }
      break;

      case "histogram":
      //?algorithm=histogram&opt1=[value,saturation]

        System.out.println(opt1);
        /*if(!(opt1 == "value" || opt1 == "saturation" ) ){
          System.out.println("errooooooorrrr");
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
          
        }*/

        try{
          ImageChanger.HistoHSV(input,opt1);
          System.out.println("no error histograme ");
        }catch(Exception e){
          System.out.println("error histograme  catch");
        }

      break;

      case "color":
      //?algorithm=color&opt1=[red,green,blue]
        
        System.out.println(opt1);
        if(!(0<= Float.parseFloat(opt1) ) ){
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try{
          ImageChanger.Colored(input,Float.parseFloat(opt1));
          System.out.println("no error color ");
        }catch(Exception e){
          System.out.println("error color catch");
        }

      break;

      case "blur":
      //?algorithm=blur&opt1=[0,1]&opt2=[0,+∞[
        
        System.out.println(Integer.parseInt(opt1, 10));
        System.out.println(Integer.parseInt(opt2, 10));
        if(   !(0<=Integer.parseInt(opt2, 10)) || !(opt1.equals("M") || opt1.equals("G")) ){
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try{
          ImageChanger.Blured(input,opt1,Integer.parseInt(opt2, 10));
          System.out.println("no error blur ");
        }catch(Exception e){
          System.out.println("error blur catch");
        }

      break;

      case "outline" :
      //?algorithm=outline

        try{
          ImageChanger.Outline(input);
          System.out.println("no error outline ");
        }catch(Exception e){
          System.out.println("error outline  catch");
        }

      break;

      case "grayLevel":
      //?algorithm=grayLevel

        try{
        ImageChanger.FromRGBtoG(input);
        System.out.println("no error GrayLevel");
        }catch(Exception e){
          System.out.println("error GrayLevel  catch");
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