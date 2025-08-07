package example.transactions.examples;

// Spring Transaction Management (Управление транзакциями в Spring)

// ├── Что это:
// │ ├── Механизм управления транзакциями (commit / rollback)
// │ ├── Позволяет обернуть методы в логику:
// │ │ └── если всё ОК → commit
// │ │ └── если ошибка → rollback
// │ └── Работает поверх JDBC, JPA, Hibernate, JTA и других

// ├── Основной инструмент: аннотация @Transactional
// │ ├── Позволяет обернуть метод в транзакцию
// │ ├── Работает через прокси (AOP) — перехват вызова метода
// │ └── Можно ставить на метод, класс или интерфейс

// ├── Как всё работает (по шагам):
// │ 1. Spring видит @Transactional на методе
// │ 2. AOP создает прокси вокруг этого метода
// │ 3. Когда ты вызываешь метод → сначала вызывается прокси
// │ 4. Прокси вызывает TransactionInterceptor
// │ 5. TransactionInterceptor вызывает PlatformTransactionManager
// │ 6. Открывается транзакция (через DataSource / EntityManager)
// │ 7. Вызывается твой метод
// │ 8. Если нет ошибки → PlatformTransactionManager.commit()
// │ 9. Если ошибка → PlatformTransactionManager.rollback()
// │ 10. Прокси возвращает результат / бросает исключение

// ├── PlatformTransactionManager (интерфейс управления транзакциями)
// │ ├── Интерфейс из Spring Core
// │ ├── Методы:
// │ │ ├── getTransaction()
// │ │ ├── commit()
// │ │ └── rollback()
// │ └── Реализации:
// │ ├── DataSourceTransactionManager (JDBC)
// │ ├── JpaTransactionManager (JPA / Hibernate)
// │ ├── HibernateTransactionManager (устаревший)
// │ ├── JtaTransactionManager (для распределенных систем)
// │ └── ReactiveTransactionManager (WebFlux)

// ├── Где хранятся транзакции:
// │ └── Внутри TransactionSynchronizationManager (ThreadLocal)
// │ ├── Хранит контекст транзакции
// │ └── Позволяет передавать его вниз по стеку вызовов

// ├── Proxy (через Spring AOP)
// │ ├── Только вызовы извне проходят через прокси!
// │ ├── self-invocation (если вызвать метод внутри того же класса) → НЕ работает
// │ ├── По умолчанию:
// │ │ ├── JDK Proxy (если интерфейс)
// │ │ └── CGLIB Proxy (если нет интерфейса)
// │ └── Прокси → перехватывает вызов → запускает транзакцию

// ├── Анатомия @Transactional:
// │ ├── propagation — поведение вложенных транзакций:
// │ │ ├── REQUIRED (по умолчанию) — использует текущую или создает новую
// │ │ ├── REQUIRES_NEW — всегда создает новую (старую приостанавливает)
// │ │ ├── SUPPORTS — использует текущую, если есть
// │ │ ├── NOT_SUPPORTED — приостанавливает текущую (без транзакции)
// │ │ ├── NEVER — кидает ошибку, если есть активная транзакция
// │ │ └── MANDATORY — требует существующей транзакции
// │ ├── isolation — уровень изоляции (для конкурентных транзакций):
// │ │ ├── DEFAULT (по умолчанию БД)
// │ │ ├── READ_COMMITTED
// │ │ ├── REPEATABLE_READ
// │ │ ├── SERIALIZABLE
// │ │ └── READ_UNCOMMITTED (редко используется)
// │ ├── rollbackFor — какие исключения вызывают rollback
// │ ├── noRollbackFor — какие НЕ вызывают rollback
// │ ├── timeout — максимальное время транзакции
// │ └── readOnly — для оптимизации чтения (может игнорироваться)

// ├── Подводные камни:
// │ ├── Не работает self-invocation (вызов своего метода)
// │ ├── readOnly не всегда уважается (особенно в Hibernate)
// │ ├── rollback по умолчанию — только на unchecked exceptions (RuntimeException)
// │ ├── Ленивая загрузка (LAZY) вне транзакции → LazyInitializationException
// │ └── Прокси создается ТОЛЬКО если класс/метод — бин в контексте Spring

// ├── Spring Boot и транзакции:
// │ ├── Автоматически создает:
// │ │ ├── PlatformTransactionManager (JpaTransactionManager)
// │ │ └── DataSource (если spring.datasource настроен)
// │ ├── @EnableTransactionManagement подключается автоматически
// │ ├── Тебе нужно только добавить @Transactional — всё остальное готово
// │ └── Spring Boot Starter Data JPA включает всю конфигурацию

// Spring Boot
// ↓ (автоконфигурирует)
// Spring Core
// ↓ (предоставляет)
// PlatformTransactionManager
// ↓ (используется через)
// TransactionInterceptor (Spring AOP)
// ↓
// @Transactional
// ↓
// Оборачивает метод в прокси
// ↓
// Начинает транзакцию → вызывает метод → коммит / роллбэк


public class SpringTransactionManagementExample {
    // https://www.marcobehler.com/guides/spring-transaction-management-transactional-in-depth#commento-login-box-container
}
