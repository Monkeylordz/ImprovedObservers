package net.improved_observers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.improved_observers.block.AdvancedObserverBlock;
import net.improved_observers.block.DirectionalObserverBlock;
import net.improved_observers.block.entity.AdvancedObserverBlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ImprovedObserversMod implements ModInitializer {

    public static final String MOD_ID = "improved_observers";
    public static final Identifier DIRECTIONAL = new Identifier(MOD_ID, "directional_observer");
    public static final Identifier ADVANCED = new Identifier(MOD_ID, "advanced_observer");

    public static final DirectionalObserverBlock DIRECTIONAL_OBSERVER_BLOCK;
    public static final AdvancedObserverBlock ADVANCED_OBSERVER_BLOCK;
    public static final BlockEntityType<AdvancedObserverBlockEntity> ADVANCED_OBSERVER_BLOCK_ENTITY;

    static {
        DIRECTIONAL_OBSERVER_BLOCK = Registry.register(Registry.BLOCK, DIRECTIONAL,
                new DirectionalObserverBlock(FabricBlockSettings.copyOf(Blocks.OBSERVER)
                        .solidBlock((state, world, pos) -> false)));
        ADVANCED_OBSERVER_BLOCK = Registry.register(Registry.BLOCK, ADVANCED,
                new AdvancedObserverBlock(FabricBlockSettings.copyOf(Blocks.OBSERVER)
                        .solidBlock((state, world, pos) -> false)));
        ADVANCED_OBSERVER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, ADVANCED,
                BlockEntityType.Builder.create(AdvancedObserverBlockEntity::new, ADVANCED_OBSERVER_BLOCK).build(null));
    }

    @Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, DIRECTIONAL,
                new BlockItem(DIRECTIONAL_OBSERVER_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE)));
        Registry.register(Registry.ITEM, ADVANCED,
                new BlockItem(ADVANCED_OBSERVER_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE)));
	}
}