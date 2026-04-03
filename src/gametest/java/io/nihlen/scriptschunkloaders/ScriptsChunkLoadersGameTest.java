package io.nihlen.scriptschunkloaders;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.Objects;
import java.util.function.Function;

/*
* Tests:
* - Registers with default name:
*   - Minecart
*   - Minecart with Hopper
*   - Minecart with Chest
*   - Minecart with Furnace
*   - Minecart with TNT
*   - Minecart with Command Block
*
* - Registers with first slot custom item name:
*   - Minecart with Hopper
*   - Minecart with Chest
*
* - Registers with default name when custom item in other slot:
*   - Minecart with Hopper
*   - Minecart with Chest
*
* - Registers with first slot no name item:
*   - Minecart with Hopper
*   - Minecart with Chest
*
* - Minecart registers and unregisters
* - Minecart registers, unregisters and registers again
*
* - Minecart does not register with empty dispenser
* */

public class ScriptsChunkLoadersGameTest {
    String defaultName = "Chunk Loader";
    String customItemName = "My Custom Item";

    Function<Entity, String> getCustomName = entity -> {
        var customName = entity.getCustomName();
        if (Objects.isNull(customName)) return null;
        return customName.getString();
    };

    private ItemStack createNamedItem() {
        ItemStack item = new ItemStack(Items.PAPER);
        item.set(DataComponents.CUSTOM_NAME, Component.literal(customItemName));
        return item;
    }

    private ItemStack createUnnamedItem() {
        return new ItemStack(Items.EMERALD);
    }

    private void clearTest(GameTestHelper context) {
        context.killAllEntities();
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_minecart(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityType.MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityType.MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_hopperMinecart(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityType.HOPPER_MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityType.HOPPER_MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_chestMinecart(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityType.CHEST_MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityType.CHEST_MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_furnaceMinecart(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityType.FURNACE_MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityType.FURNACE_MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_tntMinecart(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityType.TNT_MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityType.TNT_MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_commandBlockMinecart(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityType.COMMAND_BLOCK_MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityType.COMMAND_BLOCK_MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

//    @GameTest(structure = "scl_tests:basic")
//    public void registersWithFirstItemName_hopperMinecart(TestContext context) {
//        clearTest(context);
//
//        clearTest(context);
//
//        var entity = context.spawnEntity(EntityType.HOPPER_MINECART, 2, 1, 2);
//        entity.setInventoryStack(0, createNamedItem());
//
//        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
//        context.waitAndRun(4, () -> {
//            context.expectEntityWithData(
//                    new BlockPos(2, 1, 2),
//                    EntityType.HOPPER_MINECART,
//                    getCustomName,
//                    customItemName
//            );
//            context.complete();
//        });
//    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithFirstItemName_chestMinecart(GameTestHelper context) {
        clearTest(context);

        context.killAllEntities();
        var entity = context.spawn(EntityType.CHEST_MINECART, 2, 1, 2);
        entity.setChestVehicleItem(0, createNamedItem());

        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityType.CHEST_MINECART,
                    getCustomName,
                    customItemName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultNameOtherSlot_hopperMinecart(GameTestHelper context) {
        clearTest(context);

        context.killAllEntities();
        var entity = context.spawn(EntityType.HOPPER_MINECART, 2, 1, 2);
        entity.setChestVehicleItem(1, createNamedItem());

        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityType.HOPPER_MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultNameOtherSlot_chestMinecart(GameTestHelper context) {
        clearTest(context);

        context.killAllEntities();
        var entity = context.spawn(EntityType.CHEST_MINECART, 2, 1, 2);
        entity.setChestVehicleItem(1, createNamedItem());

        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityType.CHEST_MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultNameUnnamedItem_hopperMinecart(GameTestHelper context) {
        clearTest(context);

        context.killAllEntities();
        var entity = context.spawn(EntityType.HOPPER_MINECART, 2, 1, 2);
        entity.setChestVehicleItem(0, createUnnamedItem());

        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityType.HOPPER_MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultNameUnnamedItem_chestMinecart(GameTestHelper context) {
        clearTest(context);

        context.killAllEntities();
        var entity = context.spawn(EntityType.CHEST_MINECART, 2, 1, 2);
        entity.setChestVehicleItem(0, createUnnamedItem());

        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityType.CHEST_MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registers_and_unregisters(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityType.MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        
        context.runAfterDelay(4, () -> {
            context.assertEntityData(new BlockPos(2, 1, 2), EntityType.MINECART, getCustomName, defaultName);

            context.runAfterDelay(4, () -> {
                context.pulseRedstone(new BlockPos(1, 1, 1), 1);

                context.runAfterDelay(4, () -> {
                    context.assertEntityData(new BlockPos(2, 1, 2), EntityType.MINECART, getCustomName, null);
                    context.succeed();
                });
            });
        });
    }

    /**
     * Covers <a href="https://github.com/scriptcoded/scripts-chunk-loaders/issues/34">#34</a>
     */
    @GameTest(structure = "scl_tests:basic")
    public void registers_unregisters_and_registers(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityType.MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(new BlockPos(2, 1, 2), EntityType.MINECART, getCustomName, defaultName);
            context.pulseRedstone(new BlockPos(1, 1, 1), 1);

            context.runAfterDelay(4, () -> {
                context.assertEntityData(new BlockPos(2, 1, 2), EntityType.MINECART, getCustomName, null);
                context.pulseRedstone(new BlockPos(1, 1, 1), 1);

                context.runAfterDelay(4, () -> {
                    context.assertEntityData(new BlockPos(2, 1, 2), EntityType.MINECART, getCustomName, defaultName);
                    context.succeed();
                });
            });
        });
    }

    @GameTest(structure = "scl_tests:empty")
    public void doesNotRegisterWithEmptyDispenser(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityType.MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityType.MINECART,
                    getCustomName,
                    null
            );
            context.succeed();
        });
    }
}