package org.springframework.javassist.bytecode.definition;

import static org.junit.Assert.*;

import org.junit.Test;

public class MvcApiImplicitParamTest {

    @Test
    public void shouldCreateEmptyInstance() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        assertEquals("", param.getName());
        assertEquals("", param.getValue());
        assertFalse(param.isRequired());
        assertEquals("", param.getDataType());
        assertEquals(Void.class, param.getDataTypeClass());
    }

    @Test
    public void shouldCreateWithFourArgs() {
        MvcApiImplicitParam param = new MvcApiImplicitParam("name", "desc", true, "String");
        assertEquals("name", param.getName());
        assertEquals("desc", param.getValue());
        assertTrue(param.isRequired());
        assertEquals("String", param.getDataType());
    }

    @Test
    public void shouldCreateWithDataTypeClass() {
        MvcApiImplicitParam param = new MvcApiImplicitParam("id", "identifier", false, Integer.class);
        assertEquals("id", param.getName());
        assertEquals("identifier", param.getValue());
        assertFalse(param.isRequired());
        assertEquals(Integer.class, param.getDataTypeClass());
    }

    @Test
    public void shouldCreateWithAllArgs() {
        MvcApiImplicitParam param = new MvcApiImplicitParam(
                "name", "value", "def", "1,2,3", true,
                "access", false, "dataType", String.class, "query",
                "example", "type", "format", true, false, "csv");
        assertEquals("name", param.getName());
        assertEquals("value", param.getValue());
        assertEquals("def", param.getDefaultValue());
        assertEquals("1,2,3", param.getAllowableValues());
        assertTrue(param.isRequired());
        assertEquals("access", param.getAccess());
        assertFalse(param.isAllowMultiple());
        assertEquals("dataType", param.getDataType());
        assertEquals(String.class, param.getDataTypeClass());
        assertEquals("query", param.getParamType());
        assertEquals("example", param.getExample());
        assertEquals("type", param.getType());
        assertEquals("format", param.getFormat());
        assertTrue(param.isAllowEmptyValue());
        assertFalse(param.isReadOnly());
        assertEquals("csv", param.getCollectionFormat());
    }

    @Test
    public void shouldSetAndGetName() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setName("updated");
        assertEquals("updated", param.getName());
    }

    @Test
    public void shouldSetAndGetValue() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setValue("desc");
        assertEquals("desc", param.getValue());
    }

    @Test
    public void shouldSetAndGetDefaultValue() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setDefaultValue("42");
        assertEquals("42", param.getDefaultValue());
    }

    @Test
    public void shouldSetAndGetAllowableValues() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setAllowableValues("1,2,3");
        assertEquals("1,2,3", param.getAllowableValues());
    }

    @Test
    public void shouldSetAndGetRequired() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setRequired(true);
        assertTrue(param.isRequired());
    }

    @Test
    public void shouldSetAndGetAccess() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setAccess("hidden");
        assertEquals("hidden", param.getAccess());
    }

    @Test
    public void shouldSetAndGetAllowMultiple() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setAllowMultiple(true);
        assertTrue(param.isAllowMultiple());
    }

    @Test
    public void shouldSetAndGetDataType() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setDataType("Long");
        assertEquals("Long", param.getDataType());
    }

    @Test
    public void shouldSetAndGetDataTypeClass() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setDataTypeClass(Long.class);
        assertEquals(Long.class, param.getDataTypeClass());
    }

    @Test
    public void shouldSetAndGetParamType() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setParamType("header");
        assertEquals("header", param.getParamType());
    }

    @Test
    public void shouldSetAndGetExample() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setExample("test");
        assertEquals("test", param.getExample());
    }

    @Test
    public void shouldSetAndGetType() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setType("string");
        assertEquals("string", param.getType());
    }

    @Test
    public void shouldSetAndGetFormat() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setFormat("uuid");
        assertEquals("uuid", param.getFormat());
    }

    @Test
    public void shouldSetAndGetAllowEmptyValue() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setAllowEmptyValue(true);
        assertTrue(param.isAllowEmptyValue());
    }

    @Test
    public void shouldSetAndGetReadOnly() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setReadOnly(true);
        assertTrue(param.isReadOnly());
    }

    @Test
    public void shouldSetAndGetCollectionFormat() {
        MvcApiImplicitParam param = new MvcApiImplicitParam();
        param.setCollectionFormat("pipes");
        assertEquals("pipes", param.getCollectionFormat());
    }
}
