package com.jmatio.types;

import java.nio.ByteBuffer;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class MLSparse extends MLNumericArray<Double>
{
    int nzmax;
    private SortedSet<IndexMN> indexSet;
    private SortedMap<IndexMN, Double> real;  
    private SortedMap<IndexMN, Double> imaginary;  
    
    /**
     * @param name name
     * @param dims dims
     * @param attributes attributes
     * @param nzmax nzmax
     */
    public MLSparse(String name, int[] dims, int attributes, int nzmax )
    {
        super(name, dims, MLArray.mxSPARSE_CLASS, attributes);
        this.nzmax = nzmax;
    }
    
    protected void allocate()
    {
        real = new TreeMap<IndexMN, Double>();
        if ( isComplex() )
        {
            imaginary = new TreeMap<IndexMN, Double>();
        }
        indexSet = new TreeSet<IndexMN>();
    }
    
    /**
     * Gets maximum number of non-zero values
     * 
     * @return max num of non-zero values
     */
    public int getMaxNZ()
    {
        return nzmax;
    }
    /**
     * Gets row indices
     * 
     * <kbd>ir</kbd> points to an integer array of length nzmax containing the row indices of
     * the corresponding elements in <kbd>pr</kbd> and <kbd>pi</kbd>.
     * @return row indices
     */
    public int[] getIR()
    {
        int[] ir = new int[nzmax];
        int i = 0;
        for ( IndexMN index : indexSet )
        {
            ir[i++] = index.m;
        }
        return ir;
    }
    /**
     * Gets column indices. 
     * 
     * <kbd>jc</kbd> points to an integer array of length N+1 that contains column index information.
     * For j, in the range <kbd>0&lt;=j&lt;=N 1</kbd>, <kbd>jc[j]</kbd> is the index in ir and <kbd>pr</kbd> (and <kbd>pi</kbd>
     * if it exists) of the first nonzero entry in the jth column and <kbd>jc[j+1] 1</kbd> index
     * of the last nonzero entry. As a result, <kbd>jc[N]</kbd> is also equal to nnz, the number
     * of nonzero entries in the matrix. If nnz is less than nzmax, then more nonzero
     * entries can be inserted in the array without allocating additional storage
     * 
     * @return column indices
     */
    public int[] getJC()
    {
        int[] jc = new int[getN()+1];
        // jc[j] is the number of nonzero elements in all preceeding columns
        for ( IndexMN index : indexSet )
        {
            for (int column = index.n + 1; column < jc.length; column++)
            {
                jc[column]++;
            }
        }
        return jc;
    }
    
    /* (non-Javadoc)
     * @see com.paradigmdesigner.matlab.types.MLNumericArray#getReal(int, int)
     */
    public Double getReal(int m, int n)
    {
        IndexMN i = new IndexMN(m,n);
        if ( real.containsKey(i) )
        {
            return real.get(i);
        }
        return new Double(0);
    }
    
    /* (non-Javadoc)
     * @see com.jmatio.types.MLNumericArray#getReal(int)
     */
    public Double getReal ( int index )
    {
        throw new IllegalArgumentException("Can't get Sparse array elements by index. " +
        "Please use getReal(int index) instead.");
    }
    /**
     * @param value value
     * @param m m
     * @param n n
     */
    public void setReal(Double value, int m, int n)
    {
        IndexMN i = new IndexMN(m,n);
        indexSet.add(i);
        real.put(i, value );
    }
    /**
     * @param value value
     * @param index index
     */
    public void setReal(Double value, int index)
    {
        throw new IllegalArgumentException("Can't set Sparse array elements by index. " +
                "Please use setReal(Double value, int m, int n) instead.");
    }
    /**
     * @param value value
     * @param m m
     * @param n n
     */
    public void setImaginary(Double value, int m, int n)
    {
        IndexMN i = new IndexMN(m,n);
        indexSet.add(i);
        imaginary.put(i, value );
    }
    /**
     * @param value imaginary value
     * @param index index
     */
    public void setImaginary(Double value, int index)
    {
        throw new IllegalArgumentException("Can't set Sparse array elements by index. " +
        "Please use setImaginary(Double value, int m, int n) instead.");
    }
    /* (non-Javadoc)
     * @see com.paradigmdesigner.matlab.types.MLNumericArray#getImaginary(int, int)
     */
    public Double getImaginary(int m, int n)
    {
        IndexMN i = new IndexMN(m,n);
        if ( imaginary.containsKey(i) )
        {
            return imaginary.get(i);
        }
        return new Double(0);
    }
    /* (non-Javadoc)
     * @see com.jmatio.types.MLNumericArray#getImaginary(int)
     */
    public Double getImaginary( int index )
    {
        throw new IllegalArgumentException("Can't get Sparse array elements by index. " +
        "Please use getImaginary(int index) instead.");
    }
    
    /**
     * Returns the real part (PR) array. PR has length number-of-nonzero-values.
     *
     * @return real part
     */
    public Double[] exportReal()
    {
        Double[] ad = new Double[indexSet.size()];
        int i = 0;
        for (IndexMN index: indexSet) {
            if (real.containsKey(index)) {
                ad[i] = real.get(index);
            } else {
                ad[i] = 0.0;
            }
            i++;
        }
        return ad;
    }
    
    /**
     * Returns the imaginary part (PI) array. PI has length number-of-nonzero-values.
     *
     * @return imaginary part
     */
    public Double[] exportImaginary()
    {
        Double[] ad = new Double[indexSet.size()];
        int i = 0;
        for (IndexMN index: indexSet) {
            if (imaginary.containsKey(index)) {
                ad[i] = imaginary.get(index);
            } else {
                ad[i] = 0.0;
            }
            i++;
        }
        return ad;
    }
    /* (non-Javadoc)
     * @see com.paradigmdesigner.matlab.types.MLArray#contentToString()
     */
    public String contentToString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(name + " = \n");
        
        for ( IndexMN i : indexSet )
        {
            sb.append("\t(");
            sb.append(i.m + "," + i.n);
            sb.append(")");
            sb.append("\t" + getReal(i.m, i.n) );
            if ( isComplex() )
            {
                sb.append("+" + getImaginary(i.m, i.n) );
            }
            sb.append("\n");
            
        }
        
        return sb.toString();
    }
    
    /**
     * Matrix index (m,n)
     * 
     * @author Wojciech Gradkowski &lt;wgradkowski@gmail.com&gt;
     */
    private class IndexMN implements Comparable<IndexMN>
    {
        int m;
        int n;
        
        public IndexMN( int m, int n )
        {
            this.m = m;
            this.n = n;
        }
        
        
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(IndexMN anOtherIndex) {
            return getIndex(m,n) - getIndex(anOtherIndex.m,anOtherIndex.n);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object o)
        {
            if (o instanceof IndexMN )
            {
                return m == ((IndexMN)o).m && n == ((IndexMN)o).n;
            }
            return super.equals(o);
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            StringBuffer sb = new StringBuffer();
            sb.append("{");
            sb.append("m=" + m);
            sb.append(", ");
            sb.append("n=" + n);
            sb.append("}");
            return sb.toString();
        }
    }

    public int getBytesAllocated()
    {
        return Double.SIZE << 3;
    }
    public Double buldFromBytes(byte[] bytes)
    {
        if ( bytes.length != getBytesAllocated() )
        {
            throw new IllegalArgumentException( 
                        "To build from byte array I need array of size: " 
                                + getBytesAllocated() );
        }
        return ByteBuffer.wrap( bytes ).getDouble();
        
    }
    public byte[] getByteArray(Double value)
    {
        int byteAllocated = getBytesAllocated();
        ByteBuffer buff = ByteBuffer.allocate( byteAllocated );
        buff.putDouble( value );
        return buff.array();
    }
    
    public Class<Double> getStorageClazz()
    {
        return Double.class;
    }


}