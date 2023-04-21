package com.github.dericksm.aspectjmdc;

import java.io.Serializable;

public class GenericResponse implements Serializable {

    private final String one;
    private final String two;
    private final String three;

    public GenericResponse(String one, String two, String three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }
}
