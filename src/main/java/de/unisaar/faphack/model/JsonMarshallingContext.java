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
      writecache = new IdentityHashMap<>();
      readcache = new IdentityHashMap<>();
      stack = new ArrayDeque<>();
      factory = fact; // todo: do input validation!
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
    // getSimpleName will return "" for anonymous inner class
    String classname = s.getClass().getSimpleName();  // e.g. World  or Item  or ...
    return classname + "@" + id; // World@00001
  }

  private Storable extractClassFromString(String str) {
    // str = "World@0001"
    String[] parts = str.split("@");
    assert parts.length == 2;   // todo turn this into if statement
    String classname = parts[0];
    Storable c = factory.newInstance(classname);
    return c; // todo: can be null!
  }

  @Override
  public void save(Storable s) {
    JSONObject jo = getJOforStorable(s);
    try {
      FileWriter fw = new FileWriter(this.file);
      // fw.write(jo.toJSONString());
      fw.write(jo.toString());  // writejsonstring???
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
    JSONParser parser = new JSONParser();
    try {
      // jsonobject jo1 = parse_file(file)
      FileReader reader = new FileReader(file);
      Object obj = parser.parse(reader);
      JSONObject jobj = (JSONObject) obj;
      // get id field and ask readchache if object already seen
      String id = (String) jobj.get("id");  // id = "World@000001":
      if (id == null) { throw new ParseException(42); }
      Storable s = this.readcache.get(id);
      if (s != null) { // already seen this object, just return it!
        // todo: maybe check if matched other fields if any?
        return s;
      }
      // if not seen (s == null), need to read in:
      stack.push(jobj);
      // string classname = extract_classname("storable@00001")
      // storable o1 = factory.get_new_object_from_class(classname)
      s = this.extractClassFromString(id);
      // assert (id, s) not in readcache
      // put new storable s in cache
      this.readcache.put(id, s);
      s.unmarshal(this);
      stack.pop();
      // o1.unmarshal(this)
      //  [ in unmarshal:  x  = .read("attribute")   in read:  jo jo2 = jo1.get("attribute"); stack.push(o2)  ]
      return s;
    }
    catch (ParseException pe) {
      System.out.println("position: " + pe.getPosition());
      System.out.println(pe);
    }
    catch (FileNotFoundException e) {
      System.out.println("File not found: " + file.toString());
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  private JSONObject getJOforStorable(Storable s) {
    JSONObject jo = new JSONObject();
    if (s == null) { // todo ? here or somewhere else?
        return null;
    }
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
    // TODO do this next
    // make it mirror write(String key, Storable object)
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
    // this.x = c.readInt("x");
    assert stack.size() > 0;
    JSONObject jobj = stack.pop();
    Object o = jobj.get(key);
    int result = (int) o;
    // convert to int, and return that int
    // todo can throw classcasexpection?
    // todo can throw NullPointerException
    stack.push(jobj);
    return result;
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
    assert stack.size() > 0;
    JSONObject jobj = stack.pop();
    Object o = jobj.get(key);
    double result = (double) o;
    // convert to int, and return that int
    // todo can throw classcasexpection?
    // todo can throw NullPointerException
    stack.push(jobj);
    return result;
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
    assert stack.size() > 0;
    JSONObject jobj = stack.pop();
    Object o = jobj.get(key);
    String result = (String) o;
    // convert to int, and return that int
    // todo can throw classcasexpection?
    // todo can throw NullPointerException
    stack.push(jobj);
    return result;
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
