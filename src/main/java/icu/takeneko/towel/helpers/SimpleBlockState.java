package icu.takeneko.towel.helpers;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class SimpleBlockState {
    private final Block block;
    private final int meta;

    public SimpleBlockState(Block block, int meta) {
        this.block = block;
        this.meta = meta;
    }

    public Block getBlock() {
        return block;
    }

    public int getMeta() {
        return meta;
    }

    public static SimpleBlockState getBlockState(World world, BlockPos blockPos){
        return new SimpleBlockState(
            world.getBlock(blockPos.x, blockPos.y, blockPos.z),
            world.getBlockMetadata(blockPos.x, blockPos.y, blockPos.z)
        );
    }
}
