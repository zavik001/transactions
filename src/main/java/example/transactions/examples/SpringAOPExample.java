package example.transactions.examples;

// Spring AOP (Aspect-Oriented Programming)

// Что это:
// Aspect-Oriented Programming (AOP) — парадигма программирования, которая позволяет отделять
// сквозную логику (cross-cutting concerns) от основной бизнес-логики.
// Примеры сквозной логики: логирование, безопасность, обработка транзакций, метрики и т.п.

// Spring AOP — модуль Spring Framework, реализующий AOP через прокси (JDK Dynamic Proxies или
// CGLIB).

// Цель:
// - Избегать дублирования кода (DRY)
// - Централизованно управлять сквозной логикой
// - Чистый, понятный, легко сопровождаемый код

// Основные понятия:
// - Aspect — модуль сквозной логики (Java-класс с аннотацией @Aspect)
// - Join point — точка в программе, в которую можно "врезаться" (например, вызов метода)
// - Advice — действие, которое выполняется в Join point (до, после, вокруг и т.п.)
// - Pointcut — выражение, определяющее, к каким Join point применить Advice
// - Weaving — процесс связывания Aspect'ов с основной логикой (во время выполнения, компиляции или
// загрузки)
// - Proxy — обёртка вокруг целевого объекта, через которую проходит вызов метода (Spring AOP делает
// это через прокси)

// Аннотации AOP:
// - @Aspect — объявляет класс как аспект
// - @Before("execution(...)") — выполнить до метода
// - @After("execution(...)") — выполнить после метода
// - @AfterReturning — выполнить после успешного завершения
// - @AfterThrowing — выполнить при выбросе исключения
// - @Around — обернуть метод (контроль до и после выполнения)
// - @Pointcut — шаблон для переиспользуемых выражений

// Пример кода:
// @Aspect
// @Component
// public class LoggingAspect {

// @Before("execution(* com.example.service.*.*(..))")
// public void logBefore(JoinPoint joinPoint) {
// System.out.println("Before method: " + joinPoint.getSignature().getName());
// }
// }

// Выражения Pointcut (AspectJ expression language):
// - execution(* com.example.service.*.*(..)) — все методы всех классов в пакете service
// - within(com.example.repository..) — все классы внутри пакета
// - this(MyInterface) — все бины, реализующие интерфейс
// - args(String, ..) — методы с аргументами

// Как работает Spring AOP под капотом:
// 1. Spring создает прокси-объекты:
// - Если интерфейс есть → используется JDK Dynamic Proxy
// - Если интерфейса нет → используется CGLIB (создание подкласса)
// 2. Все вызовы метода проходят через прокси
// 3. Если есть совпадение по Pointcut → выполняется Advice
// 4. В конце вызывается оригинальный метод

// Ограничения Spring AOP:
// - Работает только с методами Spring-бинов
// - Только во время выполнения (runtime weaving)
// - Только public методы
// - Не применимо к self-invocation (вызов метода изнутри того же класса)

// Расширенные возможности (через AspectJ):
// - Compile-time или load-time weaving (через ajc или LTW)
// - Более мощная система Pointcut'ов
// - Требует дополнительных настроек и зависимостей

// Типичные применения AOP:
// - Логирование (до/после методов)
// - Транзакции (@Transactional работает через AOP)
// - Валидация
// - Обработка исключений
// - Аудит, метрики

// Связь с Spring Core:
// - AOP использует механизмы DI и контекста из Spring Core
// - AOP реализуется через Spring ProxyFactoryBean, BeanPostProcessor и т.п.

// Зависимости и настройка (Spring Boot):
// - Spring Boot auto-config включает поддержку AOP через spring-boot-starter-aop
// - Требуется аннотация @EnableAspectJAutoProxy в конфигурации

// Жизненный цикл AOP:
// 1. Контейнер поднимает контекст и находит @Aspect
// 2. Создает прокси-объекты вокруг целевых бинов
// 3. Все вызовы методов идут через прокси
// 4. Advice применяются в зависимости от Pointcut'а

// Архитектура в контексте Spring:
// Spring Framework
// ├── Core (DI, контейнер)
// │ └── AOP (сквозная логика)
// │ ├── Прокси (JDK / CGLIB)
// │ ├── @Aspect, @Before, @After и т.д.
// │ └── Используется в Spring Transaction, Spring Security и др.

// Spring AOP — мощный инструмент для внедрения сквозной логики без засорения бизнес-кода.
// Он строится на Spring Core и DI, использует прокси, и тесно связан с другими модулями.
public class SpringAOPExample {

}
