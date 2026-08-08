package org.springframework.javassist.bytecode;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.junit.Test;

public class EndpointApiTest {

    @Test
    public void shouldCreateWithNoArgConstructor() {
        EndpointApi api = new EndpointApi() {};
        assertNull(api.getHandler());
    }

    @Test
    public void shouldCreateWithHandler() {
        InvocationHandler handler = (proxy, method, args) -> null;
        EndpointApi api = new EndpointApi(handler) {};
        assertSame(handler, api.getHandler());
    }

    @Test
    public void shouldCreateWithNullHandler() {
        EndpointApi api = new EndpointApi(null) {};
        assertNull(api.getHandler());
    }
}
