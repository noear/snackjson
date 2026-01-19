package features.snack4.json;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

/**
 *
 * @author noear 2026/1/18 created
 *
 */
public class Issse_IDKKVH {
    @Test
    public void case1(){
        String json = "{\n" +
                "\"id\" : \"019bcf951613930f2a8b4d8148cb9287\",\n" +
                "\"object\" : \"chat.completion.chunk\",\n" +
                "\"created\" : 1768714212,\n" +
                "\"model\" : \"Qwen/QwQ-32B\",\n" +
                "\"choices\" : [ {\n" +
                "\"index\" : 0,\n" +
                "\"delta\" : {\n" +
                "\"content\" : null,\n" +
                "\"reasoning_content\" : \"是否提到\",\n" +
                "\"role\" : \"assistant\"\n" +
                "},\n" +
                "\"finish_reason\" : null\n" +
                "} ],\n" +
                "\"system_fingerprint\" : \"\",\n" +
                "\"usage\" : {\n" +
                "\"prompt_tokens\" : 17,\n" +
                "\"completion_tokens\" : 294,\n" +
                "\"total_tokens\" : 311,\n" +
                "\"completion_tokens_details\" : {\n" +
                "\"reasoning_tokens\" : 294\n" +
                "}\n" +
                "}\n" +
                "}";

        ONode.ofJson(json);
    }
}
