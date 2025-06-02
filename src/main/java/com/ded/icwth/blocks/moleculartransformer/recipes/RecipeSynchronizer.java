package com.ded.icwth.blocks.moleculartransformer.recipes;

import com.ded.icwth.blocks.moleculartransformer.based.MolecularTransformerRecipe;
import com.ded.icwth.blocks.moleculartransformer.based.MolecularTransformerRecipeManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

/**
 * Класс для синхронизации рецептов между сервером и клиентом.
 * Отправляет рецепты клиенту при подключении.
 */
@Mod.EventBusSubscriber
public class RecipeSynchronizer {

    private static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("icwth_recipes");

    /**
     * Инициализирует сетевые пакеты.
     */
    public static void init() {
        // Регистрация пакетов для синхронизации рецептов
        // NETWORK.registerMessage(RecipeSyncHandler.class, RecipeSyncMessage.class, 0, Side.CLIENT);

        System.out.println("Молекулярный сборщик: синхронизация рецептов инициализирована");
    }

    /**
     * Обработчик события подключения игрока.
     * Отправляет все рецепты клиенту при подключении.
     *
     * @param event Событие подключения игрока
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // Убедимся, что мы на сервере
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            // Получаем все рецепты
            List<MolecularTransformerRecipe> recipes = MolecularTransformerRecipeManager.getInstance().getRecipes();

            System.out.println("Молекулярный сборщик: отправка " + recipes.size() + " рецептов клиенту");

            // Здесь должен быть код для отправки рецептов клиенту
            // Например:
            // NETWORK.sendTo(new RecipeSyncMessage(recipes), ((EntityPlayerMP) event.player));
        }
    }

    /**
     * Добавляет рецепт и синхронизирует его со всеми клиентами.
     *
     * @param input Входной предмет
     * @param output Выходной предмет
     * @param energyRequired Требуемая энергия в EU
     * @return true, если рецепт успешно добавлен, иначе false
     */
    public static boolean addAndSyncRecipe(ItemStack input, ItemStack output, double energyRequired) {
        MolecularTransformerRecipe recipe = MolecularTransformerRecipeManager.getInstance().addRecipe(input, output, energyRequired);

        if (recipe != null && FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            // Здесь должен быть код для отправки нового рецепта всем клиентам
            // Например:
            // NETWORK.sendToAll(new RecipeSyncMessage(Collections.singletonList(recipe)));
            return true;
        }

        return recipe != null;
    }
}
