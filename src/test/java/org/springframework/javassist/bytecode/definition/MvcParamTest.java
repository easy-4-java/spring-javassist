package org.springframework.javassist.bytecode.definition;

import static org.junit.Assert.*;

import org.junit.Test;

public class MvcParamTest {

    @Test
    public void shouldCreateWithTypeAndName() {
        MvcParam<String> param = new MvcParam<>(String.class, "name");
        assertEquals(String.class, param.getType());
        assertEquals("name", param.getName());
        assertEquals(MvcParamFrom.PARAM, param.getFrom());
        assertTrue(param.isRequired());
        assertNull(param.getDef());
    }

    @Test
    public void shouldCreateWithTypeAndNameAndFrom() {
        MvcParam<String> param = new MvcParam<>(String.class, "id", MvcParamFrom.PATH);
        assertEquals(MvcParamFrom.PATH, param.getFrom());
    }

    @Test
    public void shouldCreateWithTypeAndNameAndFromAndDefault() {
        MvcParam<String> param = new MvcParam<>(String.class, "id", MvcParamFrom.PATH, "default");
        assertEquals("default", param.getDef());
    }

    @Test
    public void shouldCreateWithTypeAndNameAndDefault() {
        MvcParam<String> param = new MvcParam<>(String.class, "id", "default");
        assertEquals("default", param.getDef());
        assertEquals(MvcParamFrom.PARAM, param.getFrom());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void shouldSetAndGetType() {
        MvcParam param = new MvcParam(String.class, "name");
        param.setType(Integer.class);
        assertEquals(Integer.class, param.getType());
    }

    @Test
    public void shouldSetAndGetName() {
        MvcParam<String> param = new MvcParam<>(String.class, "name");
        param.setName("updated");
        assertEquals("updated", param.getName());
    }

    @Test
    public void shouldSetAndGetFrom() {
        MvcParam<String> param = new MvcParam<>(String.class, "name");
        param.setFrom(MvcParamFrom.HEADER);
        assertEquals(MvcParamFrom.HEADER, param.getFrom());
    }

    @Test
    public void shouldSetAndGetRequired() {
        MvcParam<String> param = new MvcParam<>(String.class, "name");
        assertTrue(param.isRequired());
        param.setRequired(false);
        assertFalse(param.isRequired());
    }

    @Test
    public void shouldSetAndGetDef() {
        MvcParam<String> param = new MvcParam<>(String.class, "name");
        assertNull(param.getDef());
        param.setDef("value");
        assertEquals("value", param.getDef());
    }
}
