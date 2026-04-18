package com.mervyn.emicompat;

import java.util.List;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.spell_engine.SpellEngineMod;

public class SpellBookBindingEmiRecipe implements EmiRecipe {
	private final Item outputItem;
	private final ResourceLocation id;

	public SpellBookBindingEmiRecipe(Item outputItem) {
		this.outputItem = outputItem;
		ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(outputItem);
		this.id = new ResourceLocation("emicompat", "spell_book_binding/" + itemKey.getNamespace() + "/" + itemKey.getPath());
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return SpellEngineEmiCompat.SPELL_BINDING_CATEGORY;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(EmiStack.of(Items.BOOK));
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of(EmiStack.of(outputItem));
	}

	@Override
	public int getDisplayWidth() {
		return 100;
	}

	@Override
	public int getDisplayHeight() {
		return 18;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		// Input book at 0, 0
		widgets.addSlot(getInputs().get(0), 0, 0);

		// Arrow at 46, 0 (centered)
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 46, 0);

		// XP Level cost under the arrow
		int levelCost = SpellEngineMod.config != null ? SpellEngineMod.config.spell_book_creation_cost : 1;
		int requiredLevel = SpellEngineMod.config != null ? SpellEngineMod.config.spell_book_creation_requirement : 1;
		
		widgets.addText(Component.literal(String.valueOf(levelCost)).withStyle(ChatFormatting.GREEN), 52, 10, 0xFFFFFF, false);
		widgets.addTooltipText(List.of(Component.literal("Requires Level " + requiredLevel)), 52, 10, 10, 10);

		// Output spellbook at 73, 0
		widgets.addSlot(getOutputs().get(0), 73, 0).recipeContext(this);
	}
}
