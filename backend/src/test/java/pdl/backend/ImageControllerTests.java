package pdl.backend;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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
		System.out.println("--- Test createImageShouldReturnSuccess ---");
		try {
			this.mockMvc.perform(post("/images")).andExpect(status().isOk());
			System.out.println("Ok ✓");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new Exception(e);
		}
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
    @Test
    @Order(9)
    public void getProcessedImageShouldReturnSuccess() throws Exception {
//        System.out.println("--- Test getImageShouldReturnSuccess ---");
//        try {
//            this.mockMvc.perform(get("/images/0?algorithm=color&opt1=200")).andExpect(status().isOk());
//            System.out.println("Ok ✓");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            throw new Exception(e);
//        }
//    }
}
