package com.ceilfors.transform.gq.ast

class GqTest extends BaseSpecification {

    def "Should write the name of a method with empty parameter"() {
        setup:
        def example = newExample(GqExample)

        when:
        def result = example."return 5"()

        then:
        result == 5
        gqFile.file.readLines().first().contains("return 5()")
    }

    def "Should write the arguments of a method call"() {
        setup:
        def example = newExample(GqExample)

        when:
        def result = example.add(3, 3)

        then:
        result == 6
        gqFile.file.readLines().first().contains("add(3, 3)")
    }

    def "Should be able to write a method when its return type is void"() {
        setup:
        def example = newExample(GqExample)

        when:
        example."return void"()

        then:
        gqFile.file.text == ("return void()\n")
    }


    def "Should write the returned value of a method call"() {
        setup:
        def example = newExample(GqExample)

        when:
        def result = example."return 5"()

        then:
        result == 5
        gqFile.file.readLines().last().contains("-> 5")
    }

    def "Should write nested method call with indentation"() {
        setup:
        def example = newExample(GqExample)

        when:
        def result = example.nested1()

        then:
        result == 15
        gqFile.file.text ==
                """nested1()
                  |  nested3()
                  |  -> 5
                  |-> 15
                  |""".stripMargin()
    }

    // --- Technical debt
    // Rename GqTransformation to GqASTTransformation to follow standard
    // Remove ast package as it's a useless layer.

    // --- Feature
    // GqSupport must print the context it's in e.g. *method_name: *expression=result
    // Support @Gq and GqSupport for groovy scripts e.g. not encapsulated in class
    // Indentation must work even if nested method have exception
    // GqSupport.gq to support void return type
    // @Gq(vars=true) to print all variable expression
    // GqSupport must support multiple arguments e.g. gc(3+5, 10+10, 15+15)
    // GqSupport must support multiline text e.g. gc(3+\n\n5) -> Trim the new line. See spock's SourceLookup?
    // q.d
}
