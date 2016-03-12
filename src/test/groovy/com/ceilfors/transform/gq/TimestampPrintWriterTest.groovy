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

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author ceilfors
 */
class TimestampPrintWriterTest extends Specification {

    StringWriter writer

    def setup() {
        writer = new StringWriter()
    }

    @Unroll
    def "Should add the timestamp #result as a prefix to the printed String"(long milli, String result) {
        setup:
        def now = [0, milli].iterator().&next
        def printer = new TimestampPrintWriter(writer, now)

        when:
        printer.print("foo")

        then:
        writer.toString() == "${result} foo"

        where:
        milli  || result
        0      || " 0.0s"
        200    || " 0.2s"
        2000   || " 2.0s"
        3333   || " 3.3s"
        10000  || "10.0s"
        100000 || "100.0s"
    }

    def "Should indent new lines and not add timestamp to the new lines from parameters"() {
        setup:
        def now = [0, 100000].iterator().&next
        def printer = new TimestampPrintWriter(writer, now)

        when:
        printer.print("foo\nbar\nboo")

        then:
        writer.toString() ==
                """100.0s foo
                  |       bar
                  |       boo""".stripMargin().denormalize()
    }

    def "Should add timestamp on new line"() {
        setup:
        def now = { -> 0 }
        def printer = new TimestampPrintWriter(writer, now)

        when:
        printer.println("foo")
        printer.print("bar")
        printer.println()
        printer.print("boo")

        then:
        writer.toString() ==
                """ 0.0s foo
                  | 0.0s bar
                  | 0.0s boo""".stripMargin().denormalize()
    }

    def "Should not add a timestamp if there is no newline"() {
        setup:
        def now = { -> 0 }
        def printer = new TimestampPrintWriter(writer, now)

        when:
        printer.print('foo')

        then:
        writer.toString() == ' 0.0s foo'

        when:
        printer.print('bar')

        then:
        writer.toString() == ' 0.0s foobar'
    }
}
