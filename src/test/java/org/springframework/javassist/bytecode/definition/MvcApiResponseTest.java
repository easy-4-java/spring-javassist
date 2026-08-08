package org.springframework.javassist.bytecode.definition;

import static org.junit.Assert.*;

import org.junit.Test;

public class MvcApiResponseTest {

    @Test
    public void shouldCreateWithCodeAndMessage() {
        MvcApiResponse response = new MvcApiResponse(200, "OK");
        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        assertEquals(Void.class, response.getResponse());
        assertEquals("", response.getReference());
        assertEquals("", response.getResponseContainer());
    }

    @Test
    public void shouldCreateWithResponseClass() {
        MvcApiResponse response = new MvcApiResponse(200, "OK", String.class);
        assertEquals(200, response.getCode());
        assertEquals("OK", response.getMessage());
        assertEquals(String.class, response.getResponse());
    }

    @Test
    public void shouldCreateWithContainer() {
        MvcApiResponse response = new MvcApiResponse(200, "OK", String.class, "List");
        assertEquals("List", response.getResponseContainer());
    }

    @Test
    public void shouldCreateWithAllArgs() {
        MvcApiResponse response = new MvcApiResponse(404, "Not Found", Void.class, "#/defs/Error", "Map");
        assertEquals(404, response.getCode());
        assertEquals("Not Found", response.getMessage());
        assertEquals(Void.class, response.getResponse());
        assertEquals("#/defs/Error", response.getReference());
        assertEquals("Map", response.getResponseContainer());
    }

    @Test
    public void shouldSetAndGetCode() {
        MvcApiResponse response = new MvcApiResponse(0, "");
        response.setCode(500);
        assertEquals(500, response.getCode());
    }

    @Test
    public void shouldSetAndGetMessage() {
        MvcApiResponse response = new MvcApiResponse(0, "");
        response.setMessage("Error");
        assertEquals("Error", response.getMessage());
    }

    @Test
    public void shouldSetAndGetResponse() {
        MvcApiResponse response = new MvcApiResponse(0, "");
        response.setResponse(Integer.class);
        assertEquals(Integer.class, response.getResponse());
    }

    @Test
    public void shouldSetAndGetReference() {
        MvcApiResponse response = new MvcApiResponse(0, "");
        response.setReference("#/defs/Ref");
        assertEquals("#/defs/Ref", response.getReference());
    }

    @Test
    public void shouldSetAndGetResponseContainer() {
        MvcApiResponse response = new MvcApiResponse(0, "");
        response.setResponseContainer("Set");
        assertEquals("Set", response.getResponseContainer());
    }
}
