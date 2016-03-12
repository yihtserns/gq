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

import java.time.Clock
import java.time.Instant
/**
 * @author ceilfors
 */
class TimestampPrintWriterTest extends Specification {

    Clock clock
    StringWriter writer

    def setup() {
        clock = Mock(Clock)
        writer = new StringWriter()
    }

    @Unroll
    def "Should add the timestamp #result as a prefix to the printed String"(long milli, String result) {
        setup:
        clock.instant() >>> [
                Instant.ofEpochMilli(0),
                Instant.ofEpochMilli(milli)
        ]
        def printer = new TimestampPrintWriter(writer, clock)

        when:
        printer.println("foo")

        then:
        writer.toString() == "${result} foo\n".denormalize()

        where:
        milli  || result
        0      || " 0.0s"
        200    || " 0.2s"
        2000   || " 2.0s"
        3333   || " 3.3s"
        10000  || "10.0s"
        100000 || "100.0s"
    }

    def "Should not add timestamp to the new line passed in by the printed String"() {
        setup:
        clock.instant() >>> [
                Instant.ofEpochMilli(0)
        ]
        def printer = new TimestampPrintWriter(writer, clock)

        when:
        printer.println("foo\nbar\nboo")

        then:
        writer.toString() == " 0.0s foo\nbar\nboo\n".denormalize()
    }

    def "Should not add a timestamp if there is no newline"() {
        setup:
        clock.instant() >> Instant.ofEpochMilli(0)
        def printer = new TimestampPrintWriter(writer, clock)

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
