/*
 * Copyright 2008 Kasper Nielsen.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://cake.codehaus.org/LICENSE
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.codehaus.cake.ops;

import static org.codehaus.cake.test.util.TestUtil.assertIsSerializable;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.codehaus.cake.ops.Ops.Procedure;
import org.codehaus.cake.test.util.SystemOutCatcher;
import org.codehaus.cake.test.util.TestUtil;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class ProceduresTest {

    @Test
    public void noop() {
        Procedure<Integer> p = Procedures.ignore();
        p.op(null);
        p.op(1);
        p.toString(); // does not fail
        assertIsSerializable(Procedures.IGNORE_PROCEDURE);
        assertSame(Procedures.IGNORE_PROCEDURE, Procedures.ignore());
        assertSame(p, TestUtil.serializeAndUnserialize(p));
    }

    @Test
    public void systemOutPrint() {
        SystemOutCatcher str = SystemOutCatcher.get();
        try {
            Procedure eh = Procedures.systemOutPrint();
            eh.op(234);
            assertTrue(str.toString().equals("234"));
        } finally {
            str.terminate();
        }
        assertIsSerializable(Procedures.SYS_OUT_PRINT_PROCEDURE);
        assertSame(Procedures.SYS_OUT_PRINT_PROCEDURE, Procedures.systemOutPrint());
        assertSame(Procedures.SYS_OUT_PRINT_PROCEDURE, TestUtil.serializeAndUnserialize(Procedures.systemOutPrint()));
    }

    @Test
    public void systemOutPrintln() {
        SystemOutCatcher str = SystemOutCatcher.get();
        try {
            Procedure eh = Procedures.systemOutPrintln();
            eh.op(234);
            assertTrue(str.toString().equals("234" + TestUtil.LINE_SEPARATOR));
        } finally {
            str.terminate();
        }
        assertIsSerializable(Procedures.SYS_OUT_PRINTLN_PROCEDURE);
        assertSame(Procedures.SYS_OUT_PRINTLN_PROCEDURE, Procedures.systemOutPrintln());
        assertSame(Procedures.SYS_OUT_PRINTLN_PROCEDURE, TestUtil
                .serializeAndUnserialize(Procedures.systemOutPrintln()));
    }

}
