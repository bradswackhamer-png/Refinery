package com.refinery.gate;

public enum GateAction {
    NONE("None", "No action executed", 0x888888, "None"),
    REDSTONE_SIGNAL("Emit Redstone", "Emits constant redstone signal (power 15)", 0xFF2222, "Redstone"),
    REDSTONE_PULSE("Redstone Pulse (1s)", "Emits periodic redstone pulses (1 sec)", 0xFF6666, "Pls 1s"),
    PULSAR_PULL_ITEM("Autarchic Pulse (Item)", "Pulls 1 item from container into pipe", 0x22EEFF, "Pull Item"),
    PULSAR_PUMP_FLUID("Autarchic Pulse (Fluid)", "Pumps fluid from tank into pipe", 0x00BBDD, "Pump Fluid"),
    EMIT_WIRE_RED("Emit Red Wire", "Broadcasts Red Wire signal along pipe network", 0xFF2222, "Red Wire"),
    EMIT_WIRE_BLUE("Emit Blue Wire", "Broadcasts Blue Wire signal along pipe network", 0x2266FF, "Blue Wire"),
    EMIT_WIRE_GREEN("Emit Green Wire", "Broadcasts Green Wire signal along pipe network", 0x22DD22, "Grn Wire"),
    EMIT_WIRE_YELLOW("Emit Yellow Wire", "Broadcasts Yellow Wire signal along pipe network", 0xFFFF22, "Yel Wire"),
    // Appended last to keep saved ordinals stable
    PULSAR_PULL_ITEM_5("Autarchic Pulse (Item x5)", "Pulls 5 items at a time from container into pipe", 0x22EEFF, "Pull x5"),
    PULSAR_PULL_ITEM_10("Autarchic Pulse (Item x10)", "Pulls 10 items at a time from container into pipe", 0x22EEFF, "Pull x10"),
    REDSTONE_PULSE_HALF_SEC("Redstone Pulse (0.5s)", "Emits rapid periodic redstone pulses (0.5 sec)", 0xFF8888, "Pls .5s");

    private final String label;
    private final String description;
    private final int displayColor;
    private final String shortLabel;

    GateAction(String label, String description, int displayColor, String shortLabel) {
        this.label = label;
        this.description = description;
        this.displayColor = 0xFF000000 | displayColor;
        this.shortLabel = shortLabel;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public int getDisplayColor() {
        return displayColor;
    }

    public String getShortLabel() {
        return shortLabel;
    }

    public boolean isPulsar() {
        return this == PULSAR_PULL_ITEM || this == PULSAR_PULL_ITEM_5 || this == PULSAR_PULL_ITEM_10 || this == PULSAR_PUMP_FLUID;
    }

    /** Number of items moved per pulse for item-pull actions. */
    public int getItemCount() {
        return switch (this) {
            case PULSAR_PULL_ITEM_5 -> 5;
            case PULSAR_PULL_ITEM_10 -> 10;
            default -> 1;
        };
    }

    public boolean isWireAction() {
        return this.name().startsWith("EMIT_WIRE_");
    }

    public PipeWireColor getWireColor() {
        if (this == EMIT_WIRE_RED) return PipeWireColor.RED;
        if (this == EMIT_WIRE_BLUE) return PipeWireColor.BLUE;
        if (this == EMIT_WIRE_GREEN) return PipeWireColor.GREEN;
        if (this == EMIT_WIRE_YELLOW) return PipeWireColor.YELLOW;
        return null;
    }

    public boolean isValid(GateTier tier, boolean autarchic) {
        if (isPulsar() && !autarchic) return false;
        if (isWireAction()) {
            PipeWireColor color = getWireColor();
            return color != null && tier.supportsWire(color);
        }
        return true;
    }

    // Natural grouped order for in-game cycling
    public static final GateAction[] CYCLE_ORDER = {
        NONE,
        REDSTONE_SIGNAL,
        REDSTONE_PULSE,
        REDSTONE_PULSE_HALF_SEC,
        PULSAR_PULL_ITEM,
        PULSAR_PULL_ITEM_5,
        PULSAR_PULL_ITEM_10,
        PULSAR_PUMP_FLUID,
        EMIT_WIRE_RED,
        EMIT_WIRE_BLUE,
        EMIT_WIRE_GREEN,
        EMIT_WIRE_YELLOW
    };

    public GateAction next(GateTier tier, boolean autarchic) {
        int idx = 0;
        for (int i = 0; i < CYCLE_ORDER.length; i++) {
            if (CYCLE_ORDER[i] == this) {
                idx = i;
                break;
            }
        }
        for (int i = 1; i <= CYCLE_ORDER.length; i++) {
            GateAction next = CYCLE_ORDER[(idx + i) % CYCLE_ORDER.length];
            if (next.isValid(tier, autarchic)) {
                return next;
            }
        }
        return NONE;
    }

    public GateAction previous(GateTier tier, boolean autarchic) {
        int idx = 0;
        for (int i = 0; i < CYCLE_ORDER.length; i++) {
            if (CYCLE_ORDER[i] == this) {
                idx = i;
                break;
            }
        }
        for (int i = 1; i <= CYCLE_ORDER.length; i++) {
            GateAction prev = CYCLE_ORDER[(idx - i + CYCLE_ORDER.length) % CYCLE_ORDER.length];
            if (prev.isValid(tier, autarchic)) {
                return prev;
            }
        }
        return NONE;
    }
}
