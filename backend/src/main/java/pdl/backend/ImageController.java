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
            @RequestParam Map<String, String> parameters
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

        algorithmSelector.selector(input, image, parameters);

        try {
            tab = ImageConverter.imageToJPEGBytes(input);
        } catch (Exception e) {
            System.out.println("getImage: error, conversion failed");
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

            //Identifier
            n.put("id", img.getId());

            //file name
            n.put("name", img.getName());

            //format
            n.put("format", img.getFormat());

            //image size
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


    //pour test ; curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X POST --data   '{"algorithmList":{ "algorithm" : "grayLevel" }, { "algorithm" : "blur", "opt1" : "M", "opt2": "5"}' "http://localhost:8080/images/custom/1"
    @RequestMapping(value = "/images/custom/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> runCustomAlgorithm(@PathVariable("id") long id, @RequestBody CustomImageProcessingAlgo customAlgo){
        //Map<String,String>[] algorithmList = customAlgo.getAlgorithmList();
        Map<String,String> algorithmList = customAlgo.getAlgorithmList();

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

        /*
        for(int i=0; i<algorithmList.length; i++){
            algorithmSelector.selector(input, image, algorithmList[i]);
        }
        */
        algorithmSelector.selector(input, image, algorithmList);

        try {
            tab = ImageConverter.imageToJPEGBytes(input);
        } catch (Exception e) {
            System.out.println("runCustomAlgorithm: error, conversion failed");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(tab);
    }
}
