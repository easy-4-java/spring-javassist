package org.springframework.javassist.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.javassist.bytecode.EndpointApi;
import org.springframework.javassist.bytecode.definition.MvcBound;
import org.springframework.javassist.bytecode.definition.MvcMapping;
import org.springframework.javassist.bytecode.definition.MvcMethod;
import org.springframework.javassist.bytecode.definition.MvcParam;
import org.springframework.javassist.bytecode.definition.MvcParamFrom;
import org.springframework.web.bind.annotation.RequestMethod;

import io.github.easy4j.javassist.utils.ClassPoolFactory;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

public class EndpointApiUtilsTest {

    private final ClassPool pool = ClassPoolFactory.getDefaultPool();

    @Test
    public void shouldMakeClass() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.MakeClass1");
        assertNotNull(ct);
        assertEquals("org.test.MakeClass1", ct.getName());
        ct.detach();
    }

    @Test
    public void shouldReturnExistingClass() throws Exception {
        CtClass first = EndpointApiUtils.makeClass(pool, "org.test.MakeClass2");
        CtClass second = EndpointApiUtils.makeClass(pool, "org.test.MakeClass2");
        assertSame(first, second);
        first.detach();
    }

    @Test
    public void shouldMakeInterface() throws Exception {
        CtClass ct = EndpointApiUtils.makeInterface(pool, "org.test.MakeInterface1");
        assertNotNull(ct);
        assertTrue(ct.isInterface());
        ct.detach();
    }

    @Test
    public void shouldReturnExistingInterface() throws Exception {
        CtClass first = EndpointApiUtils.makeInterface(pool, "org.test.MakeInterface2");
        CtClass second = EndpointApiUtils.makeInterface(pool, "org.test.MakeInterface2");
        assertSame(first, second);
        first.detach();
    }

    @Test
    public void shouldMakeDefaultConstructor() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.DefaultCons1");
        CtConstructor cons = EndpointApiUtils.defaultConstructor(ct);
        assertNotNull(cons);
        ct.detach();
    }

    @Test
    public void shouldMakeConstructor() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.MakeCons1");
        CtClass superCt = pool.get(EndpointApi.class.getName());
        ct.setSuperclass(superCt);
        CtConstructor cons = EndpointApiUtils.makeConstructor(pool, ct);
        assertNotNull(cons);
        ct.detach();
    }

    @Test
    public void shouldMakeParams() throws Exception {
        MvcParam<?>[] params = new MvcParam[]{
                new MvcParam<>(String.class, "name"),
                new MvcParam<>(Integer.class, "age")
        };
        CtClass[] result = EndpointApiUtils.makeParams(pool, params);
        assertNotNull(result);
        assertEquals(2, result.length);
    }

    @Test
    public void shouldReturnNullForNullParams() throws Exception {
        assertNull(EndpointApiUtils.makeParams(pool, (MvcParam<?>[]) null));
    }

    @Test
    public void shouldReturnNullForEmptyParams() throws Exception {
        assertNull(EndpointApiUtils.makeParams(pool, new MvcParam[0]));
    }

    @Test
    public void shouldAnnotWebBound() {
        ConstPool constPool = new ConstPool("org.test.AnnotWebBound");
        MvcBound bound = new MvcBound("uid123", "{\"key\":\"val\"}");
        Annotation annot = EndpointApiUtils.annotWebBound(constPool, bound);
        assertNotNull(annot);
        assertEquals("org.springframework.javassist.annotation.WebBound", annot.getTypeName());
    }

    @Test
    public void shouldAnnotWebBoundWithEmptyJson() {
        ConstPool constPool = new ConstPool("org.test.AnnotWebBound2");
        MvcBound bound = new MvcBound("uid123", "");
        Annotation annot = EndpointApiUtils.annotWebBound(constPool, bound);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotController() {
        ConstPool constPool = new ConstPool("org.test.AnnotController");
        Annotation annot = EndpointApiUtils.annotController(constPool, "myCtrl");
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotRestController() {
        ConstPool constPool = new ConstPool("org.test.AnnotRestController");
        Annotation annot = EndpointApiUtils.annotRestController(constPool, "myRest");
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotConfiguration() {
        ConstPool constPool = new ConstPool("org.test.AnnotConfig");
        Annotation annot = EndpointApiUtils.annotConfiguration(constPool, "myConfig");
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotBean() {
        ConstPool constPool = new ConstPool("org.test.AnnotBean");
        Annotation annot = EndpointApiUtils.annotBean(constPool, new String[]{"bean1"},
                org.springframework.beans.factory.annotation.Autowire.BY_NAME, "init", "destroy");
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotLazy() {
        ConstPool constPool = new ConstPool("org.test.AnnotLazy");
        Annotation annot = EndpointApiUtils.annotLazy(constPool, true);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotScope() {
        ConstPool constPool = new ConstPool("org.test.AnnotScope");
        Annotation annot = EndpointApiUtils.annotScope(constPool, "singleton",
                org.springframework.context.annotation.ScopedProxyMode.DEFAULT);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotRequestMappingWithMapping() {
        ConstPool constPool = new ConstPool("org.test.RequestMapping");
        MvcMapping mapping = new MvcMapping(new String[]{"/api"}, RequestMethod.GET);
        Annotation annot = EndpointApiUtils.annotRequestMapping(constPool, mapping);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotGetMappingWithMapping() {
        ConstPool constPool = new ConstPool("org.test.GetMapping");
        MvcMapping mapping = new MvcMapping(new String[]{"/api"}, RequestMethod.GET);
        Annotation annot = EndpointApiUtils.annotGetMapping(constPool, mapping);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotPostMappingWithMapping() {
        ConstPool constPool = new ConstPool("org.test.PostMapping");
        MvcMapping mapping = new MvcMapping(new String[]{"/api"}, RequestMethod.POST);
        Annotation annot = EndpointApiUtils.annotPostMapping(constPool, mapping);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotPutMappingWithMapping() {
        ConstPool constPool = new ConstPool("org.test.PutMapping");
        MvcMapping mapping = new MvcMapping(new String[]{"/api"}, RequestMethod.PUT);
        Annotation annot = EndpointApiUtils.annotPutMapping(constPool, mapping);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotDeleteMappingWithMapping() {
        ConstPool constPool = new ConstPool("org.test.DeleteMapping");
        MvcMapping mapping = new MvcMapping(new String[]{"/api"}, RequestMethod.DELETE);
        Annotation annot = EndpointApiUtils.annotDeleteMapping(constPool, mapping);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotPatchMappingWithMapping() {
        ConstPool constPool = new ConstPool("org.test.PatchMapping");
        MvcMapping mapping = new MvcMapping(new String[]{"/api"}, RequestMethod.PATCH);
        Annotation annot = EndpointApiUtils.annotPatchMapping(constPool, mapping);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotRequestMappingWithRawValues() {
        ConstPool constPool = new ConstPool("org.test.RequestMappingRaw");
        Annotation annot = EndpointApiUtils.annotRequestMapping(constPool, "name",
                new String[]{"/api"}, new RequestMethod[]{RequestMethod.GET},
                new String[]{"p=v"}, new String[]{"h=v"},
                new String[]{"application/json"}, new String[]{"application/json"});
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotGetMappingWithRawValues() {
        ConstPool constPool = new ConstPool("org.test.GetMappingRaw");
        Annotation annot = EndpointApiUtils.annotGetMapping(constPool, "name",
                new String[]{"/api"}, new String[]{"p=v"}, new String[]{"h=v"},
                new String[]{"application/json"}, new String[]{"application/json"});
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotPostMappingWithRawValues() {
        ConstPool constPool = new ConstPool("org.test.PostMappingRaw");
        Annotation annot = EndpointApiUtils.annotPostMapping(constPool, "name",
                new String[]{"/api"}, new String[]{"p=v"}, new String[]{"h=v"},
                new String[]{"application/json"}, new String[]{"application/json"});
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotPutMappingWithRawValues() {
        ConstPool constPool = new ConstPool("org.test.PutMappingRaw");
        Annotation annot = EndpointApiUtils.annotPutMapping(constPool, "name",
                new String[]{"/api"}, new String[]{"p=v"}, new String[]{"h=v"},
                new String[]{"application/json"}, new String[]{"application/json"});
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotDeleteMappingWithRawValues() {
        ConstPool constPool = new ConstPool("org.test.DeleteMappingRaw");
        Annotation annot = EndpointApiUtils.annotDeleteMapping(constPool, "name",
                new String[]{"/api"}, new String[]{"p=v"}, new String[]{"h=v"},
                new String[]{"application/json"}, new String[]{"application/json"});
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotPatchMappingWithRawValues() {
        ConstPool constPool = new ConstPool("org.test.PatchMappingRaw");
        Annotation annot = EndpointApiUtils.annotPatchMapping(constPool, "name",
                new String[]{"/api"}, new String[]{"p=v"}, new String[]{"h=v"},
                new String[]{"application/json"}, new String[]{"application/json"});
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotMethodMappingSingleGet() {
        ConstPool constPool = new ConstPool("org.test.MethodMappingGet");
        MvcMethod method = new MvcMethod("find", new String[]{"/find"}, RequestMethod.GET);
        Annotation annot = EndpointApiUtils.annotMethodMapping(constPool, method);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotMethodMappingSinglePost() {
        ConstPool constPool = new ConstPool("org.test.MethodMappingPost");
        MvcMethod method = new MvcMethod("save", new String[]{"/save"}, RequestMethod.POST);
        Annotation annot = EndpointApiUtils.annotMethodMapping(constPool, method);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotMethodMappingSinglePut() {
        ConstPool constPool = new ConstPool("org.test.MethodMappingPut");
        MvcMethod method = new MvcMethod("update", new String[]{"/update"}, RequestMethod.PUT);
        Annotation annot = EndpointApiUtils.annotMethodMapping(constPool, method);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotMethodMappingSingleDelete() {
        ConstPool constPool = new ConstPool("org.test.MethodMappingDelete");
        MvcMethod method = new MvcMethod("remove", new String[]{"/remove"}, RequestMethod.DELETE);
        Annotation annot = EndpointApiUtils.annotMethodMapping(constPool, method);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotMethodMappingSinglePatch() {
        ConstPool constPool = new ConstPool("org.test.MethodMappingPatch");
        MvcMethod method = new MvcMethod("patch", new String[]{"/patch"}, RequestMethod.PATCH);
        Annotation annot = EndpointApiUtils.annotMethodMapping(constPool, method);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotMethodMappingMultipleMethods() {
        ConstPool constPool = new ConstPool("org.test.MethodMappingMulti");
        MvcMethod method = new MvcMethod("action", new String[]{"/action"},
                new RequestMethod[]{RequestMethod.GET, RequestMethod.POST});
        Annotation annot = EndpointApiUtils.annotMethodMapping(constPool, method);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotMethodMappingWithPathAndContentType() {
        ConstPool constPool = new ConstPool("org.test.MethodMappingPath");
        for (RequestMethod rm : RequestMethod.values()) {
            Annotation annot = EndpointApiUtils.annotMethodMapping(constPool, "/test", rm, "application/json");
            assertNotNull(annot);
        }
    }

    @Test
    public void shouldAnnotParamsWithAllFromTypes() throws Exception {
        ConstPool constPool = new ConstPool("org.test.AnnotParams");
        MvcParam<?>[] params = new MvcParam[]{
                new MvcParam<>(String.class, "cookie", MvcParamFrom.COOKIE),
                new MvcParam<>(String.class, "matrix", MvcParamFrom.MATRIX),
                new MvcParam<>(String.class, "path", MvcParamFrom.PATH),
                new MvcParam<>(String.class, "attr", MvcParamFrom.ATTR),
                new MvcParam<>(String.class, "body", MvcParamFrom.BODY),
                new MvcParam<>(String.class, "header", MvcParamFrom.HEADER),
                new MvcParam<>(String.class, "param", MvcParamFrom.PARAM),
                new MvcParam<>(String.class, "part", MvcParamFrom.PART),
        };
        Annotation[][] result = EndpointApiUtils.annotParams(constPool, params);
        assertNotNull(result);
        assertEquals(8, result.length);
        for (Annotation[] pair : result) {
            assertEquals(2, pair.length);
            assertNotNull(pair[0]);
            assertNotNull(pair[1]);
        }
    }

    @Test
    public void shouldAnnotParamsWithDefaultValues() {
        ConstPool constPool = new ConstPool("org.test.AnnotParamsDef");
        MvcParam<?>[] params = new MvcParam[]{
                new MvcParam<>(String.class, "cookie", MvcParamFrom.COOKIE, "defVal"),
                new MvcParam<>(String.class, "matrix", MvcParamFrom.MATRIX, "defVal"),
                new MvcParam<>(String.class, "header", MvcParamFrom.HEADER, "defVal"),
                new MvcParam<>(String.class, "param", MvcParamFrom.PARAM, "defVal"),
        };
        Annotation[][] result = EndpointApiUtils.annotParams(constPool, params);
        assertNotNull(result);
        assertEquals(4, result.length);
    }

    @Test
    public void shouldReturnNullForNullAnnotParams() {
        ConstPool constPool = new ConstPool("org.test.AnnotParamsNull");
        assertNull(EndpointApiUtils.annotParams(constPool, (MvcParam<?>[]) null));
    }

    @Test
    public void shouldReturnNullForEmptyAnnotParams() {
        ConstPool constPool = new ConstPool("org.test.AnnotParamsEmpty");
        assertNull(EndpointApiUtils.annotParams(constPool, new MvcParam[0]));
    }

    @Test
    public void shouldSetMethodBodyFromString() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.MethodBodyStr");
        CtClass superCt = pool.get(EndpointApi.class.getName());
        ct.setSuperclass(superCt);
        CtMethod m = new CtMethod(CtClass.voidType, "test", null, ct);
        ct.addMethod(m);
        EndpointApiUtils.methodBody(m, "test");
        assertNotNull(m.getMethodInfo());
        ct.detach();
    }

    @Test
    public void shouldSetMethodBodyFromMvcMethod() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.MethodBodyMvc");
        CtClass superCt = pool.get(EndpointApi.class.getName());
        ct.setSuperclass(superCt);
        CtMethod m = new CtMethod(CtClass.voidType, "test", null, ct);
        ct.addMethod(m);
        MvcMethod method = new MvcMethod("test", new String[]{"/test"}, RequestMethod.GET);
        EndpointApiUtils.methodBody(m, method);
        assertNotNull(m.getMethodInfo());
        ct.detach();
    }

    @Test
    public void shouldAddMethodCatch() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.MethodCatch");
        CtClass superCt = pool.get(EndpointApi.class.getName());
        ct.setSuperclass(superCt);
        CtMethod m = new CtMethod(CtClass.voidType, "test", null, ct);
        ct.addMethod(m);
        EndpointApiUtils.methodBody(m, "test");
        EndpointApiUtils.methodCatch(pool, m);
        assertNotNull(m.getMethodInfo());
        ct.detach();
    }

    @Test
    public void shouldAddMethodAnnotationsWithBound() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.MethodAnnotBound");
        CtClass superCt = pool.get(EndpointApi.class.getName());
        ct.setSuperclass(superCt);
        CtMethod m = new CtMethod(CtClass.voidType, "test", null, ct);
        ct.addMethod(m);
        EndpointApiUtils.methodBody(m, "test");
        EndpointApiUtils.methodCatch(pool, m);
        ConstPool constPool = ct.getClassFile().getConstPool();
        MvcBound bound = new MvcBound("uid1", "{}");
        EndpointApiUtils.methodAnnotations(m, constPool, "/test", RequestMethod.GET, "application/json", bound, null);
        assertNotNull(m.getMethodInfo());
        ct.detach();
    }

    @Test
    public void shouldAddMethodAnnotationsWithoutBound() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.MethodAnnotNoBound");
        CtClass superCt = pool.get(EndpointApi.class.getName());
        ct.setSuperclass(superCt);
        CtMethod m = new CtMethod(CtClass.voidType, "test", null, ct);
        ct.addMethod(m);
        EndpointApiUtils.methodBody(m, "test");
        EndpointApiUtils.methodCatch(pool, m);
        ConstPool constPool = ct.getClassFile().getConstPool();
        MvcParam<?>[] params = new MvcParam[]{new MvcParam<>(String.class, "name")};
        EndpointApiUtils.methodAnnotations(m, constPool, "/test", RequestMethod.POST, "application/json", null, params);
        assertNotNull(m.getMethodInfo());
        ct.detach();
    }

    @Test
    public void shouldAddMethodAnnotationsFromMvcMethod() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.MethodAnnotMvc");
        CtClass superCt = pool.get(EndpointApi.class.getName());
        ct.setSuperclass(superCt);
        CtMethod m = new CtMethod(CtClass.voidType, "test", null, ct);
        ct.addMethod(m);
        EndpointApiUtils.methodBody(m, "test");
        EndpointApiUtils.methodCatch(pool, m);
        ConstPool constPool = ct.getClassFile().getConstPool();
        MvcMethod method = new MvcMethod("test", new String[]{"/test"}, RequestMethod.GET);
        MvcBound bound = new MvcBound("uid1", "{}");
        EndpointApiUtils.methodAnnotations(m, constPool, method, bound, (MvcParam<?>[]) null);
        assertNotNull(m.getMethodInfo());
        ct.detach();
    }

    @Test
    public void shouldAddMethodAnnotationsFromMvcMethodWithResponseBody() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.MethodAnnotMvcRB");
        CtClass superCt = pool.get(EndpointApi.class.getName());
        ct.setSuperclass(superCt);
        CtMethod m = new CtMethod(CtClass.voidType, "test", null, ct);
        ct.addMethod(m);
        EndpointApiUtils.methodBody(m, "test");
        EndpointApiUtils.methodCatch(pool, m);
        ConstPool constPool = ct.getClassFile().getConstPool();
        MvcMethod method = new MvcMethod("test", new String[]{"/test"}, true, RequestMethod.GET);
        EndpointApiUtils.methodAnnotations(m, constPool, method, null, (MvcParam<?>[]) null);
        assertNotNull(m.getMethodInfo());
        ct.detach();
    }

    @Test
    public void shouldAddMethodAnnotationsFromMvcMethodWithParams() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.MethodAnnotMvcParam");
        CtClass superCt = pool.get(EndpointApi.class.getName());
        ct.setSuperclass(superCt);
        CtMethod m = new CtMethod(CtClass.voidType, "test", null, ct);
        ct.addMethod(m);
        EndpointApiUtils.methodBody(m, "test");
        EndpointApiUtils.methodCatch(pool, m);
        ConstPool constPool = ct.getClassFile().getConstPool();
        MvcMethod method = new MvcMethod("test", new String[]{"/test"}, RequestMethod.GET);
        MvcParam<?>[] params = new MvcParam[]{new MvcParam<>(String.class, "name")};
        EndpointApiUtils.methodAnnotations(m, constPool, method, null, params);
        assertNotNull(m.getMethodInfo());
        ct.detach();
    }

    @Test
    public void shouldAddMethodBound() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.MethodBound");
        CtClass superCt = pool.get(EndpointApi.class.getName());
        ct.setSuperclass(superCt);
        CtMethod m = new CtMethod(CtClass.voidType, "test", null, ct);
        ct.addMethod(m);
        EndpointApiUtils.methodBody(m, "test");
        ConstPool constPool = ct.getClassFile().getConstPool();
        MvcBound bound = new MvcBound("uid1", "{}");
        EndpointApiUtils.methodBound(m, constPool, bound);
        assertNotNull(m.getMethodInfo());
        ct.detach();
    }

    @Test
    public void shouldAddMethodBoundWithNull() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.MethodBoundNull");
        CtClass superCt = pool.get(EndpointApi.class.getName());
        ct.setSuperclass(superCt);
        CtMethod m = new CtMethod(CtClass.voidType, "test", null, ct);
        ct.addMethod(m);
        EndpointApiUtils.methodBody(m, "test");
        ConstPool constPool = ct.getClassFile().getConstPool();
        EndpointApiUtils.methodBound(m, constPool, null);
        assertNotNull(m.getMethodInfo());
        ct.detach();
    }

    @Test
    public void shouldAnnotRequestMappingWithEmptyArrays() {
        ConstPool constPool = new ConstPool("org.test.RequestMappingEmpty");
        Annotation annot = EndpointApiUtils.annotRequestMapping(constPool, null, null, null, null, null, null, null);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotGetMappingWithEmptyArrays() {
        ConstPool constPool = new ConstPool("org.test.GetMappingEmpty");
        Annotation annot = EndpointApiUtils.annotGetMapping(constPool, null, null, null, null, null, null);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotPostMappingWithEmptyArrays() {
        ConstPool constPool = new ConstPool("org.test.PostMappingEmpty");
        Annotation annot = EndpointApiUtils.annotPostMapping(constPool, null, null, null, null, null, null);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotPutMappingWithEmptyArrays() {
        ConstPool constPool = new ConstPool("org.test.PutMappingEmpty");
        Annotation annot = EndpointApiUtils.annotPutMapping(constPool, null, null, null, null, null, null);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotDeleteMappingWithEmptyArrays() {
        ConstPool constPool = new ConstPool("org.test.DeleteMappingEmpty");
        Annotation annot = EndpointApiUtils.annotDeleteMapping(constPool, null, null, null, null, null, null);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotPatchMappingWithEmptyArrays() {
        ConstPool constPool = new ConstPool("org.test.PatchMappingEmpty");
        Annotation annot = EndpointApiUtils.annotPatchMapping(constPool, null, null, null, null, null, null);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotMethodMappingWithAllRequestMethods() {
        ConstPool constPool = new ConstPool("org.test.MethodMappingAll");
        for (RequestMethod rm : RequestMethod.values()) {
            MvcMethod method = new MvcMethod("test", new String[]{"/test"}, rm);
            Annotation annot = EndpointApiUtils.annotMethodMapping(constPool, method);
            assertNotNull(annot);
        }
    }

    @Test
    public void shouldAnnotRequestMappingWithMappingContainingAllAttributes() {
        ConstPool constPool = new ConstPool("org.test.RequestMappingFull");
        MvcMapping mapping = new MvcMapping("name", new String[]{"/api"},
                new RequestMethod[]{RequestMethod.GET}, new String[]{"p=v"},
                new String[]{"h=v"}, new String[]{"application/json"},
                new String[]{"application/json"});
        Annotation annot = EndpointApiUtils.annotRequestMapping(constPool, mapping);
        assertNotNull(annot);
    }

    @Test
    public void shouldSetSuperclass() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.SetSuperclass");
        EndpointApiUtils.setSuperclass(pool, ct, java.util.ArrayList.class);
        assertEquals("java.util.ArrayList", ct.getSuperclass().getName());
        ct.detach();
    }

    @Test
    public void shouldAnnotMethodMappingPathContentTypeAllMethods() {
        ConstPool constPool = new ConstPool("org.test.MethodMappingAllRM");
        for (RequestMethod rm : RequestMethod.values()) {
            Annotation annot = EndpointApiUtils.annotMethodMapping(constPool, "/path", rm, "text/plain");
            assertNotNull(annot);
        }
    }

    @Test
    public void shouldAddMethodAnnotationsWithMultipleParams() throws Exception {
        CtClass ct = EndpointApiUtils.makeClass(pool, "org.test.MethodAnnotMultiParam");
        CtClass superCt = pool.get(EndpointApi.class.getName());
        ct.setSuperclass(superCt);
        CtMethod m = new CtMethod(CtClass.voidType, "test", null, ct);
        ct.addMethod(m);
        EndpointApiUtils.methodBody(m, "test");
        EndpointApiUtils.methodCatch(pool, m);
        ConstPool constPool = ct.getClassFile().getConstPool();
        MvcParam<?>[] params = new MvcParam[]{
                new MvcParam<>(String.class, "name"),
                new MvcParam<>(Integer.class, "age", MvcParamFrom.PATH),
                new MvcParam<>(String.class, "body", MvcParamFrom.BODY)
        };
        EndpointApiUtils.methodAnnotations(m, constPool, "/test", RequestMethod.POST, "application/json",
                new MvcBound("uid1", "{}"), params);
        assertNotNull(m.getMethodInfo());
        ct.detach();
    }

    @Test
    public void shouldAnnotMethodMappingMvcWithMultipleMethods() {
        ConstPool constPool = new ConstPool("org.test.MethodMappingMultiAll");
        for (RequestMethod rm1 : RequestMethod.values()) {
            for (RequestMethod rm2 : RequestMethod.values()) {
                if (rm1 != rm2) {
                    MvcMethod method = new MvcMethod("test", new String[]{"/test"},
                            new RequestMethod[]{rm1, rm2});
                    Annotation annot = EndpointApiUtils.annotMethodMapping(constPool, method);
                    assertNotNull(annot);
                }
            }
        }
    }
}
