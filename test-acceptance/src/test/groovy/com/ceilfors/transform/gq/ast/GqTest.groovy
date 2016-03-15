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

package com.ceilfors.transform.gq.ast

import groovy.transform.NotYetImplemented

import static com.ceilfors.groovy.spock.FileComparisonHelper.fileContentEquals

class GqTest extends BaseSpecification {

    def "Should write the name of a method with empty parameter"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @Gq
            int "return 5"() {
                5
            }
        """))

        when:
        def result = instance."return 5"()

        then:
        result == 5
        gqFile.readLines().first().contains("return 5()")
    }

    def "Should write the returned value of a method call"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @Gq
            int "return 5"() {
                5
            }
        """))

        when:
        def result = instance."return 5"()

        then:
        result == 5
        gqFile.readLines().last().contains("-> 5")
    }

    def "Should write the arguments of a method call"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @Gq
            int add(int x, int y) {
                return x + y
            }
        """))

        when:
        def result = instance.add(3, 3)

        then:
        result == 6
        gqFile.readLines().first().contains("add(3, 3)")
    }

    def "Should be able to write a method when its return type is void"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @Gq
            void "return void"() {}
        """))

        when:
        instance."return void"()

        then:
        fileContentEquals gqFile, "return void()\n"
    }

    def "Should write nested method call with indentation"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @Gq int nested()          { nested2() + 5 }
                private int nested2() { nested3() + 5 }
            @Gq private int nested3() { 5 }
        """))

        when:
        def result = instance.nested()

        then:
        result == 15
        fileContentEquals gqFile,
                """nested()
                  |  nested3()
                  |  -> 5
                  |-> 15
                  |""".stripMargin()
    }

    def "Should write exception details if an exception is thrown"() {
        setup:
        def example = newExample(GqExample)

        when:
        example.throwException()

        then:
        RuntimeException e = thrown(RuntimeException)
        e.message == "Hello!"
        fileContentEquals gqFile,
                """throwException()
                  |!> RuntimeException('Hello!') at GqExample.groovy:26
                  |""".stripMargin()
    }

    def "Should write exception details if an exception is thrown from a nested method"() {
        setup:
        def example = newExample(GqExample)

        when:
        example.nestedThrowException1()

        then:
        RuntimeException e = thrown(RuntimeException)
        e.message == "Hello!"
        fileContentEquals gqFile,
                """nestedThrowException1()
                  |  nestedThrowException2()
                  |    nestedThrowException3()
                  |    !> RuntimeException('Hello!') at GqExample.groovy:43
                  |  !> RuntimeException('Hello!') at GqExample.groovy:37
                  |!> RuntimeException('Hello!') at GqExample.groovy:31
                  |""".stripMargin()
    }

    def "Should restore indentation when an exception is thrown"() {
        setup:
        def example = newExample(GqExample)

        when:
        example.throwException()

        then:
        thrown(RuntimeException)
        gqFile.delete()

        when:
        example.throwException()

        then:
        thrown(RuntimeException)
        fileContentEquals gqFile,
                """throwException()
                  |!> RuntimeException('Hello!') at GqExample.groovy:26
                  |""".stripMargin()
    }

    def "Should be able to be used in standalone Groovy script"() {
        setup:
        def instance = toInstance(insertPackageAndImport("""
            @Gq
            def simplyReturn(arg) { arg }

            simplyReturn(5)
        """))

        when:
        instance.main()

        then:
        fileContentEquals gqFile,
                """simplyReturn(5)
                  |-> 5
                  |""".stripMargin()
    }

    @NotYetImplemented
    def "Should save long expression value to a separated file"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @Gq
            int simplyReturn(arg) {
                return arg
            }
        """))

        when:
        def result = instance.simplyReturn("0" * 100)

        then:
        def lines = gqFile.text.readLines()
        def methodLine = lines[0] =~ "simplyReturn('${"0" * 30}\\.\\.\\.${"0" * 30 }' (file://(.*)))"
        methodLine.matches()
        new File(methodLine.group(3)).text == "0" * 100
        def returnLine = lines[1] =~ "-> '${"0" * 30}\\.\\.\\.${"0" * 30 }' (file://(.*))"
        returnLine.matches()
        new File(returnLine.group(2)).text == "0" * 100
    }

    def "Should be able to use CALL operation"() {
        setup:
        def instance = toInstance(insertPackageAndImport("""
            def val = 1

            assert Gq(val + 2) == 3
        """))

        when:
        instance.main()

        then:
        fileContentEquals gqFile, "run: val + 2=3\n"
    }

    def "Should be able to use OR operator"() {
        setup:
        def instance = toInstance(insertPackageAndImport("""
            def val = 1

            assert (Gq|val + 2) == 3
        """))

        when:
        instance.main()

        then:
        fileContentEquals gqFile, "run: val + 2=3\n"
    }

    def "Should be able to use DIV operator"() {
        setup:
        def instance = toInstance(insertPackageAndImport("""
            def val = 1

            assert Gq/val + 2 == 3
        """))

        when:
        instance.main()

        then:
        fileContentEquals gqFile, "run: val=1\n"
    }
}
