package com.defdaemon.extreme_survival.screen;

import com.defdaemon.extreme_survival.Extreme_Survival;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AlloyMixerScreen extends AbstractContainerScreen<AlloyMixerMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Extreme_Survival.MOD_ID, "textures/gui/alloy_mixer_gui.png");

    public AlloyMixerScreen(AlloyMixerMenu pMenu, Inventory pPlayerInventory, Component pTitle)
    {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

        if(menu.isPowered())
        {
            blit(pPoseStack, x + 25, y + 35, 176, 0, 13, 26);
        }
        if(menu.isCrafting())
        {
            // Slot #1
            blit(pPoseStack, x + 66, y + 31, 176, 18, menu.getScaledProgress(), 12);
            // Slot #2
            blit(pPoseStack, x + 66, y + 45, 176, 30, menu.getScaledProgress(), 12);
            // Slot#3
            blit(pPoseStack, x + 142, y - 31, 176, 42, menu.getScaledProgress(), 12);
            // Slot#4
            blit(pPoseStack, x + 114, y - 45, 176, 54, menu.getScaledProgress(), 12);
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta)
    {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }
}
