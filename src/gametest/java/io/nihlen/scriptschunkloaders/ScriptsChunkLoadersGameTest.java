package io.nihlen.scriptschunkloaders;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;

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
*
* - Minecart registers with sculk sensor
*
* - Minecart unregisters with sculk sensor
* */
@SuppressWarnings("unused")
public class ScriptsChunkLoadersGameTest {
    String defaultName = "Chunk Loader";
    String customItemName = "My Custom Item";
    int startLoaderFrequency = 6;
    int stopLoaderFrequency = 5;

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

        context.spawn(EntityTypes.MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityTypes.MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_hopperMinecart(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityTypes.HOPPER_MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityTypes.HOPPER_MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_chestMinecart(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityTypes.CHEST_MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityTypes.CHEST_MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_furnaceMinecart(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityTypes.FURNACE_MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityTypes.FURNACE_MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_tntMinecart(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityTypes.TNT_MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityTypes.TNT_MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_commandBlockMinecart(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityTypes.COMMAND_BLOCK_MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityTypes.COMMAND_BLOCK_MINECART,
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
        var entity = context.spawn(EntityTypes.CHEST_MINECART, 2, 1, 2);
        entity.setChestVehicleItem(0, createNamedItem());

        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityTypes.CHEST_MINECART,
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
        var entity = context.spawn(EntityTypes.HOPPER_MINECART, 2, 1, 2);
        entity.setChestVehicleItem(1, createNamedItem());

        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityTypes.HOPPER_MINECART,
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
        var entity = context.spawn(EntityTypes.CHEST_MINECART, 2, 1, 2);
        entity.setChestVehicleItem(1, createNamedItem());

        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityTypes.CHEST_MINECART,
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
        var entity = context.spawn(EntityTypes.HOPPER_MINECART, 2, 1, 2);
        entity.setChestVehicleItem(0, createUnnamedItem());

        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityTypes.HOPPER_MINECART,
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
        var entity = context.spawn(EntityTypes.CHEST_MINECART, 2, 1, 2);
        entity.setChestVehicleItem(0, createUnnamedItem());

        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityTypes.CHEST_MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registers_and_unregisters(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityTypes.MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        
        context.runAfterDelay(4, () -> {
            context.assertEntityData(new BlockPos(2, 1, 2), EntityTypes.MINECART, getCustomName, defaultName);

            context.runAfterDelay(4, () -> {
                context.pulseRedstone(new BlockPos(1, 1, 1), 1);

                context.runAfterDelay(4, () -> {
                    context.assertEntityData(new BlockPos(2, 1, 2), EntityTypes.MINECART, getCustomName, null);
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

        context.spawn(EntityTypes.MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(new BlockPos(2, 1, 2), EntityTypes.MINECART, getCustomName, defaultName);
            context.pulseRedstone(new BlockPos(1, 1, 1), 1);

            context.runAfterDelay(4, () -> {
                context.assertEntityData(new BlockPos(2, 1, 2), EntityTypes.MINECART, getCustomName, null);
                context.pulseRedstone(new BlockPos(1, 1, 1), 1);

                context.runAfterDelay(4, () -> {
                    context.assertEntityData(new BlockPos(2, 1, 2), EntityTypes.MINECART, getCustomName, defaultName);
                    context.succeed();
                });
            });
        });
    }

    @GameTest(structure = "scl_tests:empty")
    public void doesNotRegisterWithEmptyDispenser(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityTypes.MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);
        context.runAfterDelay(4, () -> {
            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityTypes.MINECART,
                    getCustomName,
                    null
            );
            context.succeed();
        });
    }

    /// Checks if the sculk sensor receives the correct vibration on activation
    @GameTest(structure = "scl_tests:sculk_activate")
    public void registerWithVibration(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityTypes.MINECART, 2, 1, 2);
        context.setBlock(new BlockPos(3, 1, 2), Blocks.AIR);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);

        context.runAfterDelay(15, () -> {
            BlockPos pos = new BlockPos( 4, 1, 2);
            SculkSensorBlockEntity sensor = context.getBlockEntity(pos, SculkSensorBlockEntity.class);

            if (sensor.getLastVibrationFrequency() != startLoaderFrequency) {
                throw context.assertionException(pos, String.format(
                        "Expected a vibration frequency of %s, instead got %s",
                        (Object) startLoaderFrequency,
                        (Object) sensor.getLastVibrationFrequency()
                ));
            }

            context.assertEntityData(
                    new BlockPos(2, 1, 2),
                    EntityTypes.MINECART,
                    getCustomName,
                    defaultName
            );
            context.succeed();
        });
    }

    /// Checks if the sculk sensor receives the correct vibration on deactivation
    @GameTest(structure = "scl_tests:sculk_activate")
    public void unregisterWithVibration(GameTestHelper context) {
        clearTest(context);

        context.spawn(EntityTypes.MINECART, 2, 1, 2);
        context.pulseRedstone(new BlockPos(1, 1, 1), 1);

        context.runAfterDelay(4, () -> {
            context.assertEntityData(new BlockPos(2, 1, 2), EntityTypes.MINECART, getCustomName, defaultName);
            context.setBlock(new BlockPos(3, 1, 2), Blocks.AIR);

            context.runAfterDelay(4, () -> {
                context.pulseRedstone(new BlockPos(1, 1, 1), 1);

                context.runAfterDelay(8, () -> {
                    BlockPos pos = new BlockPos( 4, 1, 2);
                    SculkSensorBlockEntity sensor = context.getBlockEntity(pos, SculkSensorBlockEntity.class);

                    if (sensor.getLastVibrationFrequency() != stopLoaderFrequency) {
                        throw context.assertionException(pos, String.format(
                                "Expected a vibration frequency of %s, instead got %s",
                                (Object) stopLoaderFrequency,
                                (Object) sensor.getLastVibrationFrequency()
                        ));
                    }

                    context.assertEntityData(
                            new BlockPos(2, 1, 2),
                            EntityTypes.MINECART,
                            getCustomName,
                            null
                    );
                    context.succeed();
                });
            });
        });
    }
}