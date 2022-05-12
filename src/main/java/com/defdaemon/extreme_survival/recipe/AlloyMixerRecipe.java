package com.defdaemon.extreme_survival.recipe;

import com.defdaemon.extreme_survival.Extreme_Survival;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class AlloyMixerRecipe extends ShapelessRecipe
{
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    final String group;
    private final boolean isSimple;

    public AlloyMixerRecipe(ResourceLocation id, String pGroup, ItemStack output, NonNullList<Ingredient> recipeItems)
    {
        super(id, pGroup, output, recipeItems);
        this.id = id;
        this.group = pGroup;
        this.output = output;
        this.recipeItems = recipeItems;
        this.isSimple = recipeItems.stream().allMatch(Ingredient::isSimple);
    }

    public ResourceLocation getId()
    {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    public String getGroup()
    {
        return this.group;
    }

    @Override
    public ItemStack getResultItem()
    {
        return output.copy();
    }

    public NonNullList<Ingredient> getIngredients()
    {
        return this.recipeItems;
    }

    @Override
    public boolean matches(CraftingContainer pInv, Level pLevel)
    {
        StackedContents stackedcontents = new StackedContents();
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for(int j = 0; j < pInv.getContainerSize(); ++j) {
            ItemStack itemstack = pInv.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                if (isSimple)
                    stackedcontents.accountStack(itemstack, 1);
                else inputs.add(itemstack);
            }
        }
        return i == this.recipeItems.size() && (isSimple ? stackedcontents.canCraft(this, (IntList)null) : net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs,  this.recipeItems) != null);
    }

    @Override
    public ItemStack assemble(CraftingContainer pInv)
    {
        return this.output .copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight)
    {
        return pWidth * pHeight >= this.recipeItems.size();
    }

    @Override
    public RecipeType<?> getType()
    {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<AlloyMixerRecipe>
    {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "alloy_mixing";
    }

    public static class Serializer implements RecipeSerializer<AlloyMixerRecipe>
    {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(Extreme_Survival.MOD_ID,"alloy_mixing");

        @Override
        public AlloyMixerRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            String s = GsonHelper.getAsString(json, "group", "");
            NonNullList<Ingredient> inputs = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
            if(inputs.isEmpty())
            {
                throw new JsonParseException("No ingredients for alloy mixer recipe");
            } else if (inputs.size() > 4) {
                throw new JsonParseException("Too many ingredients for shapeless recipe. The maximum is 4");
            } else {
                ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
                return new AlloyMixerRecipe(id, s, output, inputs);
            }
        }

        private static NonNullList<Ingredient> itemsFromJson(JsonArray pIngredientArray)
        {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();
            for(int i = 0; i < pIngredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(pIngredientArray.get(i));
                if (net.minecraftforge.common.ForgeConfig.SERVER.skipEmptyShapelessCheck.get() || !ingredient.isEmpty()) {
                    nonnulllist.add(ingredient);
                }
            }
            return nonnulllist;
        }


        @Override
        public AlloyMixerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf)
        {
            String s = buf.readUtf();
            int i = buf.readVarInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(i, Ingredient.EMPTY);

            for (int j = 0; j < inputs.size(); i++) {
                inputs.set(j, Ingredient.fromNetwork(buf));
            }
            ItemStack output = buf.readItem();
            return new AlloyMixerRecipe(id, s, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, AlloyMixerRecipe recipe)
        {
            buf.writeUtf(recipe.group);
            buf.writeVarInt(recipe.recipeItems.size());
            for (Ingredient ing: recipe.recipeItems)
            {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(), false);
        }

        @Override
        public RecipeSerializer<?> setRegistryName(ResourceLocation name)
        {
            return INSTANCE;
        }

        @Nullable
        @Override
        public ResourceLocation getRegistryName()
        {
            return ID;
        }

        @Override
        public Class<RecipeSerializer<?>> getRegistryType()
        {
            return Serializer.castClass(RecipeSerializer.class);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls)
        {
            return (Class<G>)cls;
        }
    }
}
