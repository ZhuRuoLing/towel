package icu.takeneko.towel.coremod.transformers;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class MinecraftServerTransformer implements ClassNodeTransformer {

    public static final String TARGET = "net.minecraft.server.MinecraftServer";
    public static final String MINECRAFT_SERVER = TARGET.replace(".", "/");
    public static final String TICK_SPEED = "icu/takeneko/towel/helpers/TickSpeed";
    public static final String TICK_BRIDGE = "bridge$tick";
    public static final String TICK_SIGNATURE = "()V";

    @Override
    public boolean accept(ClassNode node, String name) {
        if (!name.equals(TARGET)) {
            return false;
        }

        for (MethodNode method : node.methods) {
            if (method.name.equals("run")) {
                patchRun(method);
            }
        }

        return true;
    }

    public void patchRun(MethodNode node) {
        ListIterator<AbstractInsnNode> it = node.instructions.iterator();
        while (it.hasNext()) {
            AbstractInsnNode insnNode = it.next();
            if (insnNode instanceof FieldInsnNode fi && insnNode.getOpcode() == Opcodes.GETFIELD) {
                if (fi.owner.equals(MINECRAFT_SERVER)
                    && (fi.name.equals("serverRunning") || fi.name.equals("field_71317_u") || fi.name.equals("v"))// why
                    && fi.desc.equals("Z")) {
                    AbstractInsnNode probablyLabel = null;
                    while (it.hasPrevious()) {
                        probablyLabel = it.previous();
                        if (probablyLabel instanceof LabelNode) break;
                    }
                    if (probablyLabel instanceof LabelNode jumpBackNode) {
                        it.next();
                        while (it.hasNext()) {
                            AbstractInsnNode cont = it.next();
                            if (cont instanceof LabelNode) {
                                it.next();
                                break;
                            }
                        }
                        injectWarp(it, jumpBackNode);
                    } else {
                        while (it.hasNext()) {
                            AbstractInsnNode cont = it.next();
                            if (cont instanceof FieldInsnNode) {
                                break;
                            }
                        }
                    }
                    continue;
                }
            }
            int fallingBehindIndex = node.maxLocals + 1;
            if (insnNode instanceof VarInsnNode && ((VarInsnNode) insnNode).var == 1) {
                if (insnNode.getPrevious() instanceof MethodInsnNode) continue;
                AbstractInsnNode prev = insnNode.getPrevious();
                // i = j
                if (prev instanceof VarInsnNode && prev.getOpcode() == Opcodes.LLOAD && ((VarInsnNode) prev).var == 5) {
                    it.add(new InsnNode(Opcodes.ICONST_0));
                    it.add(new VarInsnNode(Opcodes.ISTORE, fallingBehindIndex));
                    continue;
                }
            }
            if (insnNode instanceof LdcInsnNode && ((LdcInsnNode) insnNode).cst.equals(50L)) {

                AbstractInsnNode probLdiv = insnNode.getNext();
                if (probLdiv instanceof InsnNode && probLdiv.getOpcode() == Opcodes.LDIV) continue;
                if (probLdiv instanceof VarInsnNode && probLdiv.getOpcode() == Opcodes.LLOAD
                    && ((VarInsnNode) probLdiv).var == 3) {
                    it.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    it.add(new VarInsnNode(Opcodes.ILOAD, fallingBehindIndex));
                    it.add(new VarInsnNode(Opcodes.LLOAD, 3));
                    it.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, MINECRAFT_SERVER, "bridge$sleepOrNot", "(ZJ)V"));
                    continue;
                }
                AbstractInsnNode endWhileJumpInsn = insnNode;
                LabelNode endWhile = null;
                while (endWhileJumpInsn.getNext() != null) {
                    endWhileJumpInsn = endWhileJumpInsn.getNext();
                    if (endWhileJumpInsn instanceof JumpInsnNode && endWhileJumpInsn.getOpcode() == Opcodes.IFLE) {
                        endWhile = ((JumpInsnNode) endWhileJumpInsn).label;
                        break;
                    }

                }
                it.set(new FieldInsnNode(Opcodes.GETSTATIC, TICK_SPEED, "mspt", "J"));
                it.add(new InsnNode(Opcodes.ICONST_0));
                int keepingUpIndex = node.maxLocals + 2;
                it.add(new VarInsnNode(Opcodes.ISTORE, keepingUpIndex));

                while (it.hasNext()) {
                    AbstractInsnNode next = it.next();
                    if (next instanceof LdcInsnNode && ((LdcInsnNode) next).cst.equals(50L)) {
                        it.set(new FieldInsnNode(Opcodes.GETSTATIC, TICK_SPEED, "mspt", "J"));
                        break;
                    }
                }
                while (it.hasNext()) {
                    AbstractInsnNode next = it.next();
                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL
                        && ((MethodInsnNode) next).desc.equals("()V")
                        && ((MethodInsnNode) next).owner.equals(MINECRAFT_SERVER)) {
                        break;
                    }
                }
                it.previous();
                it.add(new VarInsnNode(Opcodes.ILOAD, keepingUpIndex));
                LabelNode endIfKeepingUp = new LabelNode();
                it.add(new JumpInsnNode(Opcodes.IFEQ, endIfKeepingUp));
                it.add(new VarInsnNode(Opcodes.ALOAD, 0));
                it.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, MINECRAFT_SERVER, "bridge$setServerRunning", "()V"));
                it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J"));
                it.add(new VarInsnNode(Opcodes.LSTORE, 1));
                it.add(new InsnNode(Opcodes.ICONST_1));
                it.add(new VarInsnNode(Opcodes.ISTORE, fallingBehindIndex));
                it.add(endIfKeepingUp);
                it.next();
                it.add(new InsnNode(Opcodes.ICONST_1));
                it.add(new VarInsnNode(Opcodes.ISTORE, keepingUpIndex));
                // l = currentTimeMillis() - j
                it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J"));
                it.add(new VarInsnNode(Opcodes.LLOAD, 5));
                it.add(new InsnNode(Opcodes.LSUB));
                it.add(new VarInsnNode(Opcodes.LSTORE, 3));
                it.add(new JumpInsnNode(Opcodes.GOTO, endWhile));
            }
        }
    }

    public void injectWarp(ListIterator<AbstractInsnNode> it, LabelNode whileStart) {
        it.add(new FieldInsnNode(Opcodes.GETSTATIC, TICK_SPEED, "time_warp_start_time", "J"));
        it.add(new LdcInsnNode(0L));
        it.add(new InsnNode(Opcodes.LCMP));
        LabelNode endIfTimeWarpStartTime = new LabelNode();
        it.add(new JumpInsnNode(Opcodes.IFEQ, endIfTimeWarpStartTime));
        it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, TICK_SPEED, "continueWarp", "()Z"));
        LabelNode endIfContinueWarp = new LabelNode();
        it.add(new JumpInsnNode(Opcodes.IFEQ, endIfContinueWarp));
        it.add(new VarInsnNode(Opcodes.ALOAD, 0));
        it.add(new InsnNode(Opcodes.DUP));
        it.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, MINECRAFT_SERVER, TICK_BRIDGE, TICK_SIGNATURE, false));
        it.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, MINECRAFT_SERVER, "bridge$setServerRunning", "()V"));
        it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J"));
        it.add(new VarInsnNode(Opcodes.LSTORE, 1));
        it.add(endIfContinueWarp);
        it.add(new JumpInsnNode(Opcodes.GOTO, whileStart));
        it.add(endIfTimeWarpStartTime);
    }
}
