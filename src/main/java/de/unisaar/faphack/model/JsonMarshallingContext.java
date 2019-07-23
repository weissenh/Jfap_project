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

  private String getNewId() {
    String idstr = String.format("%06d", idGenerator);
    // %06 : fill with zeros, such that string has at least length 6
    // assert id wasn't used before
    idGenerator++;
    return idstr;
  }

  private String getClassNamePlusID(Storable s) {
    String id = this.getNewId();  // e.g. 000001
    String classname = s.getClass().getName();  // e.g. World  or Item  or ...
    return classname + "@" + id; // World@00001
  }

  @Override
  public void save(Storable s) {
    JSONObject jo = getJOforStorable(s);
    try {
      FileWriter fw = new FileWriter(this.file);
      fw.write(jo.toJSONString());
      fw.flush();
      fw.close();
    }
    catch (java.io.IOException e) {
      e.printStackTrace();
    }
//    finally {
//      fw.flush();
//      fw.close();
//    }
    // return;
  }

  public Storable read() {
    // jsonobject jo1 = parse_file(file)
    // string id = extract_id("storable@00001")
    // if id in chache:
    //   return readchache(id)
    // else:
    // string classname = extract_classname("storable@00001")
    // stack.push(jo1)
    // storable o1 = factory.get_new_object_from_class(classname)
    // reachchache(id -> o1)
    // o1.unmarshal(this)
    //  [ in unmarshal:  x  = .read("attribute")   in read:  jo jo2 = jo1.get("attribute"); stack.push(o2)  ]
    // stack.pop() ?
    // return o1
    return null;
  }

  private JSONObject getJOforStorable(Storable s) {
    JSONObject jo = new JSONObject();
    String id = this.writecache.get(s);
    if (id != null) { // seen before!
      // want jo to be just  { id : classname@uniquenumber }
      jo.put("id", id);
      return jo;
    }
    else { // not seen before! add to cache and marshall object
      String classnameplusid = this.getClassNamePlusID(s);
      this.writecache.put(s, classnameplusid);
      jo.put("id", classnameplusid);
      // json object still empty, pop from stack if completely filled with all instance variables
      stack.push(jo);
      s.marshal(this); // add all attributes of s to the json object
      // note: marshal calls write and write can access stack, writechache for inner objects
      assert stack.size() > 0;
      jo = stack.pop(); // jo completely processed
      return jo;
    }
  }

  @Override
  public void write(String key, Storable object) {
    JSONObject jo4storable = getJOforStorable(object); // value
    assert stack.size() > 0;
    JSONObject currentjo = this.stack.pop();
    currentjo.put(key, jo4storable); // key : value
    this.stack.push(currentjo);
  }

  @Override
  public <T extends Storable> T read(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void write(String key, int object) {
    // json object on stack, would like to add a "key": object  pair to it
    assert stack.size() > 0;
    JSONObject jo = this.stack.pop();
    jo.put(key, object);
    this.stack.push(jo);
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
    // json object on stack, would like to add a "key": object  pair to it
    assert stack.size() > 0;
    JSONObject jo = this.stack.pop();
    jo.put(key, object);
    this.stack.push(jo);
  }

  @Override
  public double readDouble(String key) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void write(String key, String object) {
    // json object on stack, would like to add a "key": "object"  pair to it
    // if (this.stack.size() == 0) raise Error? shouldnt happen
    assert stack.size() > 0;
    JSONObject jo = this.stack.pop();
    jo.put(key, object);
    this.stack.push(jo);
  }

  @Override
  public String readString(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void write(String key, Collection<? extends Storable> coll) {
    /*
    "meineliste" : [
    {"id": "Wearable@000001"},
    {"id": "Wearable@000002",
      "otherstuff" : "otherstuff",
      "otherstuff2" : "otherstuff2",
      "othercomplex" : {
        "id": "complexthing@001",
        "otherstuff" : "otherstuff"
      }
    }
  ]
   */
    // json object on stack, would like to add a "key":  pair to it
    // if (this.stack.size() == 0) raise Error? shouldnt happen
    assert stack.size() > 0;
    JSONObject jo = this.stack.pop();
    // for each element in the collection, write it (either id if written before or complete object)
    JSONArray jarray = new JSONArray();
    // for each item (storable!) in the collection coll: get its json object
    for (Storable item : coll) {
      JSONObject joitem = getJOforStorable(item);
      jarray.add(joitem);
    }
    // todo: assert that jarray has same size as collection
    jo.put(key, jarray);  // "key" : [ { <item 1> }, { <item 2> }, ...]
    // jo.put(key, object);
    this.stack.push(jo);
  }

  @Override
  public void readAll(String key, Collection<? extends Storable> coll) {
    // TODO Auto-generated method stub

  }

  @Override
  public void write(String key, Tile[][] coll) {
    /*
    key: [
      [ Tileobj1 , Tileobj2 , ... ],
      [ Tileobj1, Tileobj2, ...],
      [ Tileobj1 , Tileobj2, ....]
    ]
    */
    // if (this.stack.size() == 0) raise Error? shouldnt happen
    assert stack.size() > 0;
    JSONObject jo = this.stack.pop();
    JSONArray outerjsonarray = new JSONArray();
    for (Tile[] innerarray : coll) {
      // start array for innerlist
      JSONArray innerjsonarray = new JSONArray();
      // fill this array with the json objects for the Tiles
      for (Tile cell : innerarray) {
        JSONObject jotile = getJOforStorable(cell);
        innerjsonarray.add(jotile);
      } // for each cell/Tile
      // built one jsonarray with tiles as json objects
      // add it to our outer array, collecting the rows of Tiles
      outerjsonarray.add(innerjsonarray);
    } // innerarray (for each row of Tiles)
    jo.put(key, outerjsonarray);
    this.stack.push(jo);
  }

  @Override
  public Tile[][] readBoard(String key) {
    // TODO Auto-generated method stub
    return null;
  }

}
