/*
 * MIT License
 *
 * Copyright © 2023 dev-toolbox.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.devtoolbox.util.array.test;

import static org.devtoolbox.util.array.ArrayTools.bytesEqual;
import static org.devtoolbox.util.array.ArrayTools.concat;
import static org.devtoolbox.util.array.ArrayTools.concatWithSeparator;
import static org.devtoolbox.util.array.ArrayTools.hexToArray;
import static org.devtoolbox.util.array.ArrayTools.hexToByte;
import static org.devtoolbox.util.array.ArrayTools.indexOfFirst;
import static org.devtoolbox.util.array.ArrayTools.indexOfLast;
import static org.devtoolbox.util.array.ArrayTools.split;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.List;

import org.devtoolbox.util.array.ArrayTools;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests for {@link ArrayTools}.
 *
 * @author Arnaud Lecollaire
 */
public class ArrayToolsTest {

    @Test
    public void hexToByteTest() {
        // simple case
        checkHexToByte("A", 10);
        checkHexToByte("0A", 10);

        // zero value
        checkHexToByte("0", 0);
        checkHexToByte("00", 0);

        // value with most significant bit to 1 (>= 128)
        checkHexToByte("80", 128);
        checkHexToByte("FF", 255);
    }

    protected void checkHexToByte(String inputString, int expectedIntValue) {
        byte result = hexToByte(inputString);
        assertEquals(expectedIntValue, new BigInteger(new byte[] { 0, result }).intValue());
    }

    @Test
    public void hexToArrayTest() {
        // simple case
        checkHexToBytes("2A", 1, 42);

        // check that leading zero values are preserved
        checkHexToBytes("00 2A", 2, 42);

        // check that first byte is accepted with only one character
        checkHexToBytes("A 2A", 2, 2602);

        // value with most significant bit to 1 (>= 128)
        checkHexToBytes("80", 1, 128);

        // bigger number
        long longValue = Long.MAX_VALUE;
        byte[] result = hexToArray(Long.toHexString(longValue));
        assertEquals(Long.BYTES, result.length);
        assertEquals(longValue, new BigInteger(result).longValue());

        // check spaces, tabs and lower/upper case
        checkHexToBytes("FE 05\t4a", 3, 16_647_498);

        // check newlines
        checkHexToBytes("""
                FE
                05
                4a\r
            """, 3, 16_647_498);
    }

    protected void checkHexToBytes(String inputString, int expectedArrayLength, int expectedIntValue) {
        byte[] result = hexToArray(inputString);
        assertEquals(expectedArrayLength, result.length);
        assertEquals(expectedIntValue, new BigInteger(concat(new byte[] { 0 }, result)).intValue());
    }

    @Test
    public void equalsTest() {
        byte[] array = hexToArray("FF 37 01 87 53 01 87 45 A9");
        byte[] otherArrayInstance = hexToArray("FF 37 01 87 53 01 87 45 A9");
        byte[] shorterArray = hexToArray("FF 37 01 87 53 01 87 45");
        byte[] largerArray = hexToArray("FF 37 01 87 53 01 87 45 A9 64");

        assertTrue(ArrayTools.equals(array, otherArrayInstance));

        assertFalse(ArrayTools.equals(array, shorterArray));
        assertFalse(ArrayTools.equals(shorterArray, array));

        assertFalse(ArrayTools.equals(array, largerArray));
        assertFalse(ArrayTools.equals(largerArray, array));
    }

    @Test
    public void bytesEqualTest() {
        byte[] haystack = hexToArray("FF 37 01 87 53 01 87 45 A9");

        assertTrue(bytesEqual(haystack, 0, hexToArray("FF")));
        assertTrue(bytesEqual(haystack, 0, hexToArray("FF"), 0));
        assertFalse(bytesEqual(haystack, 0, hexToArray("45"), 0));

        assertTrue(bytesEqual(haystack, 1, hexToArray("37")));
        assertTrue(bytesEqual(haystack, 1, hexToArray("37"), 0));
        assertFalse(bytesEqual(haystack, 0, hexToArray("45"), 0));

        assertTrue(bytesEqual(haystack, 1, hexToArray("55 37"), 1));
        assertFalse(bytesEqual(haystack, 1, hexToArray("55 42"), 1));

        assertTrue(bytesEqual(haystack, 0, hexToArray("FF 37"), 0));
        assertFalse(bytesEqual(haystack, 0, hexToArray("55 37"), 0));
        assertFalse(bytesEqual(haystack, 0, hexToArray("FF 77"), 0));

        assertTrue(bytesEqual(haystack, 0, hexToArray("FF 37 01 87 53 01 87 45 A9"), 0));
        assertTrue(bytesEqual(haystack, 1, hexToArray("37 01 87 53 01 87 45 A9"), 0));
        assertTrue(bytesEqual(haystack, 8, hexToArray("FF 37 01 87 53 01 87 45 A9"), 8));
    }

    @Test
    public void indexOfFirstByteTest() {
        byte[] haystack = hexToArray("FF 37 01 87 52 01 37 01 A9 37 44 53");

        assertEquals(0, indexOfFirst(haystack, hexToByte("FF")).orElse(-1));
        assertTrue(indexOfFirst(haystack, hexToByte("F2")).isEmpty());

        assertEquals(1, indexOfFirst(haystack, hexToByte("37")).orElse(-1));
        assertEquals(1, indexOfFirst(haystack, hexToByte("37"), 0).orElse(-1));
        assertEquals(6, indexOfFirst(haystack, hexToByte("37"), 2).orElse(-1));
        assertEquals(6, indexOfFirst(haystack, hexToByte("37"), 6).orElse(-1));
        assertEquals(9, indexOfFirst(haystack, hexToByte("37"), 7).orElse(-1));
        assertTrue(indexOfFirst(haystack, hexToByte("37"), 10).isEmpty());

        assertEquals(11, indexOfFirst(haystack, hexToByte("53")).orElse(-1));
        assertTrue(indexOfFirst(haystack, hexToByte("53"), 12).isEmpty());
    }

    @Test
    public void indexOfFirstTest() {
        byte[] haystack = hexToArray("FF 37 01 87 53 01 37 01 A9 37 44 53");

        assertEquals(0, indexOfFirst(haystack, hexToArray("FF")).orElse(-1));
        assertTrue(indexOfFirst(haystack, hexToArray("F2")).isEmpty());

        assertEquals(1, indexOfFirst(haystack, hexToArray("37")).orElse(-1));
        assertEquals(1, indexOfFirst(haystack, hexToArray("37"), 0).orElse(-1));
        assertEquals(6, indexOfFirst(haystack, hexToArray("37"), 2).orElse(-1));
        assertEquals(6, indexOfFirst(haystack, hexToArray("37"), 6).orElse(-1));
        assertEquals(9, indexOfFirst(haystack, hexToArray("37"), 7).orElse(-1));
        assertTrue(indexOfFirst(haystack, hexToArray("37"), 10).isEmpty());

        assertTrue(indexOfFirst(haystack, hexToArray("37 02")).isEmpty());
        assertTrue(indexOfFirst(haystack, hexToArray("37 02"), 10).isEmpty());

        assertEquals(1, indexOfFirst(haystack, hexToArray("37 01")).orElse(-1));
        assertEquals(1, indexOfFirst(haystack, hexToArray("37 01"), 0).orElse(-1));
        assertEquals(6, indexOfFirst(haystack, hexToArray("37 01"), 2).orElse(-1));
        assertTrue(indexOfFirst(haystack, hexToArray("37 01"), 7).isEmpty());

        assertEquals(10, indexOfFirst(haystack, hexToArray("44 53"), 10).orElse(-1));

        assertTrue(indexOfFirst(haystack, hexToArray("53 02"), 0).isEmpty());
    }

    @Test
    public void indexOfLastByteTest() {
        byte[] haystack = hexToArray("FF 37 01 87 44 01 37 01 A9 37 44 53");

        assertEquals(11, indexOfLast(haystack, hexToByte("53")).orElse(-1));
        assertTrue(indexOfLast(haystack, hexToByte("54")).isEmpty());

        assertEquals(9, indexOfLast(haystack, hexToByte("37")).orElse(-1));
        assertEquals(6, indexOfLast(haystack, hexToByte("37"), 8).orElse(-1));
        assertEquals(1, indexOfLast(haystack, hexToByte("37"), 5).orElse(-1));
        assertTrue(indexOfLast(haystack, hexToByte("37"), 0).isEmpty());

        assertEquals(0, indexOfLast(haystack, hexToByte("FF")).orElse(-1));
        assertTrue(indexOfLast(haystack, hexToByte("FF"), -1).isEmpty());
    }

    @Test
    public void indexOfLastTest() {
        byte[] haystack = hexToArray("37 01 87 53 01 37 01 A9 37 44 53 FF");

        assertEquals(10, indexOfLast(haystack, hexToArray("53")).orElse(-1));
        assertTrue(indexOfLast(haystack, hexToArray("54")).isEmpty());

        assertEquals(8, indexOfLast(haystack, hexToArray("37")).orElse(-1));
        assertEquals(5, indexOfLast(haystack, hexToArray("37"), 7).orElse(-1));
        assertEquals(0, indexOfLast(haystack, hexToArray("37"), 4).orElse(-1));
        assertEquals(0, indexOfLast(haystack, hexToArray("37"), 0).orElse(-1));

        assertTrue(indexOfLast(haystack, hexToArray("37 02")).isEmpty());

        assertEquals(5, indexOfLast(haystack, hexToArray("37 01")).orElse(-1));
        assertEquals(0, indexOfLast(haystack, hexToArray("37 01"), 4).orElse(-1));
        assertEquals(0, indexOfLast(haystack, hexToArray("37 01"), 0).orElse(-1));

        assertEquals(10, indexOfLast(haystack, hexToArray("53 FF")).orElse(-1));
        assertEquals(10, indexOfLast(haystack, hexToArray("53 FF"), 11).orElse(-1));
        assertTrue(indexOfLast(haystack, hexToArray("53 FF"), 9).isEmpty());
    }

    @Test
    public void concatTest() {
        byte[] first = hexToArray("FF 37");
        byte[] second = hexToArray("01 87 53");
        byte[] third = hexToArray("01 87 45 A9");
        byte[] result = concat(first, second, third);
        assertEquals(first.length + second.length + third.length, result.length);
        assertTrue(bytesEqual(result, 0, first, 0));
        assertTrue(bytesEqual(result, 2, second, 0));
        assertTrue(bytesEqual(result, 5, third, 0));
    }

    @Test
    public void concatWithSimpleSeparatorTest() {
        byte separator = (byte) 0;
        byte[] first = hexToArray("FF 37");
        byte[] second = hexToArray("01 87 53");
        byte[] third = hexToArray("01 87 45 A9");
        byte[] result = concatWithSeparator(separator, first, second, third);
        assertEquals(2 + first.length + second.length + third.length, result.length);
        assertTrue(bytesEqual(result, 0, first, 0));
        assertTrue(bytesEqual(result, 3, second, 0));
        assertTrue(bytesEqual(result, 7, third, 0));
        assertEquals(separator, result[2]);
        assertEquals(separator, result[6]);
    }

    @Test
    public void concatWithSeparatorTest() {
        byte[] separator = hexToArray("FE EF");
        byte[] first = hexToArray("FF 37");
        byte[] second = hexToArray("01 87 53");
        byte[] third = hexToArray("01 87 45 A9");
        byte[] result = concatWithSeparator(separator, first, second, third);
        assertEquals(2 * separator.length + first.length + second.length + third.length, result.length);
        assertTrue(bytesEqual(result, 0, first, 0));
        assertTrue(bytesEqual(result, 4, second, 0));
        assertTrue(bytesEqual(result, 9, third, 0));
        assertTrue(bytesEqual(result, 2, separator, 0));
        assertTrue(bytesEqual(result, 7, separator, 0));
    }

    @Test
    public void splitSimpleTest() {
        byte separator = (byte) 0;
        byte[] first = hexToArray("FF 37");
        byte[] second = hexToArray("01 87 53");
        byte[] third = hexToArray("01 87 45 A9");
        List<byte[]> result = split(separator, concatWithSeparator(separator, first, second, third));
        assertEquals(3, result.size());
        assertTrue(bytesEqual(result.get(0), 0, first, 0));
        assertTrue(bytesEqual(result.get(1), 0, second, 0));
        assertTrue(bytesEqual(result.get(2), 0, third, 0));
        
        result = split((byte) 0x33, hexToArray("33    33    55 11"));
        assertEquals(3, result.size());
        assertEquals(0, result.get(0).length);
        assertEquals(0, result.get(1).length);
        assertTrue(bytesEqual(result.get(2), 0, hexToArray("55 11")));

        result = split((byte) 0x33, hexToArray("55 11    33    33"));
        assertEquals(3, result.size());
        assertTrue(bytesEqual(result.get(0), 0, hexToArray("55 11")));
        assertEquals(0, result.get(1).length);
        assertEquals(0, result.get(2).length);
    }

    @Test
    public void splitTest() {
        byte[] separator = hexToArray("FE EF");
        byte[] first = hexToArray("FF 37");
        byte[] second = hexToArray("01 87 53");
        byte[] third = hexToArray("01 87 45 A9");
        List<byte[]> result = split(separator, concatWithSeparator(separator, first, second, third));
        assertEquals(3, result.size());
        assertTrue(bytesEqual(result.get(0), 0, first, 0));
        assertTrue(bytesEqual(result.get(1), 0, second, 0));
        assertTrue(bytesEqual(result.get(2), 0, third, 0));
        
        result = split(separator, hexToArray("FE EF    FE EF    55 11"));
        assertEquals(3, result.size());
        assertEquals(0, result.get(0).length);
        assertEquals(0, result.get(1).length);
        assertTrue(bytesEqual(result.get(2), 0, hexToArray("55 11")));

        result = split(separator, hexToArray("55 11    FE EF    FE EF"));
        assertEquals(3, result.size());
        assertTrue(bytesEqual(result.get(0), 0, hexToArray("55 11")));
        assertEquals(0, result.get(1).length);
        assertEquals(0, result.get(2).length);

        result = split(separator, hexToArray("55 11    FE EF    FE"));
        assertEquals(2, result.size());
        assertTrue(bytesEqual(result.get(0), 0, hexToArray("55 11")));
        assertTrue(bytesEqual(result.get(1), 0, hexToArray("FE")));
    }
}