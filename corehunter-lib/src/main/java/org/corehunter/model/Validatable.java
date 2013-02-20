package org.corehunter.model;

import org.corehunter.CoreHunterException;

public interface Validatable
{

	/** 
	 * Validates this object prior to starting a search
	 * 
	 * @throws CoreHunterException
	 */
	public void validate() throws CoreHunterException;
}
