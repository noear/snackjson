package org.noear.snack4.json;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonReader 内部缓冲区（8192）边界测试：
 * peekChar(offset) 跨越缓冲区末尾时，需补读数据而不是返回 0
 */
class JsonReaderBufferBoundaryTest {
    private static final long TS = 1700000000000L;

    /**
     * 构造：`n` 恰好位于 nPos 位置的 `[   ...  new Date(TS)]`
     */
    private static String build(int nPos) {
        StringBuilder sb = new StringBuilder(nPos + 32);
        sb.append('[');
        while (sb.length() < nPos) {
            sb.append(' ');
        }
        sb.append("new Date(").append(TS).append(")]");
        return sb.toString();
    }

    private static void assertDate(ONode node, String json) {
        assertTrue(node.isArray(), json.length() + "");
        assertEquals(1, node.size());
        assertTrue(node.get(0).isDate());
        assertEquals(TS, node.get(0).getDate().getTime());
    }

    @Test
    public void stringInput() throws IOException {
        for (int nPos = 8185; nPos <= 8195; nPos++) {
            String json = build(nPos);
            assertDate(new JsonReader(json).read(), json);
            assertDate(ONode.ofJson(json), json);
        }
    }

    @Test
    public void readerInput() throws IOException {
        for (int nPos = 8185; nPos <= 8195; nPos++) {
            String json = build(nPos);
            assertDate(new JsonReader(new StringReader(json)).read(), json);
        }
    }

    /**
     * 每次只读少量字符的 Reader（模拟网络流）
     */
    @Test
    public void chunkedReaderInput() throws IOException {
        for (int chunk = 1; chunk <= 3; chunk++) {
            for (int nPos = 1; nPos <= 12; nPos++) {
                String json = build(nPos);
                assertDate(new JsonReader(new ChunkedReader(json, chunk)).read(), json);
            }
        }
    }

    static class ChunkedReader extends Reader {
        private final String src;
        private final int chunk;
        private int pos;

        ChunkedReader(String src, int chunk) {
            this.src = src;
            this.chunk = chunk;
        }

        @Override
        public int read(char[] cbuf, int off, int len) {
            if (pos >= src.length()) {
                return -1;
            }

            int n = Math.min(Math.min(len, chunk), src.length() - pos);
            src.getChars(pos, pos + n, cbuf, off);
            pos += n;
            return n;
        }

        @Override
        public void close() {
        }
    }
}
