package example.transactions.examples;

// Spring Boot
// ├── Что это:
// │ ├── Надстройка над Spring Framework (Core, Context, AOP и т.д.)
// │ ├── Упрощает создание приложений на Spring:
// │ │ ├── Без лишней конфигурации (XML и т.п.)
// │ │ ├── Без ручной настройки серверов, бинов, компонентов
// │ │ └── Всё "работает из коробки" — opinionated defaults
// │ └── Основан на Spring Core — без него Boot не работает

// ├── Главные особенности:
// │ ├── Автоконфигурация (Spring Boot делает всё сам)
// │ ├── Встроенные серверы (Tomcat, Jetty, Undertow)
// │ ├── Spring Boot Starter'ы — зависимости по категориям
// │ ├── application.yml / application.properties — внешняя конфигурация
// │ ├── Actuator — для мониторинга и метрик
// │ └── Spring Boot DevTools — автообновление, HMR и т.п.

// ├── Основной запуск: `@SpringBootApplication`
// │ ├── Это составная аннотация:
// │ │ ├── @Configuration — объявляет класс как Java-конфигурацию
// │ │ ├── @EnableAutoConfiguration — включает автоконфигурацию
// │ │ └── @ComponentScan — сканирует текущий пакет на @Component и др.
// │ └── Вызов: `SpringApplication.run(MainClass.class)` → запускает всё

// ├── Как работает Boot под капотом:
// │ ├── JVM запускает метод main() → вызывает SpringApplication.run()
// │ ├── Spring Boot создаёт ApplicationContext
// │ ├── Сканирует компоненты через @ComponentScan
// │ ├── Обрабатывает аннотации:
// │ │ ├── @Bean, @Service, @Repository, @Controller
// │ │ └── @ConfigurationProperties и др.
// │ ├── Читает application.yml или .properties
// │ ├── Включает автоконфигурации (через META-INF/spring.factories)
// │ │ ├── spring-boot-autoconfigure.jar
// │ │ └── Загружает классы @ConditionalOn... (например, Web, JPA, Mongo и т.д.)
// │ ├── Поднимает встроенный сервер (если web-приложение)
// │ └── Готовит приложение к работе (REST, Web, Data, и т.п.)

// ├── Компоненты Spring Boot:
// │ ├── Starters:
// │ │ ├── spring-boot-starter-web — REST + MVC + Jackson
// │ │ ├── spring-boot-starter-data-jpa — Spring Data + JPA + Hibernate
// │ │ ├── spring-boot-starter-security — Spring Security
// │ │ ├── spring-boot-starter-aop — AOP
// │ │ ├── spring-boot-starter-test — JUnit, Mockito, и т.д.
// │ │ └── и другие
// │ ├── application.yml / application.properties:
// │ │ ├── Позволяет настраивать всё: порты, базы, пути, биндинг
// │ │ └── Можно использовать @Value и @ConfigurationProperties
// │ └── Actuator:
// │ ├── Метрики, статусы, логи
// │ └── /actuator/health, /metrics, /env и т.д.

// ├── Связь с другими модулями:
// │ ├── Spring Boot → Spring Core:
// │ │ └── Использует DI, ApplicationContext, BeanPostProcessor и др.
// │ ├── Spring Boot → Spring AOP:
// │ │ └── Через аннотации @Aspect, @EnableAspectJAutoProxy
// │ ├── Spring Boot → Spring Data:
// │ │ └── Автоматически находит JpaRepository, подключает Hibernate
// │ └── Spring Boot → Spring Web:
// │ └── Через DispatcherServlet, Controllers, REST, WebConfig

// └── Пример потока выполнения:
// ├── Точка входа: main() → SpringApplication.run()
// ├── Создаётся ApplicationContext
// ├── Запускается сканирование классов
// ├── Автоконфигурация подключает нужные бины
// ├── Приложение стартует с REST-контроллерами, сервисами и репозиториями
// └── Всё работает через DI, бины, прокси и event'ы
public class SpringBootExample {

}
