package gay.nyako.itemrename;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.TextParserUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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
            NbtCompound nbt = heldStack.getOrCreateNbt();
            NbtCompound nbtDisplay = nbt.getCompound(ItemStack.DISPLAY_KEY);
            NbtList nbtLore = new NbtList();

            nbtDisplay.put(ItemStack.LORE_KEY, nbtLore);
            nbt.put(ItemStack.DISPLAY_KEY, nbtDisplay);
            heldStack.setNbt(nbt);
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
            NbtCompound nbt = heldStack.getOrCreateNbt();
            NbtCompound nbtDisplay = nbt.getCompound(ItemStack.DISPLAY_KEY);
            NbtList nbtLore = nbtDisplay.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE);

            nbtLore.add(NbtString.of(Text.Serializer.toJson(((MutableText)newText).styled(x -> x.withItalic(false)))));

            nbtDisplay.put(ItemStack.LORE_KEY, nbtLore);
            nbt.put(ItemStack.DISPLAY_KEY, nbtDisplay);
            heldStack.setNbt(nbt);
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
            NbtCompound nbt = heldStack.getOrCreateNbt();
            NbtCompound nbtDisplay = nbt.getCompound(ItemStack.DISPLAY_KEY);
            NbtList nbtLore = nbtDisplay.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE);

            nbtLore.add(NbtString.of(Text.Serializer.toJson(Text.empty())));

            nbtDisplay.put(ItemStack.LORE_KEY, nbtLore);
            nbt.put(ItemStack.DISPLAY_KEY, nbtDisplay);
            heldStack.setNbt(nbt);
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
            NbtCompound nbt = heldStack.getOrCreateNbt();
            NbtCompound nbtDisplay = nbt.getCompound(ItemStack.DISPLAY_KEY);
            NbtList nbtLore = nbtDisplay.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE);

            nbtLore.add(index, NbtString.of(Text.Serializer.toJson(Text.empty())));

            nbtDisplay.put(ItemStack.LORE_KEY, nbtLore);
            nbt.put(ItemStack.DISPLAY_KEY, nbtDisplay);
            heldStack.setNbt(nbt);
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
            NbtCompound nbt = heldStack.getOrCreateNbt();
            NbtCompound nbtDisplay = nbt.getCompound(ItemStack.DISPLAY_KEY);
            NbtList nbtLore = nbtDisplay.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE);

            nbtLore.add(index, NbtString.of(Text.Serializer.toJson(((MutableText)newText).styled(x -> x.withItalic(false)))));

            nbtDisplay.put(ItemStack.LORE_KEY, nbtLore);
            nbt.put(ItemStack.DISPLAY_KEY, nbtDisplay);
            heldStack.setNbt(nbt);
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
            NbtCompound nbt = heldStack.getOrCreateNbt();
            NbtCompound nbtDisplay = nbt.getCompound(ItemStack.DISPLAY_KEY);
            NbtList nbtLore = nbtDisplay.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE);

            if (index < 0 || index >= nbtLore.size()) {
                context.getSource().sendError(Text.literal("Index out of bounds."));
            } else {
                nbtLore.set(index, NbtString.of(Text.Serializer.toJson(((MutableText)newText).styled(x -> x.withItalic(false)))));

                nbtDisplay.put(ItemStack.LORE_KEY, nbtLore);
                nbt.put(ItemStack.DISPLAY_KEY, nbtDisplay);
                heldStack.setNbt(nbt);
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
            NbtCompound nbt = heldStack.getOrCreateNbt();
            NbtCompound nbtDisplay = nbt.getCompound(ItemStack.DISPLAY_KEY);
            NbtList nbtLore = nbtDisplay.getList(ItemStack.LORE_KEY, NbtElement.STRING_TYPE);

            if (index < 0 || index >= nbtLore.size()) {
                context.getSource().sendError(Text.literal("Index out of bounds."));
            } else {
                nbtLore.remove(index);

                nbtDisplay.put(ItemStack.LORE_KEY, nbtLore);
                nbt.put(ItemStack.DISPLAY_KEY, nbtDisplay);
                heldStack.setNbt(nbt);
                context.getSource().sendFeedback(() -> Text.literal("Lore removed."), false);
            }
        }
        return 1;
    }
}
