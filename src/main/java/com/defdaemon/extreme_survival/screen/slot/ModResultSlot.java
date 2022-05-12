package com.defdaemon.extreme_survival.screen.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ModResultSlot extends SlotItemHandler
{
    public ModResultSlot(IItemHandler itemHandler, int index, int x, int y)
    {
        super(itemHandler, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return false;
    }
}
