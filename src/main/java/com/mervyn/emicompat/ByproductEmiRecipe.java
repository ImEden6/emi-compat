package com.mervyn.emicompat;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.favouriteless.enchanted.common.init.registry.EItems;
import net.favouriteless.enchanted.common.recipes.ByproductRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ByproductEmiRecipe implements EmiRecipe {
	private final ByproductRecipe recipe;
	private final ResourceLocation id;
	private final EmiIngredient input;
	private final EmiIngredient smeltingOutput;
	private final EmiStack byproductOutput;
	private final EmiStack clayJar;
	private final List<EmiIngredient> inputs;
	private final List<EmiStack> outputs;

	public ByproductEmiRecipe(ByproductRecipe recipe) {
		this.recipe = recipe;
		this.id = recipe.getId();
		
		RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
		ItemStack byproductStack = recipe.getResultItem(registryAccess);
		int count = byproductStack.getCount();
		
		this.input = EmiIngredient.of(recipe.getInput());
		this.byproductOutput = EmiStack.of(byproductStack);
		this.clayJar = EmiStack.of(EItems.CLAY_JAR.get(), count);

		// Dynamically resolve furnace smelting outputs
		List<EmiStack> smeltOutputs = new ArrayList<>();
		if (Minecraft.getInstance().level != null) {
			for (ItemStack stack : recipe.getInput().getItems()) {
				Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING)
						.stream()
						.filter(smelt -> smelt.getIngredients().get(0).test(stack))
						.findFirst()
						.ifPresent(smeltRecipe -> smeltOutputs.add(EmiStack.of(smeltRecipe.getResultItem(registryAccess))));
			}
		}
		this.smeltingOutput = EmiIngredient.of(smeltOutputs);

		// Set inputs & outputs list for recipe relationships
		this.inputs = List.of(input, clayJar);
		this.outputs = List.of(byproductOutput, EmiStack.of(smeltOutputs.isEmpty() ? ItemStack.EMPTY : smeltOutputs.get(0).getItemStack()));
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return EnchantedEmiCompat.BYPRODUCT_CATEGORY;
	}

	@Override
	public @Nullable ResourceLocation getId() {
		return id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return inputs;
	}

	@Override
	public List<EmiStack> getOutputs() {
		return outputs;
	}

	@Override
	public int getDisplayWidth() {
		return 96;
	}

	@Override
	public int getDisplayHeight() {
		return 65;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		// Background texture from witch_oven.png
		widgets.addTexture(new ResourceLocation("enchanted", "textures/gui/witch_oven.png"), 0, 0, 96, 65, 40, 10);

		// Animated fire (from bottom to top)
		widgets.addAnimatedTexture(new ResourceLocation("enchanted", "textures/gui/witch_oven.png"), 40, 27, 14, 14, 176, 0, 6000, false, true, false);

		// Animated progress arrow (from left to right)
		widgets.addAnimatedTexture(new ResourceLocation("enchanted", "textures/gui/witch_oven.png"), 36, 6, 24, 17, 176, 14, 6000, true, false, false);

		// Slots
		widgets.addSlot(input, 13, 7);
		widgets.addSlot(smeltingOutput, 67, 7).recipeContext(this);
		widgets.addSlot(byproductOutput, 67, 43).recipeContext(this);
		widgets.addSlot(clayJar, 13, 43);
	}
}
