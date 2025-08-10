package example.transactions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import example.transactions.examples.helpers.Person;

class GCTest {

    @Test
    void t1() throws InterruptedException, ExecutionException {
        ExecutorService excutor = Executors.newFixedThreadPool(12);

        List<List<Person>> persons = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            Future<List<Person>> call = excutor.submit(() -> {
                List<Person> list = new ArrayList<>();
                for (int j = 0; j < 1_000/* _000 */; j++) {
                    list.add(new Person("name", 100));
                }
                return list;
            });
            persons.add(call.get());
        }

        Thread.sleep(10000);
        persons = null;

        List<List<Person>> personss = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            Future<List<Person>> call = excutor.submit(() -> {
                List<Person> pers = new ArrayList<>();
                for (int j = 0; j < 1_000/* _000 */; j++) {
                    pers.add(new Person("name", 100));
                }
                return pers;
            });
            personss.add(call.get());
        }

        personss = null;
        Thread.sleep(10000);

        List<List<Person>> personsss = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            Future<List<Person>> call = excutor.submit(() -> {
                List<Person> pers = new ArrayList<>();
                for (int j = 0; j < 1_000/* _000 */; j++) {
                    pers.add(new Person("name", 100));
                }
                return pers;
            });
            personsss.add(call.get());
        }

        personsss = null;
        Thread.sleep(10000);

        excutor.shutdown();
    }
}
