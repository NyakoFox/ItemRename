package gay.nyako.itemrename;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.TextParserUtils;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.*;

public final class LoreCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("lore")
                .then(literal("clear").executes(LoreCommand::clearLore))
                .then(literal("add")
                        .executes(LoreCommand::addEmptyLore)
                        .then(argument("text", StringArgumentType.greedyString()).executes(LoreCommand::addLore)))
                .then(literal("insert")
                    .then(argument("index", IntegerArgumentType.integer())
                            .executes(LoreCommand::addEmptyLoreIndex)
                            .then(argument("lore", StringArgumentType.greedyString())
                                    .executes(LoreCommand::addLoreIndex)
                            )
                    ))
                .then(literal("set")
                        .then(argument("index", IntegerArgumentType.integer())
                                .then(argument("text", StringArgumentType.greedyString()).executes(LoreCommand::setLore))))
                .then(literal("remove")
                        .then(argument("index", IntegerArgumentType.integer()).executes(LoreCommand::removeLore)))
        );
    }

    public static int clearLore(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        PlayerEntity player = source.getPlayer();
        if (player == null) return 0;
        ItemStack heldStack = player.getMainHandStack();
        if (heldStack.isEmpty()) {
            context.getSource().sendError(Text.literal("You can't clear the lore of nothing."));
        } else {
            heldStack.remove(DataComponentTypes.LORE);
            context.getSource().sendFeedback(() -> Text.literal("Lore cleared."), false);
        }
        return 1;
    }

    public static int addLore(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        PlayerEntity player = source.getPlayer();
        if (player == null) return 0;
        ItemStack heldStack = player.getMainHandStack();
        Text newText = TextParserUtils.formatTextSafe(context.getArgument("text", String.class));
        if (heldStack.isEmpty()) {
            source.sendError(Text.literal("You can't add lore to nothing."));
        } else {
            var currentLore = heldStack.getOrDefault(DataComponentTypes.LORE, new LoreComponent(List.of()));
            ArrayList<Text> lines = new ArrayList<>(currentLore.lines());
            lines.add(((MutableText)newText).styled(x -> x.withItalic(false)));
            heldStack.set(DataComponentTypes.LORE, new LoreComponent(lines));
            source.sendFeedback(() -> Text.literal("Lore applied."), false);
        }
        return 1;
    }

    public static int addEmptyLore(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        PlayerEntity player = source.getPlayer();
        if (player == null) return 0;
        ItemStack heldStack = player.getMainHandStack();
        if (heldStack.isEmpty()) {
            source.sendError(Text.literal("You can't add lore to nothing."));
        } else {
            var currentLore = heldStack.getOrDefault(DataComponentTypes.LORE, new LoreComponent(List.of()));
            ArrayList<Text> lines = new ArrayList<>(currentLore.lines());
            lines.add(Text.empty());
            heldStack.set(DataComponentTypes.LORE, new LoreComponent(lines));
            source.sendFeedback(() -> Text.literal("Lore applied."), false);
        }
        return 1;
    }

    public static int addEmptyLoreIndex(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        PlayerEntity player = source.getPlayer();
        if (player == null) return 0;
        ItemStack heldStack = player.getMainHandStack();
        int index = context.getArgument("index", Integer.class);
        if (heldStack.isEmpty()) {
            source.sendError(Text.literal("You can't add lore to nothing."));
        } else {
            var currentLore = heldStack.getOrDefault(DataComponentTypes.LORE, new LoreComponent(List.of()));
            ArrayList<Text> lines = new ArrayList<>(currentLore.lines());
            lines.add(index, Text.empty());
            heldStack.set(DataComponentTypes.LORE, new LoreComponent(lines));
            source.sendFeedback(() -> Text.literal("Lore applied."), false);
        }
        return 1;
    }

    public static int addLoreIndex(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        PlayerEntity player = source.getPlayer();
        if (player == null) return 0;
        ItemStack heldStack = player.getMainHandStack();
        int index = context.getArgument("index", Integer.class);
        Text newText = TextParserUtils.formatTextSafe(context.getArgument("lore", String.class));
        if (heldStack.isEmpty()) {
            source.sendError(Text.literal("You can't add lore to nothing."));
        } else {
            var currentLore = heldStack.getOrDefault(DataComponentTypes.LORE, new LoreComponent(List.of()));
            ArrayList<Text> lines = new ArrayList<>(currentLore.lines());
            lines.add(index, ((MutableText)newText).styled(x -> x.withItalic(false)));
            heldStack.set(DataComponentTypes.LORE, new LoreComponent(lines));
            source.sendFeedback(() -> Text.literal("Lore applied."), false);
        }
        return 1;
    }

    public static int setLore(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        PlayerEntity player = source.getPlayer();
        if (player == null) return 0;
        ItemStack heldStack = player.getMainHandStack();
        Text newText = TextParserUtils.formatTextSafe(context.getArgument("text", String.class));
        int index = context.getArgument("index", Integer.class);
        if (heldStack.isEmpty()) {
            context.getSource().sendError(Text.literal("You can't set the lore of nothing."));
        } else {
            var currentLore = heldStack.getOrDefault(DataComponentTypes.LORE, new LoreComponent(List.of()));
            ArrayList<Text> lines = new ArrayList<>(currentLore.lines());
            if (index < 0 || index >= lines.size()) {
                context.getSource().sendError(Text.literal("Index out of bounds."));
            } else {
                lines.set(index, ((MutableText)newText).styled(x -> x.withItalic(false)));
                heldStack.set(DataComponentTypes.LORE, new LoreComponent(lines));
                context.getSource().sendFeedback(() -> Text.literal("Lore applied."), false);
            }
        }
        return 1;
    }

    public static int removeLore(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        PlayerEntity player = source.getPlayer();
        if (player == null) return 0;
        ItemStack heldStack = player.getMainHandStack();
        int index = context.getArgument("index", Integer.class);
        if (heldStack.isEmpty()) {
            context.getSource().sendError(Text.literal("You can't remove the lore of nothing."));
        } else {
            var currentLore = heldStack.getOrDefault(DataComponentTypes.LORE, new LoreComponent(List.of()));
            ArrayList<Text> lines = new ArrayList<>(currentLore.lines());
            if (index < 0 || index >= lines.size()) {
                context.getSource().sendError(Text.literal("Index out of bounds."));
            } else {
                lines.remove(index);
                heldStack.set(DataComponentTypes.LORE, new LoreComponent(lines));
                context.getSource().sendFeedback(() -> Text.literal("Lore removed."), false);
            }
        }
        return 1;
    }
}
