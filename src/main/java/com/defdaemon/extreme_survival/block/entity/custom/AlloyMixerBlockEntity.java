package com.defdaemon.extreme_survival.block.entity.custom;

import com.defdaemon.extreme_survival.Extreme_Survival;
import com.defdaemon.extreme_survival.block.custom.AlloyMixerBlock;
import com.defdaemon.extreme_survival.block.entity.ModBlockEntities;
import com.defdaemon.extreme_survival.recipe.AlloyMixerRecipe;
import com.defdaemon.extreme_survival.screen.AlloyMixerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;

public class AlloyMixerBlockEntity extends BlockEntity implements MenuProvider
{
    private final ItemStackHandler itemHandler = new ItemStackHandler(5)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;
    private AlloyMixerMenu alloyMixerMenu;

    public AlloyMixerBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) 
    {
        super(ModBlockEntities.ALLOY_MIXER_BLOCK_ENTITY.get(), pWorldPosition, pBlockState);
        this.data = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0: return AlloyMixerBlockEntity.this.progress;
                    case 1: return AlloyMixerBlockEntity.this.maxProgress;
                    default: return 0;
                }
            }

            public void set(int index, int value) {
                switch(index) {
                    case 0: AlloyMixerBlockEntity.this.progress = value; break;
                    case 1: AlloyMixerBlockEntity.this.maxProgress = value; break;
                }
            }

            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName()
    {
        return new TextComponent("Alloy Mixer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer)
    {
        this.alloyMixerMenu = new AlloyMixerMenu(pContainerId, pInventory, this, this.data);
        return this.alloyMixerMenu;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side)
    {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag)
    {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("alloy_mixer.progress", progress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("alloy_mixer.progress");
    }

    public void drops()
    {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++)
        {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, AlloyMixerBlockEntity pBlockEntity)
    {
        if(hasRecipe(pBlockEntity))
        {
            if(!pState.getValue(AlloyMixerBlock.POWERED))
            {
                pBlockEntity.progress++;
                setChanged(pLevel, pPos, pState);
                if(pBlockEntity.progress > pBlockEntity.maxProgress)
                {
                    craftItem(pBlockEntity);
                }
            }
        }
        else
        {
            pBlockEntity.resetProgress();
            setChanged(pLevel, pPos, pState);
        }
     }

    private static void craftItem(AlloyMixerBlockEntity entity)
    {
        if(entity.alloyMixerMenu != null) {
            Level level = entity.level;
            CraftingContainer container = new CraftingContainer(entity.alloyMixerMenu, 1, 5);
            for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
                container.setItem(i, entity.itemHandler.getStackInSlot(i));
            }
            Optional<AlloyMixerRecipe> match = level.getRecipeManager().getRecipeFor(AlloyMixerRecipe.Type.INSTANCE, container, level);
            if (match.isPresent()) {
                if (!entity.itemHandler.getStackInSlot(0).isEmpty()) {
                    entity.itemHandler.extractItem(0, 1, false);
                }
                if (!entity.itemHandler.getStackInSlot(1).isEmpty()) {
                    entity.itemHandler.extractItem(1, 1, false);
                }
                if (!entity.itemHandler.getStackInSlot(2).isEmpty()) {
                    entity.itemHandler.extractItem(2, 1, false);
                }
                if (!entity.itemHandler.getStackInSlot(3).isEmpty()) {
                    entity.itemHandler.extractItem(3, 1, false);
                }
                entity.itemHandler.setStackInSlot(4, new ItemStack(match.get().getResultItem().getItem(), entity.itemHandler.getStackInSlot(4).getCount() + 1));
                entity.resetProgress();
            }
        }
    }

    private static boolean hasRecipe(AlloyMixerBlockEntity entity)
    {
        if(entity.alloyMixerMenu!= null) {
            Level level = entity.level;
            CraftingContainer container = new CraftingContainer(entity.alloyMixerMenu, 1, 5);
            for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
                container.setItem(i, entity.itemHandler.getStackInSlot(i));
            }
            Optional<AlloyMixerRecipe> match = level.getRecipeManager().getRecipeFor(AlloyMixerRecipe.Type.INSTANCE, container, level);
            return match.isPresent() && canInsertAmountIntoOutputSlot(container) && canInsertItemIntoOutputSlot(container, match.get().getResultItem());
        }
        else
        {
            return false;
        }
    }

    private void resetProgress()
    {
        this.progress = 0;
    }

    private static boolean canInsertItemIntoOutputSlot(CraftingContainer inventory, ItemStack output) {
        return inventory.getItem(4).getItem() == output.getItem() || inventory.getItem(4).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(CraftingContainer inventory)
    {
        return inventory.getItem(4).getMaxStackSize() > inventory.getItem(4).getCount();
    }
}
