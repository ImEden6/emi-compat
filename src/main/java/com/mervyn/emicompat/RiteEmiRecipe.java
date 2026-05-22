package com.mervyn.emicompat;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.favouriteless.enchanted.common.circle_magic.CircleMagicShape;
import net.favouriteless.enchanted.common.circle_magic.RiteType;
import net.favouriteless.enchanted.common.init.EData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RiteEmiRecipe implements EmiRecipe {
	private final ResourceLocation id;
	private final RiteType rite;
	private final List<EmiIngredient> inputs;
	private final List<EmiStack> outputs;

	public RiteEmiRecipe(ResourceLocation id, RiteType rite) {
		this.id = id;
		this.rite = rite;
		
		this.inputs = new ArrayList<>();
		for (ItemStack stack : rite.getItems()) {
			this.inputs.add(EmiStack.of(stack));
		}
		
		this.outputs = new ArrayList<>();
		if (rite.getOutputs() != null) {
			for (ItemStack stack : rite.getOutputs()) {
				this.outputs.add(EmiStack.of(stack));
			}
		}
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return EnchantedEmiCompat.RITE_CATEGORY;
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
		return 180;
	}

	@Override
	public int getDisplayHeight() {
		return 120;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		// Background
		widgets.addTexture(new ResourceLocation("enchanted", "textures/gui/jei/circle_magic.png"), 0, 0, 180, 120, 0, 0);

		// Golden Glyph
		widgets.addTexture(new ResourceLocation("enchanted", "textures/gui/jei/gold_glyph.png"), 0, 14, 110, 110, 0, 0, 110, 110, 110, 110);

		// Concentric Shapes
		RegistryAccess access = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.registryAccess() : RegistryAccess.EMPTY;
		var shapeRegistry = access.registry(EData.CIRCLE_SHAPE_REGISTRY).orElse(null);
		if (shapeRegistry != null) {
			for (var entry : rite.getShapes().entrySet()) {
				CircleMagicShape shape = shapeRegistry.get(entry.getKey());
				if (shape != null) {
					ResourceLocation location = entry.getKey().location();
					ResourceLocation texture = new ResourceLocation(
							location.getNamespace(),
							String.format(
									"textures/gui/circle_magic_shapes/%s_%s.png",
									location.getPath(),
									BuiltInRegistries.BLOCK.getKey(entry.getValue()).getPath()
							)
					);
					if (Minecraft.getInstance().getResourceManager().getResource(texture).isPresent()) {
						widgets.addTexture(texture, 0, 14, 110, 110, 0, 0, 110, 110, 110, 110);
					}
				}
			}
		}

		// Inputs
		List<EmiIngredient> itemList = new ArrayList<>(inputs);
		int circleNum = 1;
		int itemsRemaining = itemList.size();

		while (itemsRemaining > 0) {
			int radius = 15 + (circleNum - 1) * 15;
			int limit = (int) Math.round(Math.pow(6.0, circleNum));
			int itemCount = Math.min(itemsRemaining, limit);

			for (int i = 0; i < itemCount; i++) {
				EmiIngredient stack = itemList.remove(0);
				float angle = i * (float) (Math.PI * 2) / itemCount + (float) Math.PI;
				int cx = (int) Math.round(Math.sin(-angle) * radius) - 8;
				int cy = (int) Math.round(Math.cos(-angle) * radius) - 8;
				widgets.addSlot(stack, 47 + cx, 60 + cy);
				itemsRemaining--;
			}
			circleNum++;
		}

		// Outputs
		if (!outputs.isEmpty()) {
			int numRows = (int) Math.ceil(outputs.size() / 3.0F);
			int height = numRows * 17;
			int startX = 119;
			int startY = 61 - Math.round(height / 2.0F);

			for (int i = 0; i < outputs.size(); i++) {
				widgets.addSlot(outputs.get(i), startX + i % 3 * 17, startY + i / 3 * 17).recipeContext(this);
			}
		}

		// Text
		Component nameComponent = Component.translatable("rite." + id.getNamespace() + "." + id.getPath());
		int nameWidth = Minecraft.getInstance().font.width(nameComponent);
		widgets.addText(nameComponent, 90 - nameWidth / 2, 0, 0x404040, false);

		Component powerComponent = Component.translatable("emi.compat.enchanted.required_altar_power", rite.getPower());
		int powerWidth = Minecraft.getInstance().font.width(powerComponent);
		widgets.addText(powerComponent, 90 - powerWidth / 2, 112, 0x404040, false);
	}
}
