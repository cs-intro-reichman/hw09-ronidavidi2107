/** A linked list of character data objects.
 *  (Actually, a list of Node objects, each holding a reference to a character data object.
 *  However, users of this class are not aware of the Node objects. As far as they are concerned,
 *  the class represents a list of CharData objects. Likwise, the API of the class does not
 *  mention the existence of the Node objects). */
public class List {

    // Points to the first node in this list
    private Node first;

    // The number of elements in this list
    private int size;
	
    /** Constructs an empty list. */
    public List() {
        first = null;
        size = 0;
    }
    
    /** Returns the number of elements in this list. */
    public int getSize() {
 	      return size;
    }
    /** Returns the CharData of the first element in this list. */
    public CharData getFirst() {
        // If you prefer, you can throw an exception when size==0.
        if (first == null) return null;
        return first.cp;
    }
    

    /** GIVE Adds a CharData object with the given character to the beginning of this list. */
    public void addFirst(char chr) {
        CharData newData = new CharData(chr);
        Node newHead = new Node(newData, first);
        first = newHead;
        size++;
    }
    
    /** GIVE Textual representation of this list. */
    public String toString() {
    if (size == 0) return "()";

    Node currentNode = first;
     StringBuilder builder = new StringBuilder();
     builder.append("(");

    while (currentNode != null) {
        builder.append(currentNode.toString()).append(" ");
        currentNode = currentNode.next;
        }

        // remove trailing space and close
     builder.setLength(builder.length() - 1);
     builder.append(")");


     return builder.toString();
    }

    /** Returns the index of the first CharData object in this list
     *  that has the same chr value as the given char,
     *  or -1 if there is no such object in this list. */
    public int indexOf(char chr) {
    if (size == 0) return -1;

     int index = 0;
     Node currentNode = first;

        while (currentNode != null) {
            if (chr == currentNode.cp.chr) {
                return index;
            }
            currentNode = currentNode.next;
            index++;
        }

        return -1;
    }
    /** If the given character exists in one of the CharData objects in this list,
     *  increments its counter. Otherwise, adds a new CharData object with the
     *  given chr to the beginning of this list. */
   public void update(char chr) {
    int foundIndex = indexOf(chr);

    if (foundIndex == -1) {
            addFirst(chr);
            return;
        }

    Node currentNode = first;
    for (int i = 0; i < foundIndex; i++) {
            currentNode = currentNode.next;
        }
        currentNode.cp.count++;
    }

    /** GIVE If the given character exists in one of the CharData objects
     *  in this list, removes this CharData object from the list and returns
     *  true. Otherwise, returns false. */
     public boolean remove(char chr) {
        int indexToRemove = indexOf(chr);

        if (indexToRemove == -1) return false;
        if (first == null) return false;

        if (indexToRemove == 0) {
            first = first.next;
            size--;
            return true;
        }

        Node prev = first;
        for (int i = 0; i < indexToRemove - 1; i++) {
            prev = prev.next;
        }

        prev.next = prev.next.next;
        size--;
        return true;
    }

    /** Returns the CharData object at the specified index in this list. 
     *  If the index is negative or is greater than the size of this list, 
     *  throws an IndexOutOfBoundsException. */
     public CharData get(int index) {
     if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException();
        }

    Node currentNode = first;
        for (int i = 0; i < index; i++) {
            currentNode = currentNode.next;
        }

        return currentNode.cp;
    }

    /** Returns an array of CharData objects, containing all the CharData objects in this list. */
    public CharData[] toArray() {
	    CharData[] arr = new CharData[size];
	    Node current = first;
	    int i = 0;
        while (current != null) {
    	    arr[i++]  = current.cp;
    	    current = current.next;
        }
        return arr;
    }

    /** Returns an iterator over the elements in this list, starting at the given index. */
    public ListIterator listIterator(int index) {
	    // If the list is empty, there is nothing to iterate   
	    if (size == 0) return null;
	    // Gets the element in position index of this list
	    Node current = first;
	    int i = 0;
        while (i < index) {
            current = current.next;
            i++;
        }
        // Returns an iterator that starts in that element
	    return new ListIterator(current);
    }
}