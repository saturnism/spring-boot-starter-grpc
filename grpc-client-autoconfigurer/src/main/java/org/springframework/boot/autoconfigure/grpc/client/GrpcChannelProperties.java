package org.springframework.boot.autoconfigure.grpc.client;

/**
 * Created by rayt on 5/17/16.
 */
public class GrpcChannelProperties {
  private boolean discover = false;
  private String host = "localhost";
  private int port = 9090;
  private boolean plaintext = true;

  public boolean isDiscover() {
    return discover;
  }

  @Override
  public String toString() {
    return "GrpcChannelProperties{" +
        "discover=" + discover +
        ", host='" + host + '\'' +
        ", port=" + port +
        ", plaintext=" + plaintext +
        '}';
  }

  public void setDiscover(boolean discover) {
    this.discover = discover;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public boolean isPlaintext() {
    return plaintext;
  }

  public void setPlaintext(boolean plaintext) {
    this.plaintext = plaintext;
  }

}
