package pdl.backend;

public class Image {
  private static Long count = Long.valueOf(0);
  private final Long id;
  private long size;
  private String name;
  private String format;
  private final byte[] data;

  //constructor
  public Image(final String name, final byte[] data) {
    id = count++;
    this.name = name;
    this.data = data;
  }

  //getters
  public long getId() {
    return id;
  }

  public long getSize() {
    return size;
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
  public void setSize(final long size) {
    this.size = size;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setFormat(final String format) {
    this.format = format;
  }
}
