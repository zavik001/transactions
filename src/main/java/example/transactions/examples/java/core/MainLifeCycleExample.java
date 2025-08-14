package example.transactions.examples.java.core;

// https://topjava.ru/blog/glubokoye-pogruzheniye-v-protsess-zapuska-jvm
public class MainLifeCycleExample {

}
// Java — Main Life Cycle (от исходника до выполнения)
// │
// ├── 1. Исходный код (Source code)
// │ ├── (.java файлы), generics, аннотации, lambdas, API.
// │ ├── Платформенно-независимая логика (JVM решает детали платформы).
// │ └── Перед выполнением всегда проходит через javac.
// │
// ├── 2. Компиляция (javac)
// │ ├── Преобразует .java → .class (байткод).
// │ ├── Делает:
// │ │ ├── Лексический и синтаксический анализ (AST).
// │ │ ├── Семантическую проверку (type check, модификаторы доступа).
// │ │ ├── Стирание дженериков (type erasure).
// │ │ └── Генерацию инструкций JVM.
// │ ├── Байткод сохраняется в файлах .class.
// │ └── Байткод не зависит от ОС/процессора — ключ к кроссплатформенности.
// │
// ├── 3. Загрузка классов (Class Loading)
// │ ├── Делается через ClassLoader.
// │ ├── Этапы:
// │ │ ├── Loading — чтение .class в память.
// │ │ ├── Linking — проверка байткода (verification) + подготовка static-полей.
// │ │ └── Initialization — выполнение static-блоков и инициализация констант.
// │ ├── Виды ClassLoader'ов:
// │ │ ├── Bootstrap — грузит стандартную библиотеку (java.*).
// │ │ ├── Platform — грузит модули и API.
// │ │ └── Application — грузит код приложения.
// │ └── Кроссплатформенность:
// │ ├── Один и тот же .class файл можно загрузить на любой системе с JVM.
// │ └── ClassLoader адаптирует доступ к системным ресурсам через абстракцию.
// │
// ├── 4. Связь с Reflection
// │ ├── Reflection — API для анализа/вызова классов и методов в runtime.
// │ ├── Работает через Class<?> объекты, полученные от ClassLoader.
// │ ├── Позволяет:
// │ │ ├── Создавать объекты по имени класса.
// │ │ ├── Вызывать методы, даже приватные.
// │ │ └── Читать аннотации.
// │ ├── Reflection часто используют фреймворки для конфигурации в runtime.
// │ └── Пример:
// │ Class<?> c = Class.forName("MyClass");
// │ Object obj = c.getDeclaredConstructor().newInstance();
// │
// ├── 5. Исполнение (Execution)
// │ ├── JVM запускает байткод через интерпретатор и JIT-компилятор.
// │ ├── JIT переводит часто используемый байткод в машинный код для скорости.
// │ ├── Исполнение управляется:
// │ │ ├── Управлением памятью (Garbage Collector).
// │ │ ├── Потоками (Thread Scheduler).
// │ │ └── Безопасностью (Security Manager, модули).
// │
// ├── 6. Завершение программы
// │ ├── Освобождаются ресурсы (память, файлы, сокеты).
// │ ├── Останавливаются потоки (если не daemon).
// │ └── JVM выгружает классы и завершает процесс.
// │
// └── Итого:
// ├── javac создаёт универсальный байткод.
// ├── ClassLoader адаптирует его под любую платформу.
// ├── Reflection позволяет работать с классами динамически.
// └── JVM + JIT обеспечивают исполнение с оптимизациями.


// 1. JIT & HotSpot оптимизации
// Как JIT компилирует горячие методы в машинный код.
// Inline expansion, escape analysis, loop unrolling.
// Tiered compilation (C1/C2 компиляторы).
// Как посмотреть JIT-лог (-XX:+UnlockDiagnosticVMOptions -XX:+PrintCompilation).

// 2. Memory Model (JMM)
// Happens-before правила.
// Visibility и ordering.
// Почему volatile — это не просто "без кеша".
// Как JMM гарантирует корректность между потоками.

// 3. Мониторы и синхронизация
// Монитор в байткоде (monitorenter / monitorexit).
// Biased Locking, Lightweight Locking, Lock Elision.
// Как синхронизация оптимизируется под капотом.

// 4. Native Interface
// JNI (Java C/C++).
// JNA и Panama (новое API для нативных вызовов).

// 5. JVM internals utilities
// jcmd, jmap, jstack, jconsole, jvisualvm.
// Heap dump анализ (Eclipse MAT).

// 6. Class Data Sharing (CDS) / AppCDS
// Как JVM кеширует предзагруженные классы между запусками.

// 7. Flight Recorder / Mission Control
// Профайлинг и диагностика производительности.

// 8. Bytecode Engineering
// Чтение и правка байткода (javap, ASM, ByteBuddy).
// Генерация классов на лету.
