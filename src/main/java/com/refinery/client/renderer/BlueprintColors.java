package com.refinery.client.renderer;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class BlueprintColors {

    public static int getTextColor(String name, int fallbackColor) {
        if (name == null) return 0xFFFFFF;
        String lower = name.toLowerCase().trim();

        // 1. Controllers (Vivid Emerald Green)
        if (lower.contains("controller")) {
            return 0x22C55E;
        }

        // 2. Specialized Ports (Each distinct by function and fluid type)
        if (lower.contains("crude oil")) {
            return 0xF59E0B; // Petroleum Amber Gold (High contrast, clearly visible)
        }
        if (lower.contains("diesel")) {
            return 0xFACC15; // Canary Yellow
        }
        if (lower.contains("gas port") || lower.contains("gasoline")) {
            return 0x06B6D4; // Electric Cyan
        }
        if (lower.contains("steam")) {
            return 0x38BDF8; // Frost Sky Blue
        }
        if (lower.contains("water")) {
            return 0x3B82F6; // Royal Water Blue
        }
        if (lower.contains("energy")) {
            return 0xA3E635; // Neon Lime Green
        }
        if (lower.contains("fuel")) {
            return 0xFB923C; // Flame Orange
        }
        if (lower.contains("byproduct")) {
            return 0xEC4899; // Radiant Fuchsia Pink
        }
        if (lower.contains("foundry input") || (lower.contains("grinder") && lower.contains("input"))) {
            return 0x4ADE80; // Bright Mint Green
        }
        if (lower.contains("foundry output") || (lower.contains("grinder") && lower.contains("output"))) {
            return 0xFB923C; // Warm Tangerine
        }

        // 3. Functional Internal Components
        if (lower.contains("combustion chamber") || lower.contains("firebox")) {
            return 0xEF4444; // Fiery Crimson Red
        }
        if (lower.contains("heat exchanger")) {
            return 0xFB7185; // Coral Rose
        }
        if (lower.contains("distillation column")) {
            return 0x67E8F9; // Aquamarine Ice
        }
        if (lower.contains("glass")) {
            return 0xC084FC; // Crystal Amethyst Purple
        }
        if (lower.contains("pressure pipe") || lower.contains("pipe")) {
            return 0xFDBA74; // Copper / Bronze
        }
        if (lower.contains("rotor")) {
            return 0x2DD4BF; // Electric Teal
        }
        if (lower.contains("anvil") || lower.contains("press bed")) {
            return 0x818CF8; // Cobalt Indigo
        }
        if (lower.contains("hydraulic") || lower.contains("press head")) {
            return 0xA855F7; // Vivid Purple
        }
        if (lower.contains("crushing chamber") || lower.contains("chamber")) {
            return 0xF43F5E; // Deep Ruby
        }
        if (lower.contains("empty chamber") || lower.contains("air")) {
            return 0xE2E8F0; // Soft White
        }

        // 4. Casings (High-contrast distinct metallic and industrial tones)
        if (lower.contains("reinforced casing")) {
            return 0xCBD5E1; // Platinum Slate
        }
        if (lower.contains("blast furnace casing") || lower.contains("foundry casing")) {
            return 0x93C5FD; // Light Steel Blue
        }
        if (lower.contains("steel boiler casing") || lower.contains("boiler casing")) {
            return 0xEAB308; // Industrial Brass
        }
        if (lower.contains("turbine casing")) {
            return 0xA5F3FC; // Titanium Cyan
        }
        if (lower.contains("press casing")) {
            return 0xC7D2FE; // Heavy Periwinkle
        }
        if (lower.contains("grinder casing") || lower.contains("structural casing")) {
            return 0xFCD34D; // Industrial Hazard Yellow
        }

        // Fallback to brightened outline color if specified
        int rgb = fallbackColor & 0x00FFFFFF;
        if (rgb > 0x1F1F1F) {
            return rgb;
        }

        return 0xFFFFFF;
    }

    public static int getOutlineColor(String name, int originalColor) {
        int textRgb = getTextColor(name, originalColor);
        return 0xFF000000 | textRgb;
    }

    public static Component formatName(String name, int fallbackColor) {
        int rgb = getTextColor(name, fallbackColor);
        return Component.literal(name).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb)).withBold(true));
    }
}
