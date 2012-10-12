package org.corehunter.test;
import org.junit.runner.RunWith; 
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses; 

@RunWith(Suite.class) 
@SuiteClasses({TestSSRDataset.class, TestSSRMeasures.class, TestAccessionCollection.class})

public class CorehunterTestSuite {
	
} 
