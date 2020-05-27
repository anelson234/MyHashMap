import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
/**
 * @author Adam Nelson
 * 
 * Description: The purpose of this class is to create my own Generic HashMap, mimicking
 * the java HashMap class. MyHashMap is backed by an ArrayList and each bucket
 * being a LinkedList. Bucket size for this project is 8 and included methods are 
 * put, get, containsKey, containsValue, isEmpty, keySet, size, remove, clear, which
 * are all methods from the java HashMap class, but also a custom printTable method
 * that shows each indexed bucket, how many collisions occur there and all of the elements
 * in that bucket.
 *
 * @param <K>
 * @param <V>
 */
public class MyHashMap<K,V> {
	
	private List<LinkedList<HashNode<K,V>>> hashTable;
	
	public MyHashMap() {
		hashTable = new ArrayList<LinkedList<HashNode<K,V>>>();
		for (int i = 0; i < 8; i++) {
			hashTable.add(new LinkedList<HashNode<K,V>>());
		}
	}
	
	/**
	 *  This method puts a key value pair into the HashMap, if the key
	 *  already exists within the map, the program will update value in place.
	 *  Otherwise this will put an element at the front of the bucket.
	 * @param key
	 * @param val
	 * @return The previous value that a key had, if any, otherwise return null
	 */
	public V put(K key,V val) {
		int idx = hash(key);
		V value = null;
		HashNode<K,V> node = new HashNode<K,V>(key,val);
		if (!containsKey(key)) {
			hashTable.get(idx).add(0,node);
			return value;
		}else {
			for (int i = 0; i < hashTable.get(idx).size(); i++) {
				if (hashTable.get(idx).get(i).getKey().equals(key)) {
					value = hashTable.get(idx).get(i).getValue();
					hashTable.get(idx).get(i).setValue(val);
					return value;
				}
			}
		}
		return value;
	}
	
	/**
	 * This method is given a key and returns the associated value.
	 * @param key
	 * @return a value from the HashMap if the value
	 * exists, otherwise null
	 */
	public V get(Object key) {
		int idx = hash(key);
		for (int i = 0; i < hashTable.get(idx).size(); i++) {
			if (hashTable.get(idx).get(i).getKey().equals(key)) {
				return hashTable.get(idx).get(i).getValue();
			}
		}
		return null;
	}
	
	/**
	 * This method tells us if our HashMap contains a certain key
	 * @param key
	 * @return true or false
	 */
	public boolean containsKey(Object key) {
		int idx = hash(key);
		for (int i = 0; i < hashTable.get(idx).size(); i++) {
			if (hashTable.get(idx).get(i).getKey().equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method tells us if our HashMap contains a certain value, if the
	 * value is found anywhere, even for multiple keys, returns true.
	 * @param value
	 * @return true or false.
	 */
	public boolean containsValue(Object value) {
		for (int i = 0; i < hashTable.size(); i ++) {
			for (int j = 0; j < hashTable.get(i).size(); j++) {
				if (hashTable.get(i).get(j).getValue().equals(value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * This method tells us if our HashMap is empty
	 * @return true or false
	 */
	public boolean isEmpty() {
		return size() == 0;
	}
	
	/**
	 * This method creates a Set of K elements and puts all of the keys from the
	 * HashMap into this Set
	 * @return Set<K>
	 */
	public Set<K> keySet(){
		Set<K> keySet = new HashSet<K>();
		for (int i = 0; i < hashTable.size(); i++) {
			for (int j = 0; j < hashTable.get(i).size(); j++) {
				keySet.add(hashTable.get(i).get(j).getKey());
			}
		}
		return keySet;
	}
	
	/**
	 * @return The current size of the HashMap
	 */
	public int size() {
		int size = 0;
		for (int i = 0; i < hashTable.size(); i++) {
			Set<K> set = indexKeySet(i);
			size += set.size();
		}
		return size;
	}
	
	/**
	 * This method removes a specified key value pair from the HashMap
	 * @param key
	 * @return the value that was removed
	 */
	public V remove(Object key) {
		int idx = hash(key);
		V val = null;
		for (int i = 0; i < hashTable.get(idx).size(); i++) {
			if (hashTable.get(idx).get(i).getKey().equals(key)) {
				val = hashTable.get(idx).get(i).getValue();
				hashTable.get(idx).remove(i);
			}
		}
		return val;
	}
	
	/**
	 * This method clears the entire HashMap making it new again
	 */
	public void clear() {
		hashTable.clear();
		for (int i = 0; i < 8; i++) {
			hashTable.add(new LinkedList<HashNode<K,V>>());
		}
		
	}
	
	/**
	 * This method acts like a toString method for our HashMap and will look
	 * as such: 
	 * 
	 * Index 0: (0 conflicts), [carrots]
	 * Index 1: (0 conflicts), []
	 * Index 2: (0 conflicts), [coconuts]
	 * Index 3: (1 conflicts), [oranges, bananas]
	 * Index 4: (0 conflicts), []
	 * Index 5: (0 conflicts), [pears]
	 * Index 6: (0 conflicts), []
	 * Index 7: (1 conflicts), [cherries, apples]
	 * Total # of conflicts: 2
	 */
	public void printTable() {
		int totalConflict = 0;
		for (int i = 0; i < hashTable.size(); i++) {
			int conflicts = conflicts(i);
			totalConflict += conflicts;
			Set<K> keyAtIndex = indexKeySet(i);
			if (conflicts < 1) {
				System.out.println("Index " + i + ": (" + conflicts + " conflicts), " + keyAtIndex);
			}else {
				conflicts -= 1;
				totalConflict -= 1;
				System.out.println("Index " + i + ": (" + conflicts + " conflicts), " + keyAtIndex);
			}
		}
		System.out.println("Total # of conflicts: " + totalConflict);
	}
	
	/**
	 * This private method is used to calculate how many conflicts 
	 * my buckets have so that it may be used in the printTable method
	 * @param index
	 * @return number of conflicts in the HashMap
	 */
	private int conflicts(int index) {
		int conflicts = 0;
		if (hashTable.get(index).size() > 1) {
			for (int i = 0; i < hashTable.get(index).size(); i++) {
				conflicts += 1;
			}
		}
		return conflicts;
	}
	
	/**
	 * This private method is used to get the keySet at a specified index
	 * this is also to help our printTable method so that we may see
	 * every element in each specific bucket
	 * @param index
	 * @return Set<K>
	 */
	private Set<K> indexKeySet(int index){
		Set<K> keySet = new HashSet<K>();
		for (int i = 0; i < hashTable.get(index).size(); i++) {
			keySet.add(hashTable.get(index).get(i).getKey());
		}
		return keySet;
	}
	
	/**
	 * This method is the HashCode method that allows us to create buckets 
	 * and access our indexes 
	 * @param key
	 * @return Index in the HashMap
	 */
	private int hash(Object key) {
		int hashCode = key.hashCode();
		int index = hashCode % 8;
		return Math.abs(index);
	}

}
