package net.improved_observer;

import net.fabricmc.api.ModInitializer;
import net.improved_observer.block_entities.ImprovedObserverBlockEntity;
import net.improved_observer.blocks.ImprovedObserverBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ImprovedObserverMod implements ModInitializer {

    public static final ImprovedObserverBlock improvedObserverBlock =
            new ImprovedObserverBlock(AbstractBlock.Settings.of(Material.STONE).nonOpaque());
    public static BlockEntityType<ImprovedObserverBlockEntity> improvedObserverBlockEntity;

    @Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("improved_observer", "improved_observer"),
                improvedObserverBlock);
		Registry.register(Registry.ITEM, new Identifier("improved_observer", "improved_observer"),
                new BlockItem(improvedObserverBlock, new Item.Settings().group(ItemGroup.REDSTONE)));
        improvedObserverBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                "improved_observer:improved_observer_block_entity",
                BlockEntityType.Builder.create(ImprovedObserverBlockEntity::new, improvedObserverBlock).build(null));
	}
}
