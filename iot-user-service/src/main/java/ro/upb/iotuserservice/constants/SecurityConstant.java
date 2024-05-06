package ro.upb.iotuserservice.constants;

public class SecurityConstant {
    public static final long EXPIRATION_TIME =
            (24 * 60 * 60 * 1000);

    public static final long REFRESH_TOKEN_EXP =
            (24 * 60 * 60 * 1000);
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String Company_LLC = "Iot-Platform";
    public static final String Company_ADMINISTRATION = "IoT Platform Portal";
    public static final String AUTHORITIES = "authorities";
}
