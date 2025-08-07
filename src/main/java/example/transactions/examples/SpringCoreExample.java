package example.transactions.examples;

// Spring Core (СЕРДЦЕ всего Spring Framework)

// Основная задача:
// Spring Core — это ядро Spring Framework, обеспечивающее:
// ✔ Dependency Injection (внедрение зависимостей)
// ✔ Управление жизненным циклом и конфигурацией объектов (бинов)
// ✔ Контейнеры ApplicationContext / BeanFactory
// ✔ Инфраструктуру для других модулей: Boot, Data, Web и т.д.


// 1. Dependency Injection (DI)
// DI = передача зависимостей (объектов) в классы снаружи, а не через new.
// Виды DI:
// ├── Constructor Injection (рекомендуемый способ)
// │ └── Через конструктор класса
// ├── Field Injection
// │ └── Через аннотацию @Autowired над полем
// ├── Setter Injection
// │ └── Через сеттер-метод
// Способы DI:
// ├── Annotation-based
// │ ├── @Component — объявляет бин
// │ ├── @Autowired — внедряет зависимость
// │ └── @Qualifier — уточняет бин по имени
// ├── Java-based (конфигурационные классы)
// │ ├── @Configuration — класс с бинами
// │ └── @Bean — метод, создающий бин
// └── XML-based (устарело, но работает)
// └── <bean>, <property>, <constructor-arg> и т.п.



// 2. Spring Containers (IoC-контейнер)
// Контейнер = среда, которая управляет жизненным циклом объектов.
// Типы контейнеров:
// ├── BeanFactory (низкоуровневый)
// │ └── Создает объекты только по запросу (Lazy)
// ├── ApplicationContext
// │ ├── Расширяет BeanFactory
// │ ├── Поддержка аннотаций, событий, интернационализации
// │ └── Типичные реализации:
// │ ├── AnnotationConfigApplicationContext
// │ ├── ClassPathXmlApplicationContext
// │ └── FileSystemXmlApplicationContext
// ApplicationContext = основной способ инициализации контекста



// 3. Бины и их жизненный цикл
// Bean = объект, управляемый Spring IoC-контейнером
// Жизненный цикл Bean'а:
// 1. Определение (через @Component, @Bean, XML и т.д.)
// 2. Сканирование и регистрация в контейнере
// 3. Создание (через конструктор)
// 4. Внедрение зависимостей (DI)
// 5. Вызов методов @PostConstruct (инициализация)
// 6. Использование
// 7. Вызов @PreDestroy перед уничтожением (если Singleton)
// Scopes (Области жизни бинов):
// ├── singleton (по умолчанию) — один экземпляр на Spring-контекст
// ├── prototype — каждый раз создается новый экземпляр
// ├── request — один бин на HTTP-запрос (только для Web)
// ├── session — один бин на сессию (только для Web)
// └── application — один бин на приложение (ServletContext)
// Аннотации:
// ├── @Component — помечает класс как Spring Bean
// ├── @Service, @Repository, @Controller — производные от @Component
// ├── @Scope("singleton") — явно указывает скоуп
// └── @Lazy — ленивое создание бина



// 4. Конфигурации
// Spring поддерживает разные стили конфигурации:
// Аннотационная конфигурация (современно):
// ├── @Configuration — класс конфигурации
// └── @Bean — метод, создающий объект
// Компонентное сканирование:
// ├── @ComponentScan — сканирует пакеты на предмет @Component
// └── basePackages / basePackageClasses — где искать
// XML-конфигурация (устаревшее, но поддерживается):
// └── <beans>, <context:component-scan>, <bean>, <property> и т.п.



// Environment, Profiles и Properties
// Spring позволяет работать с переменными окружения и профилями:
// Environment — интерфейс для работы с настройками
// ├── application.properties / application.yml — источник конфигурации
// ├── @Value — внедряет значение из проперти
// └── @ConfigurationProperties — бин настраивается из проперти
// Profiles:
// ├── Позволяют группировать бины по окружениям (dev, test, prod)
// ├── Аннотация: @Profile("dev")
// └── Активируются через:
// ├── application.properties: spring.profiles.active=dev
// └── переменные среды



// Spring Events
// Spring поддерживает обработку событий между бинами:
// Компоненты:
// ├── ApplicationEvent — базовый класс событий
// ├── ApplicationListener<E extends ApplicationEvent> — слушатель
// ├── @EventListener — более современный способ
// └── ApplicationEventPublisher — позволяет отправлять события
// Пример:
// - Один бин генерирует событие (например, при регистрации пользователя)
// - Другой бин подписан и реагирует (например, отправка email)



// Жизненный цикл приложения и хуки
// Spring позволяет добавлять код в моменты инициализации / завершения:
// Интерфейсы:
// ├── InitializingBean — метод afterPropertiesSet()
// ├── DisposableBean — метод destroy()
// ├── SmartLifecycle — автозапуск при старте, остановка при завершении
// └── ApplicationRunner / CommandLineRunner — хуки после запуска
// Аннотации:
// ├── @PostConstruct — метод вызывается после создания бина
// └── @PreDestroy — вызывается перед удалением бина



// Различия между Spring Core и Spring Boot
// Spring Core — это фундамент (DI, IoC-контейнер, конфигурация)
// Spring Boot — надстройка, автоматизирующая настройки и запуск приложения
// Spring Boot:
// ├── Включает Spring Core как зависимость
// ├── Автоматически конфигурирует зависимости (AutoConfiguration)
// ├── Подключает встроенный web-сервер (Tomcat)
// ├── Использует application.yml/.properties
// └── Делает старт проекта быстрым (spring-boot-starter и т.д.)



// Как Spring Core взаимодействует с другими модулями
// Spring Boot, Spring Data, Spring MVC и другие используют Spring Core.
// ├── Spring Data — использует DI и бин-контейнер для репозиториев
// ├── Spring MVC — контроллеры — это @Controller-бины
// ├── Spring Security — использует бины конфигурации фильтров
// └── Spring WebSocket, Batch и др. — всё на базе Core

// => Spring Core = фундамент всей экосистемы Spring.
public class SpringCoreExample {
    // https://habr.com/ru/articles/490586/
}
