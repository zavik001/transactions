package example.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import example.transactions.examples.helpers.Goat;
import example.transactions.examples.helpers.Person;

class ReflectionTest {

    @Test
    void givenObject_whenGetsFieldNamesAtRuntime_thenCorrect() {
        Object person = new Person();
        Stream<Field> fields = Arrays.stream(person.getClass().getDeclaredFields());

        List<String> actualFieldNames = fields.map(Field::getName).toList();

        assertTrue(Arrays.asList("name", "age").containsAll(actualFieldNames));
    }

    @Test
    public void givenObject_whenGetsClassName_thenCorrect() {
        Object goat = new Goat("goat");
        Class<?> clazz = goat.getClass();

        assertEquals("Goat", clazz.getSimpleName());
        assertEquals("example.transactions.examples.helpers.Goat", clazz.getName());
        assertEquals("example.transactions.examples.helpers.Goat", clazz.getCanonicalName());
    }

    @Test
    public void givenClassName_whenCreatesObject_thenCorrect() throws ClassNotFoundException {
        Class<?> clazz = Class.forName("example.transactions.examples.helpers.Goat");

        assertEquals("Goat", clazz.getSimpleName());
        assertEquals("example.transactions.examples.helpers.Goat", clazz.getName());
        assertEquals("example.transactions.examples.helpers.Goat", clazz.getCanonicalName());
    }

    @Test
    public void givenClass_whenRecognisesModifiers_thenCorrect() throws ClassNotFoundException {
        Class<?> goatClass = Class.forName("example.transactions.examples.helpers.Goat");
        Class<?> animalClass = Class.forName("example.transactions.examples.helpers.Animal");

        int goatMods = goatClass.getModifiers();
        int animalMods = animalClass.getModifiers();

        assertTrue(Modifier.isPublic(goatMods));
        assertTrue(Modifier.isAbstract(animalMods));
        assertTrue(Modifier.isPublic(animalMods));
    }

    @Test
    public void givenClass_whenGetsPackageInfo_thenCorrect() {
        Goat goat = new Goat("goat");
        Class<?> goatClass = goat.getClass();
        Package pkg = goatClass.getPackage();

        assertEquals("example.transactions.examples.helpers", pkg.getName());
    }
    // 5.5. Суперкласс
}
