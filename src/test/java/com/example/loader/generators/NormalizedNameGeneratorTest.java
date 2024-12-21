package com.example.loader.generators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class NormalizedNameGeneratorTest {

    NormalizedNameGenerator subject;

    @BeforeEach
    void setUp() {
        subject = new NormalizedNameGenerator();
    }

    @Test
    void testGenerateNormalizedName() {
        String name;
        String result;

        name =  "Používateľ";
        result = subject.generateNormalizedName(name);

        assertEquals("pouzivatel", result);

        // try once again with same name
        result = subject.generateNormalizedName(name);

        assertEquals("pouzivatel1", result);

        // try similar name
        name =  "PouŽivateĽ";
        result = subject.generateNormalizedName(name);

        assertEquals("pouzivatel2", result);

        // try spaces
        name =  "hello world";
        result = subject.generateNormalizedName(name);

        assertEquals("hello-world", result);

        // try spaces and dialect
        name =  "hĚllÔ wÔrld";
        result = subject.generateNormalizedName(name);

        assertEquals("hello-world1", result);

        // try names with problematic symbols
        name =  "h`e@ll{(o w$o#r/l/d";
        result = subject.generateNormalizedName(name);

        assertEquals("hello-world2", result);
    }
}