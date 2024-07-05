package codesquad.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HttpMessageUtils {

    private HttpMessageUtils() {
    }

    public static String getCurrentTime() {
        LocalDateTime localDateTime = LocalDateTime.now();

        // 로컬 날짜 및 시간을 GMT 시간대로 변환
        ZonedDateTime gmtDateTime = localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("GMT"));

        // HTTP Date 헤더 형식으로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);

        return gmtDateTime.format(formatter);
    }
}
