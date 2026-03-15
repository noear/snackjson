/*
 * Copyright 2005-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.snack4.jsonpath.util;

/**
 * 逻辑表达式项分析工具。
 * 采用字符级扫描和层级保护机制，实现高性能、高通用性的表达式项（Term）解析。
 *
 * @author noear 2025/10/13 created
 * @since 4.0
 */
public class TermUtil {
    /**
     * 分析表达式项，将其分解为 [left, op, right] 三部分。
     * * 解析规则：
     * 1. 表达式（Term）必须有 left (左操作数)。
     * 2. op (操作符) 和 right (右操作数) 可以同时缺失（如函数表达式）。
     * 3. 如果 op 存在，则 right 必须存在（特殊情况如 'is null'，解析逻辑会将其 right 置为 null）。
     * 4. op 可能是多词操作符，中间包含空格（如 "starts with", "not like"）。
     * 5. 通过括号、方括号、花括号和引号层级保护，确保解析边界的准确性。
     *
     * @param expr 待解析的表达式字符串。
     * @return 包含 [leftStr, opStr, rightStr] 三个元素的字符串数组。
     */
    private static final String[] symbolOps = {"==", "=~", "!=", "<=", ">=", "=", "<", ">"};

    private static boolean isWhitespace(char c) {
        // RFC 9535 定义的空白符：空格 (0x20), HT (0x09), LF (0x0A), CR (0x0D)
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    public static String[] resolve(String expr) {
        expr = expr.trim();
        int inputLength = expr.length();

        String leftStr;
        String opStr = null;
        String rightStr = null;

        int parenLevel = 0;
        int bracketLevel = 0;
        int braceLevel = 0;
        char quoteChar = 0;

        int endOfLeft = -1;
        String foundSymbolOp = null;

        // 1. 正向扫描：寻找 leftStr 的边界
        for (int i = 0; i < inputLength; i++) {
            char c = expr.charAt(i);

            if (quoteChar != 0) {
                if (c == quoteChar) quoteChar = 0;
                continue;
            }

            if (parenLevel == 0 && bracketLevel == 0 && braceLevel == 0) {
                // 优先探测符号操作符（处理 1==1, 1\t==1）
                String matchedOp = null;
                for (String sOp : symbolOps) {
                    if (expr.startsWith(sOp, i)) {
                        matchedOp = sOp;
                        break;
                    }
                }

                if (matchedOp != null) {
                    endOfLeft = i;
                    foundSymbolOp = matchedOp;
                    break;
                }

                // 探测空白（处理 1 == 1, 1 starts with 1）
                if (isWhitespace(c)) {
                    endOfLeft = i;
                    break;
                }
            }

            switch (c) {
                case '\'': case '"': quoteChar = c; break;
                case '(': parenLevel++; break;
                case ')': if (parenLevel > 0) parenLevel--; break;
                case '[': bracketLevel++; break;
                case ']': if (bracketLevel > 0) bracketLevel--; break;
                case '{': braceLevel++; break;
                case '}': if (braceLevel > 0) braceLevel--; break;
            }
        }

        if (endOfLeft == -1) {
            leftStr = expr;
        } else {
            leftStr = expr.substring(0, endOfLeft).trim();

            // 确定操作符的起始位置（跳过 leftStr 后的空白）
            int startOfOp = endOfLeft;
            while (startOfOp < inputLength && isWhitespace(expr.charAt(startOfOp))) {
                startOfOp++;
            }

            if (startOfOp == inputLength) {
                return new String[]{leftStr, null, null};
            }

            // 检查 startOfOp 处是否是符号操作符（修复 1 ==1, 1\t==1）
            // 如果 foundSymbolOp 不为空，说明正向扫描直接命中了符号（1==1）
            // 如果为空，这里需要二次确认是否存在被空格隔开的符号（1 == 1）
            String finalSymbolOp = foundSymbolOp;
            if (finalSymbolOp == null) {
                for (String sOp : symbolOps) {
                    if (expr.startsWith(sOp, startOfOp)) {
                        finalSymbolOp = sOp;
                        break;
                    }
                }
            }

            if (finalSymbolOp != null) {
                // 模式 A：符号模式
                opStr = finalSymbolOp;
                // 必须基于发现符号的实际位置截取 rightStr
                int opOffset = (foundSymbolOp != null) ? endOfLeft : startOfOp;
                rightStr = expr.substring(opOffset + finalSymbolOp.length()).trim();
            } else {
                // 模式 B：多词操作符模式（反向扫描）
                int separatorIndex = -1;
                parenLevel = 0; bracketLevel = 0; braceLevel = 0; quoteChar = 0;

                for (int i = inputLength - 1; i >= startOfOp; i--) {
                    char c = expr.charAt(i);
                    if (quoteChar != 0) {
                        if (c == quoteChar) quoteChar = 0;
                        continue;
                    }
                    switch (c) {
                        case '\'': case '"': quoteChar = c; break;
                        case ')': parenLevel++; break;
                        case '(': if (parenLevel > 0) parenLevel--; break;
                        case ']': bracketLevel++; break;
                        case '[': if (bracketLevel > 0) bracketLevel--; break;
                        case '}': braceLevel++; break;
                        case '{': if (braceLevel > 0) braceLevel--; break;
                        default:
                            if (isWhitespace(c) && parenLevel == 0 && bracketLevel == 0 && braceLevel == 0) {
                                separatorIndex = i;
                                i = startOfOp - 1;
                            }
                            break;
                    }
                }

                if (separatorIndex == -1) {
                    opStr = expr.substring(startOfOp);
                } else {
                    int endOfOp = separatorIndex - 1;
                    while (endOfOp >= startOfOp && isWhitespace(expr.charAt(endOfOp))) endOfOp--;

                    int startOfRight = separatorIndex + 1;
                    while (startOfRight < inputLength && isWhitespace(expr.charAt(startOfRight))) startOfRight++;

                    if (endOfOp >= startOfOp) {
                        opStr = expr.substring(startOfOp, endOfOp + 1);
                    }
                    if (startOfRight < inputLength) {
                        rightStr = expr.substring(startOfRight).trim();
                    }

                    if (opStr != null && (rightStr == null || rightStr.isEmpty())) {
                        opStr = expr.substring(startOfOp).trim();
                        rightStr = null;
                    }
                }
            }
        }

        return new String[]{leftStr, opStr, rightStr};
    }
}