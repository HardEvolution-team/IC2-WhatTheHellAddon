package com.ded.icwth.integration.jei;

import com.ded.icwth.Tags;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Категория рецептов для молекулярного сборщика в JEI.
 * Определяет внешний вид и расположение элементов в окне рецепта.
 */
public class MolecularTransformerCategory implements IRecipeCategory<MolecularTransformerRecipeWrapper> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("advanced_solar_panels", "textures/gui/MolecularTransformer JEI.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public MolecularTransformerCategory(IGuiHelper guiHelper) {
        // Фон для рецепта (размер 170x64, как в XML)
        this.background = guiHelper.createDrawable(TEXTURE, 3, 12, 170, 64);

        // Иконка категории
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(com.ded.icwth.blocks.ModBlocks.MolecularTransformer));

        // Локализованное название категории
        this.localizedName = I18n.format("tile.molecular_assembler.name");
    }

    @Override
    public String getUid() {
        return JEICompat.MOLECULAR_ASSEMBLER_UID;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public String getModName() {
        return Tags.MODID;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MolecularTransformerRecipeWrapper recipeWrapper, IIngredients ingredients) {
        // Настраиваем отображение входных и выходных предметов
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

        // Входной предмет (слева, верхний слот)
        itemStacks.init(0, true, 13, 6);

        // Выходной предмет (слева, нижний слот)
        itemStacks.init(1, false, 13, 41);

        // Устанавливаем предметы
        itemStacks.set(ingredients);
    }
}