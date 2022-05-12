package com.defdaemon.extreme_survival.block.entity;

import com.defdaemon.extreme_survival.Extreme_Survival;
import com.defdaemon.extreme_survival.block.ModBlocks;
import com.defdaemon.extreme_survival.block.entity.custom.AlloyMixerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Extreme_Survival.MOD_ID);

    public static final RegistryObject<BlockEntityType<AlloyMixerBlockEntity>> ALLOY_MIXER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("alloy_mixer_block_entity", () -> BlockEntityType.Builder.of(AlloyMixerBlockEntity::new, ModBlocks.ALLOY_MIXER.get()).build(null));


    public static void register(IEventBus eventBus)
    {
        BLOCK_ENTITIES.register(eventBus);
    }
}
