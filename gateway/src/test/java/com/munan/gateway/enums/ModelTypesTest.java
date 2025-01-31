package com.munan.gateway.enums;

import static org.junit.jupiter.api.Assertions.*;
import static org.wildfly.common.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

public class ModelTypesTest {

    @Test
    void testEnumValuesExist() {
        assertEquals(6, ModelTypes.values().length);
        assertNotNull(ModelTypes.valueOf("FIRST"));
        assertNotNull(ModelTypes.valueOf("PERSON"));
        assertNotNull(ModelTypes.valueOf("EDUCATION"));
        assertNotNull(ModelTypes.valueOf("COMPANY"));
        assertNotNull(ModelTypes.valueOf("LOCATION"));
        assertNotNull(ModelTypes.valueOf("SKILLS"));
    }

    @Test
    void testEnumFromString() {
        assertEquals(ModelTypes.FIRST, ModelTypes.valueOf("FIRST"));
        assertEquals(ModelTypes.PERSON, ModelTypes.valueOf("PERSON"));
        assertEquals(ModelTypes.EDUCATION, ModelTypes.valueOf("EDUCATION"));
    }

    @Test
    void testInvalidEnumThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ModelTypes.valueOf("UNKNOWN"));
    }

    @Test
    void testEnumIteration() {
        ModelTypes[] expected = {
            ModelTypes.FIRST,
            ModelTypes.PERSON,
            ModelTypes.EDUCATION,
            ModelTypes.COMPANY,
            ModelTypes.LOCATION,
            ModelTypes.SKILLS,
        };
        assertArrayEquals(expected, ModelTypes.values());
    }
}
