package be.vdsteen.tools.fetchjiratime.cmdline;

import com.beust.jcommander.IStringConverter;

import java.time.LocalDate;

public class LocalDateConvertor implements IStringConverter<LocalDate> {
  @Override
  public LocalDate convert(String value) {
    if("today".equalsIgnoreCase(value)) return LocalDate.now();
    try {
      return LocalDate.parse(value);
    }
    catch (Exception exception) {
      throw new RuntimeException("No valid date format '" + value + "'", exception);
    }
  }

}
