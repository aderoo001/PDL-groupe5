package pdl.backend;

public class Image {

  private static Long count = Long.valueOf(0);
  private final Long id;
  private String name;
  private String format;
  private final byte[] data;

  //constructor
  public Image(final String name, final byte[] data, final String format) {
    id = count++;
    this.name = name;
    this.data = data;
    this.format = format;
  }

  //getters
  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getFormat() {
    return format;
  }

  public byte[] getData() {
    return data;
  }

  //setters
  public void setName(final String name) {
    this.name = name;
  }

}
