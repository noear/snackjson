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
package org.noear.snack4.codec.decode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author noear 2025/10/7 created
 * @since 4.0
 */
public class DurationDecoder implements ObjectDecoder<Duration> {

    // 用正则匹配纯数字 + 字母单位的 Simple 格式（不区分大小写，如 5m, 500ms, -10s）
    private static final Pattern SIMPLE_PATTERN = Pattern.compile("^([+-]?\\d+)([a-zA-Z]{1,2})$");

    @Override
    public Duration decode(DecodeContext ctx, ONode node) {
        // 1. 快速通道：数字类型直接处理
        if (node.isNumber()) {
            return Duration.ofMillis(node.getLong());
        }

        if (node.isNotEmptyString()) {
            String text = node.getString().trim();
            int len = text.length();
            if (len == 0) {
                return null;
            }

            char first = text.charAt(0);

            // 2. 快速通道：标准 ISO-8601 格式
            if (first == 'P' || first == 'p' || (first == '-' && len > 1 && (text.charAt(1) == 'P' || text.charAt(1) == 'p'))) {
                return Duration.parse(text);
            }

            // 3. 高性能解析 Simple 格式 (从后往前找第一个数字的位置)
            int numEndIdx = -1;
            for (int i = len - 1; i >= 0; i--) {
                char ch = text.charAt(i);
                if (ch >= '0' && ch <= '9') {
                    numEndIdx = i + 1;
                    break;
                }
            }

            // 如果找到了数字与字母的分界线，且不是最后一位（说明后面有单位）
            if (numEndIdx > 0 && numEndIdx < len) {
                try {
                    long value = Long.parseLong(text.substring(0, numEndIdx));
                    String unit = text.substring(numEndIdx).toLowerCase();

                    switch (unit) {
                        case "d":   return Duration.ofDays(value);
                        case "h":   return Duration.ofHours(value);
                        case "m":   return Duration.ofMinutes(value);
                        case "s":   return Duration.ofSeconds(value);
                        case "ms":  return Duration.ofMillis(value);
                        case "us":  return Duration.ofMillis(value / 1000L).plusNanos((value % 1000L) * 1000L);
                        case "ns":  return Duration.ofNanos(value);
                    }
                } catch (NumberFormatException e) {
                    // 转换失败则说明不是合法的简单数字，掉落到下方兜底
                }
            }

            // 4. 兜底旧版逻辑
            String tmp = text.toUpperCase();
            if (tmp.indexOf('D') > 0) {
                tmp = "P" + tmp;
            } else {
                tmp = "PT" + tmp;
            }
            return Duration.parse(tmp);
        }

        return null;
    }
}