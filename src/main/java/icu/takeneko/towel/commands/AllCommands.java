package icu.takeneko.towel.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import icu.takeneko.towel.helpers.Messenger;
import icu.takeneko.towel.helpers.TickSpeed;

public class AllCommands {

    public static final CommandDispatcher<ICommandSender> DISPATCHER = new CommandDispatcher<>();
    public static final String COMMAND_TICK_NAME = "tick";
    public static final LiteralArgumentBuilder<ICommandSender> COMMAND_TICK = literal(COMMAND_TICK_NAME)
        .then(literal("rate").then(argument("value", FloatArgumentType.floatArg(0.01f)).executes(context -> {
            float arg = context.getArgument("value", Float.class);
            Messenger
                .m(context.getSource(), "w Current tps is ", Messenger.resolve(String.format("wb %.1f", arg), null));
            TickSpeed.setTickRate(arg);
            return 0;
        }))
            .then(literal("20").executes(context -> {
                Messenger.m(context.getSource(), "w Current tps is ", Messenger.resolve("wb 20", null));
                TickSpeed.setTickRate(20);
                return 0;
            })))
        .then(literal("warp").then(argument("value", LongArgumentType.longArg(1)).executes(context -> {
            long amount = context.getArgument("value", Long.class);
            EntityPlayer player = context.getSource() instanceof EntityPlayer ? (EntityPlayer) context.getSource()
                : null;
            String msg = TickSpeed.tickrate_advance(player, amount, null, context.getSource());
            Messenger.m(context.getSource(), msg);
            return 1;
        }))
            .executes(context -> {
                EntityPlayer player = context.getSource() instanceof EntityPlayer ? (EntityPlayer) context.getSource()
                    : null;
                String msg = TickSpeed.tickrate_advance(player, 0, null, context.getSource());
                Messenger.m(context.getSource(), msg);
                return 1;
            }))
        .then(literal("freeze").executes(context -> {
            TickSpeed.is_paused = !TickSpeed.is_paused;
            if (TickSpeed.is_paused) {
                Messenger.m(context.getSource(), "gi Game is frozen");
            } else {
                Messenger.m(context.getSource(), "gi Game runs normally");
            }
            return 0;
        }))
        .then(literal("step").then(argument("value", IntegerArgumentType.integer(1)).executes(context -> {
            int amount = context.getArgument("value", Integer.class);
            TickSpeed.add_ticks_to_run_in_pause(amount);
            Messenger.m(context.getSource(), String.format("gi Stepping %d tick", amount));
            return amount;
        }))
            .executes(context -> {
                TickSpeed.add_ticks_to_run_in_pause(1);
                Messenger.m(context.getSource(), "gi Stepping 1 tick");
                return 0;
            }));

    static {
        DISPATCHER.register(COMMAND_TICK);
    }

    public static void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new TDelegatedCommand(DISPATCHER, COMMAND_TICK_NAME));
    }

    public static LiteralArgumentBuilder<ICommandSender> literal(String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public static <T> RequiredArgumentBuilder<ICommandSender, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
