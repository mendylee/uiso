package com.xrk.uiac.common.utils;

public class SortEntity<T>
{
	private long sortKey;
	private T value;
	public SortEntity(long sortKey, T value)
	{
		setSortKey(sortKey);
		setValue(value);
	}
	public long getSortKey()
    {
	    return sortKey;
    }
	public void setSortKey(long sortKey)
    {
	    this.sortKey = sortKey;
    }
	public T getValue()
    {
	    return value;
    }
	public void setValue(T value)
    {
	    this.value = value;
    }
}
