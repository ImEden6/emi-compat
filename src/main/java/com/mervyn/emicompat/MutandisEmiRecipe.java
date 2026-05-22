package com.mervyn.emicompat;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class MutandisEmiRecipe implements EmiRecipe {
	private final EmiRecipeCategory category;
	private final ResourceLocation id;
	private final EmiIngredient input;
	private final EmiStack output;
	private final Component description;

	public MutandisEmiRecipe(EmiRecipeCategory category, ResourceLocation id, List<ItemStack> allInputs, ItemStack output, Component description) {
		this.category = category;
		this.id = id;
		this.input = EmiIngredient.of(allInputs.stream().map(EmiStack::of).collect(Collectors.toList()));
		this.output = EmiStack.of(output);
		this.description = description;
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return category;
	}

	@Override
	public @Nullable ResourceLocation getId() {
		return id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(input);
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of(output);
	}

	@Override
	public int getDisplayWidth() {
		return 120;
	}

	@Override
	public int getDisplayHeight() {
		return 68;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		// Background
		widgets.addTexture(new ResourceLocation("enchanted", "textures/gui/jei/mutandis.png"), 0, 0, 120, 68, 0, 0);

		// Input Slot
		widgets.addSlot(input, 27, 35);

		// Output Slot
		widgets.addSlot(output, 76, 35).recipeContext(this);

		// Description Text
		String text = description.getString();
		int width = Minecraft.getInstance().font.width(text);
		if (width > 150) {
			String first = text.substring(0, text.length() / 2);
			String second = text.substring(text.length() / 2);
			int w1 = Minecraft.getInstance().font.width(first);
			int w2 = Minecraft.getInstance().font.width(second);
			widgets.addText(Component.literal(first), 60 - w1 / 2, 10, 0x404040, false);
			widgets.addText(Component.literal(second), 60 - w2 / 2, 20, 0x404040, false);
		} else {
			widgets.addText(description, 60 - width / 2, 10, 0x404040, false);
		}
	}
}
