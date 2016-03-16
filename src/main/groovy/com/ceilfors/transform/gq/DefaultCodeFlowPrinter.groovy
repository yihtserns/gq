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

/**
 * @author ceilfors
 */
class DefaultCodeFlowPrinter implements CodeFlowPrinter {

    private PrintWriter out

    DefaultCodeFlowPrinter(PrintWriter writer) {
        this.out = writer
    }

    @Override
    void printMethodStart(MethodInfo methodInfo) {
        out.print("${methodInfo.name}")
        out.print('(')
        methodInfo.args.eachWithIndex { Object arg, i ->
            if (i != 0) {
                out.print(', ')
            }
            out.print(arg)
        }
        out.println(')')
    }

    @Override
    void printMethodEnd() {
    }

    @Override
    void printMethodEnd(Object result) {
        out.print('-> ')
        out.println(result)
    }

    @Override
    void printExpression(ExpressionInfo expressionInfo) {
        out.print("${expressionInfo.methodName}: ${expressionInfo.text.replace("\n", "")}=")
        out.print(expressionInfo.value)
        out.println()
    }

    @Override
    void print(String string) {
        out.print(string)
    }

    @Override
    void printException(ExceptionInfo exceptionInfo) {
        Throwable exception = exceptionInfo.exception
        String decoratedMethodName = 'decorated$' + exceptionInfo.methodName
        def trace = exception.stackTrace.find { it.methodName == decoratedMethodName }

        out.println("!> ${exception.class.simpleName}('${exception.message}') at ${trace.fileName}:${trace.lineNumber}")
    }
}