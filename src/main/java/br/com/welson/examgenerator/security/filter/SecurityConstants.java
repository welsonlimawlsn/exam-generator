package br.com.welson.examgenerator.security.filter;

public class SecurityConstants {

    public static final String SECRET = "welson";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final long EXPIRATION_TIME = 86400000L; // 1 day
}
