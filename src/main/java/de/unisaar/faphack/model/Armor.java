package de.unisaar.faphack.model;

import de.unisaar.faphack.model.effects.ModifyingEffect;
import de.unisaar.faphack.model.map.Tile;

public class Armor extends Wearable {

  private ModifyingEffect modifyingEffect;



  public Armor(){
    // super.isWeapon = true; // todo ???
  }

  public Armor(Tile where, String trait, CharacterModifier effect, Character c) {
    super(where, trait, effect, c);
    // todo input validation: possible trait?
    // todo modifiyng effect missing!
  }

  /**
   * The Effect connected to the item is always NULL.
   * The armor has a ModifyingEffect instead.
   */
   @Override
  public CharacterModifier getCharacterModifier(){
    return null;
  }

  public ModifyingEffect getModifyingEffect(){
    return modifyingEffect;
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("modifyingEffect", modifyingEffect);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    this.modifyingEffect = c.read("modifyingEffect");
  }
}
