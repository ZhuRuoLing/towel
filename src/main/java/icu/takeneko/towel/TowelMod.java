package icu.takeneko.towel;

import net.minecraft.server.MinecraftServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import icu.takeneko.towel.commands.AllCommands;
import icu.takeneko.towel.networking.TNetworking;

@Mod(modid = TowelMod.MODID, version = Tags.VERSION, name = "Towel", acceptedMinecraftVersions = "[1.7.10]")
public class TowelMod {

    public static final String MODID = "towel";
    public static final Logger LOG = LogManager.getLogger(MODID);
    private static MinecraftServer server;

    @SidedProxy(clientSide = "icu.takeneko.towel.ClientProxy", serverSide = "icu.takeneko.towel.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        TNetworking.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
        AllCommands.registerCommands(event);
        server = event.getServer();
    }

    public static MinecraftServer getServer() {
        return server;
    }
}
