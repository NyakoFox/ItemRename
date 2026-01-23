package gay.nyako.itemrename;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.TextParserUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import static net.minecraft.commands.Commands.*;

public final class LoreCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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

    public static int clearLore(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();
        if (player == null) return 0;
        ItemStack heldStack = player.getMainHandItem();
        if (heldStack.isEmpty()) {
            context.getSource().sendFailure(Component.literal("You can't clear the lore of nothing."));
        } else {
            heldStack.remove(DataComponents.LORE);
            context.getSource().sendSuccess(() -> Component.literal("Lore cleared."), false);
        }
        return 1;
    }

    public static int addLore(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();
        if (player == null) return 0;
        ItemStack heldStack = player.getMainHandItem();
        Component newText = TextParserUtils.formatTextSafe(context.getArgument("text", String.class));
        if (heldStack.isEmpty()) {
            source.sendFailure(Component.literal("You can't add lore to nothing."));
        } else {
            var currentLore = heldStack.getOrDefault(DataComponents.LORE, new ItemLore(List.of()));
            ArrayList<Component> lines = new ArrayList<>(currentLore.lines());
            lines.add(((MutableComponent)newText).withStyle(x -> x.withItalic(false)));
            heldStack.set(DataComponents.LORE, new ItemLore(lines));
            source.sendSuccess(() -> Component.literal("Lore applied."), false);
        }
        return 1;
    }

    public static int addEmptyLore(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();
        if (player == null) return 0;
        ItemStack heldStack = player.getMainHandItem();
        if (heldStack.isEmpty()) {
            source.sendFailure(Component.literal("You can't add lore to nothing."));
        } else {
            var currentLore = heldStack.getOrDefault(DataComponents.LORE, new ItemLore(List.of()));
            ArrayList<Component> lines = new ArrayList<>(currentLore.lines());
            lines.add(Component.empty());
            heldStack.set(DataComponents.LORE, new ItemLore(lines));
            source.sendSuccess(() -> Component.literal("Lore applied."), false);
        }
        return 1;
    }

    public static int addEmptyLoreIndex(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();
        if (player == null) return 0;
        ItemStack heldStack = player.getMainHandItem();
        int index = context.getArgument("index", Integer.class);
        if (heldStack.isEmpty()) {
            source.sendFailure(Component.literal("You can't add lore to nothing."));
        } else {
            var currentLore = heldStack.getOrDefault(DataComponents.LORE, new ItemLore(List.of()));
            ArrayList<Component> lines = new ArrayList<>(currentLore.lines());
            lines.add(index, Component.empty());
            heldStack.set(DataComponents.LORE, new ItemLore(lines));
            source.sendSuccess(() -> Component.literal("Lore applied."), false);
        }
        return 1;
    }

    public static int addLoreIndex(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();
        if (player == null) return 0;
        ItemStack heldStack = player.getMainHandItem();
        int index = context.getArgument("index", Integer.class);
        Component newText = TextParserUtils.formatTextSafe(context.getArgument("lore", String.class));
        if (heldStack.isEmpty()) {
            source.sendFailure(Component.literal("You can't add lore to nothing."));
        } else {
            var currentLore = heldStack.getOrDefault(DataComponents.LORE, new ItemLore(List.of()));
            ArrayList<Component> lines = new ArrayList<>(currentLore.lines());
            lines.add(index, ((MutableComponent)newText).withStyle(x -> x.withItalic(false)));
            heldStack.set(DataComponents.LORE, new ItemLore(lines));
            source.sendSuccess(() -> Component.literal("Lore applied."), false);
        }
        return 1;
    }

    public static int setLore(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();
        if (player == null) return 0;
        ItemStack heldStack = player.getMainHandItem();
        Component newText = TextParserUtils.formatTextSafe(context.getArgument("text", String.class));
        int index = context.getArgument("index", Integer.class);
        if (heldStack.isEmpty()) {
            context.getSource().sendFailure(Component.literal("You can't set the lore of nothing."));
        } else {
            var currentLore = heldStack.getOrDefault(DataComponents.LORE, new ItemLore(List.of()));
            ArrayList<Component> lines = new ArrayList<>(currentLore.lines());
            if (index < 0 || index >= lines.size()) {
                context.getSource().sendFailure(Component.literal("Index out of bounds."));
            } else {
                lines.set(index, ((MutableComponent)newText).withStyle(x -> x.withItalic(false)));
                heldStack.set(DataComponents.LORE, new ItemLore(lines));
                context.getSource().sendSuccess(() -> Component.literal("Lore applied."), false);
            }
        }
        return 1;
    }

    public static int removeLore(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();
        if (player == null) return 0;
        ItemStack heldStack = player.getMainHandItem();
        int index = context.getArgument("index", Integer.class);
        if (heldStack.isEmpty()) {
            context.getSource().sendFailure(Component.literal("You can't remove the lore of nothing."));
        } else {
            var currentLore = heldStack.getOrDefault(DataComponents.LORE, new ItemLore(List.of()));
            ArrayList<Component> lines = new ArrayList<>(currentLore.lines());
            if (index < 0 || index >= lines.size()) {
                context.getSource().sendFailure(Component.literal("Index out of bounds."));
            } else {
                lines.remove(index);
                heldStack.set(DataComponents.LORE, new ItemLore(lines));
                context.getSource().sendSuccess(() -> Component.literal("Lore removed."), false);
            }
        }
        return 1;
    }
}
