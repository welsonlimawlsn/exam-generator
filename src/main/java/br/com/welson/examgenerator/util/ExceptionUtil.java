package br.com.welson.examgenerator.util;

import br.com.welson.examgenerator.exception.ResourceNotFoundException;

/**
 * @author Welson Teles on 3/19/2018
 */
public class ExceptionUtil {

    public static ResourceNotFoundException throwResourceNotFoundException() {
        return new ResourceNotFoundException("Resource not found");
    }
}
