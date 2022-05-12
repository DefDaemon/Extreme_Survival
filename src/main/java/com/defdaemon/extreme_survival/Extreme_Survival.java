package com.defdaemon.extreme_survival;

import com.defdaemon.extreme_survival.block.ModBlocks;
import com.defdaemon.extreme_survival.block.custom.AlloyMixerBlock;
import com.defdaemon.extreme_survival.block.entity.ModBlockEntities;
import com.defdaemon.extreme_survival.item.ModItems;
import com.defdaemon.extreme_survival.recipe.ModRecipes;
import com.defdaemon.extreme_survival.screen.AlloyMixerScreen;
import com.defdaemon.extreme_survival.screen.ModMenuTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Extreme_Survival.MOD_ID)
public class Extreme_Survival {
    public static final String MOD_ID = "extreme_survival";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Extreme_Survival() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(eventBus);
        ModItems.register(eventBus);


        ModBlockEntities.register(eventBus);
        ModMenuTypes.register(eventBus);
        ModRecipes.register(eventBus);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ALLOY_MIXER.get(), RenderType.translucent());

        MenuScreens.register(ModMenuTypes.ALLOY_MIXER_MENU.get(), AlloyMixerScreen::new);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
    }
}
