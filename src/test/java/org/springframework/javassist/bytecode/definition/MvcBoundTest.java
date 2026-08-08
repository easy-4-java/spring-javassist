package org.springframework.javassist.bytecode.definition;

import static org.junit.Assert.*;

import org.junit.Test;

public class MvcBoundTest {

    @Test
    public void shouldCreateWithUidOnly() {
        MvcBound bound = new MvcBound("123");
        assertEquals("123", bound.getUid());
        assertEquals("", bound.getJson());
        assertEquals("", bound.getNotes());
    }

    @Test
    public void shouldCreateWithUidAndJson() {
        MvcBound bound = new MvcBound("456", "{\"key\":\"value\"}");
        assertEquals("456", bound.getUid());
        assertEquals("{\"key\":\"value\"}", bound.getJson());
    }

    @Test
    public void shouldSetAndGetUid() {
        MvcBound bound = new MvcBound("old");
        bound.setUid("new");
        assertEquals("new", bound.getUid());
    }

    @Test
    public void shouldSetAndGetJson() {
        MvcBound bound = new MvcBound("1");
        bound.setJson("{\"a\":1}");
        assertEquals("{\"a\":1}", bound.getJson());
    }

    @Test
    public void shouldSetAndGetNotes() {
        MvcBound bound = new MvcBound("1");
        bound.setNotes("some notes");
        assertEquals("some notes", bound.getNotes());
    }
}
