package example.transactions.service;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import example.transactions.exeption.NotFoundException;
import example.transactions.model.Account;
import example.transactions.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SingleThreadedJpaAccountService implements AccountService {

    private final AccountRepository repository;

    @Transactional(rollbackFor = Exception.class, timeout = 30,
            isolation = Isolation.READ_COMMITTED)
    public Account save(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        } else if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        Account saved = repository.save(account);
        log.info("Saved account with id {}", saved.getId());
        return saved;
    }

    public Optional<Account> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid account ID");
        }
        return repository.findById(id);
    }

    public Account getById(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Account not found, id=" + id));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.scale() > 2) {
            throw new IllegalArgumentException(
                    "Amount must be positive and with max 2 decimal places");
        }
    }

    private void validateSufficientFunds(BigDecimal balance, BigDecimal amount) {
        if (balance == null || balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 30,
            isolation = Isolation.READ_COMMITTED)
    public void withdraw(Account account, BigDecimal amount) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        validateAmount(amount);
        Account freshAcc = getById(account.getId());
        validateSufficientFunds(freshAcc.getBalance(), amount);
        freshAcc.setBalance(freshAcc.getBalance().subtract(amount));
        repository.save(freshAcc);
        log.info("Withdrawn {} from account {}", amount, freshAcc.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 30,
            isolation = Isolation.READ_COMMITTED)
    public void deposit(Account account, BigDecimal amount) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        validateAmount(amount);
        Account freshAcc = getById(account.getId());
        freshAcc.setBalance(freshAcc.getBalance().add(amount));
        repository.save(freshAcc);
        log.info("Deposited {} to account {}", amount, freshAcc.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 30,
            isolation = Isolation.READ_COMMITTED)
    public void transfer(Account fromAccount, Account toAccount, BigDecimal amount) {
        if (fromAccount == null || toAccount == null) {
            throw new IllegalArgumentException("Accounts cannot be null");
        }
        if (fromAccount.getId().equals(toAccount.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        withdraw(fromAccount, amount);
        deposit(toAccount, amount);
        log.info("Transferred {} from account {} to account {}", amount, fromAccount.getId(),
                toAccount.getId());
    }
    // @Transactional в Spring — это не МАГИЯ.
    // Может показаться, что Spring просто включает транзакцию,
    // но под капотом целая цепочка механизмов. Аннотация только входная точка.
    //
    // В нашем случае (Spring Boot, настройка по умолчанию spring.aop.proxy-target-class=true)
    // для бина с методом, помеченным @Transactional:
    //
    // 1. На этапе старта приложения Spring через InfrastructureAdvisorAutoProxyCreator
    // находит, что методы бина подходят под Advisor (TransactionAttributeSourceAdvisor).
    //
    // 2. Вместо прямого экземпляра класса создаётся CGLIB-прокси —
    // это сгенерированный подкласс, который переопределяет методы и
    // добавляет перехватчик DynamicAdvisedInterceptor.
    //
    // 3. Цель прокси — перехватывать вызовы методов (через AOP), чтобы при вызове
    // метода с @Transactional вызвать специальный совет (TransactionInterceptor),
    // который откроет транзакцию перед вызовом метода и закроет её (commit/rollback)
    // после выполнения метода.
    //
    // 4. При вызове метода transfer() в ТЕСТЕ мы попадаем не сразу в реализацию метода,
    // а сначала в CGLIB-подкласс, внутри которого вызывается DynamicAdvisedInterceptor.intercept().
    // Это ключевая точка входа в AOP: прокси перехватывает вызов, чтобы вставить логику транзакций.
    // В intercept() происходит:
    // - Получение реального объекта (target) из TargetSource (бин).
    // - Сбор цепочки интерсепторов (getInterceptorsAndDynamicInterceptionAdvice()), где
    // TransactionInterceptor — один из них.
    // - Если цепочка пустая (нет @Transactional или других аспектов), метод вызывается напрямую
    // через рефлексию (AopUtils.invokeJoinpointUsingReflection(target, method, args)).
    // - Но в нашем случае цепочка не пустая, так что создаётся ReflectiveMethodInvocation и
    // вызывается его proceed() — это запускает обход всех интерсепторов по порядку.
    //
    // 5. ReflectiveMethodInvocation.proceed() — сердце AOP-цепочки.
    // Это рекурсивный метод, который последовательно вызывает каждый интерсептор из цепочки:
    // - Увеличивает индекс currentInterceptorIndex.
    // - Если индекс дошёл до конца цепочки, вызывает реальный метод через
    // AopUtils.invokeJoinpointUsingReflection(target, method, args) — вот тут наконец выполняется
    // бизнес-логика в transfer().
    // - Иначе вызывает invoke() следующего интерсептора, передавая себя (this) как invocation для
    // продолжения цепочки.
    // В нашем сценарии первый (один из первых) интерсептор — TransactionInterceptor, так что
    // proceed() передаст управление ему.
    //
    // 6. TransactionInterceptor.invoke() — здесь начинается магия транзакций.
    // Интерсептор реализует логику @Transactional:
    // - Читает атрибуты транзакции (propagation, isolation, timeout и т.д.) из аннотации через
    // TransactionAttributeSource.
    // - Получает PlatformTransactionManager (обычно DataSourceTransactionManager для JDBC).
    // - Создаёт или присоединяется к транзакции через createTransactionIfNecessary(ptm, txAttr).
    // - В try-блоке вызывает invocation.proceed() — это возвращает управление в
    // ReflectiveMethodInvocation.proceed(), чтобы выполнить следующие интерсепторы или реальный
    // метод.
    // - Если метод завершился успешно, вызывает commitTransactionAfterReturning(txInfo) для
    // коммита.
    // - Если поймано исключение, вызывает completeTransactionAfterThrowing(txInfo, ex) для роллбэка
    // (если исключение соответствует rollbackOn правилам).
    // - Наконец, очищает контекст транзакции в finally-блоке.
    //
    // 7. createTransactionIfNecessary() — создание или получение транзакции.
    // Здесь формируется TransactionInfo — объект, хранящий статус транзакции:
    // - Если txAttr null, использует дефолтные настройки.
    // - Вызывает ptm.getTransaction(txAttr) — это делегирует в реализацию менеджера
    // (DataSourceTransactionManager.getTransaction()).
    // - Привязывает транзакцию к текущему потоку через
    // TransactionSynchronizationManager.bindResource().
    // Это обеспечивает, что транзакция видна во всём стеке вызовов в этом потоке.
    //
    // 8. DataSourceTransactionManager.getTransaction() — логика для JDBC-транзакций.
    // Менеджер проверяет поведение propagation из @Transactional:
    // - Создаёт объект транзакции (DataSourceTransactionObject), который содержит ConnectionHolder.
    // - Если уже есть активная транзакция (isExistingTransaction()), обрабатывает её (nested или
    // suspend).
    // - Если нет, вызывает doBegin(transaction, definition) — здесь начинается реальная работа с
    // базой.
    //
    // 9. DataSourceTransactionManager.doBegin() — открытие транзакции на уровне JDBC.
    // Это финальная точка до setAutoCommit(false):
    // - Получает Connection из DataSource (DataSourceUtils.getConnection(dataSource)).
    // - Создаёт ConnectionHolder и помечает его как активный для транзакции.
    // - Вызывает con.setAutoCommit(false) — отключает авто-коммит, чтобы Spring мог вручную
    // управлять commit/rollback.
    // - Устанавливает isolation level (если указано в @Transactional), timeout и readOnly.
    // - Привязывает ConnectionHolder к потоку через
    // TransactionSynchronizationManager.bindResource(dataSource, conHolder).
    // Теперь транзакция открыта: все последующие SQL-запросы в этом потоке (в методе
    // transfer()) будут в одной транзакции.
    //
    // 10. После открытия транзакции управление возвращается в TransactionInterceptor, где
    // invocation.proceed() вызывает реальный метод.
    // transfer() выполняется: все JDBC-операции используют этот Connection без авто-коммита.
    //
    // 11. После выполнения метода: commit или rollback.
    // - Если успех: commitTransactionAfterReturning() → ptm.commit(status) →
    // DataSourceTransactionManager.doCommit() → con.commit().
    // - Если исключение: completeTransactionAfterThrowing() проверяет rollbackOn(ex), затем
    // ptm.rollback(status) → DataSourceTransactionManager.doRollback() → con.rollback().
    // - В любом случае: doCleanupAfterCompletion() освобождает ресурсы, возвращает Connection в пул
    // и включает autoCommit обратно.
    //
    // Итог: весь механизм — это AOP-прокси + интерсепторы + менеджер транзакций, который
    // манипулирует JDBC Connection.
    // От аннотации до setAutoCommit(false) — цепочка из шагов, обеспечивающая атомарность и
    // изоляцию.
    //
    //
    //
    //
    //
    // ВАЖНЫЕ моменты:
    // Надо всегда указывать rollbackFor = Exception.class в @Transactional, потому что по умолчанию
    // роллбэк происходит только на RuntimeException и Error. Не надо полагаться на дефолт, так как
    // checked exceptions (типа SQLException или IOException) будут игнорироваться, приводя к
    // коммиту с частичными изменениями в БД и несогласованными данными.
    //
    // Надо устанавливать timeout в @Transactional (например, timeout = 30), особенно для длинных
    // операций с БД. Не надо игнорировать таймауты, иначе зависшие транзакции заблокируют
    // соединения, исчерпают пул и парализуют приложение.
    //
    // Надо избегать self-invocation (вызова @Transactional метода внутри того же бина напрямую). Не
    // надо вызывать такие методы как this.anotherTransactionalMethod(), поскольку прокси не
    // сработает и транзакция не откроется. Надо использовать AopContext.currentProxy() для вызова
    // через прокси или выносить логику в отдельный бин.
    //
    // Надо мониторить пул соединений (HikariCP в Spring Boot) через метрики (Actuator) или JMX. Не
    // надо забывать о мониторинге, так как висящие транзакции быстро исчерпают пул, вызывая
    // ConnectionPoolTimeoutException и downtime.
    //
    // Надо использовать propagation = Propagation.REQUIRED по умолчанию для большинства случаев. Не
    // надо менять на REQUIRES_NEW без необходимости, так как это создаст новую транзакцию,
    // игнорируя внешнюю, и может привести к несогласованности.
    //
    // Надо указывать isolation level (например, isolation = Isolation.READ_COMMITTED) для
    // предотвращения dirty reads. Не надо оставлять дефолт (Isolation.DEFAULT), если в вашей БД это
    // SERIALIZABLE, что вызовет лишние локи и перформанс-issues.
    //
    // Надо комбинировать @Transactional с @Cacheable осторожно, чтобы кэш обновлялся только после
    // коммита. Не надо кэшировать внутри транзакции без eviction, иначе кэш будет содержать
    // несогласованные данные.
    //
    //
    // ПОДВОДНЫЕ камни:
    // Надо помнить, что Propagation.NESTED работает только с чистым JDBC; для JPA/Hibernate
    // используйте SAVEPOINT вручную. Не надо применять NESTED в JPA без тестов, так как это
    // нестабильно и может не откатить вложенную транзакцию правильно.
    //
    // Надо синхронизировать асинхронные вызовы (@Async) с транзакциями через TransactionTemplate
    // или ручной синхронизацией. Не надо комбинировать @Async и @Transactional напрямую, поскольку
    // транзакция привязана к потоку и в новом потоке её не будет, приводя к NoTransactionException
    // или отдельным коммитам.
    //
    // Надо использовать @Transactional(readOnly = true) для операций чтения с lazy loading в JPA,
    // чтобы сессия оставалась открытой. Не надо фетчить lazy коллекции за пределами транзакции,
    // иначе получите LazyInitializationException; лучше применяйте eager fetch или
    // OpenSessionInView (но осторожно, это антипаттерн для перформанса).
    //
    // Надо явно указывать transactionManager в @Transactional(transactionManager = "myTxManager")
    // при множественных DataSource. Не надо полагаться на дефолтный менеджер, так как Spring
    // возьмёт первый, и транзакция применится к неправильной БД.
    //
    // Надо оптимизировать перформанс, вешая @Transactional только на сервис-уровне, а не на каждый
    // метод. Не надо лять транзакциями, поскольку каждый прокси добавляет overhead от рефлексии и
    // AOP, замедляя приложение на 10-20% в высоконагруженных сценариях.
    //
    // Надо учитывать, что @Transactional не работает с private/protected методами по умолчанию
    // (только public). Не надо ставить аннотацию на non-public методы; лучше делайте их public или
    // используйте aspectj-weaving для полного AOP.
    //
    // Надо проверять на наличие TransactionSynchronizationManager в сложных сценариях
    // (многопоточность). Не надо предполагать, что транзакция всегда доступна; всегда проверяйте
    // TransactionSynchronizationManager.isActualTransactionActive() перед ручными операциями.
    // Надо использовать JTA/XA для distributed transactions (БД + JMS + etc.). Не надо пытаться
    // вручную координировать несколько ресурсов без JTA, так как это приведёт к heisenbugs с
    // частичными коммитами.



    // ACID — это набор гарантий, которые транзакционная система обязуется
    // соблюдать при выполнении операций, чтобы данные оставались корректными даже в условиях
    // ошибок, сбоев, параллельного доступа и отказов.
    // Это не алгоритм, а контракт между разработчиком и системой хранения. Если СУБД
    // говорит Я поддерживаю ACID, значит, ты можешь проектировать логику с уверенностью, что
    // транзакции будут вести себя предсказуемо.



    // 1. Atomicity (Атомарность)
    // Транзакция выполняется целиком или не выполняется вообще.
    // Что это значит на практике:
    // Если ты делаешь несколько изменений в рамках одной транзакции, то либо все они применяются,
    // либо ни одно.
    // Если в середине процесса упал сервер, произошёл исключение, или ты сделал ROLLBACK —
    // изменений в базе нет вообще, как будто ничего не было.
    // Реальный пример:
    // В интернет-банке ты переводишь 100\$ с одного счёта на другой:
    // 1. UPDATE account_A SET balance = balance - 100
    // 2. UPDATE account_B SET balance = balance + 100
    // Если упадёт соединение после шага 1, но до шага 2 — атомарность гарантирует, что оба
    // действия отменятся, и деньги не исчезнун.



    // 2. Consistency (Согласованность)
    // После завершения транзакции база должна находиться в валидном состоянии, которое
    // соответствует всем ограничениям, правилам и связям.
    // Что это значит на практике:
    // Согласованность не починит данные сама — это про то, что СУБД не позволит завершить
    // транзакцию, если её результат нарушает правила (PRIMARY KEY, FOREIGN KEY, CHECK, триггеры,
    // бизнес-ограничения).
    // Приложение и СУБД должны проектироваться так, чтобы в начале и в конце транзакции данные
    // были валидны.
    // Реальный пример:
    // Если у нас есть правило balance >= 0:
    //
    // UPDATE account_A SET balance = balance - 200
    // WHERE account_id = 1;
    // Если на счёте только 100\$, то транзакция не зафиксируется (COMMIT не пройдёт), потому что
    // будет нарушено правило целостности.



    // 3. Isolation (Изоляция)
    // Параллельные транзакции не должны мешать друг другу так, чтобы это ломало согласованность
    // данных.
    // Что это значит на практике:
    // СУБД создаёт иллюзию, что транзакции выполняются последовательно, хотя реально они могут
    // работать параллельно.
    // Уровень изоляции регулирует, какие аномалии разрешены (dirty reads, non-repeatable reads,
    // phantom reads).
    // Реальный пример:
    // Если два кассира одновременно списывают товар со склада:
    // 1. Оба читают: SELECT quantity FROM stock WHERE item_id=5; → видят quantity = 1.
    // 2. Оба думают: товар есть и списывают.
    // Без изоляции в результате количество уйдёт в -1.
    // При правильном уровне изоляции (SERIALIZABLE или REPEATABLE READ) один из апдейтов будет
    // ждать или отменён.



    // ### 4. Durability (Надёжность)
    // После фиксации транзакции (COMMIT) изменения не потеряются, даже если сервер сразу же
    // упадёт.
    // Что это значит на практике:
    // СУБД не просто хранит в памяти, а гарантирует, что данные записаны на диск (и часто в
    // журнал WAL).
    // Если электричество выключится сразу после COMMIT, при следующем старте база восстановит
    // состояние из журнала.
    // Реальный пример:
    // В POS-системе кассир завершает продажу, и транзакция фиксируется. Если через секунду пропадёт
    // свет — данные о продаже будут восстановлены при перезапуске.


    // Ключевая мысль
    // ACID — это про контракт на корректность данных при любых сценариях: сбои, параллелизм,
    // ошибки приложения, аппаратные отказы.
    // Atomicity и Durability — это, в основном, зона ответственности СУБД (механизмы
    // журналирования, отката).
    // Consistency и Isolation — это совместная ответственность: правильная модель данных,
    // ограничения и правильно выбранный уровень изоляции.
}
