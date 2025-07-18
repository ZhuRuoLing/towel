package icu.takeneko.towel.helpers.fakes;

import com.mojang.authlib.GameProfile;
import icu.takeneko.towel.helpers.mixin.EntityPlayerMPExtension;
import icu.takeneko.towel.helpers.mixin.WorldServerExtension;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;

import java.util.UUID;

public class EntityPlayerMPFake extends EntityPlayerMP {
    private double lastReportedPosX;
    private double lastReportedPosY;
    private double lastReportedPosZ;

    private double setX;
    private double setY;
    private double setZ;
    private float setYaw;
    private float setPitch;

    public static EntityPlayerMPFake createFake(String username, MinecraftServer server, double x, double y, double z, double yaw, double pitch, int dimension, int gamemode) {
        WorldServer worldIn = server.worldServers[dimension];
        ItemInWorldManager interactionManagerIn = new ItemInWorldManager(worldIn);
        GameProfile gameprofile = server.func_152358_ax().func_152655_a(username);

        if (gameprofile == null) {
            UUID uuid = EntityPlayer.func_146094_a(new GameProfile((UUID) null, username));
            gameprofile = new GameProfile(uuid, username);
        } else {
        }
        EntityPlayerMPFake instance = new EntityPlayerMPFake(server, worldIn, gameprofile, interactionManagerIn);
        NetworkManagerFake networkManager = new NetworkManagerFake(false);
        NetHandlerPlayServerFake netHandler = new NetHandlerPlayServerFake(
            server,
            networkManager,
            instance
        );
        instance.setSetPosition(x, y, z, (float) yaw, (float) pitch);
        server.getConfigurationManager().initializeConnectionToPlayer(networkManager, instance, netHandler);
        if (instance.dimension != dimension) { //player was logged in in a different dimension
            WorldServer old_world = server.worldServers[instance.dimension];
            instance.dimension = dimension;
            old_world.removeEntity(instance);
            instance.isDead = false;
            worldIn.spawnEntityInWorld(instance);
            instance.setWorld(worldIn);
            server.getConfigurationManager().func_72375_a(instance, old_world);
            instance.playerNetServerHandler.setPlayerLocation(x, y, z, (float) yaw, (float) pitch);
            instance.theItemInWorldManager.setWorld(worldIn);
        }
        instance.setHealth(20.0F);
        instance.isDead = false;
        instance.stepHeight = 0.6F;
        interactionManagerIn.setGameType(WorldSettings.GameType.getByID(gamemode));
        server.getConfigurationManager().sendPacketToAllPlayersInDimension(new S19PacketEntityHeadLook(instance, (byte) (instance.rotationYawHead * 256 / 360)), instance.dimension);
        server.getConfigurationManager().sendPacketToAllPlayersInDimension(new S18PacketEntityTeleport(instance), instance.dimension);
        server.getConfigurationManager().updatePlayerPertinentChunks(instance);
        instance.setHideCape(1, false);
        createAndAddFakePlayerToTeamBot(instance);
        return instance;
    }

    public static EntityPlayerMPFake createShadow(MinecraftServer server, EntityPlayerMP player) {
//        if (CarpetSettings.cameraModeRestoreLocation && player.getGamemodeCamera()) {
//            GameType gametype = server.getGameType();
//            player.moveToStoredCameraData();
//            player.setGameType(gametype);
//            player.removePotionEffect(Potion.getPotionFromResourceLocation("night_vision"));
//        }
        player.mcServer.getConfigurationManager().playerLoggedOut(player);
        player.playerNetServerHandler.onDisconnect(new ChatComponentTranslation("multiplayer.disconnect.duplicate_login"));
        WorldServer worldIn = server.worldServers[player.dimension];
        ItemInWorldManager interactionManagerIn = new ItemInWorldManager(worldIn);
        GameProfile gameprofile = player.getGameProfile();
        EntityPlayerMPFake playerShadow = new EntityPlayerMPFake(server, worldIn, gameprofile, interactionManagerIn);
        playerShadow.setSetPosition(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        NetworkManager nm = new NetworkManagerFake(false);
        NetHandlerPlayServerFake netHandler = new NetHandlerPlayServerFake(
            server,
            nm,
            playerShadow
        );
        server.getConfigurationManager().initializeConnectionToPlayer(nm, playerShadow, netHandler);

        playerShadow.setHealth(player.getHealth());
        playerShadow.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        interactionManagerIn.setGameType(player.theItemInWorldManager.getGameType());
        EntityPlayerMPExtension.getActionPack(playerShadow).copyFrom(EntityPlayerMPExtension.getActionPack(player));
        playerShadow.stepHeight = 0.6F;

        server.getConfigurationManager().sendPacketToAllPlayersInDimension(new S19PacketEntityHeadLook(playerShadow, (byte) (player.rotationYawHead * 256 / 360)), playerShadow.dimension);
        server.getConfigurationManager().sendPacketToAllPlayers(new S18PacketEntityTeleport(/*S18PacketEntityTeleport.Action.ADD_PLAYER,*/ playerShadow));
        server.getConfigurationManager().updatePlayerPertinentChunks(playerShadow);
        createAndAddFakePlayerToTeamBot(playerShadow);
        return playerShadow;
    }

    public static EntityPlayerMPFake create(String info, MinecraftServer server) {
        String[] infos = info.split("/");
        String username = infos[0];
        WorldServer worldIn = server.worldServers[0];
        ItemInWorldManager interactionManagerIn = new ItemInWorldManager(worldIn);
        GameProfile gameprofile = server.func_152358_ax().func_152655_a(username);
        if (gameprofile == null) {
            UUID uuid = EntityPlayer.func_146094_a(new GameProfile(null, username));
            gameprofile = new GameProfile(uuid, username);
        } else {
            gameprofile = fixSkin(gameprofile);
        }
        EntityPlayerMPFake instance = new EntityPlayerMPFake(server, worldIn, gameprofile, interactionManagerIn);
        server.getConfigurationManager().readPlayerDataFromFile(instance);
        instance.setSetPosition(instance.posX, instance.posY, instance.posZ, instance.rotationYaw, instance.rotationPitch);
        WorldServerExtension.of(worldIn).setupMinecartFix();
        NetworkManagerFake networkManager = new NetworkManagerFake(false);
        NetHandlerPlayServerFake netHandler = new NetHandlerPlayServerFake(
            server,
            networkManager,
            instance
        );
        server.getConfigurationManager().initializeConnectionToPlayer(networkManager, instance, netHandler);
        WorldServerExtension.of(worldIn).teardownMinecartFix();
        if (instance.dimension != 0) //player was logged in in a different dimension
        {
            worldIn = server.worldServers[instance.dimension];
            instance.setWorld(worldIn);
            server.getConfigurationManager().func_72375_a(instance, worldIn);
            instance.theItemInWorldManager.setWorld(worldIn);
        }
        instance.isDead = false;
        instance.stepHeight = 0.6F;
        server.getConfigurationManager().sendPacketToAllPlayersInDimension(new S19PacketEntityHeadLook(instance, (byte) (instance.rotationYawHead * 256 / 360)), instance.dimension);
        server.getConfigurationManager().sendPacketToAllPlayersInDimension(new S18PacketEntityTeleport(instance), instance.dimension);
        server.getConfigurationManager().updatePlayerPertinentChunks(instance);
        instance.setHideCape(1, false); // show all model layers (incl. capes)
        createAndAddFakePlayerToTeamBot(instance);
        if (infos.length > 1) {
            EntityPlayerMPExtension.getActionPack(instance).fromString(infos[1]);
        }
        return instance;
    }

    private EntityPlayerMPFake(MinecraftServer server, WorldServer worldIn, GameProfile profile, ItemInWorldManager interactionManagerIn) {
        super(server, worldIn, profile, interactionManagerIn);
    }

    private static GameProfile fixSkin(GameProfile gameProfile) {
/*        if (!CarpetSettings.removeFakePlayerSkins && !gameProfile.getProperties().containsKey("texture"))
            return TileEntitySkull.updateGameProfile(gameProfile);
        else*/
        return gameProfile;
    }

    @Override
    protected void kill() {
        logout();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.onUpdateEntity();
        this.playerMoved();
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        logout();
    }

    private void logout() {
        this.dismountEntity(this.ridingEntity);
        mcServer.getConfigurationManager().playerLoggedOut(this);
        removePlayerFromTeams(this);
    }

    public void despawn() {
        mcServer.getConfigurationManager().playerLoggedOut(this);
        removePlayerFromTeams(this);
    }

    private void playerMoved() {
        if (posX != lastReportedPosX || posY != lastReportedPosY || posZ != lastReportedPosZ) {
            mcServer.getConfigurationManager().updatePlayerPertinentChunks(this);
            lastReportedPosX = posX;
            lastReportedPosY = posY;
            lastReportedPosZ = posZ;
        }
    }

    public void setSetPosition(double x, double y, double z, float yaw, float pitch) {
        this.setX = x;
        this.setY = y;
        this.setZ = z;
        this.setYaw = yaw;
        this.setPitch = pitch;
    }

    public void resetToSetPosition() {
        setLocationAndAngles(setX, setY, setZ, setYaw, setPitch);
    }

    private static void createAndAddFakePlayerToTeamBot(EntityPlayerMPFake player) {
        Scoreboard scoreboard = player.mcServer.worldServers[0].getScoreboard();
        if (!scoreboard.getTeamNames().contains("Bots")) {
            scoreboard.createTeam("Bots");
            ScorePlayerTeam scoreplayerteam = scoreboard.getTeam("Bots");
            EnumChatFormatting textformatting = EnumChatFormatting.getValueByName("dark_green");
            //scoreplayerteam.setColor(textformatting);
            scoreplayerteam.setNameSuffix(textformatting.toString());
            scoreplayerteam.setNameSuffix(EnumChatFormatting.RESET.toString());
        }
        scoreboard.func_151392_a(player.getGameProfile().getName(), "Bots");
    }

    public static void removePlayerFromTeams(EntityPlayerMPFake player) {
        Scoreboard scoreboard = player.mcServer.worldServers[0].getScoreboard();
        scoreboard.removePlayerFromTeams(player.getGameProfile().getName());
    }

    public static String getInfo(EntityPlayerMP p) {
        return p.getGameProfile().getName() + "/" + EntityPlayerMPExtension.getActionPack(p);
    }
}
