package example.transactions.examples.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// https://hipravin.github.io/proviling-visualvm/
public class UberService {
    private static final Random r = new Random(0);

    public static void doWork() throws IOException {
        int iterations = 100_000;
        doBinary(iterations);
        doBinaryString(iterations);
        doMathPlusMul(iterations);
        doMathDivide(iterations);
        doMathLog(iterations);
        doMathSin(iterations);
        doCollections(iterations);
        doStrings(iterations);
        doMathAsin(iterations);
        doFiles(iterations);
    }


    public static void doBinary(int iterations) {
        long v = r.nextLong();

        for (int i = 0; i < iterations; i++) {
            v = v >> 1 << 1;
            v = v ^ v ^ v;
            v = v & 0xFFFFFFFFL;
        }
    }

    public static void doBinaryString(int iterations) {
        long v = r.nextLong();

        for (int i = 0; i < iterations; i++) {
            String s = Long.toBinaryString(v);
            s = s.substring(0, s.length() - 1);
            s += "0";
        }
    }

    public static void doMathPlusMul(int iterations) {
        long v = r.nextLong();
        for (int i = 0; i < iterations; i++) {
            v = v + 1 - 2 * v;
        }
    }

    public static void doMathDivide(int iterations) {
        long v = r.nextLong();
        for (int i = 0; i < iterations; i++) {
            v *= 2;
            v = v / 2 + v % 10000;
        }

    }

    public static void doMathLog(int iterations) {
        double v = r.nextDouble();
        for (int i = 0; i < iterations; i++) {
            v += Math.log(v);
        }
    }

    public static void doMathSin(int iterations) {
        double v = r.nextDouble();
        for (int i = 0; i < iterations; i++) {
            v += Math.sin(v);
        }
    }

    public static void doMathAsin(int iterations) {
        double v = r.nextDouble();
        double sasin = 0;
        for (int i = 0; i < iterations; i++) {
            sasin += Math.asin(v);
        }
    }

    public static void doCollections(int iterations) {
        List<Long> values = Stream.generate(r::nextLong).limit(10000).collect(Collectors.toList());
        for (int i = 0; i < iterations; i++) {
            values.contains(0);
        }
    }

    public static void doStrings(int iterations) {
        String s = Stream.generate(() -> "abcdef").limit(100).collect(Collectors.joining());
        String r;
        for (int i = 0; i < iterations; i++) {
            r = s.replaceAll("a", "b");
        }
    }

    public static void doFiles(int iterations) throws IOException {

        String s = Stream.generate(() -> "abcdef").limit(100).collect(Collectors.joining());
        File f = File.createTempFile("hipravin-sample-temp", null);

        Files.write(f.toPath(), Collections.singletonList(s), StandardCharsets.UTF_8);

        String r;
        for (int i = 0; i < iterations; i++) {
            r = Files.readString(f.toPath());
        }

        f.delete();
    }
}
