package com.doc.tardisflightcore.screen;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EngineCompartmentScreen extends HandledScreen<EngineCompartmentScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/gui/container/inventory.png");
    private static final Identifier COMPONENT_TEXTURE = new Identifier("tardisflightcore", "textures/gui/component.png");

    public EngineCompartmentScreen(EngineCompartmentScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 114; // 86 + space for component slot
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        // Draw player inventory (bottom half of vanilla inventory)
        context.drawTexture(TEXTURE, x, y + 46, 0, 79, this.backgroundWidth, 80);

        // Draw component slot frame using a square 18x18 area from center of 768x768 texture
        int slotX = x + 80; // Same X as slot in handler
        int slotY = y + 18; // Same Y as slot in handler
        context.drawTexture(
            COMPONENT_TEXTURE,
            slotX, slotY,        // Screen position
            255, 255,            // Texture position (center of 768x768)
            16, 16,              // Size of region to draw
            15, 15             // Full size of texture
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
