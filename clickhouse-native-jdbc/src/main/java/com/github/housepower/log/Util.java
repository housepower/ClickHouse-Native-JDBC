/*
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
 *
 * ----------------------------------------------------------------------
 *
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.github.housepower.log;

/**
 * An internal utility class.
 *
 * @author Alexander Dorokhine
 * @author Ceki G&uuml;lc&uuml;
 */
public final class Util {

    private Util() {
    }

    public static String safeGetSystemProperty(String key) {
        if (key == null)
            throw new IllegalArgumentException("null input");

        String result = null;
        try {
            result = System.getProperty(key);
        } catch (SecurityException sm) {
            // ignore
        }
        return result;
    }

    public static boolean safeGetBooleanSystemProperty(String key) {
        String value = safeGetSystemProperty(key);
        if (value == null)
            return false;
        else
            return value.equalsIgnoreCase("true");
    }

    /**
     * Returns the name of the class which called the invoking method.
     * Uses StackWalker API (Java 9+) instead of deprecated SecurityManager.
     *
     * @return the name of the class which called the invoking method.
     */
    public static Class<?> getCallingClass() {
        try {
            StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
            return walker.walk(stream -> {
                String thisClassName = Util.class.getName();
                return stream
                    .map(StackWalker.StackFrame::getDeclaringClass)
                    .dropWhile(clazz -> !thisClassName.equals(clazz.getName()))
                    .skip(2) // Skip Util class and the immediate caller
                    .findFirst()
                    .orElse(null);
            });
        } catch (Exception e) {
            // Fallback to null if StackWalker fails
            return null;
        }
    }

    static public void report(String msg, Throwable t) {
        System.err.println(msg);
        System.err.println("Reported exception:");
        t.printStackTrace();
    }

    static public void report(String msg) {
        System.err.println("LOG: " + msg);
    }
}
