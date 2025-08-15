package example.transactions.examples.java.core;
// https://www.oracle.com/webfolder/technetwork/tutorials/obe/java/gc01/index.html


// Garbage Collector (GC) — полный, подробный разбор
// │
// |
// |
// |
// |
// |
// ├── Что это такое:
// │ ├── Механизм JVM для автоматического управления памятью: обнаруживает объекты, на которые нет
// ссылок, и освобождает занятую ими память.
// │ ├── Работает над Heap (кучей) — областью памяти, где создаются объекты во время выполнения
// приложения.
// │ ├── Основная цель: предотвратить переполнение кучи (OutOfMemoryError) и минимизировать влияние
// очистки на производительность и задержки приложения.
// │ └── Баланс интересов: максимальная пропускная способность vs минимальные задержки vs
// предсказуемость пауз.
// │
// │
// |
// |
// |
// |
// |
// ├── Почему это важно:
// │ ├── Неправильный GC/настройки → частые длинные паузы, OOME, падения throughput.
// │ ├── Правильный выбор коллектора и тюнинг — критичны для серверных приложений (latency-sensitive
// и throughput-oriented).
// │ └── Понимание внутренних механизмов помогает диагностировать утечки, промоции объектов,
// фрагментацию, «promotion failure» и т.д.
// │
// │
// |
// |
// |
// |
// |
// ├── Ключевые понятия и термины:
// │ ├── GC roots — стартовые точки (локальные переменные стеков, статические поля, JNI-локи,
// активные потоки и т.д.).
// │ ├── Reachability (достижимость):
// │ │ ├── Strong (сильные) — обычные ссылки
// │ │ ├── Soft — собираются при недостатке памяти (кеш-подобно)
// │ │ ├── Weak — быстро собираются, используются в WeakHashMap
// │ │ └── Phantom — для пост-очистки и взаимодействия с ReferenceQueue
// │ ├── Finalization / Cleaner — finalize() устаревший; Cleaner (java.lang.ref.Cleaner) —
// предпочтительная замена.
// │ └── Stop-the-world (STW) — паузы, когда все mutator-потоки приостанавливаются для выполнения
// части GC.
// │
// |
// |
// |
// |
// |
// |
// ├── cтруктура памяти Heap (в терминах HotSpot/современных collectors):
// │ ├── Young Generation (Young):
// │ │ ├── Eden — новые объекты попадают сюда
// │ │ └── Survivor0 / Survivor1 — «выжившие» после Minor GC перемещаются между ними
// │ ├── Old (Tenured) Generation — долгоживущие объекты; сюда продвигаются (promote) выжившие
// объекты после ряда сборок
// │ ├── Metaspace — метаданные классов
// │ ├── Humongous (в G1) — большие объекты, выделенные по-region
// │ └── Thread-Local Allocation Buffers (TLABs) — локальные области выделения памяти для каждого
// потока (ускоряют allocation)
// │ └── TLAB позволяет выделять объект «bump-pointer» (инкремент указателя) без синхронизации.
// ([OpenJDK][1])
// │
// │
// |
// |
// |
// |
// |
// ├── Объект в памяти — layout (важные детали):
// │ ├── Заголовок объекта (object header):
// │ │ ├── Mark word — содержит хэш, информацию о GC, состояние монитора (для synchronized), метки
// для сборщика
// │ │ └── Klass pointer — указатель на мета-информацию класса
// │ ├── Поля объекта (выровненные в памяти по alignment, например 8 байт)
// │ ├── Compressed OOPs — указатели объектов (object references) часто сжимаются (compressed oops)
// для экономии памяти
// │ └── Порождает последствия: allocation size, fragmentation, влияние на TLAB sizing
// │
// │
// |
// |
// |
// |
// |
// ├── Жизненный цикл выделения объекта (быстрый путь и медленный путь)
// │ ├── Быстрый путь (fast path / TLAB):
// │ │ ├── Компилированный код пытается «bump» указатель TLAB (инструкция: увеличить указатель на
// size объекта)
// │ │ ├── Если места достаточно — объект создан без взаимодействия с глобальным heap (без
// блокировок)
// │ │ └── Это делает выделение очень дешёвым (несколько инструкций). ([shipilev.net][2])
// │ ├── Медленный путь (slow path):
// │ │ ├── Если TLAB кончился → поток запрашивает новый TLAB у VM или идёт глобальное выделение
// │ │ ├── Большие объекты (humongous) часто выделяются напрямую в Old/Region, минуя Eden
// │ │ └── Может вызывать синхронизацию и / или запуск Minor GC при нехватке места
// │ └── Писчие барьеры (write barriers) помечают карты (card table) / remembered sets при изменении
// ссылок между регионами/генерациями
// │
// │
// |
// |
// |
// |
// |
// ├── Генерационная идея (Generational GC) — почему она работает:
// │ ├── Наблюдение: большинство объектов короткоживущие (проживут очень мало).
// │ ├── Следовательно — оптимальнее часто очищать «молодое» поколение (где большинство мусора),
// редко — старое.
// │ ├── Minor GC — быстро очищает Young (этап: пометка выживших → копирование / эвакуация в
// Survivor / продвижение).
// │ └── Major / Full GC — полная проверка/очистка Old (дороже).
// │
// │
// |
// |
// |
// |
// |
// ├── Основные фазы GC (в терминах high-level, применимо для многих алгоритмов)
// │ ├── Mark (пометка): определить живые объекты, начиная от GC roots.
// │ ├── Sweep / Clear (сбор): освобождение памяти недостижимых объектов.
// │ ├── Compact / Evacuate: перемещение живых объектов в цельную область для уменьшения
// фрагментации.
// │ ├── Concurrent этапы: часть фаз выполняется параллельно с mutator-потоками (прочие — в STW).
// │ └── Remark / Cleanup: дополнительные шаги для согласования данных, часто требуют короткого STW.
// │
// │
// |
// |
// |
// |
// |
// ├── Стандартные реализации (сравнение — что делает каждая в общих чертах)
// |
// |
// │ ├── Serial GC:
// │ │ ├── Однопоточная, простая, подходит для small heaps / single-threaded apps.
// │ │ └── Все GC-фазы — стоп-мир. (команда: -XX:+UseSerialGC)
// │ │
// │ │
// │ ├── Parallel GC (Throughput):
// │ │ ├── Многопоточный; фокус — общая пропускная способность, не минимальные паузы.
// │ │ └── Хорош для batch/throughput-oriented систем. ( -XX:+UseParallelGC )
// │ │
// │ │
// │ ├── CMS (Concurrent Mark-Sweep) — исторический low-pause collector:
// │ │ ├── Выполняет marking почти конкурентно, но не выполняет компактирование полностью—проблемы
// фрагментации.
// │ │ └── Устарел/частично removed в новых релизах.
// │ │
// │ │
// │ ├── G1 (Garbage-First) — region-based, hybrid, default в многих JDK:
// │ │ ├── Делит heap на регионы фиксированного размера; отслеживает, какие регионы содержат больше
// мусора, и эвакуирует их первыми — «garbage-first».
// │ │ ├── Выполняет concurrent global marking, затем планирует Evacuation (evacuate) регионов;
// поддерживает mixed GC (young + old regions).
// │ │ └── Балансирует паузы, throughput и фрагментацию; хорош в большинстве серверных сценариев.
// │ │
// │ │
// │ ├── ZGC — низколатентный, масштабируемый (Oracle/OpenJDK):
// │ │ ├── Проектирован для очень больших heap (GB → TB) и минимальных пауз (миллисекунды).
// │ │ ├── Почти все тяжелые этапы выполняются конкурентно; использует барьеры для корректности при
// релокации объектов.
// │ │ └── Подходит для latency-sensitive приложений; включается -XX:+UseZGC.
// │ └── Shenandoah — конкурентный compacting GC (RedHat / OpenJDK):
// │ ├── Выполняет concurrent compaction (сжатие/перемещение объектов в фоне), минимальные паузы,
// паузы не растут с размером heap.
// │ └── Отличный выбор для low-pause сценариев (альтернатива ZGC). ([docs.redhat.com][5])
// │
// │
// │
// |
// |
// |
// │
// | G1 Garbage Collector — Oracle/OpenJDK (G1 overview).
// | Z Garbage Collector (ZGC) — OpenJDK/Oracle docs.
// | Shenandoah GC — Red Hat / OpenJDK docs. ([docs.redhat.com][5])
// | Storage Management / TLAB — OpenJDK HotSpot docs. ([OpenJDK][1])
// | Практические статьи про allocation, card table и remembered sets. ([InfoQ][6])
//
//
//
//
// -Xms<size> и -Xmx<size>:
// Устанавливают начальный (-Xms) и максимальный (-Xmx) размер кучи.
// Например, -Xms512m -Xmx4g устанавливает начальный размер кучи в 512 мегабайт и максимальный в 4
// гигабайта.

// -XX:NewRatio=<ratio>:
// Определяет соотношение между старым и молодым поколением в куче.
// Например, -XX:NewRatio=2 означает, что старое поколение будет в два раза больше молодого.

// -XX:SurvivorRatio=<ratio>:
// Определяет соотношение между каждой из Survivor областей и Eden областью в молодом поколении.
// Например, -XX:SurvivorRatio=8 означает, что Eden будет в 8 раз больше каждой из Survivor
// областей.

// -XX:MaxTenuringThreshold=<value>:
// Устанавливает максимальное количество циклов сборки мусора, после которых объект из молодого
// поколения перемещается в старое.
// Более низкое значение означает более быстрое перемещение объектов в старое поколение.

// -XX:+Use<Collector>:
// Указывает, какой сборщик мусора использовать. Например, -XX:+UseG1GC, -XX:+UseParallelGC,
// -XX:+UseConcMarkSweepGC.



// Parallel GC:
// -XX:ParallelGCThreads=<n>: Устанавливает количество потоков для сборки мусора в Parallel GC.

// CMS (Concurrent Mark-Sweep):
// -XX:CMSInitiatingOccupancyFraction=<percent>: Указывает процент заполнения кучи, при котором
// начнется CMS cycle.
// -XX:+UseCMSInitiatingOccupancyOnly: Указывает JVM использовать только заданный процент для начала
// CMS cycle.

// G1 GC:
// -XX:MaxGCPauseMillis=<milliseconds>: Целевое значение для максимальной длительности паузы GC.
// -XX:G1HeapRegionSize=<size>: Устанавливает размер региона в G1 GC.

// ZGC:
// -XX:ConcGCThreads=<n>: Количество потоков, используемых для параллельной обработки в ZGC.

// Shenandoah:
// -XX:ShenandoahGCHeuristics=<heuristic>: Определяет эвристику, которую Shenandoah будет
// использовать.



// 1. Serial GC (-XX:+UseSerialGC, "обычный" до Java 7/8, default для client JVM)
// Простой, 1 поток, полный STW. Подходит для малых apps. В JDK 24: cleanup/refactoring (нет больших
// изменений).


// - Зоны heap: Young (Eden + Survivor From/To), Old (Tenured). Metaspace отдельно.
// - Что очищает: Minor — young; Major — old; Full — вся heap + metaspace (если нужно).
// - Как очищает: Copying (young: копирует выжившие); Mark-Sweep-Compact (old: mark живые, sweep
// мёртвые, compact фрагментацию).
// - Сколько потоков: 1 GC-thread (serial).
// - По шагам (minor GC пример):

// 1. Инициирование GC: Когда Eden заполняется (allocation failure), JVM проверяет, нужно ли GC.
// Если да — планирует остановку. Почему? Чтобы не тратить CPU зря. (Нет потоков — просто проверка.)
// 2. STW: Остановка app-threads: Все потоки приложения (mutators) приводятся к safepoint
// (безопасная точка, где нет полуисполненных операций). Это занимает ms, но гарантирует, что heap
// не меняется. Почему? Если app продолжит, может создать объект или изменить ссылку, сломав
// marking.
// 3. Mark: Маркировка живых объектов: Один GC-thread начинает с roots (thread stacks, static
// vars, registers). От roots рекурсивно сканирует достижимые объекты в young, помечая их как
// "живые" (bit в header объекта). Почему young только? Minor GC фокусируется на новых объектах.
// Аналогия: Уборщик отмечает "не выкидывать" на мебели, начиная от двери (roots).
// 4. Copy: Копирование выживших: Выжившие (промаркированные) из Eden копируются в Survivor To;
// из Survivor From — тоже в To (или в old, если объект "возраст" > threshold, по умолчанию 15).
// Копирование компактное — объекты кладутся подряд, без фрагментации. Почему копирование? Young —
// много мусора, копирование быстрее sweep.
// 5. Sweep: Очистка: Eden и старый Survivor From обнуляются (память освобождается). Нет
// компакта, т.к. copying уже сделало. Аналогия: Выкинуть всё немаркированное.
// 6. Смена ролей survivors: To становится From, пустой From — To. Почему? Чтобы чередовать и
// избегать фрагментации.
// 7. Конец STW: App-threads размораживаются, продолжают. JVM обновляет stats (для будущих GC).

// Для major/full: Аналогично, но mark-sweep-compact на old (compact сдвигает объекты, чтобы убрать
// дыры).
// - STW и потоки: Полный STW, потому что без него app мутирует heap, ломая marking. С 10
// app-threads: все стоп, пока 1 GC работает. Почему не фон? Не concurrent — нет barriers.

// - Визуализация (timeline, A1-A10: app-threads, G: GC):
//
// Время: 0 ----------------> GC start ----------------> GC end
// A1-A10: Работают... [STW: заморожены] Продолжают...
// G: (спит) Mark -> Copy -> Sweep (спит)
//

// - Аналогия: Один уборщик (G) стопит всех (A), иначе хаос. Паузы длинные (секунды на big
// heap).
// - Плюсы/минусы: Просто, низкий overhead. Минус: длинные паузы.



// 2. Parallel GC (-XX:+UseParallelGC, default server до Java 8)
// Много потоков для ускорения, но STW. Смысл vs Serial: паузы короче (параллелизм). В JDK 24:
// убрана синхронизация в evacuation (JDK-8269870, меньше пауз).

// - Зоны heap: Young (Eden + 2 survivors), Old.
// - Что очищает: Minor — young; Major — old (parallel compact); Full — всё.
// - Как очищает: Copying (young, parallel); Mark-Sweep-Compact (old, parallel).
// - Сколько потоков: Несколько GC-threads (~cores, -XX:ParallelGCThreads=N).
// - По шагам (minor GC):

// 1. Инициирование GC: Eden full — JVM триггерит, выбирает N GC-threads (по cores). Почему?
// Чтобы распределить нагрузку.
// 2. STW: Остановка app: Как в Serial, все mutators к safepoint. Почему? Parallel GC не
// concurrent — изменения от app сломают синхронизацию между GC-threads.
// 3. Mark: Параллельная маркировка: GC-threads делят roots (каждый берёт часть stacks). Затем
// parallel сканируют graph: один thread маркирует subtree, если большой — делит на work items.
// Barriers нет, т.к. STW. Аналогия: Уборщики делят комнату на секции, отмечают параллельно.
// 4. Copy: Параллельное копирование: Threads делят young на chunks, копируют выжившие в To/old
// atomic'но (locks на целевые области). Выжившие компактны. Почему parallel? Ускоряет на
// multi-core.
// 5. Sweep: Параллельная очистка: Threads очищают свои chunks в Eden/From (обнуляют pointers).
// 6. Смена ролей survivors: Синхронизировано, все threads ждут.
// 7. Конец STW: App возобновляется. В JDK 25: Оптимизирована evacuation (меньше sync).

// Для major: Parallel compact — threads делят old на регионы, compact каждый свой.
// - STW и потоки: STW нужен для consistency — app не мешает parallel работе GC. С 10
// app-threads: все стоп, но GC быстрее.
// - Визуализация (G1-G4: GC-threads):

//
// Время: 0 ----------------> GC start ----------------> GC end
// A1-A10: Работают... [STW: заморожены] Продолжают...
// G1-G4: (спят) Mark/Copy зона1-4 parallel (спят)
//

// - Аналогия: Несколько уборщиков делят комнату, но стопят людей — иначе зоны сломаются. Паузы
// ~сотни ms.
// - Плюсы/минусы: Высокий throughput. Минус: STW не для low-latency.



// 3. G1 GC (-XX:+UseG1GC, default с Java 9)
// Региональный, concurrent marking, STW evacuation. Цель: паузы <200ms. В JDK 24: улучшения
// predictor/memory (JDK-8343189/8336086), late barriers (JEP 475) — меньше компиляции, лучше
// ordering.

// - Зоны heap: ~2000 регионов (1-32MB, dynamic young/old/humongous).
// - Что очищает: Mixed (young + часть old); Full (fallback).
// - Как очищает: Evacuation (copying регионов); Concurrent marking.
// - Сколько потоков: Несколько (parallel STW, concurrent marking).
// - По шагам:

// 1. Initial Mark (STW, короткий): Останавливает app ненадолго (ms). Маркирует roots (stacks,
// etc.), часто пиггибек на minor GC. Почему STW? Roots могут измениться, нужно snapshot. Аналогия:
// Быстрый "стоп" для фото корней.
// 2. Concurrent Marking: App работает! GC-threads (parallel) маркируют от roots весь graph в
// old регионах. Write barriers (в app-коде) ловят изменения: если app меняет ссылку, barrier
// добавляет в card table (dirty cards для rescanning). Почему concurrent? Barriers корректируют на
// лету. В JDK 25: Late barriers (JEP 475) — barriers компилируются позже, меньше overhead.
// 3. Remark (STW, короткий): Стоп app, финализирует marking: rescans dirty cards, учитывает
// SATB (snapshot-at-the-beginning) для consistency. Почему STW? Чтобы поймать последние изменения
// atomic'но.
// 4. Cleanup (STW/Concurrent): Concurrent: Очищает полностью мёртвые регионы (reclaim без
// copy). STW: Обновляет stats (livability регионов для будущего).
// 5. Evacuation (Mixed GC, STW): Выбирает регионы с max garbage (young + old по priority). STW:
// GC-threads parallel копируют выжившие в пустые регионы (evacuation). Young полностью, old
// инкрементально. Почему STW? Copy меняет адреса — app не должен видеть halfway. Аналогия:
// Эвакуация людей в новые комнаты, пока все стоп.
// 6. Full GC (fallback, STW): Если heap full — как Parallel: full mark-sweep-compact.

// G1 балансирует: Predictor (улучшен в JDK 25) предсказывает паузы.
// - STW и потоки: Marking concurrent (barriers ловят изменения), evacuation STW (short). С 10
// app-threads: минимальные стопы.
// - Визуализация:

//
// Время: 0 ----------------> GC cycle ----------------> End
// A1-A10: Работают... Работают (barriers)... Работают...
// G-threads: (спят) Concurrent Mark... (спят)
// (Short STW: ~ms для evacuation)
//

// - Аналогия: Уборщики убирают фонно, люди с датчиками (barriers) сигнализируют изменения.
// Минимальные стопы.
// - Плюсы/минусы: Низкие паузы, big heap. Минус: overhead > Parallel.



// 4. ZGC (-XX:+UseZGC, с Java 11, stable 15)
// Ultra-low-pause, concurrent almost всё. Нет поколений. В JDK 24: удаление non-generational (JEP
// 490) — только generational теперь.

// - Зоны heap: Регионы (zPages, 2MB-16GB, dynamic).
// - Что очищает: Вся heap concurrent (циклы, нет minor/major).
// - Как очищает: Load barriers (обновляют pointers); Concurrent relocation.
// - Сколько потоков: Несколько (concurrent/parallel).
// - По шагам:

// 1. Pause Mark Start (STW, ms): Короткий стоп app (sub-ms в JDK 25). Маркирует roots (stacks,
// registers, globals). Почему STW? Roots — критично, изменения сломают. Load barriers активированы.
// Аналогия: Быстрый стоп для маркировки "входов" в комнату.
// 2. Concurrent Mark: App работает. GC-threads parallel маркируют graph от roots, используя
// colored pointers (в pointers биты: good/bad/remapped). Load barriers в app: при чтении pointer,
// если bad color — GC фиксирует (heal). Barriers обновляют marking на лету. Почему concurrent?
// Barriers обеспечивают consistency без стоп. В generational: Отдельная marking для young/old.
// 3. Pause Mark End (STW, ms): Короткий стоп: Финализирует marking, rescans барьеры, обновляет
// livemap (bitmap живых объектов по регионам). Почему STW? Atomic завершение, чтобы избежать races.
// 4. Concurrent Prepare Relocate: App работает. GC подготавливает: Выбирает регионы для
// relocation (с garbage), создаёт forwarding tables (new addresses). Parallel по threads. Почему
// concurrent? Нет изменений адресов yet.
// 5. Pause Relocate Start (STW, ms): Стоп: Relocates roots (обновляет pointers в stacks к новым
// адресам). Почему STW? Roots sensitive — app не должен исполняться.
// 6. Concurrent Relocate: App работает. GC-threads relocate объекты: Копируют в новые регионы,
// используя colored pointers (app видит старые, barrier redirect к новым). Lazy relocation: Не все
// сразу, только accessed. Почему concurrent? Barriers handle: При access, app/G C cooperate.
// Аналогия: Переезд мебели, пока люди ходят — датчики (colors) перенаправляют.
// 7. Concurrent Remap: App работает. Обновляет все pointers на новые (remap phase). Barriers
// помогают: Lazy remap при access. Завершает цикл, очищает старые регионы. В JDK 25: Оптимизировано
// для generational (young relocate быстрее).

// - STW и потоки: Почти без STW (<10ms), barriers обеспечивают. App работает nonstop.
// - Визуализации:
// (как в G1, но micro-STW).

// - Аналогия: Умные уборщики с датчиками, минимальные миг-стопы.
// - Плюсы/минусы: Минимальные паузы, huge heap. Минус: CPU/memory overhead.



// 5. Shenandoah GC (-XX:+UseShenandoahGC, с Java 12, от Red Hat)
// Concurrent, low-pause, forwarding pointers. В JDK 24: generational experimental (JEP 404) — лучше
// throughput/resilience. В JDK 25: stable.

// - Зоны heap: Регионы (~2MB, dynamic, no generations в non-gen; generational в new).
// - Что очищает: Вся heap concurrent (циклы).
// - Как очищает: Concurrent evacuation (copy); Brooks pointers.
// - Сколько потоков: Несколько (concurrent/parallel).
// - По шагам:

// 1. Init Mark (STW, короткий): Стоп app (ms). Маркирует roots. Почему STW? Snapshot roots.
// 2. Concurrent Marking: App работает. GC маркирует graph, write barriers ловят изменения
// (добавляют в queue для rescanning). Brooks pointers (forwarding: extra word в объекте для new
// address). Почему concurrent? Barriers + forwarding consistency.
// 3. Final Mark (STW): Стоп: Финализирует marking, evacuates roots (обновляет их pointers).
// Rescans queues. Почему STW? Atomic evac roots.
// 4. Concurrent Evacuation: App работает. GC копирует живые объекты в новые регионы, обновляет
// forwarding pointers. Barriers: При access, app видит forwarding и redirect. Parallel threads.
// Почему concurrent? Forwarding позволяет app видеть updates. В generational: Young evac быстрее.
// 5. Update Refs (STW/Concurrent): Concurrent: Обновляет references (pointers) на новые адреса.
// STW если нужно для финала. Barriers помогают lazy update.
// 6. Concurrent Cleanup: Очищает старые регионы (reclaim память). Почему concurrent? Регионы
// уже пусты после evac.
// 7. Full GC (fallback, STW): Редко, как Parallel.

// - STW и потоки: Почти concurrent, barriers/forwarding. <10ms паузы.
// - Визуализация:
// (как в G1, но micro-STW).

// - Аналогия: Уборщики с перенаправлениями, люди не замечают.
// - Плюсы/минусы: Низкие паузы, хороший throughput. Минус: barriers overhead.



// VisualVM + visualGC
public class GCExample {
    // https://habr.com/ru/articles/269621/
    // https://shipilev.net/#shenandoah
    // https://habr.com/ru/companies/jugru/articles/846308/
    // https://habr.com/ru/companies/otus/articles/776342/
}
