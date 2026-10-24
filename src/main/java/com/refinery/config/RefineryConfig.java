package com.refinery.config;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class RefineryConfig {
    public static final ModConfigSpec SPEC;
    public static final RefineryConfig CONFIG;

    static {
        Pair<RefineryConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(RefineryConfig::new);
        SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    public final ModConfigSpec.BooleanValue enableOilRetrogen;
    public final ModConfigSpec.DoubleValue oilPitSpawnChance;
    public final ModConfigSpec.IntValue minChunkDistanceBetweenWells;
    public final ModConfigSpec.DoubleValue rareGeyserChance;
    public final ModConfigSpec.IntValue minOilVolumeMb;
    public final ModConfigSpec.IntValue maxOilVolumeMb;
    public final ModConfigSpec.IntValue geyserOilVolumeMb;
    public final ModConfigSpec.ConfigValue<List<? extends String>> allowedBiomes;

    public RefineryConfig(ModConfigSpec.Builder builder) {
        builder.push("Oil_Worldgen_And_Retrogen");

        enableOilRetrogen = builder
                .comment("Enable automatic retrogen of Crude Oil pits and geysers in explored and loaded chunks.")
                .define("enableOilRetrogen", true);

        oilPitSpawnChance = builder
                .comment("Chance per valid chunk to spawn an oil pit (0.04 = ~4% chance, or roughly 1 in 25 chunks).")
                .defineInRange("oilPitSpawnChance", 0.04, 0.0, 1.0);

        minChunkDistanceBetweenWells = builder
                .comment("Minimum chunk distance between oil wells to prevent clustering (e.g. 4 = wells will be at least 4 chunks apart).")
                .defineInRange("minChunkDistanceBetweenWells", 4, 1, 32);

        rareGeyserChance = builder
                .comment("Chance that a spawned oil pit is a massive 1,000,000 mB super-geyser instead of a standard pit (0.20 = 20%).")
                .defineInRange("rareGeyserChance", 0.20, 0.0, 1.0);

        minOilVolumeMb = builder
                .comment("Minimum oil capacity of a standard oil pit (in mB, 1 source block = 1,000 mB).")
                .defineInRange("minOilVolumeMb", 300000, 50000, 2000000);

        maxOilVolumeMb = builder
                .comment("Maximum oil capacity of a standard oil pit (in mB).")
                .defineInRange("maxOilVolumeMb", 500000, 50000, 5000000);

        geyserOilVolumeMb = builder
                .comment("Oil capacity of a rare super-geyser (in mB).")
                .defineInRange("geyserOilVolumeMb", 1000000, 100000, 10000000);

        allowedBiomes = builder
                .comment("List of biome registry IDs or biome tags (#tag) where oil pits and geysers can spawn.",
                        "Default includes all Ocean variants, Deserts, Badlands/Mesa, and compatible modded biomes.")
                .defineListAllowEmpty(
                        "allowedBiomes",
                        () -> List.of(
                                "#minecraft:is_ocean",
                                "#minecraft:is_deep_ocean",
                                "minecraft:ocean",
                                "minecraft:deep_ocean",
                                "minecraft:warm_ocean",
                                "minecraft:lukewarm_ocean",
                                "minecraft:deep_lukewarm_ocean",
                                "minecraft:cold_ocean",
                                "minecraft:deep_cold_ocean",
                                "minecraft:frozen_ocean",
                                "minecraft:deep_frozen_ocean",
                                "#minecraft:is_desert",
                                "minecraft:desert",
                                "#minecraft:is_badlands",
                                "minecraft:badlands",
                                "minecraft:eroded_badlands",
                                "minecraft:wooded_badlands"
                        ),
                        obj -> obj instanceof String
                );

        builder.pop();
    }

    public boolean isBiomeAllowed(Holder<Biome> biomeHolder) {
        if (biomeHolder == null) return false;
        List<? extends String> list = allowedBiomes.get();

        Identifier biomeId = biomeHolder.unwrapKey().map(ResourceKey::identifier).orElse(null);
        String idStr = biomeId != null ? biomeId.toString() : "";

        for (String entry : list) {
            if (entry.startsWith("#")) {
                Identifier tagId = Identifier.tryParse(entry.substring(1));
                if (tagId != null) {
                    TagKey<Biome> tag = TagKey.create(Registries.BIOME, tagId);
                    if (biomeHolder.is(tag)) {
                        return true;
                    }
                }
            } else {
                if (entry.equalsIgnoreCase(idStr)) {
                    return true;
                }
            }
        }

        // Fallback matching for modded biomes: if mod biome ID contains "desert", "mesa", "badlands", "ocean", or "oil"
        String path = biomeId != null ? biomeId.getPath().toLowerCase() : "";
        if (path.contains("desert") || path.contains("badlands") || path.contains("mesa") || path.contains("ocean") || path.contains("oil")) {
            return true;
        }

        return false;
    }
}
