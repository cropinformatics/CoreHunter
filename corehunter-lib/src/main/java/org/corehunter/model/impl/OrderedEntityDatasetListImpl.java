package org.corehunter.model.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.corehunter.CoreHunterException;
import org.corehunter.model.Entity;
import org.corehunter.model.OrderedEntityDataset;
import org.corehunter.model.UnknownEntityException;
import org.corehunter.utils.EntityUtils;

public class OrderedEntityDatasetListImpl<ValueType extends Entity> extends EntityImpl implements OrderedEntityDataset<Integer, ValueType>
{
	private List<ValueType> elements ;
	private List<Integer> indices ;
	
	public OrderedEntityDatasetListImpl(String name, List<ValueType> elements)
  {
	  this(name, name, elements);
  }
	
	public OrderedEntityDatasetListImpl(String name, String uniqueIdentifier, List<ValueType> elements)
  {
	  super(name, name);
	  
	  this.elements = new ArrayList<ValueType>(elements) ;
	  this.indices = new ArrayList<Integer>(this.elements.size()) ;
	  
	  for(int i = 0 ; i < elements.size() ; ++i)
	  	this.indices.add(i) ;
  }

	@Override
  public ValueType getElement(Integer index)
  {
	  return elements.get(index);
  }

	@Override
  public synchronized Set<ValueType> getElements(List<Integer> indices)
  {
		Iterator<Integer> iterator = indices.iterator() ;
		
		Set<ValueType> set = new LinkedHashSet<ValueType>(indices.size()) ;
		
		while (iterator.hasNext())
		{
			set.add(elements.get(iterator.next())) ;
		}
		
	  return set;
  }
	
	@Override
	public Integer getIndex(ValueType value) 
	{
		return elements.indexOf(value) ;
	}

	@Override
  public List<Integer> getIndices()
  {
	  return indices;
  }

	@Override
  public int getSize()
  {
	  return elements.size() ;
  }

	@Override
  public void validate() throws CoreHunterException
  {

  }

	@Override
  public Set<ValueType> getElements()
  {
		Iterator<Integer> iterator = indices.iterator() ;
		
		HashSet<ValueType> set = new LinkedHashSet<ValueType>(indices.size()) ;
		
		while (iterator.hasNext())
		{
			set.add(elements.get(iterator.next())) ;
		}
		
		return set ;
  }

	@Override
  public List<ValueType> getElementsAsList()
  {
		return elements ;
  }

	@Override
  public List<ValueType> getElementsAsList(List<Integer> indices)
  {
		Iterator<Integer> iterator = indices.iterator() ;
		
		ArrayList<ValueType> set = new ArrayList<ValueType>(indices.size()) ;
		
		while (iterator.hasNext())
		{
			set.add(elements.get(iterator.next())) ;
		}
		
	  return set;
  }
	
	@SuppressWarnings("unchecked")
  @Override
  public ValueType getElementByName(String name) throws UnknownEntityException
  {
		Entity entity = EntityUtils.findByName(name, getElementsAsList());
		
		if (entity != null)
			return (ValueType) entity ;
		else
			throw new UnknownEntityException("No entity with the name :" + name) ;
  }
	
	@SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object object)
  {
		if (object instanceof OrderedEntityDataset)
			return orderedDatasetEquals((OrderedEntityDataset<Integer, ValueType>)object);
		else
			return super.equals(object);
  }

	@Override
  public String toString()
  {
	  // TODO Auto-generated method stub
	  return super.toString();
  }

	protected boolean orderedDatasetEquals(OrderedEntityDataset<Integer, ValueType> orderedDataset)
  {
		if (orderedDataset instanceof OrderedEntityDatasetListImpl)
		{
			return getElementsAsList().equals(orderedDataset.getElementsAsList());
		}
		else
		{
			Iterator<Integer> iterator = orderedDataset.getIndices().iterator() ;
			
			boolean equals = true ;
			
			while (equals  && iterator.hasNext())
				equals = elementsEquals(orderedDataset, iterator.next()) ;
			
		  return equals;
		}
  }

	protected boolean elementsEquals(
      OrderedEntityDataset<Integer, ValueType> orderedDataset, Integer index)
  {
	  try
    {
	    return getElement(index).equals(orderedDataset.getElement(index));
    }
    catch (UnknownEntityException e)
    {
	    return false ;
    }
  }	
}
