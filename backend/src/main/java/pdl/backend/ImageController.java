package pdl.backend;

import java.awt.image.BufferedImage;
import java.util.Optional;
import javax.imageio.ImageIO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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
  
    @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {
        Optional<Image> image = this.imageDao.retrieve(id);
        if (image.isPresent()) {
            this.imageDao.delete(image.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

      n.put("size", height*width);

      //format
      n.put("format", img.getFormat());
      return nodes;
    }
  }
}
                                        
