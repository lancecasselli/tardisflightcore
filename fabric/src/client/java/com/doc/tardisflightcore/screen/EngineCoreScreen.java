package com.doc.tardisflightcore.screen;

import com.doc.tardisflightcore.TardisFlightCore;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EngineCoreScreen extends HandledScreen<EngineCoreScreenHandler> {

    private static final Identifier COMPONENT_TEXTURE = new Identifier(TardisFlightCore.MOD_ID, "textures/gui/component.png");
    private static final Identifier INVENTORY_TEXTURE = new Identifier("minecraft", "textures/gui/container/inventory.png");

    public EngineCoreScreen(EngineCoreScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // Draw just bottom half of inventory (inventory + hotbar)
        RenderSystem.setShaderTexture(0, INVENTORY_TEXTURE);
        context.drawTexture(
            INVENTORY_TEXTURE,
            this.x, this.y + 78,          // Draw at GUI origin (correctly aligned)
            0, 78,                   // Start drawing from Y=75 of texture (bottom half)
            176, 91,                 // Only draw 91px of height (inventory + hotbar)
            256, 256                // Full texture resolution (Mojang default)
        );

        // Draw custom component slot (aligned accordingly)
        RenderSystem.setShaderTexture(0, COMPONENT_TEXTURE);
        context.drawTexture(COMPONENT_TEXTURE, this.x + 80, this.y + 34, 0, 0, 18, 18, 18, 18);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, 8, 6, 0x404040, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, 8, this.backgroundHeight - 94, 0x404040, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
