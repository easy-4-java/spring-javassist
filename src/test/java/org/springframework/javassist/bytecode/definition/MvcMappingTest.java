package org.springframework.javassist.bytecode.definition;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMethod;

public class MvcMappingTest {

    @Test
    public void shouldCreateWithPathAndMethods() {
        MvcMapping mapping = new MvcMapping(new String[]{"/api"}, RequestMethod.GET, RequestMethod.POST);
        assertArrayEquals(new String[]{"/api"}, mapping.getPath());
        assertArrayEquals(new RequestMethod[]{RequestMethod.GET, RequestMethod.POST}, mapping.getMethod());
        assertEquals("", mapping.getName());
    }

    @Test
    public void shouldCreateWithAllArgs() {
        MvcMapping mapping = new MvcMapping(
                "testMapping",
                new String[]{"/v1", "/v2"},
                new RequestMethod[]{RequestMethod.GET},
                new String[]{"param=val"},
                new String[]{"Accept=application/json"},
                new String[]{"application/json"},
                new String[]{"application/xml"}
        );
        assertEquals("testMapping", mapping.getName());
        assertArrayEquals(new String[]{"/v1", "/v2"}, mapping.getPath());
        assertArrayEquals(new RequestMethod[]{RequestMethod.GET}, mapping.getMethod());
        assertArrayEquals(new String[]{"param=val"}, mapping.getParams());
        assertArrayEquals(new String[]{"Accept=application/json"}, mapping.getHeaders());
        assertArrayEquals(new String[]{"application/json"}, mapping.getConsumes());
        assertArrayEquals(new String[]{"application/xml"}, mapping.getProduces());
    }

    @Test
    public void shouldSetAndGetName() {
        MvcMapping mapping = new MvcMapping(new String[]{"/"}, RequestMethod.GET);
        mapping.setName("updated");
        assertEquals("updated", mapping.getName());
    }

    @Test
    public void shouldSetAndGetMethods() {
        MvcMapping mapping = new MvcMapping(new String[]{"/"}, RequestMethod.GET);
        mapping.setMethod(new RequestMethod[]{RequestMethod.POST});
        assertArrayEquals(new RequestMethod[]{RequestMethod.POST}, mapping.getMethod());
    }

    @Test
    public void shouldSetAndGetParams() {
        MvcMapping mapping = new MvcMapping(new String[]{"/"}, RequestMethod.GET);
        mapping.setParams(new String[]{"a=b"});
        assertArrayEquals(new String[]{"a=b"}, mapping.getParams());
    }

    @Test
    public void shouldSetAndGetHeaders() {
        MvcMapping mapping = new MvcMapping(new String[]{"/"}, RequestMethod.GET);
        mapping.setHeaders(new String[]{"X-Custom"});
        assertArrayEquals(new String[]{"X-Custom"}, mapping.getHeaders());
    }

    @Test
    public void shouldSetAndGetConsumes() {
        MvcMapping mapping = new MvcMapping(new String[]{"/"}, RequestMethod.GET);
        mapping.setConsumes(new String[]{"text/plain"});
        assertArrayEquals(new String[]{"text/plain"}, mapping.getConsumes());
    }

    @Test
    public void shouldSetAndGetProduces() {
        MvcMapping mapping = new MvcMapping(new String[]{"/"}, RequestMethod.GET);
        mapping.setProduces(new String[]{"application/json"});
        assertArrayEquals(new String[]{"application/json"}, mapping.getProduces());
    }

    @Test
    public void shouldDefaultMethodToAllValues() {
        MvcMapping mapping = new MvcMapping(new String[]{"/"}, RequestMethod.values());
        assertNotNull(mapping.getMethod());
        assertEquals(RequestMethod.values().length, mapping.getMethod().length);
    }
}
