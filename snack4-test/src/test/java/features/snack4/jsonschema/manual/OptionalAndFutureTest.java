package features.snack4.jsonschema.manual;


import lombok.Data;
import org.junit.jupiter.api.Test;
import org.noear.snack4.jsonschema.JsonSchema;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author noear 2025/12/18 created
 *
 */
public class OptionalAndFutureTest {

    public Optional<String> optionalString() {
        return Optional.of("");
    }

    public Optional<User> optionalUser() {
        return null;
    }

    public CompletableFuture<String> futureString() {
        return null;
    }

    public CompletableFuture<User> futureUser() {
        return null;
    }

    @Test
    public void optionalCase1() throws Exception {
        Method method = OptionalAndFutureTest.class.getMethod("optionalString");

        String json = JsonSchema.DEFAULT.createValidator(method.getGenericReturnType()).toJson();
        System.out.println(json);

        assert "{\"type\":\"string\"}".equals(json);
    }

    @Test
    public void optionalCase2() throws Exception {
        Method method = OptionalAndFutureTest.class.getMethod("optionalUser");

        String json = JsonSchema.DEFAULT.createValidator(method.getGenericReturnType()).toJson();
        System.out.println(json);

        assert "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\"},\"name\":{\"type\":\"string\"}},\"required\":[]}".equals(json);
    }

    @Test
    public void futureCase1() throws Exception {
        Method method = OptionalAndFutureTest.class.getMethod("futureString");

        String json = JsonSchema.DEFAULT.createValidator(method.getGenericReturnType()).toJson();
        System.out.println(json);

        assert "{\"type\":\"string\"}".equals(json);
    }

    @Test
    public void futureCase2() throws Exception {
        Method method = OptionalAndFutureTest.class.getMethod("futureUser");

        String json = JsonSchema.DEFAULT.createValidator(method.getGenericReturnType()).toJson();
        System.out.println(json);

        assert "{\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\"},\"name\":{\"type\":\"string\"}},\"required\":[]}".equals(json);
    }

    @Data
    public static class User {
        private long id;
        private String name;
    }
}