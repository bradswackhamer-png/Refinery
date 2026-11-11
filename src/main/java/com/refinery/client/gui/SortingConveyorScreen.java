package com.refinery.client.gui;

import com.refinery.RefineryMod;
import com.refinery.menu.SortingConveyorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SortingConveyorScreen extends AbstractContainerScreen<SortingConveyorMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            RefineryMod.MODID, "textures/gui/sorting_conveyor.png"
    );

    public SortingConveyorScreen(SortingConveyorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 178);
        this.inventoryLabelY = 84;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int left = this.leftPos;
        int top = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, left, top, 0.0f, 0.0f, this.imageWidth, this.imageHeight, 256, 256);
    }
}
