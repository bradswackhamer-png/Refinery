package com.refinery.fluid;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;

public class RefineryFluidType extends FluidType {

    private final double dragHorizontal;
    private final double dragVertical;
    private final double gravityFactor;
    private final float inputSpeed;

    public RefineryFluidType(Properties properties, double dragHorizontal, double dragVertical, double gravityFactor, float inputSpeed) {
        super(properties);
        this.dragHorizontal = dragHorizontal;
        this.dragVertical = dragVertical;
        this.gravityFactor = gravityFactor;
        this.inputSpeed = inputSpeed;
    }

    public RefineryFluidType(Properties properties) {
        // Default thick, viscous fluid behavior (matches viscous crude oil & lava movement rules)
        this(properties, 0.55D, 0.80D, 0.25D, 0.015F);
    }

    @Override
    public boolean move(LivingEntity entity, Vec3 movementVector, double gravity) {
        // 1. Accelerate entity according to player movement inputs (WASD)
        entity.moveRelative(this.inputSpeed, movementVector);

        // 2. Perform entity collision and movement through the level
        entity.move(MoverType.SELF, entity.getDeltaMovement());

        // 3. Apply fluid drag / viscosity resistance
        Vec3 delta = entity.getDeltaMovement();
        delta = delta.multiply(this.dragHorizontal, this.dragVertical, this.dragHorizontal);

        // 4. Apply gentle sinking gravity (or buoyant upward force if gravityFactor is negative)
        if (gravity > 0.0D) {
            delta = delta.add(0.0D, -gravity * this.gravityFactor, 0.0D);
        }

        entity.setDeltaMovement(delta);
        return true;
    }

    @Override
    public void setItemMovement(ItemEntity entity) {
        // Viscous fluid slows down dropped items realistically
        entity.setFluidMovement(0.85F);
    }
}
