package pdl.backend;

import java.io.IOException;

import java.util.Optional;

import javax.imageio.ImageIO;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.awt.image.BufferedImage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

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
  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> getImage(@PathVariable("id") long id) {
    Optional<Image> image = this.imageDao.retrieve(id);
    if (image.isPresent()) return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(image.get().getData());
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }*/

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> getImage(
  @PathVariable("id") long id,
  @RequestParam(value = "algorithm", defaultValue = "nothing") String algorithm,
  @RequestParam(value = "gain",defaultValue = "0") String gain) {

    System.out.println(algorithm.compareTo("increaseLuminosity") );
    //System.out.println("increaseLuminosity".getClass());
    System.out.println(algorithm);
    switch (algorithm) {
      case "increaseLuminosity":
      try {
        long gain_L = Long.parseLong(gain, 10);
        Image_lum(id,gain_L);
      } catch (Exception e) {
        //TODO: handle exception
        
      }
      break;

      default:
      System.out.println("not switch !!!!");
      Optional<Image> image = this.imageDao.retrieve(id);
      if (image.isPresent()) return ResponseEntity.ok()
              .contentType(MediaType.IMAGE_JPEG)
              .body(image.get().getData());
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  public ResponseEntity<?> Image_lum(long id,long gain) throws IOException  {
    System.out.println("img_lum");
    Optional<Image> imgFile = imageDao.retrieve(id);
    if(imgFile.isEmpty()){return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
    Image img = imgFile.get();//trouver l'image dans imageDao.

    SCIFIOImgPlus<UnsignedByteType> input = null;
    try{
      System.out.println("no error 1");
      input = ImageConverter.imageFromJPEGBytes(img.getData());
      System.out.println("no error 1");

    }catch (Exception e) {
      System.out.println("error 1 catch");

    }
    GrayLevelProcessing.luminosité((Img<UnsignedByteType>) input,(int) gain);//modifier l'image.

    byte[] tab = null;
    try{
      tab = ImageConverter.imageToJPEGBytes(input);
      System.out.println("no error 2");

    } catch (Exception e) {
      System.out.println("error 2 catch");

    }

    final String outPath = (char)id + "ImgLum.jpg" ;
    Image Final = new Image(outPath,tab);

    //partie de fin pour renvoyer l'image modifiée .
    //on utilise imgFile pour le moment pour que ça compile correctement
    return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(Final.getData());
    //return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  
  }


  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {
    Optional<Image> image =this.imageDao.retrieve(id);
    if (image.isPresent()) {
      this.imageDao.delete(image.get());
      return new ResponseEntity<>(HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file,
                                    RedirectAttributes redirectAttributes) throws IOException {
    Image image = new Image(file.getOriginalFilename(), file.getBytes());
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

      n.put("size", height*width);

      //n.put("poids", img.getData().length);

      //format
      n.put("format", FilenameUtils.getExtension(img.getName()));

      //url
      n.put("url", "http://localhost:8080/images/"+img.getId());

      nodes.add(n);
    });

    return nodes;
  }
}

/*
      File file = new File("images/"+img.getId());
      String path = file.getAbsolutePath();
      System.out.println(path);

      final ArrayImgFactory<UnsignedByteType> factory = new ArrayImgFactory<>(new UnsignedByteType());
		  final ImgOpener imgOpener = new ImgOpener();
		  final Img<UnsignedByteType> input = (Img<UnsignedByteType>) imgOpener.openImgs(path, factory).get(0);
		  imgOpener.context().dispose();

      int iw = (int) input.max(0);
		  int ih = (int) input.max(1);
      System.out.println(iw);
      System.out.println(ih);
*/

/*
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

      n.put("size", height*width);
*/

/*
import net.imglib2.RandomAccess;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import io.scif.SCIFIO;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.ImgSaver;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.exception.IncompatibleTypeException;
import java.io.File;
import net.imglib2.view.Views;
import net.imglib2.view.IntervalView;
import net.imglib2.loops.LoopBuilder;
*/