package br.com.welson.examgenerator.security.filter;

class SecurityConstants {

    static final String SECRET = "welson";
    static final String TOKEN_PREFIX = "Bearer ";
    static final String HEADER_STRING = "Authorization";
    static final long EXPIRATION_TIME = 86400000L; // 1 day
}
