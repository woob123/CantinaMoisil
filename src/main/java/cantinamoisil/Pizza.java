package cantinamoisil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Pizza extends Item {
    public Pizza(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("It's like gold").withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity){
        if (!level.isClientSide) {
            AreaEffectCloud areaeffectcloud = new AreaEffectCloud(level, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            areaeffectcloud.setOwner(livingEntity);

            areaeffectcloud.setRadius(3.0F);
            areaeffectcloud.setRadiusOnUse(-0.5F);
            areaeffectcloud.setWaitTime(10);
            areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float)areaeffectcloud.getDuration());
            areaeffectcloud.setPotion(Potions.STRENGTH);

            level.addFreshEntity(areaeffectcloud);
        }
        return super.finishUsingItem(stack, level, livingEntity);
    }
    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }
}
