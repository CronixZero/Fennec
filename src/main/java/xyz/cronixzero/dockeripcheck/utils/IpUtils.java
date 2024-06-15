package xyz.cronixzero.dockeripcheck.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import kong.unirest.core.Unirest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IpUtils {

  private IpUtils() throws IllegalAccessException {
    throw new IllegalAccessException("Utility class");
  }

  public static InetAddress getCurrentIp() throws UnknownHostException {
    String ip = Unirest.get("https://ifconfig.me/ip")
        .asString().getBody();

    return InetAddress.getByName(ip);
  }

}
