package org.springframework.javassist.bytecode.definition;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMethod;

public class MvcMethodTest {

    @Test
    public void shouldCreateWithNamePathAndSingleMethod() {
        MvcMethod method = new MvcMethod("find", new String[]{"/find"}, RequestMethod.GET);
        assertEquals("find", method.getName());
        assertArrayEquals(new String[]{"/find"}, method.getPath());
        assertTrue(method.isResponseBody());
        assertArrayEquals(new RequestMethod[]{RequestMethod.GET}, method.getMethod());
    }

    @Test
    public void shouldCreateWithNamePathAndMethods() {
        MvcMethod method = new MvcMethod("save", new String[]{"/save"}, new RequestMethod[]{RequestMethod.POST, RequestMethod.PUT});
        assertArrayEquals(new RequestMethod[]{RequestMethod.POST, RequestMethod.PUT}, method.getMethod());
    }

    @Test
    public void shouldCreateWithResponseBodyToggle() {
        MvcMethod method = new MvcMethod("page", new String[]{"/page"}, false, RequestMethod.GET);
        assertFalse(method.isResponseBody());
    }

    @Test
    public void shouldCreateWithResponseBodyAndMethods() {
        MvcMethod method = new MvcMethod("action", new String[]{"/action"}, true, new RequestMethod[]{RequestMethod.POST});
        assertTrue(method.isResponseBody());
    }

    @Test
    public void shouldCreateWithProduces() {
        MvcMethod method = new MvcMethod("json", new String[]{"/json"}, true, RequestMethod.GET,
                new String[]{"application/json"});
        assertArrayEquals(new String[]{"application/json"}, method.getProduces());
    }

    @Test
    public void shouldCreateWithProducesAndMethods() {
        MvcMethod method = new MvcMethod("multi", new String[]{"/multi"}, true,
                new RequestMethod[]{RequestMethod.GET, RequestMethod.POST},
                new String[]{"application/json"});
        assertNotNull(method.getProduces());
    }

    @Test
    public void shouldCreateWithProducesAndConsumes() {
        MvcMethod method = new MvcMethod("xml", new String[]{"/xml"}, true, RequestMethod.POST,
                new String[]{"application/xml"}, new String[]{"application/xml"});
        assertArrayEquals(new String[]{"application/xml"}, method.getConsumes());
    }

    @Test
    public void shouldCreateWithProducesConsumesAndMethods() {
        MvcMethod method = new MvcMethod("multi", new String[]{"/multi"}, true,
                new RequestMethod[]{RequestMethod.GET, RequestMethod.POST},
                new String[]{"application/json"}, new String[]{"application/json"});
        assertNotNull(method.getConsumes());
    }

    @Test
    public void shouldCreateWithSingleMethodAndParamsAndHeaders() {
        MvcMethod method = new MvcMethod("search", new String[]{"/search"}, true, RequestMethod.GET,
                new String[]{"q"}, new String[]{"Accept"}, new String[]{"application/json"}, new String[]{"application/json"});
        assertArrayEquals(new String[]{"q"}, method.getParams());
        assertArrayEquals(new String[]{"Accept"}, method.getHeaders());
    }

    @Test
    public void shouldCreateFullConstructor() {
        MvcMethod method = new MvcMethod("full", new String[]{"/full"}, true,
                new RequestMethod[]{RequestMethod.POST}, new String[]{"p=v"}, new String[]{"h=v"},
                new String[]{"application/json"}, new String[]{"application/json"});
        assertEquals("full", method.getName());
        assertArrayEquals(new String[]{"/full"}, method.getPath());
        assertTrue(method.isResponseBody());
        assertArrayEquals(new String[]{"p=v"}, method.getParams());
        assertArrayEquals(new String[]{"h=v"}, method.getHeaders());
    }

    @Test
    public void shouldSetAndGetResponseBody() {
        MvcMethod method = new MvcMethod("test", new String[]{"/test"}, RequestMethod.GET);
        assertTrue(method.isResponseBody());
        method.setResponseBody(false);
        assertFalse(method.isResponseBody());
    }

    @Test
    public void shouldSetAndGetMethod() {
        MvcMethod method = new MvcMethod("test", new String[]{"/test"}, RequestMethod.GET);
        method.setMethod(new RequestMethod[]{RequestMethod.DELETE});
        assertArrayEquals(new RequestMethod[]{RequestMethod.DELETE}, method.getMethod());
    }

    @Test
    public void shouldSetAndGetParams() {
        MvcMethod method = new MvcMethod("test", new String[]{"/test"}, RequestMethod.GET);
        method.setParams(new String[]{"a=1"});
        assertArrayEquals(new String[]{"a=1"}, method.getParams());
    }

    @Test
    public void shouldSetAndGetHeaders() {
        MvcMethod method = new MvcMethod("test", new String[]{"/test"}, RequestMethod.GET);
        method.setHeaders(new String[]{"X-Test"});
        assertArrayEquals(new String[]{"X-Test"}, method.getHeaders());
    }

    @Test
    public void shouldSetAndGetConsumes() {
        MvcMethod method = new MvcMethod("test", new String[]{"/test"}, RequestMethod.GET);
        method.setConsumes(new String[]{"text/plain"});
        assertArrayEquals(new String[]{"text/plain"}, method.getConsumes());
    }

    @Test
    public void shouldSetAndGetProduces() {
        MvcMethod method = new MvcMethod("test", new String[]{"/test"}, RequestMethod.GET);
        method.setProduces(new String[]{"application/xml"});
        assertArrayEquals(new String[]{"application/xml"}, method.getProduces());
    }

    @Test
    public void shouldDefaultMethodsToAllValues() {
        MvcMethod method = new MvcMethod("test", new String[]{"/test"}, RequestMethod.values());
        assertEquals(RequestMethod.values().length, method.getMethod().length);
    }

    @Test
    public void shouldDefaultEmptyArrays() {
        MvcMethod method = new MvcMethod("test", new String[]{"/test"},
                true, new RequestMethod[]{RequestMethod.GET}, null, null, null, null);
        assertNotNull(method.getParams());
        assertEquals(0, method.getParams().length);
        assertNotNull(method.getHeaders());
        assertEquals(0, method.getHeaders().length);
        assertNotNull(method.getProduces());
        assertEquals(0, method.getProduces().length);
        assertNotNull(method.getConsumes());
        assertEquals(0, method.getConsumes().length);
    }
}
