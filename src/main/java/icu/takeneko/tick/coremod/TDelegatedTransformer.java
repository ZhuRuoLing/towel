package icu.takeneko.tick.coremod;

import icu.takeneko.tick.coremod.transformers.ClassNodeTransformer;
import icu.takeneko.tick.coremod.transformers.MinecraftServerTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class TDelegatedTransformer implements IClassTransformer {
    public static final Path DEBUG_DUMP = new File("./.tPatched").toPath();
    private static final Logger logger = LogManager.getLogger("TDelegatedTransformer");

    private static final List<ClassNodeTransformer> transformers = new ArrayList<>();

    static {
        transformers.add(new MinecraftServerTransformer());
        if (Files.isDirectory(DEBUG_DUMP)) {
            try {
                final Iterator<Path> iterator = Files.walk(DEBUG_DUMP)
                    .sorted(Comparator.reverseOrder()).iterator();
                while (iterator.hasNext()) {
                    Files.delete(iterator.next());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        boolean result = false;
        ClassReader reader = new ClassReader(basicClass);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);
        for (ClassNodeTransformer transformer : transformers) {
            result |= transformer.accept(node, name);
        }
        if (!result) return basicClass;
        logger.info("Patching class {}", name);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);
        byte[] patched = writer.toByteArray();
        Path dumpPath = DEBUG_DUMP.resolve(name.replace(".", "/") + ".class");
        try {
            Files.createDirectories(dumpPath.getParent());
            Files.createFile(dumpPath);
            Files.write(dumpPath, patched);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return patched;
    }
}
