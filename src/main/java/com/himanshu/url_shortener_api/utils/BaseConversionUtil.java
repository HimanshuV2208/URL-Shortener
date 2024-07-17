package com.himanshu.url_shortener_api.utils;

import lombok.NoArgsConstructor;

import java.util.InvalidPropertiesFormatException;

/**
 * Provides encoding and decoding methods Base10 to Base62 and vice versa
 */
@NoArgsConstructor
public class BaseConversionUtil {

    /**
     * Randomized a-z A-Z 0-9 characters. Each character appears only once in the string
     */
    public static final String ALLOWED_CHARACTERS_STRING = "Mheo9PI2qNs5Zpf80TBn7lmRbtQ4YKXHvwAEWxuzdra316OJigGLSVUCyFjkDc";
    private static final int BASE = ALLOWED_CHARACTERS_STRING.length();

    /**
     * @param id Base10 number of type Long
     * @return base62 encoded string representation of input Long
     */
    public static String IdToB62EncodedString(Long id) {
        StringBuilder encodedString = new StringBuilder();
        if(id == 0) return String.valueOf(ALLOWED_CHARACTERS_STRING.charAt(0));
        while (id > 0) {
            encodedString.append(ALLOWED_CHARACTERS_STRING.charAt((int) (id % BASE)));
            id = id / BASE;
        }
        return encodedString.reverse().toString();
    }

    /**
     * @param str Base62 encoded string
     * @return decoded Base10 number of type Long
     */
    public static Long B62EncodedStringToId(String str) throws InvalidPropertiesFormatException {
        long num = 0L;
        for (int i = 0; i < str.length(); i++) {
            num = num * BASE + ALLOWED_CHARACTERS_STRING.indexOf(str.charAt(i));
            if (num < 0) throw new InvalidPropertiesFormatException("String is too long");
        }
        return num;
    }
}
