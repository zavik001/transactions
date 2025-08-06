package example.transactions.examples;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import example.transactions.examples.helpers.Person;


public class OptinalExample {
    private Map<Person, Person> repository;

    public OptinalExample() {
        repository = new HashMap<>();
        repository.put(null, null);
        repository.put(new Person("name1", 1), null);
        repository.put(new Person("name2", 2), new Person(null, 15));
    }

    public Person get(Person p) {
        return repository.get(p);
    }

    public void example() {
        // 1
        Person p1 = new Person("name3", 3);
        // Person p11 = get(p1); // NullPointerException

        // 2
        Person p2 = new Person("name2", 2);
        if (repository.containsKey(p2)) {
            Person p22 = get(p2);
            // System.out.println(p22.getName().length()); // NullPointerException
        }

        // true
        if (repository.containsKey(p2)) {
            Person p22 = get(p2);
            if (p22.getName() != null) {
                System.out.println(p22.getName().length());
            }
        }
    }
    // В этом примере показано, что постоянно проверять объекты на null очень неудобно,
    // и во многих случаях не ясно, может ли метод возвращать null, это легко приводит к ошибкам
    // NullPointerException, которые могут быть незаметными.
    // многочисленные проверки на null ухудшают читаемость и раздувают код.
    //
    // Одно из решений — использовать класс Optional<T>.
    // Optional можно воспринимать как контейнер или "коробку", в которую помещается объект.
    // Он может содержать объект типа T или быть пустым. Это всего лишь обертка, которая помогает
    // избегать прямой работы с null.
    //
    // Основная идея в том, что теперь явно указано, что метод или операция могут вернуть
    // "отсутствующий" объект, и это можно обработать более явно.

    public Optional<Person> getOptional(Person p) {
        return Optional.ofNullable(repository.get(p));
    }

    public void exampleOptional() {
        Person p1 = new Person("name2", 2);
        getOptional(p1).map(Person::getName).ifPresent(System.out::println);
    }

    // произошло концептуально важное изменение: теперь точно известно, что метод getOptional()
    // возвращает контейнер, в котором объект может отсутствоват.

    // все методы можно посмотреть на доке: public final class Optional<T>{}
}
