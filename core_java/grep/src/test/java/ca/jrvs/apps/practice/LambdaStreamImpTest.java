package ca.jrvs.apps.practice;

import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class LambdaStreamImpTest {

    // Simple instantiation
    private final LambdaStreamImp lse = new LambdaStreamImp();

    @Test
    public void testStringOperations() {
        // Test 1: createStrStream
        List<String> list = lse.createStrStream("a", "b").collect(Collectors.toList());
        assertEquals(Arrays.asList("a", "b"), list);

        // Test 2: toUpperCase
        List<String> upper = lse.toUpperCase("a", "b").collect(Collectors.toList());
        assertEquals(Arrays.asList("A", "B"), upper);

        // Test 3: filter (remove strings containing "a")
        Stream<String> input = lse.createStrStream("apple", "banana", "cherry");
        List<String> filtered = lse.filter(input, "a").collect(Collectors.toList());
        assertEquals(Arrays.asList("cherry"), filtered);
    }

    @Test
    public void testIntOperations() {
        // Test 1: createIntStream from array
        List<Integer> fromArray = lse.createIntStream(new int[]{1, 2}).boxed().collect(Collectors.toList());
        assertEquals(Arrays.asList(1, 2), fromArray);

        // Test 2: createIntStream range
        List<Integer> fromRange = lse.createIntStream(1, 3).boxed().collect(Collectors.toList());
        assertEquals(Arrays.asList(1, 2, 3), fromRange);

        // Test 3: squareRootIntStream
        List<Double> roots = lse.squareRootIntStream(IntStream.of(4, 9)).boxed().collect(Collectors.toList());
        assertEquals(Arrays.asList(2.0, 3.0), roots);

        // Test 4: getOdd
        List<Integer> odds = lse.getOdd(IntStream.of(1, 2, 3, 4, 5)).boxed().collect(Collectors.toList());
        assertEquals(Arrays.asList(1, 3, 5), odds);
    }

    @Test
    public void testConversionMethods() {
        // Test toList (from Stream)
        List<String> strList = lse.toList(Stream.of("x", "y"));
        assertEquals(Arrays.asList("x", "y"), strList);

        // Test toList (from IntStream)
        List<Integer> intList = lse.toList(IntStream.of(10, 20));
        assertEquals(Arrays.asList(10, 20), intList);
    }

    @Test
    public void testFlatNestedInt() {
        // Input: [[1, 2], [3, 4]]
        List<Integer> list1 = Arrays.asList(1, 2);
        List<Integer> list2 = Arrays.asList(3, 4);
        Stream<List<Integer>> input = Stream.of(list1, list2);

        // Expected Output: [1, 4, 9, 16] (squares of all numbers)
        List<Integer> result = lse.flatNestedInt(input).collect(Collectors.toList());
        assertEquals(Arrays.asList(1, 4, 9, 16), result);
    }
}