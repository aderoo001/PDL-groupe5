package pdl.backend;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    // Test  Get /images/{id} et get /images/{id}?opt [9-19]
    // Return 200
    @Test
    @Order(1)
    public void editLuminosityMinShouldReturnSuccess() throws Exception {
        System.out.println("--- Test editLuminosityMinShouldReturnSuccess ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=increaseLuminosity&opt1=-256")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void editLuminosityMaxShouldReturnSuccess() throws Exception {
        System.out.println("--- Test editLuminosityMaxShouldReturnSuccess ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=increaseLuminosity&opt1=255")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void histogramValueShouldReturnSuccess() throws Exception {
        System.out.println("--- Test histogramShouldReturnSuccess ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=histogram&opt1=value")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void histogramSaturationShouldReturnSuccess() throws Exception {
        System.out.println("--- Test histogramShouldReturnSuccess ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=histogram&opt1=saturation")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void editColorShouldReturnSuccess() throws Exception {
        System.out.println("--- Test editColorShouldReturnSuccess ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=color&opt1=180")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void editBlurShouldReturnSuccess() throws Exception {
        System.out.println("--- Test editBlurShouldReturnSuccess ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=blur&opt1=0&opt2=13")).andExpect(status().isOk());
            this.mockMvc.perform(get("/images/0?algorithm=blur&opt1=1&opt2=13")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void outlineShouldReturnSuccess() throws Exception {
        System.out.println("--- Test outlineShouldReturnSuccess ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=outline")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void grayLevelShouldReturnSuccess() throws Exception {
        System.out.println("--- Test grayLevelShouldReturnSuccess ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=grayLevel")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    // Test  Get /images/{id} et get /images/{id}?opt [9-19]
    // Return 400
    @Test
    @Order(1)
    public void inexistantAlgorithmShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test inexistantAlgorithmShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=lul&opt1=500")).andExpect(status().isBadRequest());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void editLuminosityWithoutArgShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test editLuminosityWithoutArgShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=editLuminosity")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void editLuminosityMinShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test editLuminosityMinShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=increaseLuminosity&opt1=-500")).andExpect(status().isBadRequest());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void editLuminosityMaxShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test editLuminosityMaxShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=increaseLuminosity&opt1=500")).andExpect(status().isBadRequest());
            this.mockMvc.perform(get("/images/0?algorithm=increaseLuminosity&opt1=100&opt2=lul")).andExpect(status().isBadRequest());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void editLuminosityTooMuchArgsShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test editLuminosityTooMuchArgsShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=increaseLuminosity&opt1=100&opt2=lul")).andExpect(status().isBadRequest());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void histogramWithoutArgShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test histogramWithoutArgShouldReturnSuccess ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=histogram")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void histogramOutOfRangeShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test histogramOutOfRangeShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=histogram&opt1=2")).andExpect(status().isBadRequest());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void histogramWrongArgShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test histogramWrongArgShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=histogram&opt1=zulul")).andExpect(status().isBadRequest());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void histogramTooMuchArgsShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test histogramTooMuchArgsShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=histogram&opt1=0&opt2=lul")).andExpect(status().isBadRequest());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void colorWithoutArgShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test colorWithoutArgShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=color")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void colorMinShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test colorMinShouldReturnSuccess ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=color&opt1=-1")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void colorMaxShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test colorMaxShouldReturnSuccess ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=color&opt1=361")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void colorTooMuchArgsShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test colorTooMuchArgsShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=color&opt1=180&opt2=lul")).andExpect(status().isBadRequest());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void blurWithoutArgShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test blurWithoutArgShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=blur")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void blurOnlyOneArgShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test blurOnlyOneArgShouldReturnSuccess ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=blur&opt1=0")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void blurArg1WrongShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test blurArg1WrongShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=blur&opt1=5&opt2=3")).andExpect(status().isBadRequest());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void blurArg2MinShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test blurArg2MinShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=blur&opt1=0&opt2=-1")).andExpect(status().isBadRequest());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void outlineTooMuchArgsShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test outlineTooMuchArgsShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=outline&opt1=180&opt2=lul")).andExpect(status().isBadRequest());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void graylevelTooMuchArgsShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test graylevelTooMuchArgsShouldReturnBadRequest ---");

        try {
            this.mockMvc.perform(get("/images/0?algorithm=graylevel&opt1=180&opt2=lul")).andExpect(status().isBadRequest());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    // Test  Get /images/{id} et get /images/{id}?opt [9-19]
    // Return 404
    @Test
    @Order(1)
    public void getImageWithIncreaseLuminosityReturnNotFound() throws Exception {
        System.out.println("--- Test getImageWithIncreaseLuminosityShouldReturnNotFound ---");

        try {
            this.mockMvc.perform(get("/images/50?algorithm=increaseLuminosity&opt1=10")).andExpect(status().isNotFound());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void getImageWithHistogramReturnNotFound() throws Exception {
        System.out.println("--- Test getImageWithHistogramShouldReturnNotFound ---");

        try {
            this.mockMvc.perform(get("/images/50?algorithm=histogram&opt1=value")).andExpect(status().isNotFound());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void getImageWithColorReturnNotFound() throws Exception {
        System.out.println("--- Test getImageWithColorShouldReturnNotFound ---");

        try {
            this.mockMvc.perform(get("/images/50?algorithm=color&opt1=10")).andExpect(status().isNotFound());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void getImageWithBlurReturnNotFound() throws Exception {
        System.out.println("--- Test getImageWithBlurShouldReturnNotFound ---");

        try {
            this.mockMvc.perform(get("/images/50?algorithm=blur&opt1=0&opt2=3"))
                    .andExpect(status().isNotFound());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void getImageWithOutlineReturnNotFound() throws Exception {
        System.out.println("--- Test getImageWithOutlineShouldReturnNotFound ---");

        try {
            this.mockMvc.perform(get("/images/50?algorithm=outline"))
                    .andExpect(status().isNotFound());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void getImageWithGrayLevelReturnNotFound() throws Exception {
        System.out.println("--- Test getImageWithGrayLevelShouldReturnNotFound ---");

        try {
            this.mockMvc.perform(get("/images/50?algorithm=grayLevel"))
                    .andExpect(status().isNotFound());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    // Test  Get /images/{id} et get /images/{id}?opt [9-19]
    // Return 500
    @Test
    @Order(1)
    public void getTiffWithHistogramShouldRetourn500() throws Exception {
        System.out.println("--- Test getTiffWithHistogramShouldRetourn500 ---");

        try {
            this.mockMvc.perform(get("/images/1?algorithm=color&opt1=saturation"))
                    .andExpect(status().isInternalServerError());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void getTiffWithColorShouldRetourn500() throws Exception {
        System.out.println("--- Test getTiffWithColorShouldRetourn500 ---");

        try {
            this.mockMvc.perform(get("/images/1?algorithm=color&opt1=180"))
                    .andExpect(status().isInternalServerError());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(1)
    public void getTiffWithGrayLevelShouldRetourn500() throws Exception {
        System.out.println("--- Test getTiffWithGrayLevelShouldRetourn500 ---");

        try {
            this.mockMvc.perform(get("/images/1?algorithm=grayLevel"))
                    .andExpect(status().isInternalServerError());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }
}
