// Copyright 2008,2011 Chris Thachuk, Herman De Beukelaer, Guy Davenport
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

package org.corehunter.textui;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.corehunter.Constants;
import static org.corehunter.Constants.SECOND;
import org.corehunter.CoreHunterException;
import org.corehunter.model.ssr.AccessionSSRMarkerMatrix;
import org.corehunter.model.ssr.impl.AccessionSSRMarkerMatrixListImplDataFileReader;
import org.corehunter.model.ssr.impl.AccessionSSRMarkerMatrixListImplDataFileWriter;
import org.corehunter.neighbourhood.SubsetNeighbourhood;
import org.corehunter.objectivefunction.DuplicateMeasureException;
import org.corehunter.objectivefunction.MultipleObjectiveFunction;
import org.corehunter.objectivefunction.UnknownMeasureException;
import org.corehunter.objectivefunction.ssr.MeasureFactorySSR;
import org.corehunter.search.Search;
import org.corehunter.search.solution.SubsetSolution;

/**
 * A simple text based driver for Core Hunter.
 * 
 * @author Chris Thachuk <chris.thachuk@gmail.com>
 */
public final class CorehunterTextRunner
{

	private final String[]	    measureNames	                         = { "MR",
	    "MRmin", "CE", "CEmin", "SH", "HE", "NE", "PN", "CV", "EX"	   };

	private final long	        DEFAULT_RUNTIME	                       = 60 * SECOND; // TODO check this
	private final long	        DEFAULT_MINPROG	                       = 0;
	
	private final double	      DEFAULT_SAMPLING_INTENSITY	           = 0.2 ;

	private final double	      DEFAULT_REMC_MIN_TEMPERATURE	         = 50.0;
	private final double	      DEFAULT_REMC_MAX_TEMPERATURE	         = 200.0;
	private final int	          DEFAULT_REMC_REPLICAS	                 = 10;
	private final int	          DEFAULT_REMC_MC_STEPS	                 = 50;

	private final int	          DEFAULT_MIXREP_NR_OF_TABU_REPLICAS	   = 2;
	private final int	          DEFAULT_MIXREP_NR_OF_NON_TABU_REPLICAS	= 3;
	private final int	          DEFAULT_MIXREP_ROUNDS_WITHOUT_TABU	   = 10;
	private final int	          DEFAULT_MIXREP_TOURNAMENT_SIZE	       = 2;
	private final int	          DEFAULT_MIXREP_NR_OF_TABU_STEPS	       = 5;
	private final int	          DEFAULT_MIXREP_BOOST_NR	               = 2;
	private final long	        DEFAULT_MIXREP_BOOST_MIN_PROG	         = 1; // TODO check this
	private final int	          DEFAULT_MIXREP_BOOST_TIME_FACTOR	     = 15;
	private final long	        DEFAULT_MIXREP_MIN_BOOST_TIME	         = (long)0.25 * SECOND ; // TODO check this
	private final double	      DEFAULT_MIXREP_MIN_MC_TEMP	           = 50.0;
	private final double	      DEFAULT_MIXREP_MAX_MC_TEMP	           = 100.0;

	private final int	          DEFAULT_LR_L	                         = 2;
	private final int	          DEFAULT_LR_R	                         = 1;

	private Options	            miscOpts;
	private Options	            measuresOpts;
	private Options	            searchTypeOpts;
	private Options	            commonSearchOpts;
	private Options	            remcSearchOpts;
	private Options	            tabuSearchOpts;
	private Options	            mixrepSearchOpts;
	private Options	            lrSearchOpts;
	private Options	            opts;

	private double	            sampleIntensity;
	private long   	            runtime;
	private long    	          minProg;
	private long                stuckTime;
	private boolean	            stuckTimeSpecified	                   = false;

	private int	                sampleMin;
	private int	                sampleMax;
	private boolean	            sampleSizesSpecified	                 = false;

	private double	            remcMinT;
	private double	            remcMaxT;
	private int	                remcReplicas;
	private int	                remcMcSteps;

	private int	                tabuListSize;
	private boolean	            tabuListSizeSpecified	                 = false;

	private int	                mixrepNrOfTabuReplicas;
	private int	                mixrepNrOfNonTabuReplicas;
	private int	                mixrepRoundsWithoutTabu;
	private int	                mixrepNrOfTabuSteps;
	private int	                mixrepTournamentSize;
	private int	                mixrepBoostNr;
	private long   	            mixrepBoostMinProg;
	private int	                mixrepBoostTimeFactor;
	private long                mixrepMinBoostTime;
	private double	            mixrepMinMCTemp;
	private double	            mixrepMaxMCTemp;

	private int	                lr_l;
	private int	                lr_r;

	private String	            collectionFile;
	private String	            coresubsetFile;
	private Map<String, Double>	measureWeights;

	private boolean	            remcSearch	                           = false;
	private boolean	            exhSearch	                             = false;
	private boolean	            randSearch	                           = false;
	private boolean	            tabuSearch	                           = false;
	private boolean	            localSearch	                           = false;
	private boolean	            steepestDescentSearch	                 = false;
	private boolean	            mstratSearch	                         = false;
	private boolean	            mixedReplicaSearch	                   = false;
	private boolean	            lrSearch	                             = false;
	private boolean	            semiLrSearch	                         = false;
	private boolean	            forwardSelection	                     = false;
	private boolean	            semiForwardSelection	                 = false;
	private boolean	            backwardSelection	                     = false;

	/**
     * 
     */
	public CorehunterTextRunner()
	{
		miscOpts = new Options();
		measuresOpts = new Options();
		searchTypeOpts = new Options();
		commonSearchOpts = new Options();
		remcSearchOpts = new Options();
		tabuSearchOpts = new Options();
		mixrepSearchOpts = new Options();
		lrSearchOpts = new Options();
		opts = new Options();

		measureWeights = new HashMap<String, Double>();
		collectionFile = coresubsetFile = null;

		// set up default search parameters
		sampleIntensity = DEFAULT_SAMPLING_INTENSITY;
		runtime = DEFAULT_RUNTIME;
		minProg = DEFAULT_MINPROG;
		remcReplicas = DEFAULT_REMC_REPLICAS;
		remcMcSteps = DEFAULT_REMC_MC_STEPS;
		remcMinT = DEFAULT_REMC_MIN_TEMPERATURE;
		remcMaxT = DEFAULT_REMC_MAX_TEMPERATURE;

		mixrepNrOfTabuReplicas = DEFAULT_MIXREP_NR_OF_TABU_REPLICAS;
		mixrepNrOfNonTabuReplicas = DEFAULT_MIXREP_NR_OF_NON_TABU_REPLICAS;
		mixrepRoundsWithoutTabu = DEFAULT_MIXREP_ROUNDS_WITHOUT_TABU;
		mixrepTournamentSize = DEFAULT_MIXREP_TOURNAMENT_SIZE;
		mixrepNrOfTabuSteps = DEFAULT_MIXREP_NR_OF_TABU_STEPS;
		mixrepBoostNr = DEFAULT_MIXREP_BOOST_NR;
		mixrepBoostMinProg = DEFAULT_MIXREP_BOOST_MIN_PROG;
		mixrepBoostTimeFactor = DEFAULT_MIXREP_BOOST_TIME_FACTOR;
		mixrepMinBoostTime = DEFAULT_MIXREP_MIN_BOOST_TIME;
		mixrepMinMCTemp = DEFAULT_MIXREP_MIN_MC_TEMP;
		mixrepMaxMCTemp = DEFAULT_MIXREP_MAX_MC_TEMP;

		lr_l = DEFAULT_LR_L;
		lr_r = DEFAULT_LR_R;
	}

	public void run(String[] args)
	{
		try
    {
	    setupOptions();
	    if (!parseOptions(args))
	    {
	    	showUsage();
	    }

	    // try to create dataset
	    System.out.println("Reading dataset...");
	    AccessionSSRMarkerMatrix<Integer> dataset = new AccessionSSRMarkerMatrixListImplDataFileReader(
	        new File(collectionFile)).readData();

	    if (dataset == null)
	    {
	    	System.err.println("\nProblem parsing dataset file.  Aborting.");
	    	System.exit(0);
	    }

	    int collectionSize = dataset.getColumnCount();

	    if (!stuckTimeSpecified)
	    {
	    	stuckTime = runtime;
	    }

	    if (!sampleSizesSpecified)
	    {
	    	sampleMin = sampleMax = (int) (sampleIntensity * collectionSize);
	    }

	    if (sampleMax > collectionSize)
	    {
	    	sampleMax = collectionSize;
	    	System.err
	    	    .println("\nSpecified core size is larger than collection size.  ");
	    	System.err.println("Assuming max size is collection size.");
	    }

	    if (!tabuListSizeSpecified)
	    {
	    	// Default tabu list size = 30% of minimum sample size
	    	tabuListSize = Math.max((int) (0.3 * sampleMin), 1);
	    }

	    // create a pseudo-index and add user specified measure to it, with
	    // respective weights
	    MultipleObjectiveFunction<SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>> objectiveFunction = new MultipleObjectiveFunction<SubsetSolution<Integer>, AccessionSSRMarkerMatrix<Integer>>();
	    
	    for (int i = 0; i < measureNames.length; i++)
	    {
	    	String measure = measureNames[i];
	    	if (measureWeights.containsKey(measure))
	    	{
	    		Double weight = measureWeights.get(measure);
	    		try
	    		{
	    			objectiveFunction.addObjectiveFunction(
	    			    MeasureFactorySSR.createMeasure(measure), weight.doubleValue());
	    		}
	    		catch (DuplicateMeasureException dme)
	    		{
	    			System.err.println("");
	    			System.err.println(dme.getMessage());
	    			showUsage();
	    			System.exit(0);
	    		}
	    		catch (UnknownMeasureException ume)
	    		{
	    			System.err.println("");
	    			System.err.println(ume.getMessage());
	    			showUsage();
	    			System.exit(0);
	    		}
	    	}
	    }

	    // System.out.println("Collection score: " +
	    // pm.calculate(ac.getAccessions()));

	    // search for the core subset
	    Search<SubsetSolution<Integer>> search = null;
	    
	    if (randSearch)
	    {
	    	System.out.println("---\nRandom subset\n---");
	    	search = CoreSubsetSearch.randomSearch(dataset, objectiveFunction, sampleMin,
	    	    sampleMax);
	    }
	    else
	    	if (exhSearch)
	    	{
	    		System.out.println("---\nExhaustive search\n---");
	    		search = CoreSubsetSearch.exhaustiveSearch(dataset, objectiveFunction,
	    		    sampleMin, sampleMax);
	    	}
	    	else
	    		if (lrSearch)
	    		{
	    			// check (l,r) setting
	    			if (Math.abs(lr_l - lr_r) > 1)
	    			{
	    				System.err
	    				    .println("\n!!! Warning: current (l,r) setting may result"
	    				        + "in core size slightly different from desired size");
	    			}
	    			System.out.println("---\nLR Search (deterministic)\n---");
	    			search = CoreSubsetSearch.lrSearch(dataset, objectiveFunction, sampleMin,
	    			    sampleMax, lr_l, lr_r);
	    		}
	    		else
	    			if (semiLrSearch)
	    			{
	    				// check (l,r) setting
	    				if (Math.abs(lr_l - lr_r) > 1)
	    				{
	    					System.err
	    					    .println("\n!!! Warning: current (l,r) setting may result"
	    					        + "in core size slightly different from desired size");
	    				}
	    				System.out.println("---\nSemi LR Search (semi-deterministic)\n---");
	    				search = CoreSubsetSearch.semiLrSearch(dataset, objectiveFunction,
	    				    sampleMin, sampleMax, lr_l, lr_r);
	    			}
	    			else
	    				if (forwardSelection)
	    				{
	    					System.out
	    					    .println("---\nSequential Forward Selection (deterministic)\n---");
	    					search = CoreSubsetSearch.forwardSelection(dataset, objectiveFunction,
	    					    sampleMin, sampleMax);
	    				}
	    				else
	    					if (semiForwardSelection)
	    					{
	    						System.out
	    						    .println("---\nSemi Sequential Forward Selection (semi-deterministic)\n---");
	    						search = CoreSubsetSearch.semiForwardSelection(dataset,
	    						    objectiveFunction, sampleMin, sampleMax);
	    					}
	    					else
	    						if (backwardSelection)
	    						{
	    							System.out
	    							    .println("---\nSequential Backward Selection (deterministic)\n---");
	    							search = CoreSubsetSearch.backwardSelection(dataset,
	    							    objectiveFunction, sampleMin, sampleMax);
	    						}
	    						else
	    						{
	    							SubsetNeighbourhood<Integer, SubsetSolution<Integer>> neighbourhood;
	    							if (remcSearch)
	    							{
	    								System.out
	    								    .println("---\nREMC (Replica Exchange Monte Carlo)\n---");
	    								neighbourhood = CoreSubsetSearch.randomSingleNeighbourhood(sampleMin, sampleMax);
	    								
	    								search = CoreSubsetSearch.remcSearch(dataset, neighbourhood,
	    								    objectiveFunction, sampleMin, sampleMax, runtime,
	    								    minProg, stuckTime, remcReplicas, remcMinT, remcMaxT,
	    								    remcMcSteps);
	    							}
	    							else
	    								if (mixedReplicaSearch)
	    								{
	    									System.out.println("---\nMixed Replica Search\n---");
	    									search = CoreSubsetSearch.mixedReplicaSearch(dataset,
	    									    objectiveFunction, sampleMin, sampleMax, runtime,
	    									    minProg, stuckTime, mixrepNrOfTabuReplicas,
	    									    mixrepNrOfNonTabuReplicas, mixrepRoundsWithoutTabu,
	    									    mixrepNrOfTabuSteps, mixrepTournamentSize,
	    									    tabuListSize, mixrepBoostNr, mixrepBoostMinProg,
	    									    mixrepBoostTimeFactor, mixrepMinBoostTime,
	    									    mixrepMinMCTemp, mixrepMaxMCTemp);
	    								}
	    								else
	    									if (localSearch)
	    									{
	    										System.out.println("---\nLocal Search\n---");
	    										neighbourhood = CoreSubsetSearch.randomSingleNeighbourhood(sampleMin, sampleMax);
	    										search = CoreSubsetSearch.localSearch(dataset, neighbourhood,
	    										    objectiveFunction, runtime,
	    										    minProg, stuckTime);
	    									}
	    									else
	    										if (steepestDescentSearch)
	    										{
	    											System.out
	    											    .println("---\nSteepest Descent Search\n---");
	    											neighbourhood = CoreSubsetSearch.randomSingleNeighbourhood(sampleMin, sampleMax);
	    											search = CoreSubsetSearch.steepestDescentSearch(dataset, neighbourhood,
	    											    objectiveFunction, runtime,
	    											    minProg);
	    										}
	    										else
	    											if (mstratSearch)
	    											{
	    												System.out
	    												    .println("---\nMSTRAT Search (Heuristic Steepest Descent)\n---");
	    												neighbourhood = CoreSubsetSearch.heuristicSingleNeighbourhood(sampleMin, sampleMax);
	    												// MSTRAT = Steepest Descent with heuristic
	    												// neighbourhood
	    												search = CoreSubsetSearch.steepestDescentSearch(dataset,
	    												    neighbourhood, objectiveFunction,
	    												    runtime, minProg);
	    											}
	    											else
	    												if (tabuSearch)
	    												{
	    													System.out.println("---\nTabu Search\n---");
	    													// Tabu Search uses heuristic neighbourhood as in
	    													// MSTRAT
	    													neighbourhood = CoreSubsetSearch.heuristicSingleNeighbourhood(sampleMin, sampleMax);
	    													search = CoreSubsetSearch.tabuSearch(dataset, neighbourhood,
	    													    objectiveFunction,
	    													    runtime, minProg, stuckTime, tabuListSize);
	    												}
	    												else
	    												{
	    													System.err
	    													    .println("Error: no known search type selected (this should not happen!)");
	    													System.exit(1);
	    												}
	    						}

	    Map<String, Double> scores = objectiveFunction.componentScores(search.getBestSolution());

	    System.out.println("--------");
	    for (String comp : scores.keySet())
	    {
	    	System.out.println(comp + ": " + scores.get(comp));
	    }

	    AccessionSSRMarkerMatrixListImplDataFileWriter writer = new AccessionSSRMarkerMatrixListImplDataFileWriter("CoreSubset",
	        new File(coresubsetFile)) ;
	    
	    writer.setIndices(dataset.getIndices()) ;
	    
	    writer.writeData(dataset);
    }
    catch (CoreHunterException e)
    {
	    e.printStackTrace(System.out);
    }
	}

	@SuppressWarnings("static-access")
	private void setupOptions()
	{
		// set up the misc option group
		miscOpts.addOption(new Option("help", "print this message"));
		miscOpts.addOption(new Option("version",
		    "print the version information and exit"));
		miscOpts.addOption(new Option("quiet", "be extra quiet"));
		miscOpts.addOption(new Option("verbose", "be extra verbose"));

		// set up the search type option group
		searchTypeOpts.addOption(new Option("remc",
		    "REMC search (Replica Exchange Monte Carlo)"));
		searchTypeOpts.addOption(new Option("exh", "exhaustive search"));
		searchTypeOpts.addOption(new Option("rand", "random core set"));
		searchTypeOpts.addOption(new Option("tabu", "tabu search"));
		searchTypeOpts.addOption(new Option("local", "standard local search"));
		searchTypeOpts.addOption(new Option("steepest", "steepest descent search"));
		searchTypeOpts.addOption(new Option("mstrat",
		    "heuristic steepest descent (cfr. MSTRAT)"));
		searchTypeOpts.addOption(new Option("mixrep",
		    "parallel mixed replica search"));
		searchTypeOpts.addOption(new Option("lr", "lr search (deterministic)"));
		searchTypeOpts.addOption(new Option("lrsemi",
		    "lr search with random first pair (semi-deterministic)"));
		searchTypeOpts.addOption(new Option("sfs",
		    "sequential forward selection (deterministic)"));
		searchTypeOpts
		    .addOption(new Option("sfssemi",
		        "sequential forward selection with random first pair (semi-deterministic)"));
		searchTypeOpts.addOption(new Option("sbs",
		    "sequential backward selection (deterministic)"));

		// set up the measures option group
		measuresOpts
		    .addOption(OptionBuilder
		        .withArgName("weight")
		        .hasArg()
		        .withDescription(
		            "use mean Modified Rogers distance and specify weight")
		        .create("MR"));
		measuresOpts.addOption(OptionBuilder
		    .withArgName("weight")
		    .hasArg()
		    .withDescription(
		        "use minimum Modified Rogers distance and specify weight")
		    .create("MRmin"));
		measuresOpts.addOption(OptionBuilder
		    .withArgName("weight")
		    .hasArg()
		    .withDescription(
		        "use mean Cavalli-Sforza and Edwards distance and specify weight")
		    .create("CE"));
		measuresOpts
		    .addOption(OptionBuilder
		        .withArgName("weight")
		        .hasArg()
		        .withDescription(
		            "use minimum Cavalli-Sforza and Edwards distance and specify weight")
		        .create("CEmin"));
		measuresOpts.addOption(OptionBuilder.withArgName("weight").hasArg()
		    .withDescription("use Shannons Diversity Index and specify weight")
		    .create("SH"));
		measuresOpts.addOption(OptionBuilder
		    .withArgName("weight")
		    .hasArg()
		    .withDescription(
		        "use Heterozygous Loci Diversity Index and specify weight")
		    .create("HE"));
		measuresOpts.addOption(OptionBuilder
		    .withArgName("weight")
		    .hasArg()
		    .withDescription(
		        "use Number of Effective Alleles Index and specify weight")
		    .create("NE"));
		measuresOpts.addOption(OptionBuilder
		    .withArgName("weight")
		    .hasArg()
		    .withDescription(
		        "use Proportion of Non-informative alleles and specify weight")
		    .create("PN"));
		measuresOpts.addOption(OptionBuilder.withArgName("weight").hasArg()
		    .withDescription("use Coverage measure and specify weight")
		    .create("CV"));
		measuresOpts
		    .addOption(OptionBuilder
		        .withArgName("weight")
		        .hasArg()
		        .withDescription(
		            "use External Distance measure and specify weight (needs external distance specification in dataset file!)")
		        .create("EX"));

		// set up the common advanced search option group
		commonSearchOpts.addOption(OptionBuilder
		    .withArgName("t")
		    .hasArg()
		    .withDescription(
		        "run search for t seconds, defaults to " + DEFAULT_RUNTIME)
		    .create("runtime"));
		commonSearchOpts.addOption(OptionBuilder
		    .withArgName("p")
		    .hasArg()
		    .withDescription(
		        "stop search if progression is less than p, defaults to "
		            + DEFAULT_MINPROG).create("min_prog"));
		commonSearchOpts
		    .addOption(OptionBuilder
		        .withArgName("t")
		        .hasArg()
		        .withDescription(
		            "stop if no improvement found during last t seconds, by default no stuck_time is applied")
		        .create("stuck_time"));
		commonSearchOpts.addOption(OptionBuilder
		    .withArgName("s")
		    .hasArg()
		    .withDescription(
		        "select a fraction of size s of accessions in collection for coresubset, "
		            + "defaults to " + DEFAULT_SAMPLING_INTENSITY)
		    .create("sample_intensity"));
		commonSearchOpts.addOption(OptionBuilder
		    .withArgName("min max")
		    .hasArgs(2)
		    .withDescription(
		        "specify minimum and maximum size of core (number of accessions)"
		            + "\nNote: this overrides sample_intensity")
		    .create("sample_size"));

		// set up the REMC advanced search option group
		remcSearchOpts.addOption(OptionBuilder
		    .withArgName("r")
		    .hasArg()
		    .withDescription(
		        "use r replicas for search, defaults to " + DEFAULT_REMC_REPLICAS)
		    .create("replicas"));
		remcSearchOpts.addOption(OptionBuilder
		    .withArgName("s")
		    .hasArg()
		    .withDescription(
		        "use s local steps for each monte carlo search, defaults to "
		            + DEFAULT_REMC_MC_STEPS).create("mc_steps"));
		remcSearchOpts.addOption(OptionBuilder
		    .withArgName("t")
		    .hasArg()
		    .withDescription(
		        "minimum temperature of any replica, " + "defaults to "
		            + DEFAULT_REMC_MIN_TEMPERATURE).create("min_t"));
		remcSearchOpts.addOption(OptionBuilder
		    .withArgName("t")
		    .hasArg()
		    .withDescription(
		        "maximum temperature of any replica, " + "defaults to "
		            + DEFAULT_REMC_MAX_TEMPERATURE).create("max_t"));

		// set up the Tabu advanced search option group
		tabuSearchOpts
		    .addOption(OptionBuilder
		        .withArgName("s")
		        .hasArg()
		        .withDescription(
		            "use tabu list of size s, defaults to 30% of the minimum core size")
		        .create("list_size"));

		// set up the Mixed Replica advanced search option group
		mixrepSearchOpts.addOption(OptionBuilder
		    .withArgName("tr")
		    .hasArg()
		    .withDescription(
		        "maintain tr tabu replicas during search, defaults to "
		            + DEFAULT_MIXREP_NR_OF_TABU_REPLICAS).create("tabu_replicas"));

		mixrepSearchOpts
		    .addOption(OptionBuilder
		        .withArgName("ntr")
		        .hasArg()
		        .withDescription(
		            "maintain ntr non-tabu replicas (Local Search, SimAn) during search, defaults to "
		                + DEFAULT_MIXREP_NR_OF_NON_TABU_REPLICAS)
		        .create("non_tabu_replicas"));

		mixrepSearchOpts
		    .addOption(OptionBuilder
		        .withArgName("r")
		        .hasArg()
		        .withDescription(
		            "wait for startup of tabu replicas until after the first r search rounds, defaults to "
		                + DEFAULT_MIXREP_ROUNDS_WITHOUT_TABU)
		        .create("rounds_without_tabu"));

		mixrepSearchOpts.addOption(OptionBuilder
		    .withArgName("t")
		    .hasArg()
		    .withDescription(
		        "select parent replicas in tournaments of size t, defaults to "
		            + DEFAULT_MIXREP_TOURNAMENT_SIZE).create("tournament_size"));

		mixrepSearchOpts
		    .addOption(OptionBuilder
		        .withArgName("s")
		        .hasArg()
		        .withDescription(
		            "each tabu replica performs s steps in each search round, defaults to "
		                + DEFAULT_MIXREP_NR_OF_TABU_STEPS
		                + " (nr of steps for other replicas is automatically determined)")
		        .create("tabu_steps"));

		mixrepSearchOpts
		    .addOption(OptionBuilder
		        .withArgName("s")
		        .hasArg()
		        .withDescription(
		            "use tabu list of size s, defaults to 30% of the minimum core size")
		        .create("list_size"));

		mixrepSearchOpts.addOption(OptionBuilder
		    .withArgName("b")
		    .hasArg()
		    .withDescription(
		        "boost search with b new local search replicas at each boost, defaults to "
		            + DEFAULT_MIXREP_BOOST_NR).create("boost_nr"));

		mixrepSearchOpts.addOption(OptionBuilder
		    .withArgName("p")
		    .hasArg()
		    .withDescription(
		        "boost search as soon as global improvement drops below p, defaults to "
		            + DEFAULT_MIXREP_BOOST_MIN_PROG).create("boost_min_prog"));

		mixrepSearchOpts
		    .addOption(OptionBuilder
		        .withArgName("f")
		        .hasArg()
		        .withDescription(
		            "set how long to wait before boosting in case of no improvement, boost time is automatically "
		                + "determined based on the average time of one search round and this factor, defaults to "
		                + DEFAULT_MIXREP_BOOST_TIME_FACTOR)
		        .create("boost_time_factor"));

		mixrepSearchOpts
		    .addOption(OptionBuilder
		        .withArgName("m")
		        .hasArg()
		        .withDescription(
		            "minimum time before boosting in case of no improvement (overrides boost_time_factor), defaults to "
		                + DEFAULT_MIXREP_MIN_BOOST_TIME).create("min_boost_time"));

		mixrepSearchOpts.addOption(OptionBuilder
		    .withArgName("t")
		    .hasArg()
		    .withDescription(
		        "minimum temperature of Simple Monte Carlo replicas, defaults to "
		            + DEFAULT_MIXREP_MIN_MC_TEMP).create("min_t"));

		mixrepSearchOpts.addOption(OptionBuilder
		    .withArgName("t")
		    .hasArg()
		    .withDescription(
		        "maximum temperature of Simple Monte Carlo replicas, defaults to "
		            + DEFAULT_MIXREP_MAX_MC_TEMP).create("max_t"));

		// set up the LR Search advanced search option group
		lrSearchOpts.addOption(OptionBuilder
		    .withArgName("l")
		    .hasArg()
		    .withDescription(
		        "add l accessions in each round, defaults to " + DEFAULT_LR_L)
		    .create("l"));

		lrSearchOpts.addOption(OptionBuilder
		    .withArgName("r")
		    .hasArg()
		    .withDescription(
		        "remove r accessions in each round, defaults to " + DEFAULT_LR_R)
		    .create("r"));

		// add the option groups into one option collection
		@SuppressWarnings("rawtypes")
    Iterator i = miscOpts.getOptions().iterator();
		while (i.hasNext())
		{
			opts.addOption((Option) i.next());
		}

		i = measuresOpts.getOptions().iterator();
		while (i.hasNext())
		{
			opts.addOption((Option) i.next());
		}

		i = searchTypeOpts.getOptions().iterator();
		while (i.hasNext())
		{
			opts.addOption((Option) i.next());
		}

		i = commonSearchOpts.getOptions().iterator();
		while (i.hasNext())
		{
			opts.addOption((Option) i.next());
		}

		i = remcSearchOpts.getOptions().iterator();
		while (i.hasNext())
		{
			opts.addOption((Option) i.next());
		}

		i = tabuSearchOpts.getOptions().iterator();
		while (i.hasNext())
		{
			opts.addOption((Option) i.next());
		}

		i = mixrepSearchOpts.getOptions().iterator();
		while (i.hasNext())
		{
			opts.addOption((Option) i.next());
		}

		i = lrSearchOpts.getOptions().iterator();
		while (i.hasNext())
		{
			opts.addOption((Option) i.next());
		}
	}

	private boolean parseOptions(String[] args)
	{
		CommandLineParser parser = new GnuParser();

		try
		{
			CommandLine cl = parser.parse(opts, args);

			// check for -help
			if (cl.hasOption("help"))
			{
				showUsage();
				System.exit(0);
			}

			// check for -version
			if (cl.hasOption("version"))
			{
				showVersion();
				System.exit(0);
			}

			// make sure two required args are present
			if (cl.getArgs().length != 2)
			{
				System.err.println("\n2 required arguments expected");
				return false;
			}

			// grab the filenames
			collectionFile = cl.getArgs()[0];
			coresubsetFile = cl.getArgs()[1];

			// parse the weights for different measures
			boolean hasMeasures = false;
			for (int i = 0; i < measureNames.length; i++)
			{
				String m = measureNames[i];
				if (cl.hasOption(m))
				{
					try
					{
						double weight = Double.parseDouble(cl.getOptionValue(m));
						if (weight <= 0.0)
						{
							throw new NumberFormatException();
						}
						measureWeights.put(m, weight);
					}
					catch (NumberFormatException nfe)
					{
						System.err.println("\nweight for " + measureNames[i]
						    + " must be a positive numeric value");
						return false;
					}
					hasMeasures = true;
				}
			}

			// ensure at least one measure was set
			if (!hasMeasures)
			{
				System.err.println("\nAt least one measure must be specified");
				return false;
			}

			// check if specific core size ranges were set
			if (cl.hasOption("sample_size"))
			{
				try
				{
					sampleMin = Integer.parseInt(cl.getOptionValues("sample_size")[0]);
					sampleMax = Integer.parseInt(cl.getOptionValues("sample_size")[1]);
					if (sampleMin > sampleMax || sampleMin < 2)
						throw new NumberFormatException();
					sampleSizesSpecified = true;
				}
				catch (NumberFormatException nfe)
				{
					System.err
					    .println("\nsample_size must specify two integer values with max >= min and min >= 2");
					return false;
				}
			}

			// make sure sampling intensity is between 0 and 1 inclusive
			if (cl.hasOption("sample_intensity"))
			{
				try
				{
					sampleIntensity = Double.parseDouble(cl
					    .getOptionValue("sample_intensity"));
					if (sampleIntensity < 0.0 || sampleIntensity > 1.0)
					{
						throw new NumberFormatException();
					}
				}
				catch (NumberFormatException nfe)
				{
					System.err
					    .println("\nsample_intensity must a numeric value in the range [0..1]");
					return false;
				}
			}

			// check for runtime
			if (cl.hasOption("runtime"))
			{
				try
				{
					runtime = (long)(Double.parseDouble(cl.getOptionValue("runtime")) * Constants.MILLISECOND) ;
					if (runtime <= 0.0)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err.println("\nruntime must be a positive numeric value");
					return false;
				}
			}

			// check for min_prog
			if (cl.hasOption("min_prog"))
			{
				try
				{
					minProg = (long)(Double.parseDouble(cl.getOptionValue("min_prog")) * Constants.MILLISECOND) ;
					if (minProg < 0.0)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err.println("\nmin_prog must be a positive numeric value");
					return false;
				}
			}

			// check for stuck_time
			if (cl.hasOption("stuck_time"))
			{
				try
				{
					stuckTime = (long)(Double.parseDouble(cl.getOptionValue("stuck_time")) * Constants.MILLISECOND) ;
					if (stuckTime <= 0.0)
						throw new NumberFormatException();
					stuckTimeSpecified = true;
				}
				catch (NumberFormatException nfe)
				{
					System.err.println("\nstuck_time must be a positve numeric value");
					return false;
				}
			}

			// check selected search type

			int j = 0;

			// check for -remc
			remcSearch = cl.hasOption("remc");
			if (remcSearch)
				j++;

			// check for -exh
			exhSearch = cl.hasOption("exh");
			if (exhSearch)
				j++;

			// check for -rand
			randSearch = cl.hasOption("rand");
			if (randSearch)
				j++;

			// check for -tabu
			tabuSearch = cl.hasOption("tabu");
			if (tabuSearch)
				j++;

			// check for -local
			localSearch = cl.hasOption("local");
			if (localSearch)
				j++;

			// check for -steepest
			steepestDescentSearch = cl.hasOption("steepest");
			if (steepestDescentSearch)
				j++;

			// check for -mstrat
			mstratSearch = cl.hasOption("mstrat");
			if (mstratSearch)
				j++;

			// check for -mixrep
			mixedReplicaSearch = cl.hasOption("mixrep");
			if (mixedReplicaSearch)
				j++;

			// check for -lr
			lrSearch = cl.hasOption("lr");
			if (lrSearch)
				j++;

			// check for -semilr
			semiLrSearch = cl.hasOption("lrsemi");
			if (semiLrSearch)
				j++;

			// check for -sfs
			forwardSelection = cl.hasOption("sfs");
			if (forwardSelection)
				j++;

			// check for -sfssemi
			semiForwardSelection = cl.hasOption("sfssemi");
			if (semiForwardSelection)
				j++;

			// check for -sbs
			backwardSelection = cl.hasOption("sbs");
			if (backwardSelection)
				j++;

			// check if a search type is selected
			if (j == 0)
			{
				// select default search type = MixRep
				mixedReplicaSearch = true;
			}
			else
				if (j > 1)
				{
					// multiple search types selected
					System.err
					    .println("\nMultiple search types selected. Please select only one.");
					return false;
				}

			// check REMC advanced options

			// check for replicas
			if (cl.hasOption("replicas"))
			{
				try
				{
					remcReplicas = Integer.parseInt(cl.getOptionValue("replicas"));
					if (remcReplicas < 1 || remcReplicas > 100)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err
					    .println("\nreplicas must be a positive integer in the range [1..100]");
					return false;
				}
			}

			// check for mc_steps
			if (cl.hasOption("mc_steps"))
			{
				try
				{
					remcMcSteps = Integer.parseInt(cl.getOptionValue("mc_steps"));
					if (remcMcSteps < 1 || remcMcSteps > 1000000)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err
					    .println("\nmc_steps must be a positive integer in the range [1..1000000]");
					return false;
				}
			}

			// check for min_t
			if (cl.hasOption("min_t"))
			{
				try
				{
					remcMinT = Double.parseDouble(cl.getOptionValue("min_t"));
					mixrepMinMCTemp = remcMinT; // in case of MixRep
					if (remcMinT <= 0.0)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err.println("\nmin_t must be a positve numeric value");
					return false;
				}
			}

			// check for max_t
			if (cl.hasOption("max_t"))
			{
				try
				{
					remcMaxT = Double.parseDouble(cl.getOptionValue("max_t"));
					mixrepMaxMCTemp = remcMaxT; // in case of MixRep
					if (remcMaxT <= remcMinT)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err
					    .println("\nmax_t must be a postive numeric value, larger than min_t");
					return false;
				}
			}

			// check Tabu advanced options

			// check for list_size
			if (cl.hasOption("list_size"))
			{
				try
				{
					tabuListSize = Integer.parseInt(cl.getOptionValue("list_size"));
					if (tabuListSize <= 0)
						throw new NumberFormatException();
					tabuListSizeSpecified = true;
				}
				catch (NumberFormatException nfe)
				{
					System.err.println("\nlist_size must be a postive integer");
					return false;
				}
			}

			// check Mixed Replica Search advanced options

			// check for tabu_replicas
			if (cl.hasOption("tabu_replicas"))
			{
				try
				{
					mixrepNrOfTabuReplicas = Integer.parseInt(cl
					    .getOptionValue("tabu_replicas"));
					if (mixrepNrOfTabuReplicas < 1 || mixrepNrOfTabuReplicas > 100)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err
					    .println("\ntabu_replicas must be a positive integer in the range [1..100]");
					return false;
				}
			}

			// check for non_tabu_replicas
			if (cl.hasOption("non_tabu_replicas"))
			{
				try
				{
					mixrepNrOfNonTabuReplicas = Integer.parseInt(cl
					    .getOptionValue("non_tabu_replicas"));
					if (mixrepNrOfNonTabuReplicas < 1 || mixrepNrOfNonTabuReplicas > 100)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err
					    .println("\nnon_tabu_replicas must be a positive integer in the range [1..100]");
					return false;
				}
			}

			// check for rounds_without_tabu
			if (cl.hasOption("rounds_without_tabu"))
			{
				try
				{
					mixrepRoundsWithoutTabu = Integer.parseInt(cl
					    .getOptionValue("rounds_without_tabu"));
					if (mixrepRoundsWithoutTabu < 0 || mixrepRoundsWithoutTabu > 1000000)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err
					    .println("\nrounds_without_tabu must be an integer in the range [0..1000000]");
					return false;
				}
			}

			// check for tabu_steps
			if (cl.hasOption("tabu_steps"))
			{
				try
				{
					mixrepNrOfTabuSteps = Integer.parseInt(cl
					    .getOptionValue("tabu_steps"));
					if (mixrepNrOfTabuSteps < 1 || mixrepNrOfTabuSteps > 1000000)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err
					    .println("\ntabu_steps must be a positive integer in the range [1..1000000]");
					return false;
				}
			}

			// check for tournament_size
			if (cl.hasOption("tournament_size"))
			{
				try
				{
					mixrepTournamentSize = Integer.parseInt(cl
					    .getOptionValue("tournament_size"));
					if (mixrepTournamentSize < 1)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err
					    .println("\ntournament_size must be a postive integer >= 1");
					return false;
				}
			}

			// check for boost_nr
			if (cl.hasOption("boost_nr"))
			{
				try
				{
					mixrepBoostNr = Integer.parseInt(cl.getOptionValue("boost_nr"));
					if (mixrepBoostNr < 1 || mixrepBoostNr > 100)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err
					    .println("\nboost_nr must be a positive integer in the range [1..100]");
					return false;
				}
			}

			// check for boost_min_prog
			if (cl.hasOption("boost_min_prog"))
			{
				try
				{
					mixrepBoostMinProg = (long)(Double.parseDouble(cl
					    .getOptionValue("boost_min_prog")) * Constants.MILLISECOND);
					if (mixrepBoostMinProg <= 0.0)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err
					    .println("\nboost_min_prog must be a real number larger than 0.0");
					return false;
				}
			}

			// check for boost_time_factor
			if (cl.hasOption("boost_time_factor"))
			{
				try
				{
					mixrepBoostTimeFactor = Integer.parseInt(cl
					    .getOptionValue("boost_time_factor"));
					if (mixrepBoostTimeFactor < 1)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err.println("\nboost_time_factor must be a positive integer");
					return false;
				}
			}

			// check for min_boost_time
			if (cl.hasOption("min_boost_time"))
			{
				try
				{
					mixrepMinBoostTime = (long)(Double.parseDouble(cl
					    .getOptionValue("min_boost_time")) * Constants.MILLISECOND);
					if (mixrepMinBoostTime < 0.0)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err
					    .println("\nmin_boost_time must be a real number larger than or equal to 0.0");
					return false;
				}
			}

			// check LR Search advanced options

			// check for l
			if (cl.hasOption("l"))
			{
				try
				{
					lr_l = Integer.parseInt(cl.getOptionValue("l"));
					if (lr_l < 1)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err.println("\nl must be a postive integer >= 1");
					return false;
				}
			}

			// check for r
			if (cl.hasOption("r"))
			{
				try
				{
					lr_r = Integer.parseInt(cl.getOptionValue("r"));
					if (lr_r < 1)
						throw new NumberFormatException();
				}
				catch (NumberFormatException nfe)
				{
					System.err.println("\nr must be a postive integer >= 1");
					return false;
				}
			}

			// ensure that l and r are not equal
			if (lr_l == lr_r)
			{
				System.err.println("l and r cannot be equal");
				return false;
			}

		}
		catch (ParseException e)
		{
			System.err.println("");
			System.err.println(e.getMessage());
			return false;
		}

		return true;
	}

	private void showUsage()
	{
		System.out.println("");
		System.out
		    .println("usage: corehunter [options] [measures] <collection_file> <coresubset_file>");
		System.out.println("");
		System.out
		    .println("\texample: The following command will store a coresubset in the file 'coresubset.dat'"
		        + "\n\tby selecting 20% (default value) of the accessions from the dataset in the file "
		        + "\n\t'collection.dat'.  The accesions will be chosen by attemping to optimize a "
		        + "\n\tpseudo-objective function where 70% of the weight is based on Modified Rogers "
		        + "\n\tdistance and 30% of the weight is based on Shannons diversity index. Optimization "
		        + "\n\tis carried out using the Replica Exchange Monte Carlo algorithm.");
		System.out.println("");
		System.out
		    .println("\tcorehunter -remc -MR 0.7 -SH 0.3 collection.dat coresubset.dat");
		System.out.println("");

		HelpFormatter f = new HelpFormatter();
		f.setSyntaxPrefix("");

		f.printHelp("measures (at least one must be specified):", measuresOpts);
		System.out.println("");
		f.printHelp("search type options:", searchTypeOpts);
		System.out.println("");
		f.printHelp("common advanced search options:", commonSearchOpts);
		System.out.println("");
		f.printHelp("REMC - advanced search options:", remcSearchOpts);
		System.out.println("");
		f.printHelp("Tabu - advanced search options:", tabuSearchOpts);
		System.out.println("");
		f.printHelp("Mixed Replica Search - advanced search options:",
		    mixrepSearchOpts);
		System.out.println("");
		f.printHelp("(semi) LR Search - advanced search options:", lrSearchOpts);
		System.out.println("");
		f.printHelp("misc options:", miscOpts);
		System.out.println("");
		System.exit(0);
	}

	private void showVersion()
	{
		System.out.println("");
		System.out.println("Corehunter version: 2.0");
		System.out.println("");
	}

	public static void main(String[] args)
	{
		CorehunterTextRunner corehunter = new CorehunterTextRunner();
		corehunter.run(args);

		System.exit(0);
	}
}
