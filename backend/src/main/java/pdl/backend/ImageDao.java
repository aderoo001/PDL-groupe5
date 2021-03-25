package pdl.backend;


import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ImageDao implements Dao<Image> {

    private final Map<Long, Image> images = new HashMap<>();

    public ImageDao() {
        File dir = new File("src/main/resources/images");

        //check if file exists
        try {
            if (!dir.exists()) throw new NullPointerException();

            int fileCount = dir.listFiles().length;

            for (int i = 0; i < fileCount; i++) {
                final ClassPathResource imgFile = new ClassPathResource("images/" + dir.list()[i]);
                byte[] fileContent;


                fileContent = Files.readAllBytes(imgFile.getFile().toPath());
                Image img = new Image(imgFile.getFilename(), fileContent, FilenameUtils.getExtension(imgFile.getFilename()));
                images.put(img.getId(), img);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Image> retrieve(final long id) {
        if (this.images.get(id) != null) return Optional.of(this.images.get(id));
        return Optional.empty();
    }

    @Override
    public List<Image> retrieveAll() {
        ArrayList<Image> images = new ArrayList<>();
        this.images.forEach((aLong, image) -> images.add(image));
        return images;
    }

    @Override
    public void create(final Image img) {
        this.images.putIfAbsent(img.getId(), img);
    }

    @Override
    public void update(final Image img, final String[] params) {
        // Not used
    }

    @Override
    public void delete(final Image img) {
        AtomicLong id = new AtomicLong(-1);
        this.images.forEach((aLong, image) -> {
            if (image == img) {
                id.set(aLong);
            }
        });
        if (id.get() != -1) {
            this.images.remove(id.get(), img);
        }
    }
}
