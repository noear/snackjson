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
     * 预定义符号操作符，按长度倒序排列以确保最大匹配（如优先匹配 == 而非 =）。
     */
    private static final String[] symbolOps = {"==", "=~", "!=", "<=", ">=", "=", "<", ">"};

    /**
     * 校验字符是否为 RFC 9535 规范定义的空白符。
     */
    private static boolean isWhitespace(char c) {
        // 包括：空格 (0x20), 水平制表符 (0x09), 换行符 (0x0A), 回车符 (0x0D)
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    /**
     * 分析表达式项，将其分解为 [左操作数, 操作符, 右操作数]。
     *
     * <p>解析规则：
     * 1. 必须包含 left (左操作数)；
     * 2. op (操作符) 和 right (右操作数) 可同时缺失（如函数执行 length(@.a)）；
     * 3. 如果包含 op，通常 right 也应存在（特殊操作符如 'is null' 除外，此时 right 会被解析为 null）；
     * 4. 支持符号操作符（如 ==）和多词文本操作符（如 starts with）；
     * 5. 自动处理 ()、[]、{} 层级及引号保护，确保复杂路径或字符串内部的特殊字符不干扰解析。
     *
     * @param expr 待解析的表达式字符串
     * @return 长度为 3 的数组 [leftStr, opStr, rightStr]
     */
    public static String[] resolve(String expr) {
        expr = expr.trim();
        int inputLength = expr.length();

        String leftStr;
        String opStr = null;
        String rightStr = null;

        // 层级状态变量，确保在括号或引号内部的内容不作为分隔依据
        int parenLevel = 0;   // ()
        int bracketLevel = 0; // []
        int braceLevel = 0;   // {}
        char quoteChar = 0;   // ' 或 "

        int endOfLeft = -1; // 记录左操作数的结束索引
        String foundSymbolOp = null; // 记录正向扫描过程中直接命中的符号

        // 1. 正向扫描：确定左操作数（leftStr）与后续部分的边界
        for (int i = 0; i < inputLength; i++) {
            char c = expr.charAt(i);

            // 处于引号内部，跳过探测
            if (quoteChar != 0) {
                if (c == quoteChar) quoteChar = 0;
                continue;
            }

            // 仅在顶级层级（非括号内）进行边界探测
            if (parenLevel == 0 && bracketLevel == 0 && braceLevel == 0) {
                // 优先探测符号操作符：处理紧凑型 (1==1) 或带空白的符号 (1\t==1)
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

                // 探测空白：处理标准分隔符 (1 == 1 或 1 starts with 1)
                if (isWhitespace(c)) {
                    endOfLeft = i;
                    break;
                }
            }

            // 层级增减及引号状态切换
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
            // 未发现任何分隔符，整个表达式即为左操作数
            leftStr = expr;
        } else {
            // 提取并清理左操作数
            leftStr = expr.substring(0, endOfLeft).trim();

            // 确定操作符部分的起始点（跳过左操作数后的所有空白）
            int startOfOp = endOfLeft;
            while (startOfOp < inputLength && isWhitespace(expr.charAt(startOfOp))) {
                startOfOp++;
            }

            // 若左操作数之后仅有空白，则认为无后续部分
            if (startOfOp == inputLength) {
                return new String[]{leftStr, null, null};
            }

            // 二次探测：检查起始位置是否为符号操作符
            // 1. 若正向扫描已命中（foundSymbolOp），则直接采用
            // 2. 若正向扫描因空白停止，则此处检查空白后的内容是否为符号（修复 1 ==1）
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
                // --- 模式 A：符号操作符模式 ---
                opStr = finalSymbolOp;
                // 计算右操作数的起始位（需根据符号实际位置偏移）
                int opOffset = (foundSymbolOp != null) ? endOfLeft : startOfOp;
                rightStr = expr.substring(opOffset + finalSymbolOp.length()).trim();
            } else {
                // --- 模式 B：文本/多词操作符模式 ---
                // 采用反向扫描定位 op 和 right 的分隔点（最后一个顶级空白）
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
                                i = startOfOp - 1; // 找到即跳出
                            }
                            break;
                    }
                }

                if (separatorIndex == -1) {
                    // 无分隔符，剩余部分全部视为操作符（如 is null）
                    opStr = expr.substring(startOfOp);
                } else {
                    // 精确计算 opStr 结束位置（过滤掉尾随空白）
                    int endOfOp = separatorIndex - 1;
                    while (endOfOp >= startOfOp && isWhitespace(expr.charAt(endOfOp))) endOfOp--;

                    // 精确计算 rightStr 起始位置（过滤前导空白）
                    int startOfRight = separatorIndex + 1;
                    while (startOfRight < inputLength && isWhitespace(expr.charAt(startOfRight))) startOfRight++;

                    if (endOfOp >= startOfOp) {
                        opStr = expr.substring(startOfOp, endOfOp + 1);
                    }
                    if (startOfRight < inputLength) {
                        rightStr = expr.substring(startOfRight).trim();
                    }

                    // 异常修正：若计算后右操作数为空，则说明原部分其实只是一个长操作符
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