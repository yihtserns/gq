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
enum SingletonCodeFlowManager implements CodeFlowListener {

    INSTANCE;

    private CodeFlowPrinter codeFlowPrinter
    private FileWriter fileWriter

    private File createGqFile() {
        // By default using "/tmp" instead of using java.io.tmpdir for better user usability
        return new File(System.getProperty("GQTMP", "/tmp"), "gq")
    }

    def setGqFile(File file, boolean timestamp) {
        fileWriter?.close()
        fileWriter = new FileWriter(file, true)

        codeFlowPrinter = new IndentingCodeFlowPrinter(new DefaultCodeFlowPrinter(new PrintWriter(fileWriter)))

        if (timestamp) {
            codeFlowPrinter = new TimestampCodeFlowPrinter(codeFlowPrinter, System.&currentTimeMillis)
        }
    }

    {
        setGqFile(createGqFile(), true)
        addShutdownHook {
            fileWriter.close()
        }
    }

    @Override
    void methodStarted(MethodInfo methodInfo) {
        codeFlowPrinter.printMethodStart(methodInfo)
    }

    @Override
    void methodEnded(Object result) {
        codeFlowPrinter.printMethodEnd(result)
    }

    @Override
    void methodEnded() {
        codeFlowPrinter.printMethodEnd()
    }

    @Override
    void exceptionThrown(ExceptionInfo exceptionInfo) {
        codeFlowPrinter.printException(exceptionInfo)
    }

    @Override
    Object expressionProcessed(String methodName, ExpressionInfo... expressionInfos) {
        codeFlowPrinter.printExpression(expressionInfos[0])
        return expressionInfos[0].value
    }
}
