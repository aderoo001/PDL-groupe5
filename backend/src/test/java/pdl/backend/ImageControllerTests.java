package pdl.backend;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.core.io.ClassPathResource;
import java.nio.file.Files;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class ImageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    //test besoin 4, images par défaut présentes ?
    public void getImageListShouldReturnCorrectValues() throws Exception {
        System.out.println("--- Test getImageList ---");
        String tmp = "[{'id':0,'name':'test.jpeg','size':50362,'format':'jpeg','url':'http://localhost:8080/images/0'},{'id':1,'name':'cheval2.jpeg','size':50424,'format':'jpeg','url':'http://localhost:8080/images/1'}]";
        try {
            this.mockMvc.perform(get("/images")).andExpect(content().json(tmp));
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(2)
    //test besoin 6, 404 not found
    public void getImageShouldReturnNotFound() throws Exception {
        System.out.println("--- Test getImageShouldReturnNotFound ---");
        try {
            this.mockMvc.perform(get("/images/50")).andExpect(status().isNotFound());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(3)
    //test besoin 6, 200 ok
    public void getImageShouldReturnSuccess() throws Exception {
        System.out.println("--- Test getImageShouldReturnSuccess ---");
        try {
            this.mockMvc.perform(get("/images/0")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(4)
    //test besoin 5, 201 created
    public void createImageShouldReturnSuccess() throws Exception {
		System.out.println("--- Test createImageShouldReturnSuccess ---");
        final ClassPathResource imgFile = new ClassPathResource("images/reconnues/aube.jpeg");
        byte[] fileContent;
        fileContent = Files.readAllBytes(imgFile.getFile().toPath());
        MockMultipartFile tmp = new MockMultipartFile("file", "mock.jpeg", "image/jpeg", fileContent);
		try {
            this.mockMvc.perform(MockMvcRequestBuilders.multipart("/images").file(tmp)).andExpect(status().isCreated());
			System.out.println("Ok ✓");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new Exception(e);
		}
    }

    @Test
    @Order(5)
    //test besoin 5, 415 unsupported media type
    public void createImageShouldReturnUnsupportedMediaType() throws Exception {
        System.out.println("--- Test createImageShouldReturnUnsupportedMediaType ---");
        final ClassPathResource imgFile = new ClassPathResource("images/non_reconnues/image.txt");
        byte[] fileContent;
        fileContent = Files.readAllBytes(imgFile.getFile().toPath());
        MockMultipartFile tmp = new MockMultipartFile("file", "mock.txt", "text/plain", fileContent);
		try {
            this.mockMvc.perform(MockMvcRequestBuilders.multipart("/images").file(tmp)).andExpect(status().isUnsupportedMediaType());
			System.out.println("Ok ✓");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new Exception(e);
		}
    }

    @Test
    @Order(6)
    //test besoin 7, 404 not found
    public void deleteImageShouldReturnNotFound() throws Exception {
        System.out.println("--- Test deleteImageShouldReturnNotFound ---");
        try {
            this.mockMvc.perform(delete("/images/50")).andExpect(status().isNotFound());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(7)
    //test besoin 7, 200 ok
    public void deleteImageShouldReturnSucces() throws Exception {
        System.out.println("--- Test deleteImageShouldReturnSuccess ---");
        try {
            this.mockMvc.perform(delete("/images/0")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }
}
