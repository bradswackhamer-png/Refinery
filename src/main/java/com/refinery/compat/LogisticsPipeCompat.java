package com.refinery.compat;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.List;

public class LogisticsPipeCompat {
    private static boolean initialized = false;
    private static Class<?> itemPipeClass = null;
    private static Class<?> fluidPipeClass = null;
    private static MethodHandle addItemHandle = null;
    private static MethodHandle getTravelingItemsHandle = null;
    private static MethodHandle totalMilliBucketsHandle = null;

    private static void init() {
        if (initialized) return;
        initialized = true;
        try {
            itemPipeClass = Class.forName("com.logistics.pipe.block.entity.PipeBlockEntity");
            Method addItemMethod = itemPipeClass.getMethod("addItem", Direction.class, ItemStack.class);
            addItemHandle = MethodHandles.lookup().unreflect(addItemMethod);

            Method getTravelingMethod = itemPipeClass.getMethod("getTravelingItems");
            getTravelingItemsHandle = MethodHandles.lookup().unreflect(getTravelingMethod);
        } catch (Throwable ignored) {
        }

        try {
            fluidPipeClass = Class.forName("com.logistics.pipe.block.entity.FluidPipeBlockEntity");
            Method totalMbMethod = fluidPipeClass.getMethod("totalMillibuckets");
            totalMilliBucketsHandle = MethodHandles.lookup().unreflect(totalMbMethod);
        } catch (Throwable ignored) {
        }
    }

    public static boolean isLogisticsPipe(BlockEntity be) {
        if (be == null) return false;
        init();
        return (itemPipeClass != null && itemPipeClass.isInstance(be)) ||
               (fluidPipeClass != null && fluidPipeClass.isInstance(be));
    }

    public static boolean isLogisticsItemPipe(BlockEntity be) {
        if (be == null) return false;
        init();
        return itemPipeClass != null && itemPipeClass.isInstance(be);
    }

    public static boolean isLogisticsFluidPipe(BlockEntity be) {
        if (be == null) return false;
        init();
        return fluidPipeClass != null && fluidPipeClass.isInstance(be);
    }

    public static boolean addItemToPipe(BlockEntity pipeBe, Direction fromDir, ItemStack stack) {
        if (pipeBe == null || addItemHandle == null || !isLogisticsItemPipe(pipeBe)) return false;
        try {
            Object result = addItemHandle.invoke(pipeBe, fromDir, stack);
            return result instanceof Boolean b && b;
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean hasTravelingItems(BlockEntity pipeBe) {
        if (pipeBe == null || getTravelingItemsHandle == null || !isLogisticsItemPipe(pipeBe)) return false;
        try {
            Object result = getTravelingItemsHandle.invoke(pipeBe);
            if (result instanceof List<?> list) {
                return !list.isEmpty();
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    public static long getFluidMilliBuckets(BlockEntity pipeBe) {
        if (pipeBe == null || totalMilliBucketsHandle == null || !isLogisticsFluidPipe(pipeBe)) return 0;
        try {
            Object result = totalMilliBucketsHandle.invoke(pipeBe);
            if (result instanceof Number num) {
                return num.longValue();
            }
        } catch (Throwable ignored) {
        }
        return 0;
    }
}
