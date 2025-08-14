package example.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class StreamAPITest {
    @Test
    void t1() {
        long NumberOfWords = Stream.of("one", "", "two", "three", "for", "", "five")
                .filter(Predicate.not(String::isEmpty)).collect(Collectors.counting());

        assertEquals(5, NumberOfWords);
    }

    @Test
    void t2() {
        var result = Stream.of("foo", "bar", "buz", "quez").distinct().toList();
        assertTrue(List.of("bar", "buz", "foo", "quez").containsAll(result));
    }

    @Test
    void t3() {
        var result = IntStream.of(1, 2, 3, 4, 5, 6).filter(i -> i > 3).boxed().toList();
        var resultt = DoubleStream.of(1.1, 2.2, 3.3).boxed().toList();

        assertTrue(result.get(0) instanceof Integer);
        assertEquals("Integer", result.get(0).getClass().getSimpleName());

        assertTrue(resultt.get(0) instanceof Double);
        assertEquals("Double", resultt.get(0).getClass().getSimpleName());
        assertEquals("java.lang.Double", resultt.get(0).getClass().getName());
    }

    @Test
    void t4() {
        var result = Stream.of("one", "two", "three", "four", "five").iterator();

        assertTrue(result.hasNext());
        assertEquals("one", result.next());
        assertEquals("two", result.next());
    }

    @Test
    void t5() {
        List<List<String>> listOflist = new ArrayList<>();
        listOflist.add(new ArrayList<>() {
            {
                add("name1");
                add("name2");
            }
        });
        listOflist.add(new ArrayList<>() {

            {
                add("name-12");
                add("name4");
            }

        });

        List<String> list = listOflist.stream().flatMap(List::stream).toList();
        assertTrue(List.of("name1", "name2", "name-12", "name4").containsAll(list));

        char[] arr = {'1', '2', '3'};
        assertEquals("[C", arr.getClass().getName()); // ? class "[C"
    }
}
