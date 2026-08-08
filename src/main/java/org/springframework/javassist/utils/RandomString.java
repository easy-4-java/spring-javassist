/*
 * Copyright (c) 2018-present, easy-4-java (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.javassist.utils;


import java.util.Random;

/**
 * A provider of randomised {@link String} values used to mint unique
 * identifiers for generated classes, fields, and proxies.
 *
 * <p>The class supports three operating modes:</p>
 * <ul>
 *     <li>Static {@link #make()} / {@link #make(int)} helpers that produce
 *         a single random string on demand and discard the generator.</li>
 *     <li>Instance-based generation through {@link #nextString()} where the
 *         caller can amortise the cost of allocating a {@link Random}
 *         generator.</li>
 *     <li>A deterministic {@link #hashOf(int)} helper that turns an integer
 *         into a fixed-length string derived from the integer's bits.</li>
 * </ul>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public class RandomString {

    /**
     * The default length of a randomised string produced by the no-arg
     * {@link #make()} helper.
     */
    public static final int DEFAULT_LENGTH = 8;

    /**
     * The alphabet used to draw characters. Contains digits, lower-case
     * letters and upper-case letters.
     */
    private static final char[] SYMBOL;

    /**
     * The amount of bits to extract out of an integer for each key
     * generated.
     */
    private static final int KEY_BITS;

    /*
     * Creates the symbol array and computes {@link #KEY_BITS}.
     */
    static {
        StringBuilder symbol = new StringBuilder();
        for (char character = '0'; character <= '9'; character++) {
            symbol.append(character);
        }
        for (char character = 'a'; character <= 'z'; character++) {
            symbol.append(character);
        }
        for (char character = 'A'; character <= 'Z'; character++) {
            symbol.append(character);
        }
        SYMBOL = symbol.toString().toCharArray();
        int bits = Integer.SIZE - Integer.numberOfLeadingZeros(SYMBOL.length);
        KEY_BITS = bits - (Integer.bitCount(SYMBOL.length) == bits ? 0 : 1);
    }

    /**
     * The provider of random values used by {@link #nextString()}.
     */
    private final Random random;

    /**
     * The length of the random strings produced by {@link #nextString()}.
     */
    private final int length;

    /**
     * Creates a provider that emits strings of
     * {@link #DEFAULT_LENGTH} length.
     *
     * @since 3.0.0
     */
    public RandomString() {
        this(DEFAULT_LENGTH);
    }

    /**
     * Creates a provider that emits strings of the supplied length.
     *
     * @param length the length of each generated string; must be positive
     * @throws IllegalArgumentException if {@code length} is zero or negative
     * @since 3.0.0
     */
    public RandomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("A random string's length cannot be zero or negative");
        }
        this.length = length;
        random = new Random();
    }

    /**
     * Creates a single random string of
     * {@link #DEFAULT_LENGTH} length.
     *
     * @return a freshly generated random string, never {@code null}
     * @since 3.0.0
     */
    public static String make() {
        return make(DEFAULT_LENGTH);
    }

    /**
     * Creates a single random string of the supplied length.
     *
     * @param length the length of the random string; must be positive
     * @return a freshly generated random string, never {@code null}
     * @throws IllegalArgumentException if {@code length} is zero or negative
     * @since 3.0.0
     */
    public static String make(int length) {
        return new RandomString(length).nextString();
    }

    /**
     * Renders an integer as a fixed, deterministic string derived from its
     * bit pattern. The result is not random in the strict sense, but it
     * gives every integer a stable textual surrogate.
     *
     * @param value the value to represent
     * @return a string representation derived from {@code value}, never
     *         {@code null}
     * @since 3.0.0
     */
    public static String hashOf(int value) {
        char[] buffer = new char[(Integer.SIZE / KEY_BITS) + ((Integer.SIZE % KEY_BITS) == 0 ? 0 : 1)];
        for (int index = 0; index < buffer.length; index++) {
            buffer[index] = SYMBOL[(value >>> index * KEY_BITS) & (-1 >>> (Integer.SIZE - KEY_BITS))];
        }
        return new String(buffer);
    }

    /**
     * Creates a new random string with the length supplied at construction
     * time.
     *
     * @return a freshly generated random string, never {@code null}
     * @since 3.0.0
     */
    public String nextString() {
        char[] buffer = new char[length];
        for (int index = 0; index < length; index++) {
            buffer[index] = SYMBOL[random.nextInt(SYMBOL.length)];
        }
        return new String(buffer);
    }
}