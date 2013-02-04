package org.corehunter;

public interface Validatable
{

	/** 
	 * Validates this object prior to starting a search
	 * 
	 * @throws CoreHunterException
	 */
	public void validate() throws CoreHunterException;
}
