package pdl.backend;

import java.util.Optional;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import org.apache.commons.io.FilenameUtils;

import java.util.Arrays;
import java.util.Optional;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
  public ResponseEntity<?> getImage(@PathVariable("id") long id) {
    Optional<Image> image = this.imageDao.retrieve(id);
    if (image.isPresent()) return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(image.get().getData());
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
      img.setSize(height*width);

      //format
      String format = FilenameUtils.getExtension(img.getName());
      
      n.put("format", format);
      img.setFormat(format);

      //url
      n.put("url", "http://localhost:8080/images/"+img.getId());

      nodes.add(n);
    });

    return nodes;
  }
}