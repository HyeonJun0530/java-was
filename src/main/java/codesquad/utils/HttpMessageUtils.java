package codesquad.utils;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static codesquad.utils.StringUtils.CARRIAGE_RETURN;

public class HttpMessageUtils {

    public static final String DECODING_CHARSET = "UTF-8";

    private HttpMessageUtils() {
    }

    public static String readLine(final InputStream reader) {
        StringBuilder sb = new StringBuilder();
        try {
            int c;
            while ((c = reader.read()) != -1) {
                if (c == CARRIAGE_RETURN) {
                    reader.read();
                    break;
                }
                sb.append((char) c);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
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
