package ro.upb.iotuserservice.constants;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = (24 * 60 * 60 * 1000); // 30 * 60 * 1000; // 30 minutes
    public static final long REFRESH_TOKEN_EXP = 7 * 24 * 60 * 60 * 1000; // 7 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String Company_LLC = "Iot-Platform";
    public static final String Company_ADMINISTRATION = "IoT Platform Portal";
    public static final String AUTHORITIES = "authorities";
}
