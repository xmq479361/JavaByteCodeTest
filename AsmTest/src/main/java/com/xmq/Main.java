package com.xmq;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        final String PATH = "D:\\workspace\\web\\JavaByteCodeTest\\AsmTest\\build\\classes\\java\\main\\com\\xmq\\";
        try {
            FileInputStream fis = new FileInputStream(PATH+"Person.class");
            ClassReader classReader = new ClassReader(fis);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            classReader.accept(new TestClassVisitor(Opcodes.ASM7, classWriter), ClassReader.EXPAND_FRAMES);
            byte[] bytes = classWriter.toByteArray();
            fis.close();
            FileOutputStream outputStream = new FileOutputStream(PATH+"Person.class");
//            FileOutputStream outputStream = new FileOutputStream(PATH+"Person1.class");
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Person().print(new Exception());
    }

    static class TestClassVisitor extends ClassVisitor {

        public TestClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//            name+="1";
//            className = name;
            System.out.println("visit: "+name+signature+"="+superName);
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public void visitSource(String source, String debug) {
            System.out.println("visitSource: "+source+"， "+debug);
            className = source;
            super.visitSource(source, debug);
        }

        String className;
        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            System.out.println("\tvisitMethod: "+name+descriptor);
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            return new PrintInvokeThreadMethodVisitor(api, mv, access, className, name, descriptor);
//            return new PrintMethodVisitor(api, mv, access, className, name, descriptor);
//            return mv;
        }
    }


    public static class PrintInvokeThreadMethodVisitor extends AdviceAdapter {

        /**
         * Constructs a new {@link AdviceAdapter}.
         *
         * @param api           the ASM API version implemented by this visitor. Must be one of {@link
         *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
         * @param methodVisitor the method visitor to which this adapter delegates calls.
         * @param access        the method's access flags (see {@link Opcodes}).
         * @param name          the method's name.
         * @param descriptor    the method's descriptor (see {@link Type Type}).
         */
        protected PrintInvokeThreadMethodVisitor(int api, MethodVisitor methodVisitor, int access, String className, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
            methodName = name;
            this.className = className;
        }

        int s, s2;
        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
//            invokeStatic(Type.getType(System.class), new Method("currentTimeMillis", "()J"));
////            invokeStatic(Type.getType("Ljava/lang/System;"), new Method("currentTimeMillis", "()J"));
//            s = newLocal(Type.LONG_TYPE);
//            storeLocal(s);
        }

        String className, methodName;
        int lineNo;
        @Override
        public void visitLineNumber(int line, Label start) {
            super.visitLineNumber(line, start);
            lineNo = line;
        }

        @Override
        public void visitLdcInsn(Object value) {
            System.out.println("\t\tvisitLdcInsn: "+value);
            super.visitLdcInsn(value);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            System.out.println("\t\tvisitTypeInsn: "+opcode+", "+type);
            if (delegateInvokes.containsKey(type)) {
                type = delegateInvokes.get(type);
            }
//            Opcodes.NEW;
            super.visitTypeInsn(opcode, type);
        }

        @Override
        public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
            System.out.println("\t\tvisitMethodInsn: "+owner+", "+name+", "+descriptor);
            if (owner.equals("com/xmq/Log")) {
                String paramValues = descriptor.substring(1, descriptor.length() - 3);
                String[] params = paramValues.split(";");
                int paramLen = params.length - 1;
                List<Integer> paramsTs = new LinkedList<>();
                int tmp = params.length ;
                while (tmp-- > 1) {
                    System.out.println(tmp+", params: "+params[tmp]);
                    int ts = newLocal(Type.getType(params[tmp]));
                    storeLocal(ts);
                    paramsTs.add(ts);
                }
                System.out.println(paramsTs+", params: "+params.length+" == "+paramValues);
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                mv.visitLdcInsn("("+className+":"+lineNo+") ");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                loadLocal(paramsTs.get(paramLen-1));
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                tmp = paramLen-2;
                while (tmp >= 0) {
                    loadLocal(paramsTs.get(tmp--));
                }
                System.out.println("\t\t\tvisitMethodInsn: "+owner);
            } else if (name.equals("<init>") && delegateInvokes.containsKey(owner)) {
                String paramsAppend = descriptor.substring(0, descriptor.length() - 2);
                paramsAppend = paramsAppend +"Ljava/lang/String;"+descriptor.substring(descriptor.length() - 2);
                descriptor = paramsAppend;
                mv.visitLdcInsn("("+className+":"+lineNo+") "+methodName+"()");
                owner = delegateInvokes.get(owner);
                System.err.println("\t\tThread Replace: "+owner+" == "+descriptor);
            }
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
        }
        static Map<String, String> delegateInvokes = new HashMap<>();
        static {
            delegateInvokes.put("java/lang/Thread", "com/xmq/thread/DelegateThread");
            delegateInvokes.put("java/util/concurrent/ThreadPoolExecutor", "com/xmq/thread/DelegateThreadPoolExecutor");
        }
        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            System.out.println("\t\tvisitMaxs: "+maxStack+", "+maxLocals);
            super.visitMaxs(maxStack, maxLocals);
        }

        @Override
        public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
            System.out.println("\t\tvisitFrame: "+type+", "+numLocal+", "+numStack+", "+ Arrays.toString(local)+", "+ Arrays.toString(stack));
            super.visitFrame(type, numLocal, local, numStack, stack);
        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);
            System.out.println("\tonMethodExit: "+opcode);
//            super.endMethod();
        }
    }



    public static class PrintMethodVisitor extends AdviceAdapter {

        /**
         * Constructs a new {@link AdviceAdapter}.
         *
         * @param api           the ASM API version implemented by this visitor. Must be one of {@link
         *                      Opcodes#ASM4}, {@link Opcodes#ASM5}, {@link Opcodes#ASM6} or {@link Opcodes#ASM7}.
         * @param methodVisitor the method visitor to which this adapter delegates calls.
         * @param access        the method's access flags (see {@link Opcodes}).
         * @param name          the method's name.
         * @param descriptor    the method's descriptor (see {@link Type Type}).
         */
        protected PrintMethodVisitor(int api, MethodVisitor methodVisitor, int access, String className, String name, String descriptor) {
            super(api, methodVisitor, access, name, descriptor);
            methodName = name;
            this.className = className;
        }

        int s, s2;
        @Override
        protected void onMethodEnter() {
            super.onMethodEnter();
//            invokeStatic(Type.getType(System.class), new Method("currentTimeMillis", "()J"));
////            invokeStatic(Type.getType("Ljava/lang/System;"), new Method("currentTimeMillis", "()J"));
//            s = newLocal(Type.LONG_TYPE);
//            storeLocal(s);
        }

        String className, methodName;
        int lineNo;
        @Override
        public void visitLineNumber(int line, Label start) {
            super.visitLineNumber(line, start);
            lineNo = line;
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            System.out.println("\t\tvisitFieldInsn: "+owner+", "+name);
//            if (owner.equals("java/lang/System") && name.equals("name")) {
//                name = "err";
//                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
//                mv.visitInsn(DUP);
//                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
//                mv.visitLdcInsn("Mills: ");
//                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
//                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
//                mv.visitVarInsn(LLOAD, 2);
//                mv.visitInsn(LSUB);
//                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
//
//                System.out.println("\t\tvisitFieldInsn: "+owner);
//            }
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }

        @Override
        public void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
            System.out.println("\t\tvisitMethodInsn: "+owner+", "+name+", "+descriptor);
            if (owner.equals("com/xmq/Log")) {
                String paramValues = descriptor.substring(1, descriptor.length() - 3);
                String[] params = paramValues.split(";");
                int paramLen = params.length - 1;
                List<Integer> paramsTs = new LinkedList<>();
                int tmp = params.length ;
                while (tmp-- > 1) {
                    System.out.println(tmp+", params: "+params[tmp]);
                    int ts = newLocal(Type.getType(params[tmp]));
                    storeLocal(ts);
                    paramsTs.add(ts);
                }
                System.out.println(paramsTs+", params: "+params.length+" == "+paramValues);
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                mv.visitLdcInsn("("+className+":"+lineNo+") ");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                loadLocal(paramsTs.get(paramLen-1));
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                tmp = paramLen-2;
                while (tmp >= 0) {
                    loadLocal(paramsTs.get(tmp--));
                }
                System.out.println("\t\t\tvisitMethodInsn: "+owner);
            }
            super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            System.out.println("\t\tvisitMaxs: "+maxStack+", "+maxLocals);
            super.visitMaxs(maxStack, maxLocals);
        }

        @Override
        public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
            System.out.println("\t\tvisitFrame: "+type+", "+numLocal+", "+numStack+", "+ Arrays.toString(local)+", "+ Arrays.toString(stack));
            super.visitFrame(type, numLocal, local, numStack, stack);
        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode);
            System.out.println("\tonMethodExit: "+opcode);
            super.endMethod();
//            invokeStatic(Type.getType("Ljava/lang/System;"), new Method("currentTimeMillis", "()J"));
//            invokeStatic(Type.getType(System.class), new Method("currentTimeMillis", "()J"));
//            s2 = newLocal(Type.LONG_TYPE);
//            storeLocal(s2);
//
//                /*
//                *  GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
//                    LLOAD 3
//                    LLOAD 1
//                    LSUB
//                    INVOKEVIRTUAL java/io/PrintStream.println (J)V
//                **/
////            getStatic(Type.getType(System.class), "out", Type.getType("Ljava/io/PrintStream;"));
//            getStatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
////            getStatic(Type.getType("Ljava/lang/System;"), "out", Type.getType("Ljava/io/PrintStream;"));
//
//            loadLocal(s);
//            loadLocal(s2);
//            math(SUB, Type.LONG_TYPE);
//            invokeVirtual(Type.getType(PrintStream.class), new Method("println", "(J)V"));
        }
    }
}
