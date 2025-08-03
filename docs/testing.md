# Testing

This project uses the [Minecraft GameTest framework](https://minecraft.wiki/w/GameTest)
along with [Fabric's GameTest integration](https://docs.fabricmc.net/develop/automatic-testing#writing-game-tests)
for testing.

File structure
```
src/
  gametest/                                # Module for all things GameTest.
    java/
      io.nihlen.scriptschunkloaders/       # Package in which we put our tests.
        ScriptsChunkLoadersGameTest.java   # We can have multiple test files. Also add to fabric.mod.json.
    resources/
      data.scl_tests/
        structure/                         # Structures referenced by the tests live here.
          basic.nbt
          empty.nbt
      fabric.mod.json                      # The GameTests are in their own mod which is defined here.
  main/                                    # The ordinary mod files.
```

You can launch Minecraft with the GameTest mod loaded by running the "GameTest
Minecraft Client" configuration.

To design a new structure for a test, make sure to first define the structure in
your test code:
```java
@GameTest(structure = "scl_tests:my_new_structure")
public void my_new_test(TestContext context) {
  // Your test goes here
}
```

Then start the game. Open a world and run the command
`/test create scl_tests:scripts_chunk_loaders_game_test_my_new_test`
(autocomplete will help you).

A test area will be generated in which your can design your test. To save the
structure, right click the Test Block and click "Save Structure". Your structure
will be saved in
`run/saves/<your_save>/generated/scl_tests/structures/my_new_structure.nbt`.
Move this into `src/gametest/resources/data/scl_tests/structure` to ensure it's
commited into Git.

## Hotswapping tests

To hotswap tests, make sure to have your IDE configured to use the JetBrains
Runtime and add `-XX:+AllowEnhancedClassRedefinition` to your VM Arguments for
the "GameTest Minecraft Client" configuration. You can then start the game using
the debugger and enjoy hotswapping of existing tests.

You can read more about hotswapping in the [Fabric documentation](https://docs.fabricmc.net/develop/getting-started/launching-the-game#hotswapping-classes).


