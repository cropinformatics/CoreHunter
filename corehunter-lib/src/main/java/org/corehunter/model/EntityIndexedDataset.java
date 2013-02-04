package org.corehunter.model;

public interface EntityIndexedDataset<IndexType, ValueType extends Entity> extends
    IndexedDataset<IndexType, ValueType>
{
	public ValueType getElementByName(String name) throws UnknownEntityException;
}
