package example.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import example.transactions.examples.helpers.Bird;
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

    @Test
    public void givenClass_whenGetsSuperClass_thenCorrect() {
        Goat goat = new Goat("goat");
        String s = "stringSS";

        Class<?> goatClass = goat.getClass();
        Class<?> goatSuperClass = goatClass.getSuperclass();

        assertEquals("Animal", goatSuperClass.getSimpleName());
        assertEquals("Object", s.getClass().getSuperclass().getSimpleName());
    }

    @Test
    public void givenClass_whenGetsImplementedInterfaces_thenCorrect()
            throws ClassNotFoundException {
        Class<?> goat = Class.forName("example.transactions.examples.helpers.Goat");

        Class<?>[] interfacees = goat.getInterfaces();

        assertEquals("Locomotion", interfacees[0].getSimpleName());
    }

    @Test
    public void givenClass_whenGetsAllConstructors_thenCorrect() throws ClassNotFoundException {
        Class<?> birdClass = Class.forName("example.transactions.examples.helpers.Bird");

        Constructor<?>[] constructors = birdClass.getConstructors();

        assertEquals(3, constructors.length);
    }

    @Test
    public void givenClass_whenGetsEachConstructorByParamTypes_thenCorrect()
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, InstantiationException {

        Class<?> birdClass = Class.forName("example.transactions.examples.helpers.Bird");

        Constructor<?> cons1 = birdClass.getConstructor();
        Constructor<?> cons2 = birdClass.getConstructor(String.class);
        Constructor<?> cons3 = birdClass.getConstructor(String.class, boolean.class);

        Bird bird1 = (Bird) cons1.newInstance();
        Bird bird2 = (Bird) cons2.newInstance("birdd");
        Bird bird3 = (Bird) cons3.newInstance("birddd", true);

        assertEquals("bird", bird1.getName());
        assertEquals("birdd", bird2.getName());
        assertEquals("birddd", bird3.getName());
        assertFalse(bird2.walks());
        assertTrue(bird3.walks());
    }

    @Test
    public void givenClassField_whenSetsAndGetsValue_thenCorrect1()
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, InstantiationException,
            SecurityException, NoSuchFieldException {

        Class<?> birdClass = Class.forName("example.transactions.examples.helpers.Bird");
        Field field = birdClass.getDeclaredField("walks");
        Class<?> fieldClass = field.getType();

        assertEquals("boolean", fieldClass.getSimpleName());
    }

    @Test
    public void givenClassField_whenSetsAndGetsValue_thenCorrect2()
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, InstantiationException,
            SecurityException, NoSuchFieldException {

        Class<?> birdClass = Class.forName("example.transactions.examples.helpers.Bird");
        Bird bird = (Bird) birdClass.getConstructor().newInstance();
        Field field = birdClass.getDeclaredField("walks");
        field.setAccessible(true);

        assertFalse(field.getBoolean(bird));
        assertFalse(bird.walks());

        field.set(bird, true);

        assertTrue(field.getBoolean(bird));
        assertTrue(bird.walks());
    }

    @Test
    public void givenMethod_whenInvokes_thenCorrect()
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, InstantiationException,
            SecurityException, NoSuchFieldException {

        Class<?> birdClass = Class.forName("example.transactions.examples.helpers.Bird");
        Bird bird = (Bird) birdClass.getConstructor().newInstance();
        Method setWalksMethod = birdClass.getDeclaredMethod("setWalks", boolean.class);
        Method walksMethod = birdClass.getDeclaredMethod("walks");
        boolean walks = (boolean) walksMethod.invoke(bird);

        assertFalse(walks);
        assertFalse(bird.walks());

        setWalksMethod.invoke(bird, true);

        boolean walks2 = (boolean) walksMethod.invoke(bird);
        assertTrue(walks2);
        assertTrue(bird.walks());
    }
}
