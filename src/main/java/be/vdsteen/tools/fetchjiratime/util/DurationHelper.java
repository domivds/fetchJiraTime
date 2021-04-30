package be.vdsteen.tools.fetchjiratime.util;

public class DurationHelper {

  public static String formatSeconds(int seconds) {
    return (seconds / 3600) + "h " + ((seconds / 60) % 60) + "m";
  }
}
