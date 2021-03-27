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

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class ImageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void getImageListShouldReturnSuccess() throws Exception {
        System.out.println("--- Test getImageList ---");
        try {
            this.mockMvc.perform(get("/images")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(2)
    public void getImageShouldReturnNotFound() throws Exception {
        System.out.println("--- Test getImageShouldReturnNotFound ---");
        try {
            this.mockMvc.perform(get("/images/15")).andExpect(status().isNotFound());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(3)
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
    public void deleteImageShouldReturnBadRequest() throws Exception {
        System.out.println("--- Test deleteImageShouldReturnBadRequest ---");
        try {
            this.mockMvc.perform(delete("/images/george")).andExpect(status().isBadRequest());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(5)
    public void deleteImageShouldReturnNotFound() throws Exception {
        System.out.println("--- Test deleteImageShouldReturnNotFound ---");
        try {
            this.mockMvc.perform(delete("/images/15")).andExpect(status().isNotFound());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(6)
    public void deleteImageShouldReturnSuccess() throws Exception {
        System.out.println("--- Test deleteImageShouldReturnSuccess ---");
        try {
            this.mockMvc.perform(delete("/images/0")).andExpect(status().isOk());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    @Test
    @Order(7)
    public void createImageShouldReturnSuccess() throws Exception {
        // TODO
//		System.out.println("--- Test createImageShouldReturnSuccess ---");
//		try {
//			this.mockMvc.perform(post("/images")).andExpect(status().isOk());
//			System.out.println("Ok ✓");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			throw new Exception(e);
//		}
    }

    @Test
    @Order(8)
    public void createImageShouldReturnUnsupportedMediaType() throws Exception {
        // TODO
//		System.out.println("--- Test createImageShouldReturnUnsupportedMediaType ---");
//		try {
//			this.mockMvc.perform(post("/images")).andExpect(status().isUnsupportedMediaType());
//			System.out.println("Ok ✓");
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			throw new Exception(e);
//		}
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
            this.mockMvc.perform(get("/images/1?algorithm=grayLevek"))
                    .andExpect(status().isInternalServerError());
            System.out.println("Ok ✓");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }
}
