package example.transactions.examples;

// Spring Context (Контейнер Spring)
// ├── Что это такое:
// │ ├── Главный интерфейс для доступа ко всем возможностям Spring
// │ ├── Расширяет BeanFactory (=> DI, управление жизненным циклом и др.)
// │ ├── Отвечает за создание, хранение и связывание бинов (Spring Beans)
// │ └── Управляет всем "контекстом" приложения — как «мозг» Spring
// │
// ├── Интерфейсы и реализации:
// │ ├── ApplicationContext (основной интерфейс)
// │ │ ├── FileSystemXmlApplicationContext
// │ │ ├── ClassPathXmlApplicationContext
// │ │ ├── AnnotationConfigApplicationContext (чаще всего)
// │ │ └── GenericApplicationContext
// │ └── WebApplicationContext — расширение для веб-приложений
// │
// ├── Как работает (пошагово):
// │ 1. При запуске Spring Boot создаёт ApplicationContext
// │ 2. Он сканирует пакеты и находит компоненты через аннотации:
// │ │ ├── @Component, @Service, @Repository, @Controller
// │ │ └── @Configuration и @Bean
// │ 3. ApplicationContext регистрирует и создает все бины
// │ 4. Внедряет зависимости (через @Autowired, конструкторы и т.п.)
// │ 5. Контролирует жизненный цикл бинов (@PostConstruct, @PreDestroy)
// │ 6. Предоставляет доступ к бинaм через getBean()
// │
// ├── Отношения:
// │ ├── ApplicationContext внедряет зависимости Beans
// │ ├── ApplicationContext использует BeanPostProcessors
// │ └── EventPublisher и ResourceLoader тоже реализованы внутри
// │
// ├── Внутренние механизмы:
// │ ├── BeanDefinition — метаданные бина
// │ ├── BeanFactoryPostProcessor — настройка до создания бинов
// │ ├── BeanPostProcessor — вмешательство в бин после создания
// │ └── Environment — доступ к application.properties/yml
// │
// ├── Дополнительно:
// │ ├── Контекст можно обновить: context.refresh()
// │ ├── Можно слушать события: ApplicationEvent, @EventListener
// │ └── Используется везде: в Web, AOP, Data, Boot — всё на нём
// │
// └── Главное понимать:
// ├── ApplicationContext = "центр управления" всей Spring-системы
// ├── В нём создаются все бины, живут и управляются
// ├── Он реализует множество других интерфейсов:
// │ ├── MessageSource (i18n)
// │ ├── ResourceLoader
// │ ├── ApplicationEventPublisher
// │ └── EnvironmentCapable
// └── Без него не работает почти ничего в Spring
public class SpringContextExample {

}
