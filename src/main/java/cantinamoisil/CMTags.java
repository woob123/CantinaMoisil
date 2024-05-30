package cantinamoisil;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class CMTags {

    ///Making your own tags for items
    public static final TagKey<Block> GRASSES = BlockTags.create(new ResourceLocation(
            CantinaMoisil.MODID, "grasses"
    ));
}
