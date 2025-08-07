package example.transactions.examples;

import org.springframework.data.jpa.repository.JpaRepository;
import example.transactions.model.Account;

// SPRING DATA: ПОЛНОЕ ДЕРЕВО (SQL + JPA + Hibernate + JDBC + NoSQL)

// Spring Data
// ├── Что это:
// │ ├── Абстракция над доступом к данным (данные = любая БД или хранилище)
// │ ├── Работает с разными типами баз:
// │ │ ├── SQL (реляционные) — через Spring Data JPA
// │ │ ├── NoSQL:
// │ │ │ ├── MongoDB — через Spring Data MongoDB
// │ │ │ ├── Cassandra — через Spring Data Cassandra
// │ │ │ └── Redis — через Spring Data Redis
// │ │ └── LDAP и др.
// │ └── Предоставляет единый способ описывать репозитории (интерфейсы)
// ├── Основные интерфейсы:
// │ ├── Repository — базовый, "маркер", без методов
// │ ├── CrudRepository — базовые CRUD-операции
// │ ├── PagingAndSortingRepository — добавляет пагинацию, сортировку
// │ └── JpaRepository — JPA-specific (работает только с реляционными БД)
// ├── Основные инструменты:
// │ ├── Spring Data JPA — через ORM и аннотации
// │ ├── JdbcTemplate — ручная работа с SQL, обёртка над JDBC
// │ └── Spring Data для NoSQL (MongoDB, Redis, Cassandra и др.)
// ├── Как работает:
// │ ├── Генерация реализации интерфейсов (динамические прокси)
// │ ├── Автоматический парсинг имён методов (findByName → SQL)
// │ ├── Возможность писать SQL/JPQL вручную через @Query
// │ └── Работает через JPA или напрямую через JDBC
// └── Разница подходов:
// ├── JdbcTemplate — низкоуровневый подход (руками)
// └── Spring Data JPA — декларативный подход (интерфейсы + аннотации)

// ↓

// Spring Data JPA
// ├── Что это:
// │ ├── Подпроект Spring Data
// │ └── Работает только с реляционными базами через JPA
// ├── Репозитории:
// │ ├── JpaRepository<T, ID> — включает всё от CrudRepository и расширяет
// │ └── Поддержка Specification, EntityGraph, Paging, Auditing
// ├── Возможности:
// │ ├── Методы по имени: findByEmail, findByAgeGreaterThan
// │ ├── @Query — JPQL или native SQL
// │ ├── @Modifying — для DML (update/delete)
// │ ├── Пагинация: Pageable, Page<T>, Sort
// │ └── @CreatedDate, @LastModifiedDate — автоматический аудит
// ├── Как работает:
// │ ├── Интерфейсы → прокси → SQL-запросы
// │ ├── Под капотом использует JPA
// │ └── В Spring Boot по умолчанию — Hibernate
// └── Зависит от:
// └── JPA (Java Persistence API)

// ↓

// JPA (Java Persistence API)
// ├── Что это:
// │ ├── Официальный стандарт Java EE / Jakarta EE для ORM
// │ ├── Только API: аннотации, интерфейсы, правила
// │ └── Не содержит реализации — требует отдельного провайдера
// ├── Компоненты:
// │ ├── @Entity, @Id, @GeneratedValue — ORM-аннотации
// │ ├── @OneToMany, @ManyToOne — связи между таблицами
// │ ├── EntityManager — основной интерфейс API
// │ └── JPQL — объектно-ориентированный SQL-подобный язык
// ├── Жизненный цикл сущности:
// │ └── New → Managed → Detached → Removed
// └── Реализации JPA:
// ├── Hibernate — по умолчанию в Spring Boot
// ├── EclipseLink
// ├── OpenJPA
// └── Batoo JPA (устаревший)

// ↓

// Hibernate (реализация JPA)
// ├── Что это:
// │ ├── ORM-фреймворк (открытый исходный код)
// │ └── Реализация JPA по умолчанию в Spring Boot
// ├── Компоненты:
// │ ├── Session — альтернатива EntityManager (более гибкий)
// │ ├── HQL — расширенный JPQL (Hibernate Query Language)
// │ ├── Кэш: 1 уровень (Session), 2 уровень (настраивается отдельно)
// │ ├── LAZY / EAGER — стратегии загрузки данных
// │ ├── Dirty Checking — авто-обнаружение изменений объектов
// │ └── Поддержка SQL, stored procedures, interceptors, events
// ├── Под капотом:
// │ ├── Spring Boot конфигурирует DataSource (из application.yml)
// │ ├── Hibernate получает соединения (JDBC Connection)
// │ ├── Читает аннотации и строит модель таблиц
// │ ├── Управляет объектами в памяти
// │ ├── При commit — сравнивает данные и вызывает SQL
// │ └── Работает поверх JDBC API
// └── В цепочке зависимостей:
// Spring Boot → Hibernate → JPA → JDBC → DataSource → Driver → БД

// ↓

// JDBC API (низкоуровневый)
// ├── Что это:
// │ ├── Нативное Java API для работы с БД (java.sql.*)
// │ ├── Работа через Connection, Statement, ResultSet
// │ └── Используется внутри Hibernate, Spring, JdbcTemplate
// └── DataSource:
// ├── Источник подключений (настраивается в Spring)
// ├── Позволяет создавать Connection
// └── Используется Hibernate и JdbcTemplate

// ↓

// JdbcTemplate (альтернатива Spring Data JPA)
// ├── Что это:
// │ ├── Класс из Spring Framework
// │ └── Упрощает работу с JDBC API вручную
// ├── Особенности:
// │ ├── Пишешь SQL-запросы вручную
// │ ├── Обработка ResultSet автоматически
// │ └── Поддержка SQL-инъекций через ?-параметры
// └── Когда использовать:
// └── Когда нужен контроль над SQL или высокая производительность

// Наконец-то собрал полное дерево.Теперь можно посмотреть, как реализованы сами методы.
public interface SpringDataExample extends JpaRepository<Account, Long> {
}
