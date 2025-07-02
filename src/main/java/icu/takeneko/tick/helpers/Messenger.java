package icu.takeneko.tick.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

@SuppressWarnings("LoggingSimilarMessage")
public class Messenger {

    public static final Logger LOG = LogManager.getLogger();
    /*
     * messsage: "desc me ssa ge"
     * desc contains:
     * i = italic
     * s = strikethrough
     * u = underline
     * b = bold
     * o = obfuscated
     * w = white
     * y = yellow
     * m = magenta (light purple)
     * r = red
     * c = cyan (aqua)
     * l = lime (green)
     * t = light blue (blue)
     * f = dark gray
     * g = gray
     * d = gold
     * p = dark purple (purple)
     * n = dark red (brown)
     * q = dark aqua
     * e = dark green
     * v = dark blue (navy)
     * k = black
     * / = action added to the previous component
     */

    private static IChatComponent _applyStyleToTextComponent(IChatComponent comp, String style) {
        // could be rewritten to be more efficient
        comp.getChatStyle()
            .setItalic(style.indexOf('i') >= 0);
        comp.getChatStyle()
            .setStrikethrough(style.indexOf('s') >= 0);
        comp.getChatStyle()
            .setUnderlined(style.indexOf('u') >= 0);
        comp.getChatStyle()
            .setBold(style.indexOf('b') >= 0);
        comp.getChatStyle()
            .setObfuscated(style.indexOf('o') >= 0);
        comp.getChatStyle()
            .setColor(EnumChatFormatting.WHITE);
        if (style.indexOf('w') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.WHITE); // not needed
        if (style.indexOf('y') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.YELLOW);
        if (style.indexOf('m') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.LIGHT_PURPLE);
        if (style.indexOf('r') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.RED);
        if (style.indexOf('c') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.AQUA);
        if (style.indexOf('l') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.GREEN);
        if (style.indexOf('t') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.BLUE);
        if (style.indexOf('f') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.DARK_GRAY);
        if (style.indexOf('g') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.GRAY);
        if (style.indexOf('d') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.GOLD);
        if (style.indexOf('p') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.DARK_PURPLE);
        if (style.indexOf('n') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.DARK_RED);
        if (style.indexOf('q') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.DARK_AQUA);
        if (style.indexOf('e') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.DARK_GREEN);
        if (style.indexOf('v') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.DARK_BLUE);
        if (style.indexOf('k') >= 0) comp.getChatStyle()
            .setColor(EnumChatFormatting.BLACK);
        return comp;
    }

    public static String heatmap_color(double actual, double reference) {
        String color = "e";
        if (actual > 0.5D * reference) color = "y";
        if (actual > 0.8D * reference) color = "r";
        if (actual > reference) color = "m";
        return color;
    }

    public static String creatureTypeColor(EnumCreatureType type) {
        switch (type) {
            case monster:
                return "n";
            case creature:
                return "e";
            case ambient:
                return "f";
            case waterCreature:
                return "v";
        }
        return "w";
    }

    public static IChatComponent resolve(String message, IChatComponent previous_message) {
        String[] parts = message.split("\\s", 2);
        String desc = parts[0];
        String str = "";
        if (parts.length > 1) str = parts[1];
        if (desc.charAt(0) == '/') // deprecated
        {
            if (previous_message != null) previous_message.getChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, message));
            return previous_message;
        }
        if (desc.charAt(0) == '?') {
            if (previous_message != null) previous_message.getChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, message.substring(1)));
            return previous_message;
        }
        if (desc.charAt(0) == '!') {
            if (previous_message != null) previous_message.getChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, message.substring(1)));
            return previous_message;
        }
        if (desc.charAt(0) == '^') {
            if (previous_message != null) previous_message.getChatStyle()
                .setChatHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, Messenger.m(null, message.substring(1))));
            return previous_message;
        }
        IChatComponent txt = new ChatComponentText(str);
        return _applyStyleToTextComponent(txt, desc);
    }

    public static IChatComponent tp(String desc, BlockPos pos) {
        return tp(desc, pos.getX(), pos.getY(), pos.getZ());
    }

    public static IChatComponent tp(String desc, double x, double y, double z) {
        return tp(desc, (float) x, (float) y, (float) z);
    }

    public static IChatComponent tp(String desc, float x, float y, float z) {
        return _getCoordsTextComponent(desc, x, y, z, false);
    }

    public static IChatComponent tp(String desc, int x, int y, int z) {
        return _getCoordsTextComponent(desc, (float) x, (float) y, (float) z, true);
    }

    /// to be continued
    public static IChatComponent dbl(String style, double double_value) {
        return m(null, String.format("%s %.1f", style, double_value), String.format("^w %f", double_value));
    }

    public static IChatComponent dbls(String style, double... doubles) {
        StringBuilder str = new StringBuilder(style + " [ ");
        String prefix = "";
        for (double dbl : doubles) {
            str.append(String.format("%s%.1f", prefix, dbl));
            prefix = ", ";
        }
        str.append(" ]");
        return m(null, str.toString());
    }

    public static IChatComponent dblf(String style, double... doubles) {
        StringBuilder str = new StringBuilder(style + " [ ");
        String prefix = "";
        for (double dbl : doubles) {
            str.append(String.format("%s%f", prefix, dbl));
            prefix = ", ";
        }
        str.append(" ]");
        return m(null, str.toString());
    }

    public static IChatComponent dblt(String style, double... doubles) {
        List<Object> components = new ArrayList<>();
        components.add(style + " [ ");
        String prefix = "";
        for (double dbl : doubles) {

            components.add(String.format("%s %s%.1f", style, prefix, dbl));
            components.add("?" + dbl);
            components.add("^w " + dbl);
            prefix = ", ";
        }
        // components.remove(components.size()-1);
        components.add(style + "  ]");
        return m(null, components.toArray(new Object[0]));
    }

    private static IChatComponent _getCoordsTextComponent(String style, float x, float y, float z, boolean isInt) {
        String text;
        String command;
        if (isInt) {
            text = String.format("%s [ %d, %d, %d ]", style, (int) x, (int) y, (int) z);
            command = String.format("!/tp %d %d %d", (int) x, (int) y, (int) z);
        } else {
            text = String.format("%s [ %.2f, %.2f, %.2f]", style, x, y, z);
            command = String.format("!/tp %f %f %f", x, y, z);
        }
        return m(null, text, command);
    }

    /*
     * builds single line, multicomponent message, optionally returns it to sender, and returns as one chat messagge
     */
    public static IChatComponent m(ICommandSender receiver, Object... fields) {
        IChatComponent message = new ChatComponentText("");
        IChatComponent previous_component = null;
        for (Object o : fields) {
            if (o instanceof IChatComponent) {
                message.appendSibling((IChatComponent) o);
                previous_component = (IChatComponent) o;
                continue;
            }
            String txt = o.toString();
            // CarpetSettings.LOG.error(txt);
            IChatComponent comp = resolve(txt, previous_component);
            if (comp != previous_component) message.appendSibling(comp);
            previous_component = comp;
        }
        if (receiver != null) receiver.addChatMessage(message);
        return message;
    }

    public static IChatComponent s(ICommandSender receiver, String text) {
        return s(receiver, text, "");
    }

    public static IChatComponent s(ICommandSender receiver, String text, String style) {
        IChatComponent message = new ChatComponentText(text);
        _applyStyleToTextComponent(message, style);
        if (receiver != null) receiver.addChatMessage(message);
        return message;
    }

    public static void send(ICommandSender receiver, IChatComponent... messages) {
        send(receiver, Arrays.asList(messages));
    }

    public static void send(ICommandSender receiver, List<IChatComponent> list) {
        list.forEach(receiver::addChatMessage);
    }

    public static void print_server_message(MinecraftServer server, String message) {
        if (server == null) {
            LOG.error("Message not delivered: {}", message);
        }
        server.addChatMessage(new ChatComponentText(message));
        IChatComponent txt = m(null, "gi " + message);
        for (EntityPlayer entityplayer : server.getConfigurationManager().playerEntityList) {
            entityplayer.addChatMessage(new ChatComponentText(message));
        }
    }

    public static void print_server_message(MinecraftServer server, IChatComponent message) {
        if (server == null) {
            LOG.error("Message not delivered: {}", message.getUnformattedText());
        }
        server.addChatMessage(message);
        for (EntityPlayer entityplayer : server.getConfigurationManager().playerEntityList) {
            entityplayer.addChatMessage(message);
        }
    }
}
