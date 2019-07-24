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
      readcache = new HashMap<>();
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

  private Storable getStorableFromJsonObject(JSONObject jobj) {
    // ask for id  //parse exception?
    String id = (String) jobj.get("id");  // id = "World@000001":
    if (id == null) { return null; }
    // ask readchache if id seen
    Storable s = this.readcache.get(id);
    // if yes, return readchache(id)
    if (s != null) { // already seen this object, just return it!
      // todo: maybe check if matched other fields if any?
      return s;
    }
    assert s == null;
    assert !this.readcache.containsKey(id);
    // if no: we haven't object yet
    // push on stack, put in chache, unmarshal, pop form stack
    // if not seen (s == null), need to read in:
    stack.push(jobj);
    s = this.extractClassFromString(id);
    // assert (id, s) not in readcache
    // put new storable s in cache
    this.readcache.put(id, s);
    s.unmarshal(this);
    stack.pop();
    return s;
  }

  public Storable read() {
    JSONParser parser = new JSONParser();
    try {
      // jsonobject jo1 = parse_file(file)
      FileReader reader = new FileReader(file);
      Object obj = parser.parse(reader);
      JSONObject jobj = (JSONObject) obj; // ask getstorablefromjsonobject
      Storable s = getStorableFromJsonObject(jobj);
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
    // make it mirror write(String key, Storable object)
    // todo: change method to look less ugly
    assert stack.size() > 0;
    JSONObject jobj = this.stack.pop();
    Object o = jobj.get(key);
    // if key not in json object,
    if (o == null) {
      stack.push(jobj);
      return null;
    }
    JSONObject value = (JSONObject) o;
    // if key in json object, cast to jsonobject
    T t = (T) getStorableFromJsonObject(value); // can be null
    stack.push(jobj);
    return t;
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
    Object o = jobj.get(key); // todo what if key not present?
    Long lo = (Long) o;
    int result = lo.intValue();
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
    if (coll == null) {
      jo.put(key, null);
      stack.push(jo);
      return;
    }
    for (Storable item : coll) {
      JSONObject joitem = getJOforStorable(item);
      jarray.add(joitem);
    }
    // todo: assert that jarray has same size as collection
    jo.put(key, jarray);  // "key" : [ { <item 1> }, { <item 2> }, ...]
    this.stack.push(jo);
  }

  private <T extends Storable> void convertJArray2Collection(JSONArray jarray, Collection<T> coll) {
    // then we convert each item in the value(an array?) to a storable
    for (Object item : jarray) {
      // cast to json object
      JSONObject jitem = (JSONObject) item;
      // convert to storable
      Storable s = getStorableFromJsonObject(jitem);
      // add to collection
      coll.add((T) s);
    }
  }

  @Override
  public void readAll(String key, Collection<? extends Storable> coll) {
    // we ask the current json object for the value of key
    // coll can be null, but maybe we want
    // to change it in case the json file contains a json array instead of null
    assert stack.size() > 0;
    JSONObject jobj = stack.pop();
    Object o = jobj.get(key);
    if (o == null) {
      // either the key is not in the json object,
      // or it is but its value is null
      // for both, don't do json array...
      // todo: maybe signal if key wasn't present?
      if (jobj.containsKey(key)) {
        coll = null;
      }
      stack.push(jobj);
      return;
    }
    JSONArray jarray = (JSONArray) o;
    if (jarray != null && coll == null) {
      // in case the file contains an array (not null), but the collection in null,
      // we have to create an (empty) collection!
      coll = new ArrayList<Storable>(); // todo: don't depend on concrete impl!!!!!!!
    }
    convertJArray2Collection(jarray, coll);
    stack.push(jobj);
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
    if (coll == null) {
      jo.put(key, null);
      stack.push(jo);
      return;
    }
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
    assert stack.size() > 0;
    JSONObject jobj = stack.pop();
    Object value = jobj.get(key);
    if (value == null) {
      stack.push(jobj);
      return null;
    }
    JSONArray jarray = (JSONArray) value;
    Collection<Tile[]> tilematrix = new ArrayList<>();
    Collection<Tile> tilerow = new ArrayList<>();
    for (Object innerarray: jarray) {
      JSONArray innerjarray = (JSONArray) innerarray;  // todo what happens if cast not possible?
      convertJArray2Collection(innerjarray, tilerow);
      Tile[] tilerowarray = tilerow.toArray(new Tile[0]);
      tilematrix.add(tilerowarray);
    }
    Tile[][] tiles = tilematrix.toArray(new Tile[0][0]);
    stack.push(jobj);
    return tiles;
  }
}
