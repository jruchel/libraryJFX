package utils;

import javax.management.RuntimeErrorException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyRandom {

    public static int getRandom(int a, int b) {
        return (Math.abs(new Random().nextInt())) % (b - a + 1) + a;
    }

    public static int getRandomWithDistribution(int[] distribution) {
        int suma = sumArray(distribution);
        int liczba = getRandom(1, suma);

        for (int i = 0; i < distribution.length; i++) {
            int mniejszeOd = sumUntilIndex(distribution, i);
            if (liczba <= mniejszeOd) return i;
        }
        return -1;
    }

    public static String getRandomStringMatching(String regex, int length, int maxTimeSeconds) {
        return getRandomStringMatching(regex, length, (long) (maxTimeSeconds * Math.pow(10, 9)));
    }

    public static String getRandomStringMatching(String regex, int length, long maxTimeNanos) throws RuntimeErrorException {
        long start = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
        do {
            sb.setLength(0);
            for (int i = 0; i < length; i++) {
                sb.append(getRandomCharMatching(".+"));
            }
            matcher = pattern.matcher(sb.toString());
            long checkpoint = System.nanoTime();
            long time = checkpoint - start;
            if (time > maxTimeNanos) {
                String msg = String.format("Method took too long to complete(%.6fs)", (double) time / (Math.pow(10, 9)));
                throw new RuntimeErrorException(new Error(msg), msg);
            }
        } while (!matcher.matches());
        return sb.toString();
    }

    public static String getRandomStringWithCharsMatching(String regex, int length) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(getRandomCharMatching(regex));
        }
        return sb.toString();
    }

    public static char getRandomCharMatching(String regex) {
        char character = (char) (getRandom(32, 126));
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(String.valueOf(character));
        if (matcher.matches()) return character;
        return getRandomCharMatching(regex);
    }

    public static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char lowerCase = (char) (getRandom(97, 122));
            char upperCase = (char) (getRandom(65, 90));
            sb.append(getRandom(Arrays.asList(lowerCase, upperCase)));
        }
        return sb.toString();
    }

    public static <E> E getRandom(List<E> list) {
        return list.get(getRandom(0, list.size() - 1));
    }

    private static int sumUntilIndex(int[] tablica, int index) {
        int suma = 0;
        for (int i = 0; i <= index && i < tablica.length; i++) {
            suma += tablica[i];
        }
        return suma;
    }

    private static int sumArray(int[] tablica) {
        return sumUntilIndex(tablica, tablica.length - 1);
    }

}
