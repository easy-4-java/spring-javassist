package org.springframework.javassist.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class RandomStringTest {

    @Test
    public void shouldCreateWithDefaultLength() {
        RandomString rs = new RandomString();
        String result = rs.nextString();
        assertEquals(RandomString.DEFAULT_LENGTH, result.length());
    }

    @Test
    public void shouldCreateWithCustomLength() {
        RandomString rs = new RandomString(16);
        String result = rs.nextString();
        assertEquals(16, result.length());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectZeroLength() {
        new RandomString(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectNegativeLength() {
        new RandomString(-1);
    }

    @Test
    public void shouldMakeStaticString() {
        String result = RandomString.make();
        assertEquals(RandomString.DEFAULT_LENGTH, result.length());
    }

    @Test
    public void shouldMakeStaticStringWithLength() {
        String result = RandomString.make(12);
        assertEquals(12, result.length());
    }

    @Test
    public void shouldProduceUniqueStrings() {
        RandomString rs = new RandomString(32);
        String a = rs.nextString();
        String b = rs.nextString();
        assertNotEquals(a, b);
    }

    @Test
    public void shouldHashIntegerDeterministically() {
        String first = RandomString.hashOf(42);
        String second = RandomString.hashOf(42);
        assertEquals(first, second);
    }

    @Test
    public void shouldHashDifferentIntegersDifferently() {
        String a = RandomString.hashOf(1);
        String b = RandomString.hashOf(2);
        // They may collide in theory, but with a 62-char alphabet this is
        // extremely unlikely for small integers.
        assertNotEquals(a, b);
    }

    @Test
    public void shouldHashZero() {
        String result = RandomString.hashOf(0);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void shouldHashNegativeValue() {
        String result = RandomString.hashOf(-1);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectMakeWithZeroLength() {
        RandomString.make(0);
    }
}
