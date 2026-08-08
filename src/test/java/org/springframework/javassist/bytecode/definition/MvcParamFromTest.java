package org.springframework.javassist.bytecode.definition;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Test;

public class MvcParamFromTest {

    @Test
    public void shouldReturnCorrectKey() {
        assertEquals("COOKIE", MvcParamFrom.COOKIE.getKey());
        assertEquals("MATRIX", MvcParamFrom.MATRIX.getKey());
        assertEquals("PATH", MvcParamFrom.PATH.getKey());
        assertEquals("ATTR", MvcParamFrom.ATTR.getKey());
        assertEquals("BODY", MvcParamFrom.BODY.getKey());
        assertEquals("HEADER", MvcParamFrom.HEADER.getKey());
        assertEquals("PARAM", MvcParamFrom.PARAM.getKey());
        assertEquals("PART", MvcParamFrom.PART.getKey());
    }

    @Test
    public void shouldReturnCorrectDesc() {
        assertNotNull(MvcParamFrom.COOKIE.getDesc());
        assertNotNull(MvcParamFrom.MATRIX.getDesc());
        assertNotNull(MvcParamFrom.PATH.getDesc());
        assertNotNull(MvcParamFrom.ATTR.getDesc());
        assertNotNull(MvcParamFrom.BODY.getDesc());
        assertNotNull(MvcParamFrom.HEADER.getDesc());
        assertNotNull(MvcParamFrom.PARAM.getDesc());
        assertNotNull(MvcParamFrom.PART.getDesc());
    }

    @Test
    public void shouldResolveByExactKey() {
        assertEquals(MvcParamFrom.COOKIE, MvcParamFrom.valueOfIgnoreCase("COOKIE"));
        assertEquals(MvcParamFrom.PATH, MvcParamFrom.valueOfIgnoreCase("PATH"));
        assertEquals(MvcParamFrom.BODY, MvcParamFrom.valueOfIgnoreCase("BODY"));
        assertEquals(MvcParamFrom.PARAM, MvcParamFrom.valueOfIgnoreCase("PARAM"));
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowOnUnknownKey() {
        MvcParamFrom.valueOfIgnoreCase("NONEXISTENT");
    }

    @Test
    public void shouldCompareEqualValues() {
        assertTrue(MvcParamFrom.PARAM.equals(MvcParamFrom.PARAM));
        assertFalse(MvcParamFrom.PARAM.equals(MvcParamFrom.BODY));
    }

    @Test
    public void shouldCompareByStringKey() {
        assertTrue(MvcParamFrom.PARAM.equals("PARAM"));
        assertFalse(MvcParamFrom.PARAM.equals("BODY"));
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowOnUnknownStringKey() {
        MvcParamFrom.PARAM.equals("UNKNOWN");
    }

    @Test
    public void shouldConvertToMap() {
        Map<String, String> map = MvcParamFrom.PATH.toMap();
        assertEquals("PATH", map.get("key"));
        assertNotNull(map.get("desc"));
        assertEquals(2, map.size());
    }

    @Test
    public void shouldListAllValues() {
        List<Map<String, String>> list = MvcParamFrom.fromList();
        assertNotNull(list);
        assertEquals(MvcParamFrom.values().length, list.size());
        for (Map<String, String> entry : list) {
            assertTrue(entry.containsKey("key"));
            assertTrue(entry.containsKey("desc"));
        }
    }
}
