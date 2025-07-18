package icu.takeneko.towel.helpers;

import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;

import icu.takeneko.towel.TowelMod;
import icu.takeneko.towel.networking.TNetworking;

public class TickSpeed {

    public static final int PLAYER_GRACE = 2;
    public static float tickRate = 20.0f;
    public static long mspt = 50L;
    public static long warp_temp_mspt = 1L;
    public static long time_bias = 0;
    public static long time_warp_start_time = 0;
    public static long time_warp_scheduled_ticks = 0;
    public static EntityPlayer time_advancerer = null;
    public static String tick_warp_callback = null;
    public static ICommandSender tick_warp_sender = null;
    public static int player_active_timeout = 0;
    public static boolean process_entities = true;
    public static boolean is_paused = false;
    public static boolean is_superHot = false;

    public static void reset_player_active_timeout() {
        if (player_active_timeout < PLAYER_GRACE) {
            player_active_timeout = PLAYER_GRACE;
        }
    }

    public static void add_ticks_to_run_in_pause(int ticks) {
        player_active_timeout = PLAYER_GRACE + ticks;
    }

    public static void setTickRate(float rate) {
        tickRate = rate;
        mspt = (long) (1000.0 / tickRate);
        if (mspt <= 0) {
            mspt = 1L;
            tickRate = 1000.0f;
        }
        TNetworking.broadcastTickRateChanges(tickRate);
    }

    public static String tickrate_advance(EntityPlayer player, long advance, String callback,
        ICommandSender icommandsender) {
        if (0 == advance) {
            if (time_bias <= 0) {
                return "r No warp in progress";
            }
            tick_warp_callback = null;
            tick_warp_sender = null;
            finish_time_warp();
            return "ig Warp interrupted";
        }
        if (time_bias > 0) {
            if (time_advancerer != null) {
                return String.format(
                    "l %s is already advancing time at the moment. Try later or talk to them",
                    time_advancerer.getGameProfile()
                        .getName());
            }
            return "l Another player is already advancing time at the moment. Try later or talk to them";
        }
        time_advancerer = player;
        time_warp_start_time = System.nanoTime();
        time_warp_scheduled_ticks = advance;
        time_bias = advance;
        tick_warp_callback = callback;
        tick_warp_sender = icommandsender;
        return "ig Warp speed ....";
    }

    public static void finish_time_warp() {

        long completed_ticks = time_warp_scheduled_ticks - time_bias;
        double milis_to_complete = System.nanoTime() - time_warp_start_time;
        if (milis_to_complete == 0.0) {
            milis_to_complete = 1.0;
        }
        milis_to_complete /= 1000000.0;
        int tps = (int) (1000.0D * completed_ticks / milis_to_complete);
        double mspt = milis_to_complete / completed_ticks;
        time_warp_scheduled_ticks = 0;
        time_warp_start_time = 0;
        if (tick_warp_callback != null) {
            ICommandManager icommandmanager = TowelMod.getServer()
                .getCommandManager();
            try {
                int j = icommandmanager.executeCommand(tick_warp_sender, tick_warp_callback);

                if (j < 1) {
                    if (time_advancerer != null) {
                        Messenger.m(
                            time_advancerer,
                            "r Command Callback failed: ",
                            "rb /" + tick_warp_callback,
                            "/" + tick_warp_callback);
                    }
                }

            } catch (Throwable var23) {
                if (time_advancerer != null) {
                    Messenger.m(
                        time_advancerer,
                        "r Command Callback failed - unknown error: ",
                        "rb /" + tick_warp_callback,
                        "/" + tick_warp_callback);
                }
            }
            tick_warp_callback = null;
            tick_warp_sender = null;
        }
        if (time_advancerer != null) {
            Messenger
                .m(time_advancerer, String.format("gi ... Time warp completed with %d tps, or %.2f mspt", tps, mspt));
            time_advancerer = null;
        } else {
            Messenger.print_server_message(
                TowelMod.getServer(),
                String.format("gi ... Time warp completed with %d tps, or %.2f mspt", tps, mspt));
        }
        time_bias = 0;

    }

    public static boolean continueWarp() {
        if (time_bias > 0) {
            if (time_bias == time_warp_scheduled_ticks) // first call after previous tick, adjust start time
            {
                time_warp_start_time = System.nanoTime();
            }
            time_bias -= 1;
            return true;
        } else {
            finish_time_warp();
            return false;
        }
    }

    public static void tick(MinecraftServer server) {
        process_entities = true;
        if (player_active_timeout > 0) {
            player_active_timeout--;
        }
        if (is_paused) {
            if (player_active_timeout < PLAYER_GRACE) {
                process_entities = false;
            }
        } else if (is_superHot) {
            if (player_active_timeout <= 0) {
                process_entities = false;

            }
        }
    }

    public static boolean shouldTick() {
        return process_entities;
    }

    public static double getMSPT() {
        return MathHelper.average(TowelMod.getServer().tickTimeArray) * 1.0E-6D;
    }

    public static double getTPS() {
        return 1000.0D / Math.max((time_warp_start_time != 0) ? 0.0 : TickSpeed.mspt, getMSPT());
    }
}
