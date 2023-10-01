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

package org.devtoolbox.util.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Contains utility methods for arrays.
 *
 * @author Arnaud Lecollaire
 */
public class ArrayTools {

    /**
     * Converts a hexadecimal String of size 1 or 2 to a byte.
     *
     * @throws NullPointerException if input string is null
     * @throws IllegalArgumentException if the size of the input String is not 1 or 2
     * @throws NumberFormatException if the input String is not a valid hexadecimal value
     */
    public static byte hexToByte(String inputString) {
        int length = inputString.length();
        if ((length == 0) || (length > 2)) {
            throw new IllegalArgumentException("the size of the input String must be 1 or 2");
        }
        return internalHexToByte(length == 1 ? '0' + inputString : inputString);
    }

    protected static byte internalHexToByte(String inputString) {
        return (byte) Integer.parseInt(inputString, 16);
    }

    /**
     * Converts a String containing hexadecimal to a byte array.
     * The input string can contain whitespaces (spaces, tabs, newlines), they will be removed.
     * Any leading zeros will be removed.
     * 
     * @throws NumberFormatException if the input String is not a valid hexadecimal value
     */
    public static byte[] hexToArray(String hexString) {
        String normalizedString = hexString.replace(" ", "").replace("\t", "").replace("\n", "").replace("\r", "");
        normalizedString = ((normalizedString.length() % 2) == 1) ? "0" + normalizedString : normalizedString;
        final int length = normalizedString.length() / 2;
        final String finalString = normalizedString;

        byte[] result = new byte[length];
        IntStream.range(0, length).forEach(index -> result[index] = internalHexToByte(finalString.substring(index * 2, index * 2 + 2)));
        return result;
    }

    /**
     * Checks if the bytes in the specified arrays are equals.
     *
     * @param first the first array to compare
     * @param second the second array to compare
     */
    public static boolean equals(byte[] first, byte[] second) {
        return (first.length == second.length) ? bytesEqual(first, 0, second, 0) : false;
    }

    /**
     * Checks if the hayStack array contains the needle array at the specified offset.
     *
     * @param hayStack the array to check
     * @param hayStackOffset offset for the hayStack array
     * @param needle the array to compare to
     */
    public static boolean bytesEqual(byte[] hayStack, int hayStackOffset, byte[] needle) {
        return bytesEqual(hayStack, hayStackOffset, needle, 0);
    }

    /**
     * Checks if the hayStack array contains the needle array at the specified offsets.
     *
     * @param hayStack the array to check
     * @param hayStackOffset offset for the hayStack array
     * @param needle the array to compare to
     * @param needleOffset offset for the needle array
     */
    public static boolean bytesEqual(byte[] hayStack, int hayStackOffset, byte[] needle, int needleOffset) {
        if (hayStack[hayStackOffset] != needle[needleOffset]) {
            return false;
        }
        if (needleOffset == (needle.length - 1)) {
            return true;
        }
        return bytesEqual(hayStack, hayStackOffset + 1, needle, needleOffset + 1);
    }

    /**
     * Checks if the hayStack array contains the specified byte, and if so, returns the index of the first occurence.
     *
     * @param hayStack the array to check
     * @param needle the byte to find
     */
    public static Optional<Integer> indexOfFirst(byte[] hayStack, byte needle) {
        return indexOfFirst(hayStack, needle, 0);
    }

    /**
     * Checks if the hayStack array contains the specified byte, and if so, returns the index of the first occurence.
     *
     * @param hayStack the array to check
     * @param needle the byte to find
     * @param hayStackOffset the first index to check
     */
    public static Optional<Integer> indexOfFirst(byte[] hayStack, byte needle, int hayStackOffset) {
        for (int index = hayStackOffset; index < hayStack.length; index++) {
            if (hayStack[index] ==  needle) {
                return Optional.of(index);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if the hayStack array contains the specified byte, and if so, returns the index of the last occurence.
     *
     * @param hayStack the array to check
     * @param needle the byte to find
     */
    public static Optional<Integer> indexOfLast(byte[] hayStack, byte needle) {
        return indexOfLast(hayStack, needle, hayStack.length - 1);
    }

    /**
     * Checks if the hayStack array contains the specified byte, and if so, returns the index of the last occurence.
     *
     * @param hayStack the array to check
     * @param needle the byte to find
     * @param hayStackOffset the first index to check
     */
    public static Optional<Integer> indexOfLast(byte[] hayStack, byte needle, int hayStackOffset) {
        for (int index = hayStackOffset; index >= 0; index--) {
            if (hayStack[index] ==  needle) {
                return Optional.of(index);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if the hayStack array contains the needle array, and if so, returns the index of the first occurence.
     *
     * @param hayStack the array to check
     * @param needle the array to find
     */
    public static Optional<Integer> indexOfFirst(byte[] hayStack, byte[] needle) {
        return indexOfFirst(hayStack, needle, 0);
    }

    /**
     * Checks if the hayStack array contains the needle array, and if so, returns the index of the first occurence.
     *
     * @param hayStack the array to check
     * @param needle the array to find
     * @param hayStackOffset the first index to check
     */
    public static Optional<Integer> indexOfFirst(byte[] hayStack, byte[] needle, int hayStackOffset) {
        for (int index = hayStackOffset; index < (hayStack.length - (needle.length - 1)); index++) {
            if (bytesEqual(hayStack, index, needle, 0)) {
                return Optional.of(index);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if the hayStack array contains the needle array, and if so, returns the index of the last occurence.
     *
     * @param hayStack the array to check
     * @param needle the array to find
     */
    public static Optional<Integer> indexOfLast(byte[] hayStack, byte[] needle) {
        return indexOfLast(hayStack, needle, hayStack.length - 1);
    }

    /**
     * Checks if the hayStack array contains the needle array, and if so, returns the index of the last occurence.
     *
     * @param hayStack the array to check
     * @param needle the array to find
     * @param hayStackOffset the first index to check
     */
    public static Optional<Integer> indexOfLast(byte[] hayStack, byte[] needle, int hayStackOffset) {
        for (int index = hayStackOffset; index >= 0; index--) {
            if (bytesEqual(hayStack, index, needle, 0)) {
                return Optional.of(index);
            }
        }
        return Optional.empty();
    }

    /**
     * Concatenates 2 or more arrays into one.
     */
    public static byte[] concat(byte[]... arrays) {
        // handle the cases when there is less than 2 arrays for convenience
        if (arrays.length == 0) {
            return new byte[] {};
        }
        if (arrays.length == 1) {
            return arrays[0];
        }
        int dataSize = Arrays.stream(arrays).collect(Collectors.summingInt(array -> array.length));
        byte[] response = new byte[dataSize];
        int index = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, response, index, array.length);
            index += array.length;
        }
        return response;
    }

    /**
     * <p>Concatenates 2 or more arrays into one, adding a byte as separator between each of them.</p>
     * <p>This method does not check if any of the provided arrays contains the separator.</p> 
     */
    public static byte[] concatWithSeparator(byte separator, byte[]... arrays) {
        // handle the cases when there is less than 2 arrays for convenience
        if (arrays.length == 0) {
            return new byte[] {};
        }
        if (arrays.length == 1) {
            return arrays[0];
        }
        int dataSize = Arrays.stream(arrays).collect(Collectors.summingInt(array -> array.length));
        byte[] response = new byte[dataSize + (arrays.length - 1)];
        int index = 0;
        for (byte[] array : arrays) {
            if (index != 0) {
                response[index++] = separator;
            }
            System.arraycopy(array, 0, response, index, array.length);
            index += array.length;
        }
        return response;
    }

    /**
     * <p>Concatenates 2 or more arrays into one, adding a separator between each of them.</p>
     * <p>This method does not check if any of the provided arrays contains the separator.</p> 
     */
    public static byte[] concatWithSeparator(byte[] separator, byte[]... arrays) {
        // handle the cases when there is less than 2 arrays for convenience
        if (arrays.length == 0) {
            return new byte[] {};
        }
        if (arrays.length == 1) {
            return arrays[0];
        }
        int dataSize = Arrays.stream(arrays).collect(Collectors.summingInt(array -> array.length));
        byte[] response = new byte[dataSize + (arrays.length - 1) * separator.length];
        int index = 0;
        for (byte[] array : arrays) {
            if (index != 0) {
                System.arraycopy(separator, 0, response, index, separator.length);
                index += separator.length;
            }
            System.arraycopy(array, 0, response, index, array.length);
            index += array.length;
        }
        return response;
    }

    /**
     * Splits an array, using the given separator (similar to String::split, but not using regexp).
     */
    public static List<byte[]> split(byte separator, byte[] array) {
        List<byte[]> result = new ArrayList<>();
        int startIndex = 0;
        int separatorIndex = -1;
        do {
            separatorIndex = indexOfFirst(array, separator, startIndex).orElse(-1);
            if (separatorIndex == -1) {
                result.add(Arrays.copyOfRange(array, startIndex, array.length));
                break;
            } else {
                result.add(Arrays.copyOfRange(array, startIndex, separatorIndex));
                startIndex = separatorIndex + 1;
                // special case if the array ends with the separator
                if (startIndex == array.length) {
                    result.add(new byte[] {});
                    break;
                }
            }
        } while (startIndex < array.length);
        return result;
    }

    /**
     * Splits an array, using the given separator (similar to String::split, but not using regexp).
     */
    public static List<byte[]> split(byte[] separator, byte[] array) {
        List<byte[]> result = new ArrayList<>();
        int startIndex = 0;
        int separatorIndex = -1;
        do {
            separatorIndex = indexOfFirst(array, separator, startIndex).orElse(-1);
            if (separatorIndex == -1) {
                result.add(Arrays.copyOfRange(array, startIndex, array.length));
                break;
            } else {
                result.add(Arrays.copyOfRange(array, startIndex, separatorIndex));
                startIndex = separatorIndex + separator.length;
                // special case if the array ends with the separator
                if (startIndex == array.length) {
                    result.add(new byte[] {});
                    break;
                }
            }
        } while (startIndex < array.length);
        return result;
    }
}