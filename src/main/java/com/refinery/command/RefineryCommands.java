package com.refinery.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.refinery.worldgen.OilPitGenerator;
import com.refinery.worldgen.OilRetrogenSavedData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class RefineryCommands {

    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Register under /refinery
        dispatcher.register(
            Commands.literal("refinery")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("spawn_oil_pit")
                    .executes(ctx -> spawnOilPit(ctx.getSource(), false))
                    .then(Commands.argument("is_geyser", BoolArgumentType.bool())
                        .executes(ctx -> spawnOilPit(ctx.getSource(), BoolArgumentType.getBool(ctx, "is_geyser")))))
                .then(Commands.literal("spawn_oil")
                    .executes(ctx -> spawnOilPit(ctx.getSource(), false))
                    .then(Commands.argument("is_geyser", BoolArgumentType.bool())
                        .executes(ctx -> spawnOilPit(ctx.getSource(), BoolArgumentType.getBool(ctx, "is_geyser")))))
                .then(Commands.literal("spawn_geyser")
                    .executes(ctx -> spawnOilPit(ctx.getSource(), true)))
                .then(Commands.literal("stats")
                    .executes(ctx -> showStats(ctx.getSource())))
                .then(Commands.literal("reset_retrogen")
                    .executes(ctx -> resetRetrogen(ctx.getSource())))
        );

        // Also register under /admin for immediate convenience
        dispatcher.register(
            Commands.literal("admin")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("spawn_oil_pit")
                    .executes(ctx -> spawnOilPit(ctx.getSource(), false))
                    .then(Commands.argument("is_geyser", BoolArgumentType.bool())
                        .executes(ctx -> spawnOilPit(ctx.getSource(), BoolArgumentType.getBool(ctx, "is_geyser")))))
                .then(Commands.literal("spawn_oil")
                    .executes(ctx -> spawnOilPit(ctx.getSource(), false))
                    .then(Commands.argument("is_geyser", BoolArgumentType.bool())
                        .executes(ctx -> spawnOilPit(ctx.getSource(), BoolArgumentType.getBool(ctx, "is_geyser")))))
                .then(Commands.literal("spawn_geyser")
                    .executes(ctx -> spawnOilPit(ctx.getSource(), true)))
                .then(Commands.literal("oil_stats")
                    .executes(ctx -> showStats(ctx.getSource())))
                .then(Commands.literal("reset_oil_retrogen")
                    .executes(ctx -> resetRetrogen(ctx.getSource())))
        );
    }

    private static int spawnOilPit(CommandSourceStack source, boolean isGeyser) {
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());
        RandomSource random = level.getRandom();

        source.sendSuccess(() -> Component.literal("§6[Refinery Admin] §eSpawning " + (isGeyser ? "2,000,000 mB Super-Geyser" : "600,000-1,000,000 mB Oil Pit") + " at " + pos.toShortString() + "..."), true);
        boolean success = OilPitGenerator.generate(level, pos, isGeyser, random);
        if (success) {
            OilRetrogenSavedData data = OilRetrogenSavedData.get(level);
            data.markWellGenerated(ChunkPos.pack(pos));
            source.sendSuccess(() -> Component.literal("§a[Refinery Admin] Crude oil deposit successfully generated and registered!"), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("§c[Refinery Admin] Could not generate oil deposit here (invalid height or bounds)."));
            return 0;
        }
    }

    private static int showStats(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        OilRetrogenSavedData data = OilRetrogenSavedData.get(level);
        int total = data.getProcessedChunkCount();
        int wells = data.getWellChunkCount();
        source.sendSuccess(() -> Component.literal("§6[Refinery Retrogen Stats] §eScanned Chunks: §a" + total + " §e| Generated Wells: §b" + wells), false);
        return total;
    }

    private static int resetRetrogen(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        OilRetrogenSavedData data = OilRetrogenSavedData.get(level);
        data.clear();
        source.sendSuccess(() -> Component.literal("§6[Refinery Retrogen] §cRetrogen history cleared! §eChunks will be rescanned upon next load."), true);
        return 1;
    }
}
