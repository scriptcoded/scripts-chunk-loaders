package io.nihlen.scriptschunkloaders;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
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
    Integer startLoaderFrequency = 6;
    Integer stopLoaderFrequency = 5;

    Function<Entity, String> getCustomName = entity -> {
        var customName = entity.getCustomName();
        if (Objects.isNull(customName)) return null;
        return customName.getString();
    };

    private ItemStack createNamedItem() {
        ItemStack item = new ItemStack(Items.PAPER);
        item.set(DataComponentTypes.CUSTOM_NAME, Text.literal(customItemName));
        return item;
    }

    private ItemStack createUnnamedItem() {
        return new ItemStack(Items.EMERALD);
    }

    private void clearTest(TestContext context) {
        context.killAllEntities();
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_minecart(TestContext context) {
        clearTest(context);

        context.spawnEntity(EntityType.MINECART, 2, 1, 2);
        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
        context.waitAndRun(4, () -> {
            context.expectEntityWithData(
                    new BlockPos(2, 1, 2),
                    EntityType.MINECART,
                    getCustomName,
                    defaultName
            );
            context.complete();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_hopperMinecart(TestContext context) {
        clearTest(context);

        context.spawnEntity(EntityType.HOPPER_MINECART, 2, 1, 2);
        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
        context.waitAndRun(4, () -> {
            context.expectEntityWithData(
                    new BlockPos(2, 1, 2),
                    EntityType.HOPPER_MINECART,
                    getCustomName,
                    defaultName
            );
            context.complete();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_chestMinecart(TestContext context) {
        clearTest(context);

        context.spawnEntity(EntityType.CHEST_MINECART, 2, 1, 2);
        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
        context.waitAndRun(4, () -> {
            context.expectEntityWithData(
                    new BlockPos(2, 1, 2),
                    EntityType.CHEST_MINECART,
                    getCustomName,
                    defaultName
            );
            context.complete();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_furnaceMinecart(TestContext context) {
        clearTest(context);

        context.spawnEntity(EntityType.FURNACE_MINECART, 2, 1, 2);
        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
        context.waitAndRun(4, () -> {
            context.expectEntityWithData(
                    new BlockPos(2, 1, 2),
                    EntityType.FURNACE_MINECART,
                    getCustomName,
                    defaultName
            );
            context.complete();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_tntMinecart(TestContext context) {
        clearTest(context);

        context.spawnEntity(EntityType.TNT_MINECART, 2, 1, 2);
        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
        context.waitAndRun(4, () -> {
            context.expectEntityWithData(
                    new BlockPos(2, 1, 2),
                    EntityType.TNT_MINECART,
                    getCustomName,
                    defaultName
            );
            context.complete();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultName_commandBlockMinecart(TestContext context) {
        clearTest(context);

        context.spawnEntity(EntityType.COMMAND_BLOCK_MINECART, 2, 1, 2);
        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
        context.waitAndRun(4, () -> {
            context.expectEntityWithData(
                    new BlockPos(2, 1, 2),
                    EntityType.COMMAND_BLOCK_MINECART,
                    getCustomName,
                    defaultName
            );
            context.complete();
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
    public void registersWithFirstItemName_chestMinecart(TestContext context) {
        clearTest(context);

        context.killAllEntities();
        var entity = context.spawnEntity(EntityType.CHEST_MINECART, 2, 1, 2);
        entity.setInventoryStack(0, createNamedItem());

        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
        context.waitAndRun(4, () -> {
            context.expectEntityWithData(
                    new BlockPos(2, 1, 2),
                    EntityType.CHEST_MINECART,
                    getCustomName,
                    customItemName
            );
            context.complete();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultNameOtherSlot_hopperMinecart(TestContext context) {
        clearTest(context);

        context.killAllEntities();
        var entity = context.spawnEntity(EntityType.HOPPER_MINECART, 2, 1, 2);
        entity.setInventoryStack(1, createNamedItem());

        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
        context.waitAndRun(4, () -> {
            context.expectEntityWithData(
                    new BlockPos(2, 1, 2),
                    EntityType.HOPPER_MINECART,
                    getCustomName,
                    defaultName
            );
            context.complete();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultNameOtherSlot_chestMinecart(TestContext context) {
        clearTest(context);

        context.killAllEntities();
        var entity = context.spawnEntity(EntityType.CHEST_MINECART, 2, 1, 2);
        entity.setInventoryStack(1, createNamedItem());

        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
        context.waitAndRun(4, () -> {
            context.expectEntityWithData(
                    new BlockPos(2, 1, 2),
                    EntityType.CHEST_MINECART,
                    getCustomName,
                    defaultName
            );
            context.complete();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultNameUnnamedItem_hopperMinecart(TestContext context) {
        clearTest(context);

        context.killAllEntities();
        var entity = context.spawnEntity(EntityType.HOPPER_MINECART, 2, 1, 2);
        entity.setInventoryStack(0, createUnnamedItem());

        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
        context.waitAndRun(4, () -> {
            context.expectEntityWithData(
                    new BlockPos(2, 1, 2),
                    EntityType.HOPPER_MINECART,
                    getCustomName,
                    defaultName
            );
            context.complete();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registersWithDefaultNameUnnamedItem_chestMinecart(TestContext context) {
        clearTest(context);

        context.killAllEntities();
        var entity = context.spawnEntity(EntityType.CHEST_MINECART, 2, 1, 2);
        entity.setInventoryStack(0, createUnnamedItem());

        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
        context.waitAndRun(4, () -> {
            context.expectEntityWithData(
                    new BlockPos(2, 1, 2),
                    EntityType.CHEST_MINECART,
                    getCustomName,
                    defaultName
            );
            context.complete();
        });
    }

    @GameTest(structure = "scl_tests:basic")
    public void registers_and_unregisters(TestContext context) {
        clearTest(context);

        context.spawnEntity(EntityType.MINECART, 2, 1, 2);
        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
        
        context.waitAndRun(4, () -> {
            context.expectEntityWithData(new BlockPos(2, 1, 2), EntityType.MINECART, getCustomName, defaultName);

            context.waitAndRun(4, () -> {
                context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);

                context.waitAndRun(4, () -> {
                    context.expectEntityWithData(new BlockPos(2, 1, 2), EntityType.MINECART, getCustomName, null);
                    context.complete();
                });
            });
        });
    }

    /**
     * Covers <a href="https://github.com/scriptcoded/scripts-chunk-loaders/issues/34">#34</a>
     */
    @GameTest(structure = "scl_tests:basic")
    public void registers_unregisters_and_registers(TestContext context) {
        clearTest(context);

        context.spawnEntity(EntityType.MINECART, 2, 1, 2);
        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
        context.waitAndRun(4, () -> {
            context.expectEntityWithData(new BlockPos(2, 1, 2), EntityType.MINECART, getCustomName, defaultName);
            context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);

            context.waitAndRun(4, () -> {
                context.expectEntityWithData(new BlockPos(2, 1, 2), EntityType.MINECART, getCustomName, null);
                context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);

                context.waitAndRun(4, () -> {
                    context.expectEntityWithData(new BlockPos(2, 1, 2), EntityType.MINECART, getCustomName, defaultName);
                    context.complete();
                });
            });
        });
    }

    @GameTest(structure = "scl_tests:empty")
    public void doesNotRegisterWithEmptyDispenser(TestContext context) {
        clearTest(context);

        context.spawnEntity(EntityType.MINECART, 2, 1, 2);
        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);
        context.waitAndRun(4, () -> {
            context.expectEntityWithData(
                    new BlockPos(2, 1, 2),
                    EntityType.MINECART,
                    getCustomName,
                    null
            );
            context.complete();
        });
    }

    @GameTest(structure = "scl_tests:sculk_activate")
    public void registerWithResonance(TestContext context) {
        clearTest(context);

        context.spawnEntity(EntityType.MINECART, 2, 1, 2);
        context.removeBlock(new BlockPos(3, 1, 2));
        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);

        context.waitAndRun(15, () -> {
            BlockPos pos = new BlockPos( 4, 1, 2);
            SculkSensorBlockEntity sensor = context.getBlockEntity(pos, SculkSensorBlockEntity.class);

            if (sensor.getLastVibrationFrequency() != startLoaderFrequency) {
                throw context.createError(pos, String.format(
                        "Expected a vibration frequency of %s, instead got %s",
                        startLoaderFrequency,
                        sensor.getLastVibrationFrequency()
                ));
            }

            context.expectEntityWithData(
                    new BlockPos(2, 1, 2),
                    EntityType.MINECART,
                    getCustomName,
                    defaultName
            );
            context.complete();
        });
    }

    @GameTest(structure = "scl_tests:sculk_activate")
    public void unregisterWithResonance(TestContext context) {
        clearTest(context);

        context.spawnEntity(EntityType.MINECART, 2, 1, 2);
        context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);

        context.waitAndRun(4, () -> {
            context.expectEntityWithData(new BlockPos(2, 1, 2), EntityType.MINECART, getCustomName, defaultName);
            context.removeBlock(new BlockPos(3, 1, 2));

            context.waitAndRun(4, () -> {
                context.putAndRemoveRedstoneBlock(new BlockPos(1, 1, 1), 1);

                context.waitAndRun(8, () -> {
                    BlockPos pos = new BlockPos( 4, 1, 2);
                    SculkSensorBlockEntity sensor = context.getBlockEntity(pos, SculkSensorBlockEntity.class);

                    if (sensor.getLastVibrationFrequency() != stopLoaderFrequency) {
                        throw context.createError(pos, String.format(
                                "Expected a vibration frequency of %s, instead got %s",
                                stopLoaderFrequency,
                                sensor.getLastVibrationFrequency()
                        ));
                    }

                    context.expectEntityWithData(
                            new BlockPos(2, 1, 2),
                            EntityType.MINECART,
                            getCustomName,
                            null
                    );
                    context.complete();
                });
            });
        });
    }
}