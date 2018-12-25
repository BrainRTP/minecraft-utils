package ru.progrm_jarvis.minecraft.commons.util;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.collect.Maps.immutableEntry;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class MapUtilTest {

    ///////////////////////////////////////////////////////////////////////////
    // MapUtil
    ///////////////////////////////////////////////////////////////////////////

    @Test
    @SuppressWarnings("unchecked")
    void testFillMapFromArrayOfUncheckedPairs() {
        assertEquals(new HashMap<>(), MapUtil.fillMap(new HashMap<>()));

        assertThrows(IllegalArgumentException.class, () -> MapUtil.fillMap(new HashMap<>(), 1));

        assertThrows(IllegalArgumentException.class, () -> MapUtil.fillMap(new HashMap<>(), 1, 3, "String"));

        val entries = new HashMap<Integer, String>() {{
            put(1, "Hello");
            put(2, "world");
        }}.entrySet();

        assertThat(entries, hasSize(2));
        assertThat(entries, contains(immutableEntry(1, "Hello"), immutableEntry(2, "world")));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFillMapFromArray() {
        val entries = MapUtil.<Integer, String, Map<Integer, String>>fillMap(
                new HashMap<>(), Pair.of(1, "Hello"), Pair.of(2, "world")
        ).entrySet();

        assertThat(entries, hasSize(2));
        assertThat(entries, contains(immutableEntry(1, "Hello"), immutableEntry(2, "world")));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFillMapFromIterator() {
        val entries = MapUtil
                .fillMap(new HashMap<>(), Arrays.asList(Pair.of(1, "Hello"), Pair.of(2, "world")).iterator())
                .entrySet();

        assertThat(entries, hasSize(2));
        assertThat(entries, contains(immutableEntry(1, "Hello"), immutableEntry(2, "world")));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFillMapFromIterable() {
        val entries = MapUtil
                .fillMap(new HashMap<>(), Arrays.asList(Pair.of(1, "Hello"), Pair.of(2, "world")))
                .entrySet();

        assertThat(entries, hasSize(2));
        assertThat(entries, contains(immutableEntry(1, "Hello"), immutableEntry(2, "world")));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFillMapFromStream() {
        val entries = MapUtil.fillMap(new HashMap<>(), Stream.of(Pair.of(1, "Hello"), Pair.of(2, "world"))).entrySet();

        assertThat(entries, hasSize(2));
        assertThat(entries, contains(immutableEntry(1, "Hello"), immutableEntry(2, "world")));
    }

    ///////////////////////////////////////////////////////////////////////////
    // MapFiller
    ///////////////////////////////////////////////////////////////////////////

    @Test
    void testMapFillerConstructWithFirst() {
        assertThat(
                MapUtil.mapFiller(new HashMap<>()).map().entrySet(),
                empty()
        );

        assertEquals(
                new HashMap<String, Integer>() {{
                    put("Hello", 1);
                }},
                MapUtil.mapFiller(new HashMap<>(), "Hello", 1).map()
        );
    }

    @Test
    @SuppressWarnings("unchecked") // Hamcrest, R U fine?
    void testMapFillerPut() {
        val entries = MapUtil.mapFiller(new HashMap<String, Integer>())
                .put("one", 1)
                .put("two", 2)
                .map()
                .entrySet();

        assertThat(entries, hasSize(2));

        assertThat(entries, hasItems(immutableEntry("one", 1), immutableEntry("two", 2)));
    }

    @Test
    @SuppressWarnings("unchecked") // Hamcrest, R U fine?
    void testMapFillerFillFromArray() {
        val entries = MapUtil.mapFiller(new HashMap<String, Integer>())
                .fill(Pair.of("one", 1), Pair.of("two", 2))
                .map()
                .entrySet();

        assertThat(entries, hasSize(2));

        assertThat(entries, hasItems(immutableEntry("one", 1), immutableEntry("two", 2)));
    }

    @Test
    @SuppressWarnings("unchecked") // Hamcrest, R U fine?
    void testMapFillerFillFromIterator() {
        val entries = MapUtil.mapFiller(new HashMap<String, Integer>())
                .fill(Arrays.asList(Pair.of("one", 1), Pair.of("two", 2)).iterator())
                .map()
                .entrySet();

        assertThat(entries, hasSize(2));

        assertThat(entries, hasItems(immutableEntry("one", 1), immutableEntry("two", 2)));
    }

    @Test
    @SuppressWarnings("unchecked") // Hamcrest, R U fine?
    void testMapFillerFillFromIterable() {
        val entries = MapUtil.mapFiller(new HashMap<String, Integer>())
                .fill(Arrays.asList(Pair.of("one", 1), Pair.of("two", 2)))
                .map()
                .entrySet();

        assertThat(entries, hasSize(2));

        assertThat(entries, hasItems(immutableEntry("one", 1), immutableEntry("two", 2)));
    }

    @Test
    @SuppressWarnings("unchecked") // Hamcrest, R U fine?
    void testMapFillerFillFromStream() {
        val entries = MapUtil.mapFiller(new HashMap<String, Integer>())
                .fill(Stream.of(Pair.of("one", 1), Pair.of("two", 2)))
                .map()
                .entrySet();

        assertThat(entries,
                hasSize(2)
        );

        assertThat(entries, hasItems(immutableEntry("one", 1), immutableEntry("two", 2)));
    }

    @Test
    @SuppressWarnings("unchecked") // Hamcrest, R U fine?
    void testMapFillerFillFromEveryKind() {
        val entries = MapUtil.mapFiller(new HashMap<String, Integer>())
                .put("one", 1)
                .put("two", 2)
                .fill(Pair.of("three", 3), Pair.of("four", 4))
                .fill(Arrays.asList(Pair.of("five", 5), Pair.of("six", 6)).iterator())
                .fill(Arrays.asList(Pair.of("seven", 7), Pair.of("eight", 8)))
                .fill(Stream.of(Pair.of("nine", 9), Pair.of("ten", 10)))
                .map()
                .entrySet();

        assertThat(entries, hasSize(10));

        assertThat(
                entries,
                hasItems(
                        immutableEntry("one", 1),
                        immutableEntry("two", 2),
                        immutableEntry("three", 3),
                        immutableEntry("four", 4),
                        immutableEntry("five", 5),
                        immutableEntry("six", 6),
                        immutableEntry("seven", 7),
                        immutableEntry("eight", 8),
                        immutableEntry("nine", 9),
                        immutableEntry("ten", 10)
                )
        );
    }
}