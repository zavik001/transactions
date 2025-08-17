package example.transactions;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

// JAVA IO & NEW IO



// IO (java.io)
// СТРИМЫ, КОТОРЫЕ ВСЕ ЗНАЮТ, НО НЕ ВСЕ ЛЮБЯТ

/*
 * Ключевые абстракции:
 * 
 * InputStream / OutputStream — байтовые потоки (8-bit). Идеальны для бинарных данных: изображения,
 * файлы, сети. Но блокирующие — поток ждёт, пока данные не придут.
 * 
 * Reader / Writer - символьные потоки (Unicode chars). Для текста: учитывают кодировки, чтобы
 * избежать "кракозябр".
 */

/*
 * Характеристики (что делает их "старыми", но надёжными):
 * 
 * 1. Блокирующие по умолчанию: read() висит, пока байты не появятся. В Java 21+ с Virtual Threads
 * (JEP 444) это не проблема — blocking IO на виртуальных потоках не блокирует реальные OS-threads,
 * делая его "эффективно асинхронным" для high-concurrency.
 * 
 * 2. Потокобезопасность: Обычно НЕТ (синхронизируй сам). Исключения: PrintStream имеет внутренний
 * lock на print/println (историческая фича для System.out).
 * 
 * 3. Буферизация: Оборачивай в Buffered* для производительности — снижает системные вызовы (syscall
 * overhead). Без буфера каждый write() — дорогой вызов kernel.
 * 
 * 4. Специализированные потоки: - DataInput/OutputStream: Для примитивов (int, double) и UTF-строк.
 * Big-endian по умолчанию. - ObjectInput/OutputStream: Сериализация объектов (implements
 * Serializable). Поддержка custom writeObject/readObject, transient поля. Внимание: уязвимости
 * (deserialization attacks) — используй ObjectInputFilter (с Java 9). -
 * InputStreamReader/OutputStreamWriter: Мост байты ↔ chars. ВСЕГДА указывай Charset
 * (StandardCharsets.UTF_8), т.к. с Java 18 (JEP 400) UTF-8 — default, но для portability лучше
 * explicitly. - PrintStream/PrintWriter: Удобный вывод (println, printf). PrintWriter auto-flush на
 * \n если autoflush=true. System.out/err — PrintStream. - RandomAccessFile: Seekable файл —
 * читай/пиши с любой позиции. Режимы "r", "rw", "rwd" (sync data), "rws" (sync data+metadata). -
 * Piped*: Для inter-thread communication внутри JVM (producer-consumer без файлов).
 * 
 * 5. try-with-resources (Java 7+): Авто-закрытие, даже при exceptions. Stack suppression для
 * multiple exceptions.
 * 
 * 6. Кодировки: С Java 18 UTF-8 default (JEP 400) — прощай, platform-dependent bugs!
 */

/*
 * Когда использовать IO: Простые задачи: чтение конфигов, логи, small files. Совместимость с
 * legacy. В комбо с Virtual Threads — масштабируется как NIO.
 */

/*
 * Новое в Java 24-25: JEP 512 (Compact Source Files) вводит java.lang.IO (preview) для simple
 * console I/O в beginner-программах: IO.print, IO.readLine.
 */



// NIO & NIO.2 (java.nio*)
// МОЩЬ: БУФЕРЫ, КАНАЛЫ, ФАЙЛЫ НА СТЕРОИДАХ

/*
 * 1. Buffer (ByteBuffer, etc.): Фиксированный контейнер с position, limit, capacity. Методы:
 * put/get, flip() (write → read), clear() (reset), compact() (shift unread). - Heap vs Direct:
 * Direct (allocateDirect) — off-heap, zero-copy с native I/O, но GC не управляет (use Cleaner).
 * Интеграция с Foreign Memory API (JEP 454, Java 22): MemorySegment.asByteBuffer() для safe
 * off-heap. - ByteOrder: BIG_ENDIAN default; set LITTLE_ENDIAN для cross-platform.
 * 
 * 2. Channel: Двусторонние (read+write). FileChannel (files), SocketChannel (TCP), etc. -
 * Scatter/gather: Read в/из multiple buffers — efficiently для headers+body. - transferTo/From:
 * Zero-copy (kernel-level copy) — супер для file serving (Sendfile). - FileLock: Region locks
 * (shared/exclusive), inter-process.
 * 
 * 3. Path/Files (NIO.2, Java 7): Modern FS API. Path — immutable, Files — utils. - Files:
 * readString/writeString (Java 11+), copy/move (with options like REPLACE_EXISTING),
 * deleteIfExists, walk (Stream<Path>), lines (Stream<String>). - Attributes:
 * Basic/PosixFileAttributes, setAttribute (e.g., POSIX permissions). - mismatch (Java 12+): Compare
 * files byte-by-byte.
 * 
 * 4. Asynchronous*: AsynchronousFileChannel (futures/completion handlers),
 * AsynchronousSocketChannel. В Java 21+ с Virtual Threads — blocking NIO на vthreads efficient. •
 * Selector: Non-blocking multiplex для sockets (epoll/kqueue). Регистрируй channels, select() ждёт
 * events (OP_READ, OP_WRITE).
 * 
 * 5. WatchService: FS events (ENTRY_CREATE/MODIFY/DELETE). Polling-based на Windows, native на
 * Linux.
 */

/*
 * Характеристики (почему NIO — для pros):
 * 
 * 1. Non-blocking (configureBlocking(false) для сетевых): Не жди — multiplex с Selector.
 * 
 * 2. Async API: Futures или CompletionHandler — scale to thousands connections.
 * 
 * 3. Performance: Direct buffers минимизируют копии, transfer* — kernel optimizations.
 * 
 * 4. Thread-safety: Buffers не thread-safe (sync сам); Channels — да, но operations atomic.
 */

/*
 * Когда использовать NIO:
 * 
 * 1. High-throughput: Large files, servers (Netty/Vert.x built on NIO).
 * 
 * 2. FS ops: Files/Path > old File (handles symlinks, attributes better).
 */

/*
 * Новое в Java 24-25:
 * 
 * 1. Foreign Memory API (stable in 22, enhancements): MemorySegment для safe off-heap, integrable с
 * NIO channels (e.g., read into segment).
 * 
 * 2. Virtual Threads synergy: Async NIO + vthreads = ultimate scalability.
 * 
 * 3. Нет major changes в IO/NIO, но JEP 512's java.lang.IO — bridge to simple IO.
 */



class IOnewIOTest {

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private Path tempDir() throws IOException {
        Path dir = Files.createTempDirectory("io-nio-tests-");
        dir.toFile().deleteOnExit();
        return dir;
    }

    @Test
    void IO_basicByteStreamsWithVirtualThreads() throws Exception {
        Path dir = tempDir();
        Path file = dir.resolve("io-bytes.bin");
        byte[] src = new byte[] {0x41, 0x42, 0x43}; // A, B, C

        // Запись в blocking IO на virtual thread - не блокирует plarform thread
        try (var excutor = Executors.newVirtualThreadPerTaskExecutor()) {
            excutor.submit(() -> {
                try (OutputStream os = new FileOutputStream(file.toFile())) {
                    os.write(src);
                } catch (IOException e) {
                }
                return null;
            }).get();
        }

        byte[] dst = new byte[3];
        try (InputStream is = new FileInputStream(file.toFile())) {
            int read = is.read(dst);
            assertEquals(3, read);
        }

        assertArrayEquals(src, dst);
    }

    @Test
    void IO_readerWriterWithCharSet() throws Exception {
        Path dir = tempDir();
        Path file = dir.resolve("io_text_utf8.txt");
        String testStr = "GC java FF,io newio io - UTF-8";

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file.toFile()), UTF8)) {
            writer.write(testStr);
        }

        StringBuilder sb = new StringBuilder();
        // InputStreamReader default UTF-8 since Java 18
        try (Reader reader = new InputStreamReader(new FileInputStream(file.toFile()))) {
            char[] buf = new char[64];
            int n;
            while ((n = reader.read(buf)) != -1) {
                sb.append(buf, 0, n);
            }
        }

        assertEquals(testStr, sb.toString());
    }

    @Test
    void IO_bufferedAndMarkReset() throws Exception {
        Path dir = tempDir();
        Path file = dir.resolve("io_buffered.txt");
        String data = "Line1\nLine2\nLine3";
        Files.writeString(file, data, UTF8);

        try (InputStream is = new BufferedInputStream(new FileInputStream(file.toFile()))) {
            // mark/reset
            assertTrue(is.markSupported());

            // mark - метка
            is.mark(100);
            byte[] head = is.readNBytes(5); // Line1
            assertEquals("Line1", new String(head, UTF8));
            // reset - чтобы вернуться к установленной метке
            is.reset();

            byte[] again = is.readNBytes(5);
            assertEquals("Line1", new String(again, UTF8));
        }
    }

    @Test
    void IO_dataStreams() throws Exception {
        Path dir = tempDir();
        Path file = dir.resolve("io_data.bin");

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file.toFile()))) {
            dos.writeInt(90);
            dos.writeDouble(Math.TAU);
            dos.writeUTF("javaIO");
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(file.toFile()))) {
            assertEquals(90, dis.readInt());
            assertEquals(Math.TAU, dis.readDouble());
            assertEquals("javaIO", dis.readUTF());
        }
    }
}
