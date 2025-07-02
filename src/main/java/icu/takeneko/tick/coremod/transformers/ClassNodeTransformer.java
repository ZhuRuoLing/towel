package icu.takeneko.tick.coremod.transformers;


import org.objectweb.asm.tree.ClassNode;

public interface ClassNodeTransformer {
    boolean accept(ClassNode node, String name);
}
