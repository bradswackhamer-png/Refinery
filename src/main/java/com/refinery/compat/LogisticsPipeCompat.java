package com.refinery.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class LogisticsPipeCompat {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean initialized = false;
    private static Class<?> itemPipeClass = null;
    private static Class<?> fluidPipeClass = null;
    private static Class<?> travelingItemClass = null;
    private static Constructor<?> travelingItemConstructor = null;
    private static Method forceAddItemMethod = null;
    private static Method addItemMethod = null;
    private static Method getTravelingItemsMethod = null;
    private static Method totalMilliBucketsMethod = null;

    private static synchronized void init() {
        if (initialized) return;
        initialized = true;
        try {
            itemPipeClass = Class.forName("com.logistics.pipe.block.entity.PipeBlockEntity");
            travelingItemClass = Class.forName("com.logistics.core.lib.pipe.TravelingItem");
            travelingItemConstructor = travelingItemClass.getConstructor(ItemStack.class, Direction.class, float.class);

            forceAddItemMethod = itemPipeClass.getMethod("forceAddItem", travelingItemClass, Direction.class);
            forceAddItemMethod.setAccessible(true);

            addItemMethod = itemPipeClass.getMethod("addItem", Direction.class, ItemStack.class);
            addItemMethod.setAccessible(true);

            getTravelingItemsMethod = itemPipeClass.getMethod("getTravelingItems");
            getTravelingItemsMethod.setAccessible(true);
        } catch (Throwable t) {
            LOGGER.warn("Failed to initialize Logistics item pipe compatibility: {}", t.getMessage());
        }

        try {
            fluidPipeClass = Class.forName("com.logistics.pipe.block.entity.FluidPipeBlockEntity");
            totalMilliBucketsMethod = fluidPipeClass.getMethod("totalMillibuckets");
            totalMilliBucketsMethod.setAccessible(true);
        } catch (Throwable t) {
            LOGGER.warn("Failed to initialize Logistics fluid pipe compatibility: {}", t.getMessage());
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

    public static boolean injectItemIntoPipe(BlockEntity pipeBe, Direction fromDir, ItemStack stack) {
        if (pipeBe == null || stack == null || stack.isEmpty() || !isLogisticsItemPipe(pipeBe)) return false;
        init();

        // 1. First try forceAddItemMethod (takes TravelingItem and fromDir, bypasses normal pipe intake restrictions)
        if (forceAddItemMethod != null && travelingItemConstructor != null) {
            try {
                // When an item enters from 'fromDir' (e.g. from the adjacent machine),
                // its traveling motion heads towards fromDir.getOpposite() into the pipe network
                Object travelingItem = travelingItemConstructor.newInstance(stack, fromDir.getOpposite(), 0.05f);
                Object result = forceAddItemMethod.invoke(pipeBe, travelingItem, fromDir);
                if (result instanceof Boolean b && b) return true;
            } catch (Throwable t) {
                LOGGER.debug("Logistics forceAddItem reflection invocation failed: {}", t.getMessage());
            }
        }

        // 2. Next try addItemMethod(Direction, ItemStack)
        if (addItemMethod != null) {
            try {
                Object result = addItemMethod.invoke(pipeBe, fromDir, stack);
                if (result instanceof Boolean b && b) return true;
            } catch (Throwable t) {
                LOGGER.debug("Logistics addItem reflection invocation failed: {}", t.getMessage());
            }
        }

        return false;
    }

    public static boolean addItemToPipe(BlockEntity pipeBe, Direction fromDir, ItemStack stack) {
        return injectItemIntoPipe(pipeBe, fromDir, stack);
    }

    public static boolean hasTravelingItems(BlockEntity pipeBe) {
        if (pipeBe == null || getTravelingItemsMethod == null || !isLogisticsItemPipe(pipeBe)) return false;
        try {
            Object result = getTravelingItemsMethod.invoke(pipeBe);
            if (result instanceof List<?> list) {
                return !list.isEmpty();
            }
        } catch (Throwable ignored) {
        }
        return false;
    }

    public static long getFluidMilliBuckets(BlockEntity pipeBe) {
        if (pipeBe == null || totalMilliBucketsMethod == null || !isLogisticsFluidPipe(pipeBe)) return 0;
        try {
            Object result = totalMilliBucketsMethod.invoke(pipeBe);
            if (result instanceof Number num) {
                return num.longValue();
            }
        } catch (Throwable ignored) {
        }
        return 0;
    }
}
