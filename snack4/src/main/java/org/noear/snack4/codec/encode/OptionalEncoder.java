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
package org.noear.snack4.codec.encode;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.BeanEncoder;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;

import java.util.Optional;

/**
 *
 * @author noear 2025/12/29 created
 * @since 4.0.23
 */
public class OptionalEncoder implements ObjectEncoder<Optional> {
    @Override
    public ONode encode(EncodeContext ctx, Optional o, ONode target) {
        Object value = o.get();
        return BeanEncoder.encode(value, ctx.getOptions());
    }
}
