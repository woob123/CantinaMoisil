package cantinamoisil;

import com.mojang.brigadier.Command;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(CantinaMoisil.MODID)
public class CantinaMoisil {
    public static final String MODID = "cantinamoisil";
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final Holder<Item> BEANBOWL = ITEMS.register("beanbowl", () -> new Beanbowl(new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(3)
            .saturationMod(0.8f)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 300, 12), 1f)
            .effect(new MobEffectInstance(MobEffects.HUNGER, 60, 0), 1f)
            .effect(new MobEffectInstance(MobEffects.POISON, 60, 0), 1f)
            .build())));
    public static final Holder<Item> BEANS = ITEMS.register("beans", () -> new Beans(new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(1)
            .saturationMod(0.4f)
            .build())));

    public static final Holder<Item> PIZZA = ITEMS.register("pizza", () -> new Pizza(new Item.Properties().rarity(Rarity.RARE).food(new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(1.2F)
            .effect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1), 1.0F)
            .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0), 1.0F)
            .effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3), 1.0F)
            .alwaysEat()
            .build())));
    public static final Holder<Item> BANANA = ITEMS.register("banana", () -> new Banana(new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(3)
            .saturationMod(0.6f)
            .build())));
    public static final Holder<CreativeModeTab> MY_TAB = TABS.register("mytab", () -> CreativeModeTab.builder()
            .title(Component.literal("Cantina"))
            .icon(() -> PIZZA.value().getDefaultInstance())

            .displayItems((pParameters, pOutput) -> {
                pOutput.accept(BEANBOWL.value());
                pOutput.accept(BEANS.value());
                pOutput.accept(PIZZA.value());
                pOutput.accept(BANANA.value());
        }).build());

    public CantinaMoisil(IEventBus bus) {
        ITEMS.register(bus);
        TABS.register(bus);
        BLOCKS.register(bus);

        //Custom Command
        NeoForge.EVENT_BUS.addListener(this::onBreak);
        NeoForge.EVENT_BUS.addListener((final RegisterCommandsEvent event) -> event
                .getDispatcher().register(
                        Commands.literal("byehunger")
                                .executes(ctx -> {
                                    ctx.getSource().getPlayerOrException().getFoodData().setFoodLevel(0);
                                    return Command.SINGLE_SUCCESS;
                                })
                ));

        //Loot chests
        NeoForge.EVENT_BUS.addListener((LootTableLoadEvent event) -> {
            if (event.getName() == null) return;
            final String name = event.getName().toString();
            if (name.startsWith("minecraft:chests/village/") && name.endsWith("_house")) {
                event.getTable().addPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BEANS.value()).setWeight(5))
                        .add(LootItem.lootTableItem(BEANS.value()).setWeight(4))
                        .add(LootItem.lootTableItem(BEANS.value()).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(15)).build());
            }
            else if(name.startsWith("minecraft:chests/end_city") || name.startsWith("minecraft:chests/ruined") || name.startsWith("minecraft:chests/buried")){
                event.getTable().addPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(PIZZA.value()).setWeight(1))
                        .add(EmptyLootItem.emptyItem().setWeight(9)).build());
            }
            else if(name.startsWith("minecraft:chests/ancient")){
                event.getTable().addPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(PIZZA.value()).setWeight(2))
                        .add(EmptyLootItem.emptyItem().setWeight(6)).build());
            }
            else if(name.startsWith("minecraft:chests/jungle_temple") || name.startsWith("minecraft:chests/desert_pyramid")){
                event.getTable().addPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(BANANA.value()).setWeight(5))
                        .add(LootItem.lootTableItem(BANANA.value()).setWeight(4))
                        .add(LootItem.lootTableItem(BANANA.value()).setWeight(3))
                        .add(EmptyLootItem.emptyItem().setWeight(20)).build());
            }
        });
    }

    ///What happens when breaking a block
    public void onBreak(BlockEvent.BreakEvent event){
        if(!event.getLevel().isClientSide() && event.getState().is(CMTags.GRASSES) && event.getPlayer().getRandom().nextFloat() <= 5 / 100f){
            var pos = event.getPos().getCenter().subtract(0, 0.5, 0);
            event.getLevel().addFreshEntity(new ItemEntity(event.getPlayer().level(), pos.x(), pos.y(), pos.z(), new ItemStack(CantinaMoisil.BEANS, 1)));
        }
        if(!event.getLevel().isClientSide() && event.getState().getBlock() == Blocks.JUNGLE_LEAVES && event.getPlayer().getRandom().nextFloat() <= 5 / 100f){
            var pos = event.getPos().getCenter().subtract(0, 0.5, 0);
            event.getLevel().addFreshEntity(new ItemEntity(event.getPlayer().level(), pos.x(), pos.y(), pos.z(), new ItemStack(CantinaMoisil.BANANA, 1)));
        }
    }

}
