package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.Tile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class JsonMarshallingContext implements MarshallingContext {

  private final File file;

  private static StorableFactory factory;

  private final IdentityHashMap<Object, String> writecache;

  private final Deque<JSONObject> stack;

  private final Map<String, Storable> readcache;

  private int idGenerator = 1;

  public JsonMarshallingContext(File f, StorableFactory fact) {
    file = f;
    writecache = new IdentityHashMap<>();
    stack = new ArrayDeque<>();
    readcache = new HashMap<>();
    factory = fact;
  }

  public void save(Storable s) {
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(toJson(s).toJSONString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getUniqueLabel(Storable s) {
    return String.format("%s@%06d", StorableFactory.getClassName(s.getClass()),
        idGenerator++);
  }

  String getClassNameFromId(String id) {
    int atPos = id.indexOf('@');
    return id.substring(0, atPos);
  }

  public Storable read() {
    JSONParser parser = new JSONParser();
    try (Reader reader = new FileReader(file)) {
      JSONObject jsonObject = (JSONObject) parser.parse(reader);
      return fromJson(jsonObject);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  private JSONObject toJson(Storable s) {
    if (s == null)
      return null;
    JSONObject obj = new JSONObject();
    if (! writecache.containsKey(s)) {
      stack.push(obj);
      String uniqueId = getUniqueLabel(s);
      writecache.put(s, uniqueId);
      obj.put("id", uniqueId);
      s.marshal(this);
      stack.pop();
    } else {
      obj.put("id", writecache.get(s));
    }
    return obj;
  }

  private JSONArray toJsonArray(Collection<? extends Storable> coll) {
    if (coll == null)
      return null;
    JSONArray arr = new JSONArray();
    for (Storable s : coll) {
      JSONObject obj = toJson(s);
      arr.add(obj);
    }
    return arr;
  }

  private Storable fromJson(JSONObject obj) {
    if (obj == null)
      return null;
    String id = (String) obj.get("id");
    if (! readcache.containsKey(id)) {
      String clazz = getClassNameFromId(id);
      Storable o = factory.newInstance(clazz);
      stack.push(obj);
      readcache.put(id, o);
      o.unmarshal(this);
      stack.pop();
    }
    return readcache.get(id);
  }

  private <T extends Storable> void fromJsonArray(JSONArray arr, Collection<T> coll) {
    for (Object o: arr) {
      Storable s = fromJson((JSONObject)o);
      coll.add((T)s);
    }
  }

  @Override
  public void write(String key, Storable object) {
    stack.peek().put(key, toJson(object));
  }

  @Override
  public <T extends Storable> T read(String key) {
    return (T)fromJson((JSONObject)stack.peek().get(key));
  }

  @Override
  public void write(String key, int object) {
    stack.peek().put(key, object);
  }

  @Override
  public int readInt(String key) {
    return ((Number)stack.peek().get(key)).intValue();
  }

  @Override
  public void write(String key, double object) {
    stack.peek().put(key, object);
  }

  @Override
  public double readDouble(String key) {
    return ((Number)stack.peek().get(key)).doubleValue();
  }

  @Override
  public void write(String key, String object) {
    stack.peek().put(key, object);
  }

  @Override
  public String readString(String key) {
    return (String)stack.peek().get(key);
  }

  @Override
  public void write(String key, Collection<? extends Storable> coll) {
    toJsonArray(coll);
  }

  @Override
  public void readAll(String key, Collection<? extends Storable> coll) {
    JSONArray arr = (JSONArray)stack.peek().get(key);
    fromJsonArray(arr, coll);
  }

  @Override
  public void write(String key, Tile[][] coll) {
    JSONArray arr = new JSONArray();
    for (Tile[] s : coll) {
      JSONArray row = toJsonArray(Arrays.asList(s));
      arr.add(row);
    }
  }

  @Override
  public Tile[][] readBoard(String key) {
    JSONArray rows = (JSONArray)stack.peek().get(key);
    Tile[][] result = new Tile[rows.size()][];
    int r = 0;
    for (Object o : rows) {
      JSONArray jsoncol = (JSONArray)o;
      List<Tile> col = new ArrayList<>();
      fromJsonArray(jsoncol, col);
      result[r] = col.toArray(new Tile[col.size()]);
      ++r;
    }
    return result;
  }

}
