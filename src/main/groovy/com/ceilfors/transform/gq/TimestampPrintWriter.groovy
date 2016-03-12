/*
 * Copyright 2016 Wisen Tanasa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ceilfors.transform.gq

import java.util.concurrent.Callable

/**
 * @author ceilfors
 */
class TimestampPrintWriter extends PrintWriter {

    boolean newLine = true
    private long startMilli
    private Callable<Long> now

    private long currentElapsedMilli() {
        return now() - startMilli
    }

    TimestampPrintWriter(Writer out, Callable<Long> now) {
        super(out)
        this.now = now
        this.startMilli = now()
    }

    @Override
    void print(String s) {
        if (newLine) {
            newLine = false
            String timestampPrefix = sprintf('%4.1fs ', currentElapsedMilli() / 1000)
            s = s.replaceAll(/(\r\n|\r|\n)/, '$1' + (' ' * timestampPrefix.length()))
            super.print(timestampPrefix)
        }
        super.print(s)
    }

    @Override
    void println() {
        super.println()
        newLine = true
    }
}
