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
        printPrintable(methodInfo)
    }

    @Override
    void printMethodEnd() {
    }

    @Override
    void printMethodEnd(Object result) {
        printPrintable({ printer ->
            printer.print('-> ')
            printer.printValue(result)
        })
    }

    @Override
    void printExpression(ExpressionInfo expressionInfo) {
        printPrintable(expressionInfo)
    }

    @Override
    void printException(ExceptionInfo exceptionInfo) {
        printPrintable(exceptionInfo)
    }

    @Override
    void print(String string) {
        out.print(string)
    }

    @Override
    void printValue(Object value) {
        if (value instanceof String) {
            value = "'$value'"
        }
        out.print(value)
    }

    private void printPrintable(Printable printable) {
        printable.printTo(this)
        out.println()
        out.flush()
    }
}
