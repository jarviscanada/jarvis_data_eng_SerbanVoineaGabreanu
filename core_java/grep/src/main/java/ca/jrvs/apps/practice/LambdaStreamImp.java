package ca.jrvs.apps.practice;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *The implementation of the LambdaStreamExc interface.
 */
public class LambdaStreamImp implements LambdaStreamExc {

    /**
     *Create a String stream from array
     *
     * @param strings arbitrary number of strings
     * @return a stream of strings
     */
    @Override
    public Stream<String> createStrStream(String... strings) {
        return Arrays.stream(strings);
    }

    /**
     *Convert all strings to uppercase
     *
     * @param strings input strings
     * @return stream of uppercase strings
     */
    @Override
    public Stream<String> toUpperCase(String... strings) {
        return createStrStream(strings).map(String::toUpperCase);
    }

    /**
     *Filter strings that contain the pattern
     *
     * @param stringStream input stream
     * @param pattern the pattern to filter out
     * @return stream without the pattern
     */
    @Override
    public Stream<String> filter(Stream<String> stringStream, String pattern) {
        return stringStream.filter(s -> !s.contains(pattern));
    }

    /**
     *Create an intStream from an array
     *
     * @param arr input array
     * @return int stream
     */
    @Override
    public IntStream createIntStream(int[] arr) {
        return Arrays.stream(arr);
    }

    /**
     *Convert a stream to a list
     *
     * @param stream input stream
     * @param <E> element type
     * @return list of elements
     */
    @Override
    public <E> List<E> toList(Stream<E> stream) {
        return stream.collect(Collectors.toList());
    }

    /**
     *Convert an intStream to a list
     *
     * @param intStream input int stream
     * @return list of integers
     */
    @Override
    public List<Integer> toList(IntStream intStream) {
        return intStream.boxed().collect(Collectors.toList());
    }

    /**
     *Create an IntStream range from start to end inclusive
     *
     * @param start start value
     * @param end end value
     * @return int stream range
     */
    @Override
    public IntStream createIntStream(int start, int end) {
        return IntStream.rangeClosed(start, end);
    }

    /**
     *Convert an intStream to a doubleStream and compute square root of each element
     *
     * @param intStream input int stream
     * @return double stream of square roots
     */
    @Override
    public DoubleStream squareRootIntStream(IntStream intStream) {
        return intStream.asDoubleStream().map(Math::sqrt);
    }

    /**
     *Filter all even numbers and return odd numbers from an intStream
     *
     * @param intStream input int stream
     * @return int stream of odd numbers
     */
    @Override
    public IntStream getOdd(IntStream intStream) {
        return intStream.filter(n -> n % 2 != 0);
    }

    /**
     *Return a lambda function that prints a message with a prefix and suffix
     *
     * @param prefix prefix string
     * @param suffix suffix string
     * @return consumer lambda
     */
    @Override
    public Consumer<String> getLambdaPrinter(String prefix, String suffix) {
        return (msg) -> System.out.println(prefix + msg + suffix);
    }

    /**
     *Print each message with a given printer
     *
     * @param messages array of messages
     * @param printer consumer lambda
     */
    @Override
    public void printMessages(String[] messages, Consumer<String> printer) {
        createStrStream(messages).forEach(printer);
    }

    /**
     *Print all odd numbers from an intStream
     *
     * @param intStream input int stream
     * @param printer consumer lambda
     */
    @Override
    public void printOdd(IntStream intStream, Consumer<String> printer) {
        getOdd(intStream).forEach(n -> printer.accept(String.valueOf(n)));
    }

    /**
     *Square each number from the input list of lists
     *
     * @param ints stream of lists
     * @return stream of squared integers
     */
    @Override
    public Stream<Integer> flatNestedInt(Stream<List<Integer>> ints) {
        return ints.flatMap(List::stream).map(n -> n * n);
    }
}