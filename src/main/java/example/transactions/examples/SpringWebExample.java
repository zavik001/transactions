package example.transactions.examples;

// Spring Web (часть Spring Framework)
// ├── Что это:
// │ ├── Модуль для создания web-приложений (REST API, MVC, веб-сайты)
// │ ├── Основан на Servlet API и Spring Core (DI, контекст)
// │ └── Включает в себя Spring MVC
// │
// ├── Как работает:
// │ ├── Веб-сервер (например: Tomcat) принимает HTTP-запрос
// │ ├── Запрос передаётся в DispatcherServlet (фронт-контроллер)
// │ ├── DispatcherServlet:
// │ │ ├── Ищет подходящий контроллер (HandlerMapping)
// │ │ ├── Вызывает метод контроллера
// │ │ ├── Получает результат
// │ │ └── Возвращает ответ (через ViewResolver или HttpMessageConverter)
// │ └── Вся цепочка настраивается автоматически через Spring Boot
// │
// ├── Основные компоненты:
// │ ├── DispatcherServlet
// │ │ └── Главный фронт-контроллер, обрабатывает все входящие HTTP-запросы
// │ ├── HandlerMapping
// │ │ └── Определяет, какой контроллер обрабатывает конкретный путь
// │ ├── HandlerAdapter
// │ │ └── Вызывает соответствующий метод контроллера
// │ ├── ViewResolver (если HTML, Thymeleaf и т.п.)
// │ └── HttpMessageConverter (если JSON, XML)
// │
// ├── Аннотации контроллеров:
// │ ├── @Controller
// │ │ └── Класс является web-контроллером
// │ ├── @RestController
// │ │ └── То же самое, но сразу добавляет @ResponseBody (JSON)
// │ ├── @RequestMapping
// │ │ └── Общая аннотация для путей, методов, параметров
// │ ├── @GetMapping, @PostMapping, @PutMapping, @DeleteMapping
// │ │ └── Упрощённые версии @RequestMapping
// │ ├── @PathVariable
// │ │ └── Получить значение из части URL
// │ ├── @RequestParam
// │ │ └── Получить значение из query-параметра (?id=...)
// │ ├── @RequestBody
// │ │ └── Преобразовать тело запроса (JSON) в объект Java
// │ ├── @ResponseBody
// │ │ └── Вернуть Java-объект как JSON
// │ └── @ExceptionHandler
// │ └── Локальная обработка ошибок в контроллере
// │
// ├── Data Binding и валидация:
// │ ├── Spring сам маппит JSON в объекты (Jackson под капотом)
// │ ├── Можно использовать @Valid + JSR-380 аннотации (@NotNull, @Size и т.п.)
// │ └── Ошибки можно перехватывать через BindingResult или глобальный обработчик
// │
// ├── Как конфигурируется в Spring Boot:
// │ ├── Spring Boot автоконфигурирует:
// │ │ ├── DispatcherServlet
// │ │ ├── Jackson (JSON)
// │ │ ├── Embedded Tomcat / Jetty / Undertow
// │ │ └── HttpMessageConverters
// │ ├── Всё берётся из spring-boot-starter-web
// │ └── application.yml позволяет настраивать порты, контекст и т.д.
// │
// ├── Как Spring Boot запускает Spring Web:
// │ ├── @SpringBootApplication включает:
// │ │ └── @EnableAutoConfiguration → подтягивает web-конфигурации
// │ ├── Подключён spring-boot-starter-web → активируется web stack
// │ ├── Embedded Tomcat запускается автоматически
// │ ├── Создаётся DispatcherServlet и регистрируется на URL "/"
// │ └── Контроллеры сканируются через @ComponentScan
// │
// ├── Что входит в spring-boot-starter-web:
// │ ├── Spring Web (MVC)
// │ ├── Jackson (для JSON)
// │ ├── Embedded Tomcat
// │ ├── Validation API
// │ └── Logback (для логов)
// │
// Postman → Tomcat → DispatcherServlet
// → HandlerMapping → Controller → Service → Repository
// ← Response (JSON / HTML)
public class SpringWebExample {

}
