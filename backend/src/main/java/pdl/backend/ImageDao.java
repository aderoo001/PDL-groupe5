package pdl.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

@Repository
public class ImageDao implements Dao<Image> {

  private final Map<Long, Image> images = new HashMap<>();

  public ImageDao() {
    // placez une image test.jpg dans le dossier "src/main/resources" du projet
    final ClassPathResource imgFile = new ClassPathResource("images/test.jpeg");
    byte[] fileContent;
    try {
      fileContent = Files.readAllBytes(imgFile.getFile().toPath());
      Image img = new Image("logo.jpeg", fileContent);
      images.put(img.getId(), img);
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
