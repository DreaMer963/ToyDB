package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    private ArrayList<TDItem> arrayList = new ArrayList<>();

    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         * */
        public final Type fieldType;

        /**
         * The name of the field
         * */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        @Override
        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }

        @Override
        public boolean equals(Object o)
        {
            if(o == null)
                return false;
            if(o == this)
                return true;
            if(!(o instanceof TDItem))
                return false;

            TDItem tdio = (TDItem) o;
            return tdio.fieldType == this.fieldType;
        }

    }

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        return arrayList.iterator();
    }

    private static final long serialVersionUID = 1L;

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     *
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        assert typeAr.length > 0 : "TupleDesc must contain at least one entry";
        int typeArLen = typeAr.length;
        int fieldArLen = fieldAr.length;
        if (typeArLen < fieldArLen) {
            for (int i = 0; i < typeArLen; i++)
                arrayList.add(new TDItem(typeAr[i], fieldAr[i]));

            for (int i = typeArLen; i < fieldArLen; i++)
                arrayList.add(new TDItem(null, fieldAr[i]));
        } else {
            for (int i = 0; i < fieldArLen; i++)
                arrayList.add(new TDItem(typeAr[i], fieldAr[i]));

            for (int i = fieldArLen; i < typeArLen; i++)
                arrayList.add(new TDItem(typeAr[i], null));
        }
    }
    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     *
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        assert typeAr.length > 0 : "TupleDesc must contain at least one entry";
        for (Type aTypeAr : typeAr) {
            TDItem item = new TDItem(aTypeAr, null);
            arrayList.add(item);
        }
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        return arrayList.size();
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     *
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        if (i>=0 && i< arrayList.size())
            return arrayList.get(i).fieldName;
        else
            throw new NoSuchElementException();
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     *
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        if(i>=0 && i<arrayList.size())
            return arrayList.get(i).fieldType;
        else
            throw new NoSuchElementException();
    }

    /**
     * Find the index of the field with a given name.
     *
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        for(int i=0; i<arrayList.size(); i++)
        {
            String fieldName = arrayList.get(i).fieldName;
            if(fieldName!=null && fieldName.equals(name))
                return i;
        }
        throw new NoSuchElementException();
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        int size = 0;
        for(TDItem td : arrayList)
        {
            if(td != null )
            {
                    size += td.fieldType.getLen();

            }
        }
        return size;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     *
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        final int numFields = td1.numFields() + td2.numFields();
        Type []typeAr = new Type[numFields];
        String []fieldAr = new String[numFields];
        Iterator<TDItem> it1 = td1.iterator();
        int index = 0;
        while (it1.hasNext())
        {
            TDItem tdItem = it1.next();
            typeAr[index] = tdItem.fieldType;
            fieldAr[index] = tdItem.fieldName;
            index++;
        }
        Iterator<TDItem> it2 = td2.iterator();
        while (it2.hasNext())
        {
            TDItem tdItem = it2.next();
            typeAr[index] = tdItem.fieldType;
            fieldAr[index] = tdItem.fieldName;
            index++;
        }
        return (new TupleDesc(typeAr, fieldAr));
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they have the same number of items
     * and if the i-th type in this TupleDesc is equal to the i-th type in o
     * for every i.
     *
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */

    public boolean equals(Object o) {
        if(o == null)
            return false;
        if(o == this)
            return true;
        if(!(o instanceof TupleDesc))
            return false;

        TupleDesc tdo = (TupleDesc) o;
        if(this.numFields() != tdo.numFields())
            return false;
        Iterator<TDItem> it1 = tdo.iterator();
        Iterator<TDItem> it2 = this.iterator();
        while(it2.hasNext())
        {
            // override equals in TDItem
            if(!(it1.next().equals(it2.next())))
                return false;
        }
        return true;
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     *
     * @return String describing this descriptor.
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(TDItem td : arrayList)
        {
            s.append(td.fieldType).append("(").append(td.fieldName).append(")").append(",");
        }
        s.deleteCharAt(s.lastIndexOf(s.toString())); // delete the last ","
        return s.toString();
    }
}
