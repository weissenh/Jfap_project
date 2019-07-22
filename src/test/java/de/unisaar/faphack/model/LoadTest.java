package de.unisaar.faphack.model;

import de.unisaar.faphack.dirtyhacks.StorableRegistrator;
import org.junit.jupiter.api.Test;

import java.io.File;

import static de.unisaar.faphack.model.TestUtils.createGame;
import static de.unisaar.faphack.model.TestUtils.getTestResourceFile;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoadTest {

  @Test
  void saveGame() {
    Game game = createGame();
    File f = getTestResourceFile("", "game.json");
    StorableFactory fact = new StorableFactory();
    StorableRegistrator.registerStorables(fact);
    MarshallingContext mc = new JsonMarshallingContext(f, fact);
    mc.save(game);
    assertTrue(f.canRead());

  }


  @Test
  void loadSword() {
    File f = getTestResourceFile("", "sword.json");
    StorableFactory fact = new StorableFactory();
    StorableRegistrator.registerStorables(fact);
    MarshallingContext mc = new JsonMarshallingContext(f, fact);
    Item sword = (Item)mc.read();
    assertNotNull(sword);
    File f2 = getTestResourceFile("", "sword_out.json");
    mc = new JsonMarshallingContext(f2, fact);
    mc.save(sword);
  }
}
