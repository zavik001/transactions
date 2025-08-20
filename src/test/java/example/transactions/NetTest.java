package example.transactions;

// JAVA NETWORKING



/*
 * 1) ОТКУДА ВЗЯЛСЯ JAVA.NET: ИСТОРИЯ И КОНТЕКСТ
 * 
 * Происхождение: Пакет java.net появился в JDK 1.0 (1996 год) как часть базового API для сетевого
 * программирования. Java изначально позиционировалась как язык для интернета (апплеты, веб), так
 * что networking был ключевым. Вдохновлён BSD sockets (стандарт Unix для сетей), но адаптирован для
 * кросс-платформенности — работает на Windows, Linux, macOS без нативного кода от разработчика.
 * 
 * Эволюция:
 * 
 * - JDK 1.1 (1997): Добавлены MulticastSocket, URLConnection для HTTP.
 * 
 * - Java 4 (2002): NIO (New I/O) в java.nio.channels — расширение для non-blocking, но java.net
 * остался для blocking.
 * 
 * - Java 7 (2011): NIO.2, Asynchronous channels.
 * 
 * - Java 11 (2018): HttpClient (java.net.http) — modern замена URLConnection.
 * 
 * - Java 21 (2023): Virtual Threads (JEP 444) — революция для blocking networking, делает java.net
 * scalable без переписывания кода.
 * 
 * - Java 24-25: Улучшения в performance (JEP 467: Faster Socket Impl), интеграция с Scoped Values
 * (JEP 464) для thread-local в vthreads, но core API стабильный.
 * 
 * Что представляет: java.net — это API для низкоуровневого сетевого взаимодействия (TCP/UDP, URL),
 * абстрагирующий OS-сокеты. Он решает проблему кросс-платформенного networking: пишешь один код —
 * работает везде, без забот о Winsock или POSIX sockets.
 */

/*
 * 2) ЧТО ИЗ СЕБЯ ПРЕДСТАВЛЯЕТ: КЛЮЧЕВЫЕ КОМПОНЕНТЫ
 * 
 * Базовые абстракции:
 * 
 * - Сокеты: Socket (клиент TCP), ServerSocket (сервер TCP), DatagramSocket (UDP).
 *
 * - Адреса: InetAddress (IP/hostname), InetSocketAddress (IP+port).
 * 
 * - URL: URL (унифицированный локатор), URI (идентификатор), URLConnection (соединение для
 * HTTP/FTP).
 *
 * - Дополнительно: Proxy, CookieHandler, NetworkInterface (для локальных интерфейсов).
 * 
 * Уровни:
 * 
 * - Низкий: Сокеты для raw TCP/UDP — контроль над байтами, но blocking.
 * 
 * - Средний: URL для протоколов (HTTP, FTP) — проще, но меньше контроля.
 * 
 * - Высокий: В Java 11+ HttpClient — async, HTTP/2, WebSocket.
 * 
 * Ключевые фичи:
 * 
 * - IPv4/IPv6: Автоматическая поддержка (prefer IPv6 via system prop).
 * 
 * - Timeouts: Connect/read/write timeouts для избежания hangs.
 * 
 * - Options: SO_KEEPALIVE, TCP_NODELAY (disable Nagle для latency).
 */

/*
 * 3) КАКУЮ ПРОБЛЕМУ РЕШАЕТ: ОСНОВНЫЕ СЦЕНАРИИ
 * 
 * Проблема 1: Кросс-платформенность. Без java.net пришлось бы писать native code для каждой OS (JNI
 * для Winsock/BSD). Решение: Абстракция над OS API — JVM handles детали.
 * 
 * Проблема 2: Blocking IO в многопоточных приложениях. Старые сервера — thread per connection, не
 * scale (C10k problem). Решение: С Java 21 Virtual Threads — blocking код на vthreads не жрёт
 * OS-threads, scale как non-blocking.
 * 
 * Проблема 3: Сложность протоколов. HTTP вручную — парсинг headers, body. Решение:
 * URLConnection/HttpClient handle это, плюс redirects, auth, cookies.
 * 
 * Проблема 4: Security и reliability. Raw sockets уязвимы (DoS, injection). Решение: Built-in
 * checks (permissions), TLS via JSSE (javax.net.ssl), но для базового net — manual.
 * 
 * Общие use cases: Клиенты (fetch data), сервера (echo, chat), proxies, discovery (multicast).
 */

/*
 * 4) ПОД КАПОТОМ: ЧТО ИСПОЛЬЗУЕТ JAVA.NET САМ
 * 
 * OS-level: JVM использует native sockets:
 * 
 * - Linux/macOS: epoll/kqueue для efficient I/O (в NIO), но для blocking — standard sockets.
 * 
 * - Windows: Winsock2, IOCP для async. - JVM impl: HotSpot использует JNI для native calls (e.g.,
 * SocketImpl).
 * 
 * Internal: - SocketImpl: Абстрактный класс, impl как PlainSocketImpl (blocking) или NioSocketImpl
 * (non-blocking). - В Java 24+: Faster impl via JEP 467 — optimized для Virtual Threads, меньше
 * overhead. - Buffers: Internal ByteBuffers для I/O.
 * 
 * Dependencies: Интегрируется с java.io (streams над sockets), java.nio (channels как wrapper над
 * net).
 * 
 * Performance: Blocking — simple, но с vthreads efficient. Non-blocking (NIO) — для high-load.
 */
class NetTest {

    private static final int TEST_PORT = 9999;
    private static final String LOCALHOST = "localhost";
}
