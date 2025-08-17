package example.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;
// https://habr.com/ru/articles/274811/
// https://habr.com/ru/articles/274905/



// Старые API (до Java 8)

/*
 * java.util.Date - Представляет дату и время как количество миллисекунд с 1 января 1970 года
 * 00:00:00 UTC (эпоха Unix). - Класс mutable (изменяемый), что может привести к ошибкам в
 * многопоточных приложениях. - Неудобства: месяцы индексируются с 0 (январь = 0), годы
 * отсчитываются от 1900 (для 2025 нужно указывать 125). - Методы вроде getYear(), getMonth()
 * устарели (deprecated) в пользу Calendar. - Не поддерживает таймзоны напрямую; всегда в UTC, но
 * toString() использует системную таймзону.
 */

/*
 * java.util.Calendar - Абстрактный класс для манипуляции датами и временем; обычно используется
 * GregorianCalendar. - Поддерживает таймзоны (setTimeZone()). - Методы: get(field), set(field,
 * value), add(field, amount), roll(field, amount). - Также mutable и не thread-safe без
 * синхронизации. - Поля: YEAR, MONTH (0-based), DAY_OF_MONTH, HOUR_OF_DAY и т.д. - Не всегда
 * интуитивен: roll() не влияет на более высокие поля, в отличие от add().
 */

/*
 * java.text.SimpleDateFormat - Для форматирования Date в строку и парсинга строки в Date. -
 * Паттерны: "yyyy-MM-dd HH:mm:ss" (год, месяц, день, часы, минуты, секунды). - НЕ thread-safe: в
 * многопоточных приложениях используйте ThreadLocal или DateTimeFormatter из нового API. -
 * Наследует от DateFormat, поддерживает локали.
 */

/*
 * java.sql.Date / java.sql.Time / java.sql.Timestamp - Специализированные для JDBC: java.sql.Date -
 * только дата (без времени), java.sql.Time - только время, java.sql.Timestamp - дата + время +
 * наносекунды. - Наследуют от java.util.Date, но с ограничениями (например, java.sql.Date обнуляет
 * время). - Используются для mapping'а SQL типов DATE, TIME, TIMESTAMP.
 */

/*
 * Важные проблемы старых API: - Mutable объекты приводят к side-effect'ам. - Отсутствие поддержки
 * таймзон и daylight saving time (DST) в Date. - Неудобный API, ошибки с индексами (off-by-one). -
 * Не thread-safe в большинстве случаев.
 */



// Новый API (Java 8+, пакет java.time)

/*
 * java.time.LocalDate - Только дата: год, месяц, день (без времени и таймзоны). - Методы: of(year,
 * month, day), now(), plusDays(n), minusMonths(n), isLeapYear() и т.д.
 */

/*
 * java.time.LocalTime - Только время: часы, минуты, секунды, наносекунды (без даты и таймзоны). -
 * Методы: of(hour, minute), now(), plusHours(n), truncatedTo(ChronoUnit.MINUTES).
 */

/*
 * java.time.LocalDateTime - Дата + время без таймзоны. - Методы: of(LocalDate, LocalTime), now(),
 * atZone(ZoneId) для конвертации в ZonedDateTime.
 */

/*
 * java.time.ZonedDateTime - Дата + время + таймзона (учитывает DST). - Методы: now(ZoneId),
 * of(LocalDateTime, ZoneId), withZoneSameInstant(ZoneId) для смены зоны.
 */

/*
 * java.time.OffsetDateTime - Дата + время + offset от UTC (например, +03:00), без правил DST. -
 * Полезен для хранения времени в базах данных.
 */

/*
 * java.time.Instant - Точка во времени в UTC (миллисекунды с эпохи, как Timestamp). - Методы:
 * now(), ofEpochMilli(millis), atZone(ZoneId) для конвертации.
 */

/*
 * java.time.Duration / java.time.Period - Duration: разница во времени (секунды, наносекунды);
 * between(Temporal, Temporal), ofHours(n). - Period: разница в датах (годы, месяцы, дни);
 * between(LocalDate, LocalDate), ofYears(n). - Не смешивать: Period для календарных единиц,
 * Duration для точного времени.
 */

/*
 * java.time.format.DateTimeFormatter - Thread-safe форматирование и парсинг. - Паттерны:
 * "dd/MM/yyyy HH:mm", ofPattern(pattern), ISO_LOCAL_DATE. - Поддержка локалей и зон.
 */

/*
 * Дополнительные особенности нового API: - Все классы immutable (неизменяемые): методы возвращают
 * новые экземпляры. - Thread-safe по дизайну. - Поддержка ISO-8601 стандарта, операций (plus,
 * minus), TemporalAdjusters (firstDayOfMonth()). - ZoneId: система таймзон (например,
 * "Europe/Moscow"), ZoneOffset для фиксированных offsets. - ChronoUnit: единицы (DAYS, HOURS) для
 * between(). - Bridge с старыми API: Instant.from(Date), Date.from(Instant),
 * ZonedDateTime.toInstant(). - В Java 9+ добавлены методы вроде datesUntil() для стримов дат.
 */



@Slf4j
class DataTimeTest {

    @Test
    void testOldDate() {
        Date date = new Date(120, 0, 1); // Год 2020 (120 + 1900), месяц 0 (январь), день 1
        assertEquals(120, date.getYear());
        assertEquals(0, date.getMonth());
        assertEquals(1, date.getDate());

        // Mutable: меняем дату
        date.setTime(date.getTime() + 24 * 60 * 60 * 1000); // +1 день
        assertEquals(2, date.getDate());
    }

    @Test
    void testOldCalendar() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2020, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        assertEquals(2020, cal.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, cal.get(Calendar.MONTH));

        // add() влияет на более высокие поля
        cal.add(Calendar.DAY_OF_MONTH, 32); // +32 дня -> февраль +1 день
        assertEquals(2020, cal.get(Calendar.YEAR));
        assertEquals(Calendar.FEBRUARY, cal.get(Calendar.MONTH));
        assertEquals(2, cal.get(Calendar.DAY_OF_MONTH));

        // roll() не влияет на более высокие поля
        cal.set(2020, Calendar.JANUARY, 1);
        cal.roll(Calendar.DAY_OF_MONTH, 32); // roll в пределах месяца (январь 31 день -> 1 +
                                             // (32-31)=2? wait, roll adds modulo
        // roll добавляет, но не меняет месяц; 1 + 32 = 33, modulo 31 -> 33-31=2
        assertEquals(2, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.JANUARY, cal.get(Calendar.MONTH));
    }

    @Test
    void testOldSimpleDateFormat() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = sdf.parse("2020-01-01 12:00:00");
        assertEquals("2020-01-01 12:00:00", sdf.format(date));
    }

    @Test
    void testOldSqlDate() {
        java.sql.Date sqlDate = java.sql.Date.valueOf("2020-01-01");
        assertEquals(120, sqlDate.getYear(), "Год 2020");
        assertEquals(0, sqlDate.getMonth(), "Месяц 0");
        assertEquals(1, sqlDate.getDate(), "День 1");
    }


    @Test
    void testLocalDateTime() {
        LocalDate ld = LocalDate.of(2020, 1, 1);
        assertEquals(2020, ld.getYear());
        assertEquals(Month.JANUARY, ld.getMonth());
        assertEquals(1, ld.getDayOfMonth());

        LocalTime lt = LocalTime.of(12, 0, 0);
        assertEquals(12, lt.getHour());
        assertEquals(0, lt.getMinute());

        LocalDateTime ldt = LocalDateTime.of(ld, lt);
        assertEquals(2020, ldt.getYear());
        assertEquals(12, ldt.getHour());

        // Immutable: plus возвращает новый объект
        LocalDateTime ldtPlus = ldt.plusDays(1);
        assertEquals(2, ldtPlus.getDayOfMonth());
        assertEquals(1, ldt.getDayOfMonth());
    }

    @Test
    void testZonedDateTimeAndInstant() {
        ZoneId zone = ZoneId.of("Europe/Warsaw");
        ZonedDateTime zdt = ZonedDateTime.of(2020, 1, 1, 12, 0, 0, 0, zone);
        assertEquals(zone, zdt.getZone());
        assertEquals(2020, zdt.getYear());

        // Смена зоны с сохранением instant
        ZonedDateTime zdtUtc = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        assertEquals(11, zdtUtc.getHour());

        Instant inst = Instant.parse("2020-01-01T11:00:00Z");
        assertEquals(inst, zdt.toInstant());
    }

    @Test
    void testDurationAndPeriod() {
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2025, 8, 17);
        Period period = Period.between(start, end);
        assertEquals(5, period.getYears());
        assertEquals(7, period.getMonths());
        assertEquals(16, period.getDays()); // 2020-01-01 to 2025-08-17: 5y, 7m, 16d
                                            // (учитывая високосные)

        LocalTime t1 = LocalTime.of(10, 0);
        LocalTime t2 = LocalTime.of(15, 30);
        Duration dur = Duration.between(t1, t2);
        assertEquals(5, dur.toHours());
        assertEquals(30, dur.toMinutesPart());
        assertEquals(5 * 3600 + 30 * 60, dur.getSeconds(), "В секундах");

        // Использование ChronoUnit
        long days = ChronoUnit.DAYS.between(start, end);
        assertEquals(2055, days);
    }

    @Test
    void testDateTimeFormatter() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime ldt = LocalDateTime.of(2020, 1, 1, 12, 0);

        String formatted = ldt.format(dtf);
        assertEquals("01/01/2020 12:00", formatted);

        LocalDateTime parsed = LocalDateTime.parse("01/01/2020 12:00", dtf);
        assertEquals(ldt, parsed);
    }

    @Test
    void testBridgeBetweenOldAndNew() {
        // Old to New
        Date oldDate = new Date(120, 0, 1); // 2020-01-01
        Instant inst = oldDate.toInstant();
        LocalDate ld = LocalDateTime.ofInstant(inst, ZoneId.of("UTC")).toLocalDate();

        // New to Old
        Instant newInst = Instant.parse("2020-01-01T00:00:00Z");
        Date newToOld = Date.from(newInst);
    }
}
