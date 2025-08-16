package example.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class PatternMatcherTest {
    @Test
    void t1() {
        assertTrue(Pattern.matches("javac[XYZ]", "javacZ"));
        assertFalse(Pattern.matches("0*", "d"));
    }

    @Test
    void t2() {
        Pattern p = Pattern.compile("java");
        Matcher m = p.matcher("java123javaerewr32java");

        assertTrue(m.find());
        assertEquals(0, m.start());
        assertEquals(4, m.end());

        m.find();

        assertNotEquals(0, m.start());
        assertNotEquals(4, m.end());
        assertEquals(7, m.start());
        assertEquals(11, m.end());

        // [xyz] x,y or z
        // [^xyz] Any characters other than x,y or z
        // [a-zA-Z] characters from range a to z or A to Z.
        // [a-f[m-t]] Union of a to f and m to t.
        // [a-z && p-y] All the range of elements intersection between two ranges
        // [a-z && [^bc]] a to z union with except b and c
        // [a-z && [^m-p]] a to z union with except range m to p


        String match = m.group(0);
        assertEquals("java", match);
    }

    // etc...
}
