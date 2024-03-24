package cantinamoisil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Beanbowl extends Item {

    public Beanbowl(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal("Bomba atomica").withStyle(ChatFormatting.DARK_AQUA));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity){
        if (!level.isClientSide && livingEntity instanceof Player player) {
            player.addItem(new ItemStack(Items.BOWL));
        }
        if (!level.isClientSide && livingEntity.getRandom().nextFloat() <= 0.1) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            for (Player player : server.getPlayerList().getPlayers()) {
                player.kill();
            }
        }
        if (!level.isClientSide) {
            AreaEffectCloud areaeffectcloud = new AreaEffectCloud(level, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            areaeffectcloud.setOwner(livingEntity);

            areaeffectcloud.setRadius(3.0F);
            areaeffectcloud.setRadiusOnUse(-0.5F);
            areaeffectcloud.setWaitTime(10);
            areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float)areaeffectcloud.getDuration());
            areaeffectcloud.setPotion(Potions.POISON);

            level.addFreshEntity(areaeffectcloud);
        }
        return super.finishUsingItem(stack, level, livingEntity);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pPlayer.getFoodData().getFoodLevel() <= 4) {
            return super.use(pLevel, pPlayer, pUsedHand);
        }
        return InteractionResultHolder.fail(pPlayer.getItemInHand(pUsedHand));
    }
}
