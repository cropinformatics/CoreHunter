// Copyright 2012 Guy Davenport, Herman De Beukelaer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.corehunter.model.impl;

import java.io.File;

import org.corehunter.model.Data;
import org.corehunter.model.DataReader;

public abstract class AbstractDataFileReader<DataType extends Data> extends
    AbstractFileUtility<DataType> implements DataReader<DataType>
{

	private String dataName;
	private String dataUniqueIdentifier;
	
	public AbstractDataFileReader(File file)
	{
		this(file.getName(), file.getName(), file);
	}

	public AbstractDataFileReader(String dataName, File file)
	{
		this(dataName, dataName, file);
	}
	
	public AbstractDataFileReader(String dataUniqueIdentifier, String dataName, File file)
	{
		super(file);
		
		setDatasetName(dataName);
		setDataUniqueIdentifier(dataUniqueIdentifier);
	}

	public final String getDataName()
	{
		return dataName;
	}

	public final void setDatasetName(String dataName)
	{
		this.dataName = dataName;
	}
	
	public final String getDataUniqueIdentifier()
	{
		return dataUniqueIdentifier;
	}

	public final void setDataUniqueIdentifier(String dataUniqueIdentifier)
	{
		this.dataUniqueIdentifier = dataUniqueIdentifier;
	}
}
