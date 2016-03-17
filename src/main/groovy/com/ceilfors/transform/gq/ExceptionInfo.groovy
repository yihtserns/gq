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
class ExceptionInfo implements Printable {

    String methodName
    Throwable exception

    ExceptionInfo(String methodName, Throwable exception) {
        this.methodName = methodName
        this.exception = exception
    }

    @Override
    void printTo(CodeFlowPrinter printer) {
        String decoratedMethodName = 'decorated$' + methodName
        def trace = exception.stackTrace.find { it.methodName == decoratedMethodName }

        printer.print("!> ${exception.class.simpleName}('${exception.message}') at ${trace.fileName}:${trace.lineNumber}")
    }
}
