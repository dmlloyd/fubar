/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xnio.http.tool;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class Generator {

    private static final int CH = 1;
    private static final int BYTE_BUFFER = 2;
    private static final int STRING_BUILDER = 3;

    private static final String READ_LISTENER = "org/xnio/http/server/HttpReadListener";

    public static void main(String[] args) {
        MethodVisitor mv = null;
        mv.visitCode();
        final Label unknownMethod = new Label();
        final Label readMethod = new Label();
        final Label readUri = new Label();
        final Label readUriSubsequent = new Label();
        final Label readProto = new Label();
        final Label readProtoSubsequent = new Label();
        final Label readHeaders = new Label();
        final Label matchC = new Label();
        final Label matchCO = new Label();
        final Label matchCON = new Label();
        final Label matchCONN = new Label();
        final Label matchCONNE = new Label();
        final Label matchCONNEC = new Label();
        final Label matchCONNECT = new Label();
        final Label matchD = new Label();
        final Label matchDE = new Label();
        final Label matchDEL = new Label();
        final Label matchDELE = new Label();
        final Label matchDELET = new Label();
        final Label matchDELETE = new Label();
        final Label matchG = new Label();
        final Label matchGE = new Label();
        final Label matchGET = new Label();
        final Label matchH = new Label();
        final Label matchHE = new Label();
        final Label matchHEA = new Label();
        final Label matchHEAD = new Label();
        final Label matchO = new Label();
        final Label matchOP = new Label();
        final Label matchOPT = new Label();
        final Label matchOPTI = new Label();
        final Label matchOPTIO = new Label();
        final Label matchOPTION = new Label();
        final Label matchOPTIONS = new Label();
        final Label matchP = new Label();
        final Label matchPO = new Label();
        final Label matchPOS = new Label();
        final Label matchPOST = new Label();
        final Label matchPU = new Label();
        final Label matchPUT = new Label();
        final Label matchT = new Label();
        final Label matchTR = new Label();
        final Label matchTRA = new Label();
        final Label matchTRAC = new Label();
        final Label matchTRACE = new Label();
        final Label doReturn = new Label();
        final Label badState = new Label();

        final Label badRequest = new Label();

        mv.visitFieldInsn(GETFIELD, READ_LISTENER, "state", "I");
        final Label[] states = {
                readMethod,
                matchC,
                matchCO,
                matchCON,
                matchCONN,
                matchCONNE,
                matchCONNEC,
                matchCONNECT,
                matchD,
                matchDE,
                matchDEL,
                matchDELE,
                matchDELET,
                matchDELETE,
                matchG,
                matchGE,
                matchGET,
                matchH,
                matchHE,
                matchHEA,
                matchHEAD,
                matchO,
                matchOP,
                matchOPT,
                matchOPTI,
                matchOPTIO,
                matchOPTION,
                matchOPTIONS,
                matchP,
                matchPO,
                matchPOS,
                matchPOST,
                matchPU,
                matchPUT,
                matchT,
                matchTR,
                matchTRA,
                matchTRAC,
                matchTRACE,
                unknownMethod,
                readUri,
                readUriSubsequent,
                readProto,
                readProtoSubsequent,
                readHeaders,
        };
        mv.visitTableSwitchInsn(0, states.length - 1, badState, states);

        int c = 0;

        // Read an HTTP method
        mv.visitLabel(readMethod);
        visitRead(mv);
        final Label exitReadMethod = new Label();
        mv.visitLookupSwitchInsn(unknownMethod,
                new int[]   { -1,  'C', 'D', 'G', 'H', 'O', 'P', 'T' },
                new Label[] { exitReadMethod, matchC, matchD, matchG, matchH, matchO, matchP, matchT });

        mv.visitLabel(exitReadMethod);
        mv.visitIntInsn(BIPUSH, c++);
        mv.visitLabel(doReturn);
        mv.visitFieldInsn(PUTFIELD, READ_LISTENER, "state", "I");
        mv.visitIntInsn(BIPUSH, 1);
        mv.visitInsn(IRETURN);
        
        mv.visitLabel(badRequest);
        mv.visitIntInsn(BIPUSH, 0);
        mv.visitInsn(IRETURN);

        // match CONNECT
        doFirstStep(mv, c++, 'C', 'O', readUri, doReturn, unknownMethod, matchC, matchCO, badRequest);
        doSimpleStep(mv, c++, "CO", 'N', readUri, doReturn, unknownMethod, matchCO, matchCON, badRequest);
        doSimpleStep(mv, c++, "CON", 'N', readUri, doReturn, unknownMethod, matchCON, matchCONN, badRequest);
        doSimpleStep(mv, c++, "CONN", 'E', readUri, doReturn, unknownMethod, matchCONN, matchCONNE, badRequest);
        doSimpleStep(mv, c++, "CONNE", 'C', readUri, doReturn, unknownMethod, matchCONNE, matchCONNEC, badRequest);
        doSimpleStep(mv, c++, "CONNEC", 'T', readUri, doReturn, unknownMethod, matchCONNEC, matchCONNECT, badRequest);
        doFinalStep(mv, c++, "CONNECT", readUri, doReturn, unknownMethod, matchCONNECT, badRequest);

        // match DELETE
        doFirstStep(mv, c++, 'D', 'E', readUri, doReturn, unknownMethod, matchD, matchDE, badRequest);
        doSimpleStep(mv, c++, "DE", 'L', readUri, doReturn, unknownMethod, matchDE, matchDEL, badRequest);
        doSimpleStep(mv, c++, "DEL", 'E', readUri, doReturn, unknownMethod, matchDEL, matchDELE, badRequest);
        doSimpleStep(mv, c++, "DELE", 'T', readUri, doReturn, unknownMethod, matchDELE, matchDELET, badRequest);
        doSimpleStep(mv, c++, "DELET", 'E', readUri, doReturn, unknownMethod, matchDELET, matchDELETE, badRequest);
        doFinalStep(mv, c++, "DELETE", readUri, doReturn, unknownMethod, matchDELETE, badRequest);

        // match GET
        doFirstStep(mv, c++, 'G', 'E', readUri, doReturn, unknownMethod, matchG, matchGE, badRequest);
        doSimpleStep(mv, c++, "GE", 'T', readUri, doReturn, unknownMethod, matchGE, matchGET, badRequest);
        doFinalStep(mv, c++, "GET", readUri, doReturn, unknownMethod, matchGET, badRequest);

        // match HEAD
        doFirstStep(mv, c++, 'H', 'E', readUri, doReturn, unknownMethod, matchH, matchHE, badRequest);
        doSimpleStep(mv, c++, "HE", 'A', readUri, doReturn, unknownMethod, matchHE, matchHEA, badRequest);
        doSimpleStep(mv, c++, "HEA", 'D', readUri, doReturn, unknownMethod, matchHEA, matchHEAD, badRequest);
        doFinalStep(mv, c++, "HEAD", readUri, doReturn, unknownMethod, matchHEAD, badRequest);

        // match OPTIONS
        doFirstStep(mv, c++, 'O', 'P', readUri, doReturn, unknownMethod, matchO, matchOP, badRequest);
        doSimpleStep(mv, c++, "OP", 'T', readUri, doReturn, unknownMethod, matchOP, matchOPT, badRequest);
        doSimpleStep(mv, c++, "OPT", 'I', readUri, doReturn, unknownMethod, matchOPT, matchOPTI, badRequest);
        doSimpleStep(mv, c++, "OPTI", 'O', readUri, doReturn, unknownMethod, matchOPTI, matchOPTIO, badRequest);
        doSimpleStep(mv, c++, "OPTIO", 'N', readUri, doReturn, unknownMethod, matchOPTIO, matchOPTION, badRequest);
        doSimpleStep(mv, c++, "OPTION", 'S', readUri, doReturn, unknownMethod, matchOPTION, matchOPTIONS, badRequest);
        doFinalStep(mv, c++, "OPTIONS", readUri, doReturn, unknownMethod, matchOPTIONS, badRequest);

        // match POST or PUT
        doTwoStep(mv, c++, 'P', 'O', 'U', readUri, doReturn, unknownMethod, matchP, matchPO, matchPU, badRequest);

        // match POST
        doSimpleStep(mv, c++, "PO", 'S', readUri, doReturn, unknownMethod, matchPO, matchPOS, badRequest);
        doSimpleStep(mv, c++, "POS", 'T', readUri, doReturn, unknownMethod, matchPOS, matchPOST, badRequest);
        doFinalStep(mv, c++, "POST", readUri, doReturn, unknownMethod, matchPOST, badRequest);

        // match PUT
        doSimpleStep(mv, c++, "PU", 'T', readUri, doReturn, unknownMethod, matchPU, matchPUT, badRequest);
        doFinalStep(mv, c++, "PUT", readUri, doReturn, unknownMethod, matchPUT, badRequest);

        // match TRACE
        doFirstStep(mv, c++, 'T', 'R', readUri, doReturn, unknownMethod, matchT, matchTR, badRequest);
        doSimpleStep(mv, c++, "TR", 'A', readUri, doReturn, unknownMethod, matchTR, matchTRA, badRequest);
        doSimpleStep(mv, c++, "TRA", 'C', readUri, doReturn, unknownMethod, matchTRA, matchTRAC, badRequest);
        doSimpleStep(mv, c++, "TRAC", 'E', readUri, doReturn, unknownMethod, matchTRAC, matchTRACE, badRequest);
        doFinalStep(mv, c++, "TRACE", readUri, doReturn, unknownMethod, matchTRACE, badRequest);

        // build method from string using string builder
        mv.visitLabel(unknownMethod);
        visitRead(mv);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "isControl", "(C)Z");
        mv.visitJumpInsn(IFNE, badRequest);
        final Label doAppendMethod = new Label();
        final Label exitReadMethodBuild = new Label();
        final Label prepareReadUri = new Label();
        mv.visitLookupSwitchInsn(doAppendMethod,
                new int[]   { -1, ' ' },
                new Label[] { exitReadMethodBuild, prepareReadUri });

        mv.visitLabel(doAppendMethod);
        mv.visitVarInsn(ALOAD, STRING_BUILDER);
        mv.visitVarInsn(ILOAD, CH);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;");
        mv.visitJumpInsn(GOTO, unknownMethod);

        mv.visitLabel(exitReadMethodBuild); // ran out of buffer
        mv.visitIntInsn(BIPUSH, c++);
        mv.visitJumpInsn(GOTO, doReturn);

        mv.visitLabel(prepareReadUri); // finished reading method, clear builder and move on to URI
        mv.visitVarInsn(ALOAD, STRING_BUILDER);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
        mv.visitFieldInsn(PUTFIELD, READ_LISTENER, "method", "Ljava/lang/String;");
        mv.visitIntInsn(BIPUSH, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "setLength", "(I)V");
        //mv.visitJumpInsn(GOTO, readUri); // fall through

        // read URI
        mv.visitLabel(readUri);
        visitRead(mv);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "isControl", "(C)Z");
        mv.visitJumpInsn(IFNE, badRequest);
        final Label doAppendUri = new Label();
        final Label exitReadUri = new Label();
        mv.visitLookupSwitchInsn(doAppendUri,
                new int[]   { -1,          ' '     },
                new Label[] { exitReadUri, readUri });

        mv.visitLabel(exitReadUri);
        mv.visitIntInsn(BIPUSH, c++);
        mv.visitJumpInsn(GOTO, doReturn);

        mv.visitLabel(doAppendUri);
        mv.visitVarInsn(ALOAD, STRING_BUILDER);
        mv.visitVarInsn(ILOAD, CH);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;");
        //mv.visitJumpInsn(GOTO, readUriSubsequent); // fall through

        mv.visitLabel(readUriSubsequent);
        visitRead(mv);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "isControl", "(C)Z");
        mv.visitJumpInsn(IFNE, badRequest);
        final Label exitReadUriSubsequent = new Label();
        final Label prepareReadProtocol = new Label();
        mv.visitLookupSwitchInsn(doAppendUri,
                new int[]   { -1,                    ' ', },
                new Label[] { exitReadUriSubsequent, prepareReadProtocol });

        mv.visitLabel(exitReadUriSubsequent);
        mv.visitIntInsn(BIPUSH, c++);
        mv.visitJumpInsn(GOTO, doReturn);

        mv.visitLabel(prepareReadProtocol);
        mv.visitVarInsn(ALOAD, STRING_BUILDER);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
        mv.visitFieldInsn(PUTFIELD, READ_LISTENER, "requestUri", "Ljava/lang/String;");
        mv.visitIntInsn(BIPUSH, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "setLength", "(I)V");
        //mv.visitJumpInsn(GOTO, readProto); // fall through

        mv.visitLabel(readProto);
        visitRead(mv);
        final Label doAppendProto = new Label();
        final Label exitReadProto = new Label();
        mv.visitLookupSwitchInsn(doAppendProto,
                new int[]   { -1,            ' ' },
                new Label[] { exitReadProto, readProto });

        mv.visitLabel(exitReadProto);
        mv.visitIntInsn(BIPUSH, c++);
        mv.visitJumpInsn(GOTO, doReturn);

        mv.visitLabel(doAppendProto);
        mv.visitVarInsn(ILOAD, CH);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "isControl", "(C)Z");
        mv.visitJumpInsn(IFNE, badRequest);
        mv.visitVarInsn(ALOAD, STRING_BUILDER);
        mv.visitVarInsn(ILOAD, CH);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;");
        //mv.visitJumpInsn(GOTO, readProtoSubsequent); // fall through

        mv.visitLabel(readProtoSubsequent);
        visitRead(mv);
        final Label exitReadProtoSubsequent = new Label();
        final Label prepareReadHeaders = new Label();
        mv.visitLookupSwitchInsn(doAppendProto,
                new int[]   { -1,                      '\r',               ' ' },
                new Label[] { exitReadProtoSubsequent, prepareReadHeaders, badRequest });

        mv.visitLabel(exitReadProtoSubsequent);
        mv.visitIntInsn(BIPUSH, c++);
        mv.visitJumpInsn(GOTO, doReturn);

        mv.visitLabel(prepareReadHeaders);

        // done!
        mv.visitMaxs();
    }

    private static void doFirstStep(final MethodVisitor mv, final int stateId, final char matchedSoFar, final char nextChar, final Label readUri, final Label doReturn, final Label unknownMethod, final Label ourLabel, final Label nextLabel, final Label badRequest) {
        final Label isUnknown = new Label();
        final Label wholeMatched = new Label();
        final Label exit = new Label();
        mv.visitLabel(ourLabel);
        visitRead(mv);
        mv.visitLookupSwitchInsn(isUnknown,
                new int[]   { -1,   nextChar,  ' '          },
                new Label[] { exit, nextLabel, wholeMatched });

        mv.visitLabel(exit);
        mv.visitIntInsn(BIPUSH, stateId);
        mv.visitJumpInsn(GOTO, doReturn);

        mv.visitLabel(isUnknown);
        mv.visitVarInsn(ILOAD, CH);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "isControl", "(C)Z");
        mv.visitJumpInsn(IFNE, badRequest);
        mv.visitFieldInsn(GETFIELD, READ_LISTENER, "stringBuilder", "Ljava/lang/StringBuilder;");
        mv.visitIntInsn(BIPUSH, matchedSoFar);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;");
        mv.visitVarInsn(ILOAD, CH);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;");
        mv.visitJumpInsn(GOTO, unknownMethod);

        mv.visitLabel(wholeMatched);
        mv.visitLdcInsn(matchedSoFar);
        mv.visitFieldInsn(PUTFIELD, READ_LISTENER, "method", "Ljava/lang/String;");
        mv.visitJumpInsn(GOTO, readUri);
    }

    private static void doTwoStep(final MethodVisitor mv, final int stateId, final char matchedSoFar, final char nextChar1, final char nextChar2, final Label readUri, final Label doReturn, final Label unknownMethod, final Label ourLabel, final Label nextLabel1, final Label nextLabel2, final Label badRequest) {
        final Label isUnknown = new Label();
        final Label wholeMatched = new Label();
        final Label exit = new Label();
        mv.visitLabel(ourLabel);
        visitRead(mv);
        mv.visitLookupSwitchInsn(isUnknown,
                new int[]   { -1,   nextChar1,  nextChar2,  ' '          },
                new Label[] { exit, nextLabel1, nextLabel2, wholeMatched });

        mv.visitLabel(exit);
        mv.visitIntInsn(BIPUSH, stateId);
        mv.visitJumpInsn(GOTO, doReturn);

        mv.visitLabel(isUnknown);
        mv.visitVarInsn(ILOAD, CH);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "isControl", "(C)Z");
        mv.visitJumpInsn(IFNE, badRequest);
        mv.visitFieldInsn(GETFIELD, READ_LISTENER, "stringBuilder", "Ljava/lang/StringBuilder;");
        mv.visitIntInsn(BIPUSH, matchedSoFar);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;");
        mv.visitVarInsn(ILOAD, CH);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;");
        mv.visitJumpInsn(GOTO, unknownMethod);

        mv.visitLabel(wholeMatched);
        mv.visitLdcInsn(matchedSoFar);
        mv.visitFieldInsn(PUTFIELD, READ_LISTENER, "method", "Ljava/lang/String;");
        mv.visitJumpInsn(GOTO, readUri);
    }

    private static void doSimpleStep(final MethodVisitor mv, final int stateId, final String matchedSoFar, final char nextChar, final Label readUri, final Label doReturn, final Label unknownMethod, final Label ourLabel, final Label nextLabel, final Label badRequest) {
        final Label isUnknown = new Label();
        final Label wholeMatched = new Label();
        final Label exit = new Label();
        mv.visitLabel(ourLabel);
        visitRead(mv);
        mv.visitLookupSwitchInsn(isUnknown,
                new int[]   { -1,   nextChar,  ' '          },
                new Label[] { exit, nextLabel, wholeMatched });

        mv.visitLabel(exit);
        mv.visitIntInsn(BIPUSH, stateId);
        mv.visitJumpInsn(GOTO, doReturn);

        mv.visitLabel(isUnknown);
        mv.visitVarInsn(ILOAD, CH);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "isControl", "(C)Z");
        mv.visitJumpInsn(IFNE, badRequest);
        mv.visitFieldInsn(GETFIELD, READ_LISTENER, "stringBuilder", "Ljava/lang/StringBuilder;");
        mv.visitLdcInsn(matchedSoFar);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        mv.visitVarInsn(ILOAD, CH);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;");
        mv.visitJumpInsn(GOTO, unknownMethod);

        mv.visitLabel(wholeMatched);
        mv.visitLdcInsn(matchedSoFar);
        mv.visitFieldInsn(PUTFIELD, READ_LISTENER, "method", "Ljava/lang/String;");
        mv.visitJumpInsn(GOTO, readUri);
    }

    private static void doFinalStep(final MethodVisitor mv, final int stateId, final String matchedSoFar, final Label readUri, final Label doReturn, final Label unknownMethod, final Label ourLabel, final Label badRequest) {
        final Label isUnknown = new Label();
        final Label wholeMatched = new Label();
        final Label exit = new Label();
        mv.visitLabel(ourLabel);
        visitRead(mv);
        mv.visitLookupSwitchInsn(isUnknown,
                new int[]   { -1,   ' '          },
                new Label[] { exit, wholeMatched });

        mv.visitLabel(exit);
        mv.visitIntInsn(BIPUSH, stateId);
        mv.visitJumpInsn(GOTO, doReturn);

        mv.visitLabel(isUnknown);
        mv.visitVarInsn(ILOAD, CH);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "isControl", "(C)Z");
        mv.visitJumpInsn(IFNE, badRequest);
        mv.visitFieldInsn(GETFIELD, READ_LISTENER, "stringBuilder", "Ljava/lang/StringBuilder;");
        mv.visitFieldInsn(GETSTATIC, "org/xnio/http/util/Methods", matchedSoFar, "Ljava/lang/String;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        mv.visitVarInsn(ILOAD, CH);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;");
        mv.visitJumpInsn(GOTO, unknownMethod);

        mv.visitLabel(wholeMatched);
        mv.visitFieldInsn(GETSTATIC, "org/xnio/http/util/Methods", matchedSoFar, "Ljava/lang/String;");
        mv.visitFieldInsn(PUTFIELD, READ_LISTENER, "method", "Ljava/lang/String;");
        mv.visitJumpInsn(GOTO, readUri);
    }

    /**
     * Read character from buffer, store it in CH and on stack
     *
     * @param mv
     */
    private static void visitRead(final MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, BYTE_BUFFER);
        mv.visitMethodInsn(INVOKESTATIC, READ_LISTENER, "read", "(java/nio/ByteBuffer)I");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ISTORE, CH);
    }
}
