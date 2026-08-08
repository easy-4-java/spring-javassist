package org.springframework.javassist.bytecode;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.javassist.bytecode.definition.MvcBound;

import io.github.easy4j.javassist.utils.ClassPoolFactory;

import javassist.ClassPool;
import javassist.CtClass;

public class CtClassBuilderTest {

    private final ClassPool pool = ClassPoolFactory.getDefaultPool();

    @Test
    public void shouldBuildClassWithDefaultPool() throws Exception {
        CtClassBuilder builder = new CtClassBuilder("org.test.CtClassBuilder1");
        CtClass ct = builder.build();
        assertNotNull(ct);
        assertEquals("org.test.CtClassBuilder1", ct.getName());
        ct.detach();
    }

    @Test
    public void shouldBuildClassWithSuperclass() throws Exception {
        CtClassBuilder builder = new CtClassBuilder("org.test.CtClassBuilder2", java.util.ArrayList.class);
        CtClass ct = builder.build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldBuildClassWithPool() throws Exception {
        CtClassBuilder builder = new CtClassBuilder(pool, "org.test.CtClassBuilder3", Object.class);
        CtClass ct = builder.build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldMakeFieldFromSource() throws Exception {
        CtClass ct = new CtClassBuilder("org.test.CtClassBuilderField")
                .makeField("public int x = 0;")
                .build();
        assertNotNull(ct.getField("x"));
        ct.detach();
    }

    @Test
    public void shouldMakeFieldFromClassAndName() throws Exception {
        CtClass ct = new CtClassBuilder("org.test.CtClassBuilderField2")
                .makeField(String.class, "name")
                .build();
        assertNotNull(ct.getField("name"));
        ct.detach();
    }

    @Test
    public void shouldNewFieldWithValue() throws Exception {
        CtClass ct = new CtClassBuilder("org.test.CtClassBuilderField3")
                .newField(String.class, "uid", "\"abc\"")
                .build();
        assertNotNull(ct.getField("uid"));
        ct.detach();
    }

    @Test
    public void shouldRemoveField() throws Exception {
        CtClassBuilder builder = new CtClassBuilder("org.test.CtClassBuilderRemoveField");
        builder.makeField("public int temp = 0;");
        builder.removeField("temp");
        CtClass ct = builder.build();
        try {
            ct.getField("temp");
            fail("Expected NotFoundException");
        } catch (javassist.NotFoundException e) {
            // expected
        }
        ct.detach();
    }

    @Test
    public void shouldRemoveFieldNoop() throws Exception {
        CtClass ct = new CtClassBuilder("org.test.CtClassBuilderRemoveFieldNoop")
                .removeField("nonexistent")
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldMakeMethod() throws Exception {
        CtClass ct = new CtClassBuilder("org.test.CtClassBuilderMethod")
                .makeMethod("public void hello() { System.out.println(\"hi\"); }")
                .build();
        assertNotNull(ct.getDeclaredMethod("hello"));
        ct.detach();
    }

    @Test
    public void shouldMakeSetter() throws Exception {
        CtClass ct = new CtClassBuilder("org.test.CtClassBuilderSetter")
                .makeField(String.class, "name")
                .makeSetter("name")
                .build();
        assertNotNull(ct.getDeclaredMethod("setName"));
        ct.detach();
    }

    @Test
    public void shouldMakeGetter() throws Exception {
        CtClass ct = new CtClassBuilder("org.test.CtClassBuilderGetter")
                .makeField(String.class, "name")
                .makeGetter("name")
                .build();
        assertNotNull(ct.getDeclaredMethod("getName"));
        ct.detach();
    }

    @Test
    public void shouldBindWithUidAndJson() throws Exception {
        CtClass ct = new CtClassBuilder("org.test.CtClassBuilderBind")
                .bind("uid123", "{\"key\":\"value\"}")
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldBindWithMvcBound() throws Exception {
        CtClass ct = new CtClassBuilder("org.test.CtClassBuilderBind2")
                .bind(new MvcBound("uid456", "{}"))
                .build();
        assertNotNull(ct);
        ct.detach();
    }

    @Test
    public void shouldAutowired() throws Exception {
        CtClass ct = new CtClassBuilder("org.test.CtClassBuilderAutowired")
                .autowired(String.class, "service", true)
                .build();
        assertNotNull(ct.getDeclaredField("service"));
        ct.detach();
    }

    @Test
    public void shouldAutowiredWithQualifier() throws Exception {
        CtClass ct = new CtClassBuilder("org.test.CtClassBuilderAutowiredQ")
                .autowired(String.class, "service", true, "myQualifier")
                .build();
        assertNotNull(ct.getDeclaredField("service"));
        ct.detach();
    }

    @Test
    public void shouldAutowiredWithBlankQualifier() throws Exception {
        CtClass ct = new CtClassBuilder("org.test.CtClassBuilderAutowiredQB")
                .autowired(String.class, "service", false, "")
                .build();
        assertNotNull(ct.getDeclaredField("service"));
        ct.detach();
    }

    @Test
    public void shouldAutowiredHandler() throws Exception {
        CtClassBuilder base = new CtClassBuilder("org.test.CtClassBuilderHandler");
        base.makeField("private java.lang.reflect.InvocationHandler handler;");
        base.autowiredHandler(false, "myQualifier");
        CtClass ct = base.build();
        assertNotNull(ct.getDeclaredField("handler"));
        ct.detach();
    }

    @Test
    public void shouldAutowiredHandlerWithoutQualifier() throws Exception {
        CtClassBuilder base = new CtClassBuilder("org.test.CtClassBuilderHandler2");
        base.makeField("private java.lang.reflect.InvocationHandler handler;");
        base.autowiredHandler(true, "");
        CtClass ct = base.build();
        assertNotNull(ct.getDeclaredField("handler"));
        ct.detach();
    }

    @Test
    public void shouldBuildAndMakeSetterGetter() throws Exception {
        CtClass ct = new CtClassBuilder("org.test.CtClassBuilderSG")
                .makeField(Integer.class, "count")
                .makeSetter("count")
                .makeGetter("count")
                .build();
        assertNotNull(ct.getDeclaredMethod("setCount"));
        assertNotNull(ct.getDeclaredMethod("getCount"));
        ct.detach();
    }
}
