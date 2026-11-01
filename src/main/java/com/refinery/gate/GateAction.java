package com.refinery.gate;

public enum GateAction {
    NONE("None", "No action executed", 0x888888),
    REDSTONE_SIGNAL("Emit Redstone", "Emits constant redstone signal (power 15)", 0xFF2222),
    REDSTONE_PULSE("Redstone Pulse", "Emits periodic redstone pulses (1 sec)", 0xFF6666),
    PULSAR_PULL_ITEM("Autarchic Pulse (Item)", "Pulls 1 item from container into pipe", 0x22EEFF),
    PULSAR_PUMP_FLUID("Autarchic Pulse (Fluid)", "Pumps fluid from tank into pipe", 0x00BBDD),
    EMIT_WIRE_RED("Emit Red Wire", "Broadcasts Red Wire signal along pipe network", 0xFF2222),
    EMIT_WIRE_BLUE("Emit Blue Wire", "Broadcasts Blue Wire signal along pipe network", 0x2266FF),
    EMIT_WIRE_GREEN("Emit Green Wire", "Broadcasts Green Wire signal along pipe network", 0x22DD22),
    EMIT_WIRE_YELLOW("Emit Yellow Wire", "Broadcasts Yellow Wire signal along pipe network", 0xFFFF22);

    private final String label;
    private final String description;
    private final int displayColor;

    GateAction(String label, String description, int displayColor) {
        this.label = label;
        this.description = description;
        this.displayColor = 0xFF000000 | displayColor;
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

    public boolean isPulsar() {
        return this == PULSAR_PULL_ITEM || this == PULSAR_PUMP_FLUID;
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

    public GateAction next(GateTier tier, boolean autarchic) {
        GateAction[] values = values();
        int idx = this.ordinal();
        for (int i = 1; i <= values.length; i++) {
            GateAction next = values[(idx + i) % values.length];
            if (next.isValid(tier, autarchic)) {
                return next;
            }
        }
        return NONE;
    }

    public GateAction previous(GateTier tier, boolean autarchic) {
        GateAction[] values = values();
        int idx = this.ordinal();
        for (int i = 1; i <= values.length; i++) {
            GateAction prev = values[(idx - i + values.length) % values.length];
            if (prev.isValid(tier, autarchic)) {
                return prev;
            }
        }
        return NONE;
    }
}
