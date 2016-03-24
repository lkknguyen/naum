/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kordamp.naum.diff;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.kordamp.naum.model.ConstructorInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Andres Almiray
 */
@Data(staticConstructor = "constructorDiffer")
@EqualsAndHashCode(callSuper = true)
public class ConstructorDiffer extends AbstractMemberDiffer<ConstructorInfo> {
    private final ConstructorInfo previous;
    private final ConstructorInfo next;

    @Override
    public Collection<Diff> diff() {
        if (previous.getContentHash().equals(next.getContentHash())) {
            return Collections.emptyList();
        }

        List<Diff> list = new ArrayList<>();

        // 1. modifiers
        checkModifiers(list, "constructor");

        // 2. arguments

        // 3. exceptions
        checkExceptions(list);

        // 4. annotations

        return list;
    }

    private void checkExceptions(Collection<Diff> list) {
        if (!Arrays.equals(previous.getExceptions(), next.getExceptions())) {
            List<String> p = new ArrayList<>(asList(previous.getExceptions()));
            List<String> n = new ArrayList<>(asList(next.getExceptions()));

            List<String> c = new ArrayList<>();
            for (String i : p) {
                if (n.contains(i)) {
                    c.add(i);
                }
            }

            p.removeAll(c);
            n.removeAll(c);

            // anything left in p was removed
            for (String e : p) {
                list.add(
                    Diff.diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.REMOVED)
                        .messageKey("constructor.exception.removed")
                        .messageArg(e)
                        .build());
            }
            // anything left in n was added
            for (String e : n) {
                list.add(
                    Diff.diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.ADDED)
                        .messageKey("constructor.exception.added")
                        .messageArg(e)
                        .build());
            }
        }
    }
}
