package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.Tile;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class JsonMarshallingContext implements MarshallingContext {

  private final File file;

  private static StorableFactory factory;

  private IdentityHashMap<Object, String> writecache;

  private Deque<JSONObject> stack;

  private Map<String, Storable> readcache;

  private int idGenerator = 1;

  public JsonMarshallingContext(File f, StorableFactory fact) {
    file = f;
  }

  @Override
  public void save(Storable s) {
    // ask writechache if aleardy seen
    // if seen: return { "id" : writechache(s) }    // eg. { 'id' : 'World@0001' }
    // else: // not seen before
    //   jo1 = new jsonobject()
    //   String newid = getNewID()  // todo: write this method
    //   writechache.put(s, newId)
    //   stack.push(jo1)
    //   s.marshal(this)  // maybe recursive: write, ....ask chache already seeen ....
    //   stack.pop()
  }

  public Storable read() {
    // jsonobject jo1 = parse_file(file)
    // string classname = extract_classname("storable@00001")
    // string id = extract_id("storable@00001")
    // if id in chache:
    //   return readchache(id)
    // else:
    // stack.push(jo1)
    // storable o1 = factory.get_new_object_from_class(classname)
    // o1.unmarshal(this)
    //  [ in unmarshal:  x  = .read("attribute")   in read:  jo jo2 = jo1.get("attribute"); stack.push(o2)  ]
    // return o1
    return null;
  }

  @Override
  public void write(String key, Storable object) {
    // todo write this method
  }

  @Override
  public <T extends Storable> T read(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void write(String key, int object) {
    // TODO Auto-generated method stub

  }

  @Override
  public int readInt(String key) {
    // TODO Auto-generated method stub
    // deque.pop
    //
    // deque.push
    return 0;
  }

  @Override
  public void write(String key, double object) {
    // TODO Auto-generated method stub

  }

  @Override
  public double readDouble(String key) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void write(String key, String object) {
    // TODO Auto-generated method stub

  }

  @Override
  public String readString(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void write(String key, Collection<? extends Storable> coll) {
    // TODO Auto-generated method stub

  }

  @Override
  public void readAll(String key, Collection<? extends Storable> coll) {
    // TODO Auto-generated method stub

  }

  @Override
  public void write(String key, Tile[][] coll) {
    // TODO Auto-generated method stub

  }

  @Override
  public Tile[][] readBoard(String key) {
    // TODO Auto-generated method stub
    return null;
  }

}
