package org.springframework.javassist.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.javassist.bytecode.definition.MvcApiImplicitParam;
import org.springframework.javassist.bytecode.definition.MvcApiResponse;

import io.swagger.annotations.ApiKeyAuthDefinition.ApiKeyLocation;

import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

public class SwaggerApiUtilsTest {

    private final ConstPool constPool = new ConstPool("org.test.Swagger");

    @Test
    public void shouldAnnotApiWithTags() {
        Annotation annot = SwaggerApiUtils.annotApi(constPool, "tag1", "tag2");
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiWithEmptyTags() {
        Annotation annot = SwaggerApiUtils.annotApi(constPool, (String[]) null);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiWithNoTags() {
        Annotation annot = SwaggerApiUtils.annotApi(constPool);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiIgnore() {
        Annotation annot = SwaggerApiUtils.annotApiIgnore(constPool, "hidden");
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiKeyAuthDefinition() {
        Annotation annot = SwaggerApiUtils.annotApiKeyAuthDefinition(constPool,
                "Authorization", "apiKey", "API key auth", ApiKeyLocation.HEADER);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiKeyAuthDefinitionWithNulls() {
        Annotation annot = SwaggerApiUtils.annotApiKeyAuthDefinition(constPool,
                null, null, null, ApiKeyLocation.QUERY);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiOperationWithValueAndNotes() {
        Annotation annot = SwaggerApiUtils.annotApiOperation(constPool, "value", "notes");
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiOperationWithNulls() {
        Annotation annot = SwaggerApiUtils.annotApiOperation(constPool, null, null);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiOperationWithResponseClass() {
        Annotation annot = SwaggerApiUtils.annotApiOperation(constPool, "value", "notes", String.class);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiOperationWithNullResponseClass() {
        Annotation annot = SwaggerApiUtils.annotApiOperation(constPool, "value", "notes", (Class<?>) null);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiOperationWithTagsAndResponse() {
        Annotation annot = SwaggerApiUtils.annotApiOperation(constPool, "value", "notes",
                new String[]{"tag1"}, String.class);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiOperationWithNullTagsAndNullResponse() {
        Annotation annot = SwaggerApiUtils.annotApiOperation(constPool, "value", "notes",
                null, null);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiOperationWithResponseContainer() {
        Annotation annot = SwaggerApiUtils.annotApiOperation(constPool, "value", "notes",
                new String[]{"tag1"}, String.class, "List");
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiOperationWithResponseContainerAndNulls() {
        Annotation annot = SwaggerApiUtils.annotApiOperation(constPool, "value", "notes",
                null, null, null);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiOperationWithResponseReference() {
        Annotation annot = SwaggerApiUtils.annotApiOperation(constPool, "value", "notes",
                new String[]{"tag1"}, String.class, "List", "#/defs/Ref");
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiOperationWithAllAttributes() {
        Annotation annot = SwaggerApiUtils.annotApiOperation(constPool, "value", "notes",
                new String[]{"tag1"}, String.class, "List", "#/defs/Ref",
                "GET", "nickname", "produces", "consumes", "protocols");
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiOperationWithAllAttributesAndNulls() {
        Annotation annot = SwaggerApiUtils.annotApiOperation(constPool, null, null,
                null, null, null, null, null, null, null, null, null);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiOperationFull() {
        Annotation annot = SwaggerApiUtils.annotApiOperation(constPool, "value", "notes",
                new String[]{"tag1"}, String.class, "List", "#/defs/Ref",
                "GET", "nickname", "produces", "consumes", "protocols",
                true, 200, true);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiImplicitParams() {
        MvcApiImplicitParam param = new MvcApiImplicitParam("name", "desc", true, "String");
        Annotation annot = SwaggerApiUtils.annotApiImplicitParams(constPool, param);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiImplicitParamsWithMultiple() {
        MvcApiImplicitParam p1 = new MvcApiImplicitParam("name1", "desc1", true, "String");
        MvcApiImplicitParam p2 = new MvcApiImplicitParam("name2", "desc2", false, Integer.class);
        Annotation annot = SwaggerApiUtils.annotApiImplicitParams(constPool, p1, p2);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiImplicitParamsWithFullParam() {
        MvcApiImplicitParam param = new MvcApiImplicitParam(
                "name", "value", "def", "1,2,3", true,
                "access", false, "dataType", String.class, "query",
                "example", "type", "format", true, false, "csv");
        Annotation annot = SwaggerApiUtils.annotApiImplicitParams(constPool, param);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiImplicitParamsWithNullDataTypeClass() {
        MvcApiImplicitParam param = new MvcApiImplicitParam("name", "desc", true, "String");
        param.setDataTypeClass(null);
        Annotation annot = SwaggerApiUtils.annotApiImplicitParams(constPool, param);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiResponses() {
        MvcApiResponse response = new MvcApiResponse(200, "OK", String.class);
        Annotation annot = SwaggerApiUtils.annotApiResponses(constPool, response);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiResponsesWithMultiple() {
        MvcApiResponse r1 = new MvcApiResponse(200, "OK");
        MvcApiResponse r2 = new MvcApiResponse(404, "Not Found", Void.class, "ref", "List");
        Annotation annot = SwaggerApiUtils.annotApiResponses(constPool, r1, r2);
        assertNotNull(annot);
    }

    @Test
    public void shouldAnnotApiResponsesWithNullResponse() {
        MvcApiResponse response = new MvcApiResponse(200, "OK");
        response.setResponse(null);
        Annotation annot = SwaggerApiUtils.annotApiResponses(constPool, response);
        assertNotNull(annot);
    }
}
