package icu.takeneko.towel.helpers.fakes;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import icu.takeneko.towel.helpers.Util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import org.joml.Math;

import java.util.List;

public class EntityPlayerActionPack {
    private EntityPlayerMP player;

    private boolean doesAttack;
    private int attackInterval;
    private int attackCooldown;

    private boolean doesUse;
    private int useInterval;
    private int useCooldown;

    private boolean doesJump;
    private int jumpInterval;
    private int jumpCooldown;

    private BlockPos currentBlock = new BlockPos(-1, -1, -1);
    private int blockHitDelay;
    private boolean isHittingBlock;
    private float curBlockDamageMP;

    private boolean sneaking;
    private boolean sprinting;
    private float forward;
    private float strafing;

    public EntityPlayerActionPack(EntityPlayerMP playerIn) {
        player = playerIn;
        stop();
    }

    public void copyFrom(EntityPlayerActionPack other) {
        doesAttack = other.doesAttack;
        attackInterval = other.attackInterval;
        attackCooldown = other.attackCooldown;

        doesUse = other.doesUse;
        useInterval = other.useInterval;
        useCooldown = other.useCooldown;

        doesJump = other.doesJump;
        jumpInterval = other.jumpInterval;
        jumpCooldown = other.jumpCooldown;


        currentBlock = other.currentBlock;
        blockHitDelay = other.blockHitDelay;
        isHittingBlock = other.isHittingBlock;
        curBlockDamageMP = other.curBlockDamageMP;

        sneaking = other.sneaking;
        sprinting = other.sprinting;
        forward = other.forward;
        strafing = other.strafing;
    }

    public String toString() {
        return (doesAttack ? "t" : "f") + ":" +
            attackInterval + ":" +
            attackCooldown + ":" +
            (doesUse ? "t" : "f") + ":" +
            useInterval + ":" +
            useCooldown + ":" +
            (doesJump ? "t" : "f") + ":" +
            jumpInterval + ":" +
            jumpCooldown + ":" +
            (sneaking ? "t" : "f") + ":" +
            (sprinting ? "t" : "f") + ":" +
            forward + ":" +
            strafing;
    }

    public void fromString(String s) {
        String[] list = s.split(":");
        doesAttack = list[0].equals("t");
        attackInterval = Integer.parseInt(list[1]);
        attackCooldown = Integer.parseInt(list[2]);
        doesUse = list[3].equals("t");
        useInterval = Integer.parseInt(list[4]);
        useCooldown = Integer.parseInt(list[5]);
        doesJump = list[6].equals("t");
        jumpInterval = Integer.parseInt(list[7]);
        jumpCooldown = Integer.parseInt(list[8]);
        sneaking = list[9].equals("t");
        sprinting = list[10].equals("t");
        forward = Float.parseFloat(list[11]);
        strafing = Float.parseFloat(list[12]);
    }

    public EntityPlayerActionPack setAttack(int interval, int offset) {
        if (interval < 1) {
            //CarpetSettings.LOG.error("attack interval needs to be positive");
            return this;
        }
        this.doesAttack = true;
        this.attackInterval = interval;
        this.attackCooldown = interval + offset;
        return this;
    }

    public EntityPlayerActionPack setUse(int interval, int offset) {
        if (interval < 1) {
            //CarpetSettings.LOG.error("use interval needs to be positive");
            return this;
        }
        this.doesUse = true;
        this.useInterval = interval;
        this.useCooldown = interval + offset;
        return this;
    }

    public EntityPlayerActionPack setUseForever() {
        this.doesUse = true;
        this.useInterval = 1;
        this.useCooldown = 1;
        return this;
    }

    public EntityPlayerActionPack setAttackForever() {
        this.doesAttack = true;
        this.attackInterval = 1;
        this.attackCooldown = 1;
        return this;
    }

    public EntityPlayerActionPack setJump(int interval, int offset) {
        if (interval < 1) {
            //CarpetSettings.LOG.error("jump interval needs to be positive");
            return this;
        }
        this.doesJump = true;
        this.jumpInterval = interval;
        this.jumpCooldown = interval + offset;
        return this;
    }

    public EntityPlayerActionPack setJumpForever() {
        this.doesJump = true;
        this.jumpInterval = 1;
        this.jumpCooldown = 1;
        return this;
    }

    public EntityPlayerActionPack setSneaking(boolean doSneak) {
        sneaking = doSneak;
        player.setSneaking(doSneak);
        if (sprinting && sneaking)
            setSprinting(false);
        return this;
    }

    public EntityPlayerActionPack setSprinting(boolean doSprint) {
        sprinting = doSprint;
        player.setSprinting(doSprint);
        if (sneaking && sprinting)
            setSneaking(false);
        return this;
    }

    public EntityPlayerActionPack setForward(float value) {
        forward = value;
        return this;
    }

    public EntityPlayerActionPack setStrafing(float value) {
        strafing = value;
        return this;
    }

    public boolean look(String where) {
        return switch (where) {
            case "north" -> {
                look(180.0f, 0.0F);
                yield true;
            }
            case "south" -> {
                look(0.0F, 0.0F);
                yield true;
            }
            case "east" -> {
                look(-90.0F, 0.0F);
                yield true;
            }
            case "west" -> {
                look(90.0F, 0.0F);
                yield true;
            }
            case "up" -> {
                look(player.rotationYaw, -90.0F);
                yield true;
            }
            case "down" -> {
                look(player.rotationYaw, 90.0F);
                yield true;
            }
            case "left", "right" -> turn(where);
            default -> false;
        };
    }

    public EntityPlayerActionPack look(float yaw, float pitch) {
        setPlayerRotation(yaw, Math.clamp(pitch, -90.0F, 90.0F));
        return this;
    }

    public void setPlayerRotation(float yaw, float pitch) {
        player.rotationYaw = yaw % 360f;
        player.rotationPitch = pitch % 360f;
    }

    public boolean turn(String where) {
        return switch (where) {
            case "left" -> {
                turn(-90.0F, 0.0F);
                yield true;
            }
            case "right" -> {
                turn(90.0F, 0.0F);
                yield true;
            }
            case "up" -> {
                turn(0.0F, -5.0F);
                yield true;
            }
            case "down" -> {
                turn(0.0F, 5.0F);
                yield true;
            }
            default -> false;
        };
    }

    public EntityPlayerActionPack turn(float yaw, float pitch) {
        setPlayerRotation(player.rotationYaw + yaw, Math.clamp(player.rotationPitch + pitch, -90.0F, 90.0F));
        return this;
    }


    public EntityPlayerActionPack stop() {
        this.doesUse = false;
        this.doesAttack = false;
        this.doesJump = false;
        resetBlockRemoving();
        setSneaking(false);
        setSprinting(false);
        forward = 0.0F;
        strafing = 0.0F;
        player.setJumping(false);


        return this;
    }

//    public void swapHands() {
//        player.playerNetServerHandler.processPlayerDigging(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.SWAP_HELD_ITEMS, null, null));
//    }

    public void dropItem() {

        player.playerNetServerHandler.processPlayerDigging(new C07PacketPlayerDigging(4, 0, 0, 0, 0));
    }

    public void mount() {
        List<Entity> entities = player.worldObj.getEntitiesWithinAABBExcludingEntity(
            player,
            player.getBoundingBox().expand(3.0D, 1.0D, 3.0D),
            other -> !(other instanceof EntityPlayer)
        );
        if (entities.isEmpty()) {
            return;
        }
        Entity closest = entities.get(0);
        double distance = player.getDistanceSq(closest.posX, closest.posY, closest.posZ);
        for (Entity e : entities) {
            double dd = player.getDistanceSq(closest.posX, closest.posY, closest.posZ);
            if (dd < distance) {
                distance = dd;
                closest = e;
            }
        }
        player.mountEntity(closest);
    }

    public void dismount() {
        player.dismountEntity(this.player.ridingEntity);
    }

    public void onUpdate() {
        if (doesJump) {
            if (--jumpCooldown == 0) {
                jumpCooldown = jumpInterval;
                //jumpOnce();
                player.setJumping(true);
            } else {
                player.setJumping(false);
            }
        }

        boolean used = false;

        if (doesUse && (--useCooldown) == 0) {
            useCooldown = useInterval;
            used = useOnce();
        }
        if (doesAttack) {
            if ((--attackCooldown) == 0) {
                attackCooldown = attackInterval;
                if (!(used)) attackOnce();
            } else {
                resetBlockRemoving();
            }
        }
        if (forward != 0.0F) {
            //CarpetSettings.LOG.error("moving it forward");
            player.moveForward = forward * (sneaking ? 0.3F : 1.0F);
        }
        if (strafing != 0.0F) {
            player.moveStrafing = strafing * (sneaking ? 0.3F : 1.0F);
        }
    }

    public void jumpOnce() {
        if (player.onGround) {
            player.jump();
        }
    }

    public void attackOnce() {
        MovingObjectPosition raytraceresult = mouseOver();
        if (raytraceresult == null) return;

        switch (raytraceresult.typeOfHit) {
            case ENTITY:
                player.attackTargetEntityWithCurrentItem(raytraceresult.entityHit);
                this.player.swingItem();
                break;
            case MISS:
                break;
            case BLOCK:
                int x = raytraceresult.blockX;
                int y = raytraceresult.blockY;
                int z = raytraceresult.blockZ;
                if (player.getEntityWorld().getBlock(x, y, z).getMaterial() != Material.air) {
                    onPlayerDamageBlock(new BlockPos(x, y, z), Util.opposite(raytraceresult.sideHit));
                    this.player.swingItem();
                    break;
                }
        }
    }

    public boolean useOnce() {
        MovingObjectPosition raytraceresult = mouseOver();
        ItemStack itemstack = this.player.getHeldItem();
        if (raytraceresult != null) {
            switch (raytraceresult.typeOfHit) {
                case ENTITY:
                    Entity target = raytraceresult.entityHit;
                    Vec3 vec3d = Vec3.createVectorHelper(raytraceresult.hitVec.xCoord - target.posX, raytraceresult.hitVec.yCoord - target.posY, raytraceresult.hitVec.zCoord - target.posZ);

                    boolean flag = player.canEntityBeSeen(target);
                    double d0 = 36.0D;

                    if (!flag) {
                        d0 = 9.0D;
                    }

                    if (Util.distantSqr(player, target) < d0) {
                        boolean res = player.interactWith(target);
                        if (res) {
                            return true;
                        }
                        res = target.interactFirst(player);
                        if (res) {
                            return true;
                        }
                    }
                    break;
                case MISS:
                    break;
                case BLOCK:
                    int posX = raytraceresult.blockX;
                    int posY = raytraceresult.blockY;
                    int posZ = raytraceresult.blockZ;

                    if (player.getEntityWorld().getBlock(posX, posY, posZ).getMaterial() != Material.air) {
                        if (itemstack.stackSize == 0) {
                            return false;
                        }
                        float x = (float) raytraceresult.hitVec.xCoord;
                        float y = (float) raytraceresult.hitVec.yCoord;
                        float z = (float) raytraceresult.hitVec.zCoord;

                        boolean res = player.theItemInWorldManager.activateBlockOrUseItem(
                            player,
                            player.getEntityWorld(),
                            itemstack,
                            posX, posY, posZ,
                            raytraceresult.sideHit,
                            x,
                            y,
                            z
                        );
                        if (res) {
                            this.player.swingItem();
                            return true;
                        }
                    }
            }
        }
        return player.theItemInWorldManager.tryUseItem(player, player.getEntityWorld(), itemstack);
    }

    private MovingObjectPosition rayTraceBlocks(double blockReachDistance) {
        Vec3 eyeVec = player.getPosition(1.0F);
        Vec3 lookVec = player.getLook(1.0F);
        Vec3 pointVec = eyeVec.addVector(lookVec.xCoord * blockReachDistance, lookVec.yCoord * blockReachDistance, lookVec.zCoord * blockReachDistance);
        return player.getEntityWorld().func_147447_a(eyeVec, pointVec, false, false, true);
    }

    public MovingObjectPosition mouseOver() {
        World world = player.getEntityWorld();
        if (world == null)
            return null;
        MovingObjectPosition result = null;
        boolean flag = !player.theItemInWorldManager.isCreative();
        Entity pointedEntity = null;
        double reach = !flag ? 5.0D : 4.5D;
        result = rayTraceBlocks(reach);
        Vec3 eyeVec = player.getPosition(1.0F);

        if (!flag) {
            reach = 6.0D;
        }
        double extendedReach = reach;

        if (result != null) {
            extendedReach = result.hitVec.distanceTo(eyeVec);
            if (world.getBlock(result.blockX, result.blockY, result.blockZ).getMaterial() == Material.air)
                result = null;
        }

        Vec3 lookVec = player.getLook(1.0F);
        Vec3 pointVec = eyeVec.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
        Vec3 hitVec = null;
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(
            player,
            player.boundingBox.expand(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach).expand(1.0D, 1.0D, 1.0D),
            entity -> entity != null && entity.canBeCollidedWith()
        );
        double d2 = extendedReach;

        for (int j = 0; j < list.size(); ++j) {
            Entity entity1 = list.get(j);
            AxisAlignedBB axisalignedbb = entity1.boundingBox.expand(entity1.getCollisionBorderSize(), entity1.getCollisionBorderSize(), entity1.getCollisionBorderSize());
            MovingObjectPosition raytraceresult = axisalignedbb.calculateIntercept(eyeVec, pointVec);

            if (axisalignedbb.isVecInside(eyeVec)) {
                if (d2 >= 0.0D) {
                    pointedEntity = entity1;
                    hitVec = raytraceresult == null ? eyeVec : raytraceresult.hitVec;
                    d2 = 0.0D;
                }
            } else if (raytraceresult != null) {
                double d3 = eyeVec.distanceTo(raytraceresult.hitVec);

                if (d3 < d2 || d2 == 0.0D) {
                    if (Util.getLowestRidingEntity(entity1) == Util.getLowestRidingEntity(player)) {
                        if (d2 == 0.0D) {
                            pointedEntity = entity1;
                            hitVec = raytraceresult.hitVec;
                        }
                    } else {
                        pointedEntity = entity1;
                        hitVec = raytraceresult.hitVec;
                        d2 = d3;
                    }
                }
            }
        }

        if (pointedEntity != null && flag && eyeVec.distanceTo(hitVec) > 3.0D) {
            pointedEntity = null;
            result = new MovingObjectPosition(
                (int) hitVec.xCoord,
                (int) hitVec.yCoord,
                (int) hitVec.zCoord,
                -1,
                hitVec,
                false
            );
        }

        if (pointedEntity != null && (d2 < extendedReach || result == null)) {
            result = new MovingObjectPosition(pointedEntity, hitVec);
        }

        return result;
    }

    public boolean clickBlock(BlockPos loc, int face) // don't call this one
    {
        World world = player.getEntityWorld();
        if (player.theItemInWorldManager.getGameType() != WorldSettings.GameType.ADVENTURE) {
//            if (player.theItemInWorldManager.getGameType() == WorldSettings.GameType.SPECTATOR) {
//                return false;
//            }

            if (!player.capabilities.allowEdit) {
                ItemStack itemstack = player.getHeldItem();

                if (itemstack.stackSize == 0) {
                    return false;
                }

                if (!itemstack.func_150998_b(world.getBlock(loc.x, loc.y, loc.z))) {
                    return false;
                }
            }
        }

        if (!Util.isOutsideOfWorld(loc)) {
            return false;
        } else {
            if (player.theItemInWorldManager.getGameType() == WorldSettings.GameType.CREATIVE) {
                player.playerNetServerHandler.processPlayerDigging(new C07PacketPlayerDigging(2, loc.x, loc.y, loc.z, face));
                clickBlockCreative(world, loc, face);
                this.blockHitDelay = 5;
            } else if (!this.isHittingBlock || !(currentBlock.equals(loc))) {
                if (this.isHittingBlock) {
                    player.playerNetServerHandler.processPlayerDigging(new C07PacketPlayerDigging(1, this.currentBlock.x, this.currentBlock.y, this.currentBlock.z, face));
                }

                Block iblockstate = world.getBlock(loc.x, loc.y, loc.z);
                player.playerNetServerHandler.processPlayerDigging(new C07PacketPlayerDigging(0, loc.x, loc.y, loc.z, face));
                boolean flag = iblockstate.getMaterial() != Material.air;

                if (flag && this.curBlockDamageMP == 0.0F) {
                    iblockstate.onBlockClicked(world, loc.x, loc.y, loc.z, player);
                }

                if (flag && iblockstate.getPlayerRelativeBlockHardness(player, world, loc.x, loc.y, loc.z) >= 1.0F) {
                    this.onPlayerDestroyBlock(loc);
                } else {
                    this.isHittingBlock = true;
                    this.currentBlock = loc;
                    this.curBlockDamageMP = 0.0F;
                    world.destroyBlockInWorldPartially(player.getEntityId(), this.currentBlock.x, this.currentBlock.y, this.currentBlock.z, (int) (this.curBlockDamageMP * 10.0F) - 1);
                }
            }

            return true;
        }
    }

    private void clickBlockCreative(World world, BlockPos pos, int facing) {
        if (!world.extinguishFire(player, pos.x, pos.y, pos.z, facing)) {
            onPlayerDestroyBlock(pos);
        }
    }

    public boolean onPlayerDamageBlock(BlockPos posBlock, int directionFacing) //continue clicking - one to call
    {
        if (this.blockHitDelay > 0) {
            --this.blockHitDelay;
            return true;
        }
        World world = player.getEntityWorld();
        if (player.theItemInWorldManager.isCreative() && Util.isOutsideOfWorld(posBlock)) {
            this.blockHitDelay = 5;
            player.playerNetServerHandler.processPlayerDigging(new C07PacketPlayerDigging(0, posBlock.x, posBlock.y, posBlock.z, directionFacing));
            clickBlockCreative(world, posBlock, directionFacing);
            return true;
        } else if (posBlock.equals(currentBlock)) {
            Block iblockstate = world.getBlock(posBlock.x, posBlock.y, posBlock.z);

            if (iblockstate.getMaterial() == Material.air) {
                this.isHittingBlock = false;
                return false;
            } else {
                this.curBlockDamageMP += iblockstate.getPlayerRelativeBlockHardness(player, world, posBlock.x, posBlock.y, posBlock.z);

                if (this.curBlockDamageMP >= 1.0F) {
                    this.isHittingBlock = false;
                    player.playerNetServerHandler.processPlayerDigging(new C07PacketPlayerDigging(1, posBlock.x, posBlock.y, posBlock.z, directionFacing));
                    this.onPlayerDestroyBlock(posBlock);
                    this.curBlockDamageMP = 0.0F;
                    this.blockHitDelay = 5;
                }
                //player.getEntityId()
                //send to all, even the breaker
                world.destroyBlockInWorldPartially(-1, this.currentBlock.x, this.currentBlock.y, this.currentBlock.z, (int) (this.curBlockDamageMP * 10.0F) - 1);
                return true;
            }
        } else {
            return this.clickBlock(posBlock, directionFacing);
        }
    }

    private boolean onPlayerDestroyBlock(BlockPos pos) {
        World world = player.getEntityWorld();
        if (player.theItemInWorldManager.getGameType() != WorldSettings.GameType.ADVENTURE) {

            if (player.capabilities.allowEdit) {
                ItemStack itemstack = player.getHeldItem();

                if (itemstack.stackSize == 0) {
                    return false;
                }

                if (!itemstack.getItem().canHarvestBlock(world.getBlock(pos.x, pos.y, pos.z), itemstack)) {
                    return false;
                }
            }
        }

        if ((player.theItemInWorldManager.getGameType() == WorldSettings.GameType.CREATIVE)
            && !(player.getHeldItem().stackSize == 0)
            && (player.getHeldItem().getItem() instanceof ItemSword)
        ) {
            return false;
        } else {
            Block block = world.getBlock(pos.x, pos.y, pos.z);
            int meta = world.getBlockMetadata(pos.x, pos.y, pos.z);
            if (block.getMaterial() == Material.air) {
                return false;
            } else {
                world.addBlockEvent(pos.x, pos.y, pos.z, block,2001,Block.getIdFromBlock(block));
                block.onBlockHarvested(world, pos.x, pos.y, pos.z, meta, player);
                boolean flag = world.setBlock(pos.x, pos.y, pos.z, Blocks.air);

                if (flag) {
                    block.onBlockDestroyedByPlayer(world,pos.x, pos.y, pos.z, meta);
                }

                this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());

                if (!(player.theItemInWorldManager.getGameType() == WorldSettings.GameType.CREATIVE)) {
                    ItemStack itemstack1 = player.getHeldItem();

                    if (!(itemstack1.stackSize == 0)) {
                        itemstack1.func_150999_a(world, block, pos.x, pos.y, pos.z, player);

                        if (itemstack1.stackSize == 0) {
                            player.inventory.mainInventory[player.inventory.currentItem] = null;
                        }
                    }
                }

                return flag;
            }
        }
    }

    public void resetBlockRemoving() {
        if (this.isHittingBlock) {
            player.playerNetServerHandler.processPlayerDigging(new C07PacketPlayerDigging(1, this.currentBlock.x, this.currentBlock.y, this.currentBlock.z, 0));
            this.isHittingBlock = false;
            this.curBlockDamageMP = 0.0F;
            player.getEntityWorld().destroyBlockInWorldPartially(player.getEntityId(), this.currentBlock.x, this.currentBlock.y, this.currentBlock.z, -1);
            this.currentBlock = new BlockPos(-1, -1, -1);
        }
    }


    /*
    public EnumActionResult processRightClickBlock(EntityPlayerSP player, WorldClient worldIn, BlockPos stack, EnumFacing pos, Vec3 facing, EnumHand vec)
    {
        this.syncCurrentPlayItem();
        ItemStack itemstack = player.getHeldItem(vec);
        float f = (float)(facing.xCoord - (double)stack.getX());
        float f1 = (float)(facing.yCoord - (double)stack.getY());
        float f2 = (float)(facing.zCoord - (double)stack.getZ());
        boolean flag = false;

        if (!this.mc.world.getWorldBorder().contains(stack))
        {
            return EnumActionResult.FAIL;
        }
        else
        {
            if (this.currentGameType != WorldSettings.GameType.SPECTATOR)
            {
                Block iblockstate = worldIn.getBlockState(stack);

                if ((!player.isSneaking() || player.getHeldItemMainhand().func_190926_b() && player.getHeldItemOffhand().func_190926_b()) && iblockstate.getBlock().onBlockActivated(worldIn, stack, iblockstate, player, vec, pos, f, f1, f2))
                {
                    flag = true;
                }

                if (!flag && itemstack.getItem() instanceof ItemBlock)
                {
                    ItemBlock itemblock = (ItemBlock)itemstack.getItem();

                    if (!itemblock.canPlaceBlockOnSide(worldIn, stack, pos, player, itemstack))
                    {
                        return EnumActionResult.FAIL;
                    }
                }
            }

            this.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(stack, pos, vec, f, f1, f2));

            if (!flag && this.currentGameType != WorldSettings.GameType.SPECTATOR)
            {
                if (itemstack.func_190926_b())
                {
                    return EnumActionResult.PASS;
                }
                else if (player.getCooldownTracker().hasCooldown(itemstack.getItem()))
                {
                    return EnumActionResult.PASS;
                }
                else
                {
                    if (itemstack.getItem() instanceof ItemBlock && !player.canUseCommandBlock())
                    {
                        Block block = ((ItemBlock)itemstack.getItem()).getBlock();

                        if (block instanceof BlockCommandBlock || block instanceof BlockStructure)
                        {
                            return EnumActionResult.FAIL;
                        }
                    }

                    if (this.currentGameType.isCreative())
                    {
                        int i = itemstack.getMetadata();
                        int j = itemstack.func_190916_E();
                        EnumActionResult enumactionresult = itemstack.onItemUse(player, worldIn, stack, vec, pos, f, f1, f2);
                        itemstack.setItemDamage(i);
                        itemstack.func_190920_e(j);
                        return enumactionresult;
                    }
                    else
                    {
                        return itemstack.onItemUse(player, worldIn, stack, vec, pos, f, f1, f2);
                    }
                }
            }
            else
            {
                return EnumActionResult.SUCCESS;
            }
        }
    }

    public EnumActionResult processRightClick(EntityPlayer player, World worldIn, EnumHand stack)
    {
        if (this.currentGameType == WorldSettings.GameType.SPECTATOR)
        {
            return EnumActionResult.PASS;
        }
        else
        {
            this.syncCurrentPlayItem();
            this.connection.sendPacket(new CPacketPlayerTryUseItem(stack));
            ItemStack itemstack = player.getHeldItem(stack);

            if (player.getCooldownTracker().hasCooldown(itemstack.getItem()))
            {
                return EnumActionResult.PASS;
            }
            else
            {
                int i = itemstack.func_190916_E();
                ActionResult<ItemStack> actionresult = itemstack.useItemRightClick(worldIn, player, stack);
                ItemStack itemstack1 = actionresult.getResult();

                if (itemstack1 != itemstack || itemstack1.func_190916_E() != i)
                {
                    player.setHeldItem(stack, itemstack1);
                }

                return actionresult.getType();
            }
        }
    }

    public EnumActionResult interactWithEntity(EntityPlayer player, Entity target, EnumHand heldItem)
    {
        this.syncCurrentPlayItem();
        this.connection.sendPacket(new CPacketUseEntity(target, heldItem));
        return this.currentGameType == WorldSettings.GameType.SPECTATOR ? EnumActionResult.PASS : player.func_190775_a(target, heldItem);
    }

    /
     * Handles right clicking an entity from the entities side, sends a packet to the server.
     *
    public EnumActionResult interactWithEntity(EntityPlayer player, Entity target, MovingObjectPosition raytrace, EnumHand heldItem)
    {
        this.syncCurrentPlayItem();
        Vec3 vec3d = new Vec3(raytrace.hitVec.xCoord - target.posX, raytrace.hitVec.yCoord - target.posY, raytrace.hitVec.zCoord - target.posZ);
        this.connection.sendPacket(new CPacketUseEntity(target, heldItem, vec3d));
        return this.currentGameType == WorldSettings.GameType.SPECTATOR ? EnumActionResult.PASS : target.applyPlayerInteraction(player, vec3d, heldItem);
    }
*/

}
