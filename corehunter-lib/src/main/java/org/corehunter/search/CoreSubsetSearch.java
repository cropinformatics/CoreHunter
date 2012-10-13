//  Copyright 2008,2011 Chris Thachuk, Herman De Beukelaer
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package org.corehunter.search;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import org.corehunter.Accession;
import org.corehunter.AccessionCollection;
import org.corehunter.measures.PseudoMeasure;

/**
 * <<Class summary>>
 *
 * @author Chris Thachuk &lt;&gt;
 * @version $Rev$
 */
public final class CoreSubsetSearch {

    // Progress Writer settings
    private final static boolean WRITE_PROGRESS_FILE = true;
    private final static String PROGRESS_FILE_PATH = "progress";
    private final static long PROGRESS_WRITE_PERIOD = 100;


    final static double K_b2 = 1.360572e-9;

    // this class should not be instantiable from outside class
    private CoreSubsetSearch() {

    }

    public static AccessionCollection remcSearch(AccessionCollection ac, Neighborhood nh,
						 PseudoMeasure pm, int sampleMin, int sampleMax,
						 double runtime, double minProg, double stuckTime,
                                                 int numReplicas, double minT, double maxT, int mcSteps) {

	SimpleMonteCarloReplica replicas[] = new SimpleMonteCarloReplica[numReplicas];
	Random r = new Random();

	for(int i=0; i<numReplicas; i++) {
	    double T = minT + i*(maxT - minT)/(numReplicas - 1);
	    replicas[i] = new SimpleMonteCarloReplica(ac, pm, nh.clone(), mcSteps, -1, sampleMin, sampleMax, T);
            replicas[i].init();
	}

	double bestScore = -Double.MAX_VALUE;
	List<Accession> bestCore = new ArrayList<Accession>();

        List<Future> futures = new ArrayList<Future>(numReplicas);
        ExecutorService pool = Executors.newCachedThreadPool();

	long sTime = System.currentTimeMillis();
	long eTime = sTime + (long) (runtime * 1000);

	int swapBase = 0;
        boolean cont = true, impr;
        double prevBestScore = bestScore, prog;
        int prevBestSize = ac.size();
        long lastImprTime = 0;

        ProgressWriter pw;
        if(WRITE_PROGRESS_FILE){
            pw = new ProgressWriter(PROGRESS_FILE_PATH, PROGRESS_WRITE_PERIOD);
            pw.start();
        }
	while( cont && System.currentTimeMillis() < eTime ) {

	    // run MC search for each replica (parallel in pool!)
	    for(int i=0; i<numReplicas; i++) {
                Future fut = pool.submit(replicas[i]);
                futures.add(fut);
            }

            // Wait until all tasks have been completed
            for(int i=0; i<futures.size(); i++){
                try {
                    futures.get(i).get(); // doesn't return a result, but blocks until done
                } catch (InterruptedException ex) {
                    System.err.println("Error in thread pool: " + ex);
                    ex.printStackTrace();
                    System.exit(1);
                } catch (ExecutionException ex) {
                    System.err.println("Error in thread pool: " + ex);
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
            
            // All tasks are done, inspect results
            impr=false;
            for(int i=0; i<numReplicas; i++){
                double bestRepScore = replicas[i].getBestScore();

		if (bestRepScore > bestScore ||
		    (bestRepScore == bestScore && replicas[i].getBestCore().size() < bestCore.size())) {

		    bestScore = bestRepScore;
		    bestCore.clear();
		    bestCore.addAll(replicas[i].getBestCore());

                    impr=true;
                    lastImprTime = System.currentTimeMillis() - sTime;
		    System.out.println("best score: " + bestRepScore + "\tsize: " + bestCore.size() +
				       "\ttime: " + lastImprTime/1000.0);
                    // update progress writer
                    if(WRITE_PROGRESS_FILE){
                        pw.updateScore(bestScore);
                    }
		}
	    }

            // check min progression
            prog = bestScore - prevBestScore;
            if(impr && bestCore.size() >= prevBestSize && prog < minProg){
                cont = false;
            }
            // check stuckTime
            if((System.currentTimeMillis()-sTime-lastImprTime)/1000.0 > stuckTime){
                cont = false;
            }

            prevBestScore = bestScore;
            prevBestSize = bestCore.size();

	    // consider swapping temperatures of adjacent replicas
 	    for(int i=swapBase; i<numReplicas-1; i+=2) {
		SimpleMonteCarloReplica m = replicas[i];
		SimpleMonteCarloReplica n = replicas[i+1];

		double B_m = 1.0 / (K_b2 * m.getTemperature());
		double B_n = 1.0 / (K_b2 * n.getTemperature());
		double B_diff = B_n - B_m;
		double E_delta = m.getScore() - n.getScore();

		boolean swap = false;

		if( E_delta <= 0 ) {
		    swap = true;
		} else {
		    double p = r.nextDouble();

		    if( Math.exp(B_diff * E_delta) > p ) {
			swap = true;
		    }
		}

		if (swap) {
		    m.swapTemperature(n);
		    SimpleMonteCarloReplica temp = replicas[i];
		    replicas[i] = replicas[i+1];
		    replicas[i+1] = temp;
		}
	    }
	    swapBase = 1 - swapBase;
	}
        if(WRITE_PROGRESS_FILE){
            pw.stop();
        }
        
        System.out.println("### End time: " + (System.currentTimeMillis() - sTime)/1000.0);

	AccessionCollection core = new AccessionCollection();
	core.add(bestCore);

	return core;
    }

    /**
     * Pick a random core set
     *
     * @param ac
     * @param sampleMin
     * @param sampleMax
     * @return
     */
    public static AccessionCollection randomSearch(AccessionCollection ac, int sampleMin, int sampleMax) {
	List<Accession> accessions = new ArrayList<Accession>(ac.getAccessions());
	AccessionCollection core = new AccessionCollection();

	Random r = new Random();

        boolean cont = true;

	while(cont) {
	    int ai = r.nextInt(accessions.size());
	    Accession a = accessions.remove(ai);
	    core.add(a);
            cont = core.size() < sampleMax &&
                   (core.size() < sampleMin || r.nextDouble() > 1.0/(sampleMax-sampleMin));
	}
        // restore full accession collection
        accessions.addAll(core.getAccessions());

        return core;
    }

    public static AccessionCollection exhaustiveSearch(AccessionCollection ac, PseudoMeasure pm,
                                                       int sampleMin, int sampleMax){

        return exhaustiveSearch(ac, pm, sampleMin, sampleMax, true);

    }

    /**
     * Evaluate all possible core sets and return best one
     *
     * @param ac
     * @param pm
     * @param sampleMin
     * @param sampleMax
     * @param output
     * @return
     */
    public static AccessionCollection exhaustiveSearch(AccessionCollection ac, PseudoMeasure pm,
                                                       int sampleMin, int sampleMax, boolean output){
        // Check if sampleMin and sampleMax are equal (required for this exh search)
        if(sampleMin != sampleMax){
            System.err.println("\nError: minimum and maximum sample size should be equal for exhaustive search.\n");
            System.exit(1);
        }
	int coreSize = sampleMin;
	AccessionCollection temp = null, core = null;
        double score, bestScore = -Double.MAX_VALUE;
        int progress = 0, newProgress;
        String cacheID = PseudoMeasure.getUniqueId();

        // Calculate pseudomeasure for all possible core sets and return best core

        ThreadMXBean tb = ManagementFactory.getThreadMXBean();
	double sTime = tb.getCurrentThreadCpuTime();

        KSubsetGenerator ksub = new KSubsetGenerator(coreSize, ac.size());
        long nr = ksub.getNrOfKSubsets();
        if(output) System.out.println("Nr of possible core sets: " + nr + "\n-------------");
        Integer[] icore = ksub.first();
        for(long i=1; i<=nr; i++){
            newProgress = (int) (((double) i) / ((double) nr) * 100);
            if(newProgress > progress){
                if(output) System.out.println("### Progress: " + newProgress + "%");
                progress = newProgress;
            }
            temp = ac.subset(icore);
            // Calculate pseudomeasure
            score = pm.calculate(temp.getAccessions(), cacheID);
            if(score > bestScore){
                core = temp;
                bestScore = score;
                if(output)System.out.println("best score: " + bestScore + "\tsize: " + core.size() +
                                   "\ttime: " + (tb.getCurrentThreadCpuTime() - sTime)/1000000000);
            }
            ksub.successor(icore);
        }
        if(output) System.out.println("### End time: " + (tb.getCurrentThreadCpuTime() - sTime)/1000000000);

        return core;
    }

   public static AccessionCollection localSearch(AccessionCollection ac, Neighborhood nh,  PseudoMeasure pm,
                                                            int sampleMin, int sampleMax, double runtime,
                                                            double minProg, double stuckTime) {

        double score, newScore;
        int size, newSize;
        List<Accession> core, unselected;

        String cacheId = PseudoMeasure.getUniqueId();

        Random r = new Random();

        List<Accession> accessions = ac.getAccessions();

        // create unselected list
        unselected = new ArrayList<Accession>(accessions);
        // select an initial core
        core = new ArrayList<Accession>();
        int j;
        Accession a;
        for (int i=0; i<sampleMax; i++){
            j = r.nextInt(unselected.size());
            a = unselected.remove(j);
            core.add(a);
        }
        score = pm.calculate(core, cacheId);
        size = core.size();

      	ThreadMXBean tb = ManagementFactory.getThreadMXBean();
	double sTime = tb.getCurrentThreadCpuTime();
	double eTime = sTime + runtime * 1000000000;

        boolean cont = true;
        double lastImprTime = 0.0;

        ProgressWriter pw;
        if(WRITE_PROGRESS_FILE){
            pw = new ProgressWriter(PROGRESS_FILE_PATH, PROGRESS_WRITE_PERIOD);
            pw.start();
            pw.updateScore(score);
        }
	while ( cont && tb.getCurrentThreadCpuTime() < eTime ) {
            // run Local Search step
            nh.genRandomNeighbor(core, unselected);
            newScore = pm.calculate(core, cacheId);
            newSize = core.size();

            if (newScore > score || (newScore == score && newSize < size)) {
                // check min progression
                if(newSize >= size && newScore - score < minProg){
                    cont = false;
                }
                // report BETTER solution was found
                lastImprTime = tb.getCurrentThreadCpuTime() - sTime;
                System.out.println("best score: " + newScore + "\tsize: " + newSize +
                                   "\ttime: " + lastImprTime/1000000000);
                // accept new core!
                score = newScore;
                size = newSize;

                // update progress writer
                if(WRITE_PROGRESS_FILE){
                    pw.updateScore(score);
                }
            } else {
                // Reject new core
                nh.undoLastPerturbation(core, unselected);
                // check stuckTime
                if((tb.getCurrentThreadCpuTime()-sTime  -lastImprTime)/1000000000 > stuckTime){
                    cont = false;
                }
            }
	}
        if(WRITE_PROGRESS_FILE){
            pw.stop();
        }

        System.out.println("### End time: " + (tb.getCurrentThreadCpuTime() - sTime)/1000000000);

	AccessionCollection bestCore = new AccessionCollection();
	bestCore.add(core);

	return bestCore;

    }

    /**
     * Steepest Descent search.
     *
     * Always continue with the best of all neighbors, if it is better than the current
     * core set, and stop search if no improvement can be made. This is also called
     * an "iterative improvement" strategy.
     *
     * @param ac
     * @param nh
     * @param pm
     * @param sampleMin
     * @param sampleMax
     * @param runtime
     * @param minProg
     * @return
     */
    public static AccessionCollection steepestDescentSearch(AccessionCollection ac, Neighborhood nh,  PseudoMeasure pm,
                                                            int sampleMin, int sampleMax, double runtime, double minProg) {

        double score, newScore;
        int size, newSize;
        List<Accession> core, unselected;

        String cacheId = PseudoMeasure.getUniqueId();

        Random r = new Random();

        List<Accession> accessions = ac.getAccessions();

        // create unselected list
        unselected = new ArrayList<Accession>(accessions);
        // select an initial core
        core = new ArrayList<Accession>();
        int j;
        Accession a;
        for (int i=0; i<sampleMax; i++){
            j = r.nextInt(unselected.size());
            a = unselected.remove(j);
            core.add(a);
        }
        score = pm.calculate(core, cacheId);
        size = core.size();

      	ThreadMXBean tb = ManagementFactory.getThreadMXBean();
	double sTime = tb.getCurrentThreadCpuTime();
	double eTime = sTime + runtime * 1000000000;

        ProgressWriter pw;
        if(WRITE_PROGRESS_FILE){
            pw = new ProgressWriter(PROGRESS_FILE_PATH, PROGRESS_WRITE_PERIOD);
            pw.start();
            pw.updateScore(score);
        }
        boolean cont = true;
	while (cont) {        
            // run Steepest Descent search step
            nh.genBestNeighbor(core, unselected, pm, cacheId);
            newScore = pm.calculate(core, cacheId);
            newSize = core.size();

            if (newScore > score || (newScore == score && newSize < size)) {
                // check min progression
                if(newSize >= size && newScore - score < minProg){
                    cont = false;
                }
                // report BETTER solution was found
                System.out.println("best score: " + newScore + "\tsize: " + newSize +
                                   "\ttime: " + (tb.getCurrentThreadCpuTime() - sTime)/1000000000);                
                // accept new core!
                score = newScore;
                size = newSize;
                // continue if time left
                cont = cont && tb.getCurrentThreadCpuTime() < eTime;

                // update progress writer
                if(WRITE_PROGRESS_FILE){
                    pw.updateScore(score);
                }
            } else {
                // Don't accept new core
                nh.undoLastPerturbation(core, unselected);
                // All neighbors are worse than current core, so stop search
                cont = false;
            }
	}
        if(WRITE_PROGRESS_FILE){
            pw.stop();
        }

        System.out.println("### End time: " + (tb.getCurrentThreadCpuTime() - sTime)/1000000000);

	AccessionCollection bestCore = new AccessionCollection();
	bestCore.add(core);
	
	return bestCore;

    }

    /**
     * TABU Search.
     *
     * Tabu list is a list of indices at which the current core set cannot be
     * perturbed (delete, swap) to form a new core set as long as the index is contained
     * in the tabu list. After each perturbation step, the index of the newly added
     * accession (if it exists) is added to the tabu list, to ensure this accesion is
     * not again removed from the core set (or replaced) during the next few rounds.
     * 
     * If no new accession was added (pure deletion), a value "-1" is added to the tabu list.
     * As long as such values are contained in the tabu list, adding a new accesion without
     * removing one (pure addition) is considered tabu, to prevent immediately re-adding
     * the accession which was removed in the previous step.
     *
     * @param ac
     * @param nh
     * @param pm
     * @param sampleMin
     * @param sampleMax
     * @param runtime
     * @param minProg
     * @param stuckTime
     * @param tabuListSize
     * @return
     */
    public static AccessionCollection tabuSearch(AccessionCollection ac, Neighborhood nh,  PseudoMeasure pm, int sampleMin,
                                                 int sampleMax, double runtime, double minProg, double stuckTime, int tabuListSize) {

        double score, bestScore;
        List<Accession> core, bestCore, unselected;
        LinkedList<Integer> tabuList;

        String cacheId = PseudoMeasure.getUniqueId();

        Random r = new Random();

        List<Accession> accessions = ac.getAccessions();

        // create unselected list
        unselected = new ArrayList<Accession>(accessions);
        // select an initial core
        core = new ArrayList<Accession>();
        int j;
        Accession a;
        for (int i=0; i<sampleMax; i++){
            j = r.nextInt(unselected.size());
            a = unselected.remove(j);
            core.add(a);
        }
        score = pm.calculate(core, cacheId);

        bestCore = new ArrayList<Accession>();
        bestCore.addAll(core);
        bestScore = score;
        
        // initialize tabu list
        tabuList = new LinkedList<Integer>();

      	ThreadMXBean tb = ManagementFactory.getThreadMXBean();
	double sTime = tb.getCurrentThreadCpuTime();
	double eTime = sTime + runtime * 1000000000;

        int addIndex;
        boolean cont = true;
        double lastImprTime = 0.0;

        ProgressWriter pw;
        if(WRITE_PROGRESS_FILE){
            pw = new ProgressWriter(PROGRESS_FILE_PATH, PROGRESS_WRITE_PERIOD);
            pw.start();
            pw.updateScore(bestScore);
        }
	while ( cont && tb.getCurrentThreadCpuTime() < eTime ) {
            // run TABU search step

            // ALWAYS accept new core, even it is not an improvement
            addIndex = nh.genBestNeighbor(core, unselected, tabuList, bestScore, pm, cacheId);
            score = pm.calculate(core, cacheId);

            // check if new best core was found
            if (score > bestScore || (score == bestScore && core.size() < bestCore.size())) {
                // check min progression
                if(core.size() >= bestCore.size() && score - bestScore < minProg){
                    cont = false;
                }
                // store new best core
                bestScore = score;
                bestCore.clear();
                bestCore.addAll(core);

                lastImprTime = tb.getCurrentThreadCpuTime() - sTime;
                System.out.println("best score: " + bestScore + "\tsize: " + bestCore.size() +
                                   "\ttime: " + lastImprTime/1000000000);
                // update progress writer
                if(WRITE_PROGRESS_FILE){
                    pw.updateScore(bestScore);
                }
            } else {
                // check stuckTime
                if((tb.getCurrentThreadCpuTime()-sTime-lastImprTime)/1000000000 > stuckTime){
                    cont = false;
                }
            }

            // finally, update tabu list
            if(tabuList.size() == tabuListSize){
                // capacity reached, remove oldest tabu index
                tabuList.poll();
            }
            // add new tabu index
            //tabuList.offer(addIndex);
            tabuList.offer(addIndex);

	}
        if(WRITE_PROGRESS_FILE){
            pw.stop();
        }

        System.out.println("### End time: " + (tb.getCurrentThreadCpuTime() - sTime)/1000000000);

	AccessionCollection bestCoreCol = new AccessionCollection();
	bestCoreCol.add(bestCore);

	return bestCoreCol;

    }

    public static AccessionCollection mixedReplicaSearch(AccessionCollection ac, PseudoMeasure pm, int sampleMin,
                                                         int sampleMax, double runtime, double minProg, double stuckTime,
                                                         int nrOfTabuReplicas, int nrOfNonTabuReplicas, int roundsWithoutTabu,
                                                         int nrOfTabuSteps, int tournamentSize, int tabuListSize, int boostNr,
                                                         double boostMinProg, int boostTimeFactor, double minBoostTime,
                                                         double minMCTemp, double maxMCTemp) {

        double boostTime = 0;
        boolean boostTimeLocked = false;

        final int PROG_BOOST_FACTOR = 2;

        final int LR_L = 2;
        final int LR_R = 1;
        final boolean LR_EXH_START = false;
        // no limit on nr of steps for LR, just keeps running in background until done
        final int NR_OF_LR_STEPS = -1;

        // LS can perform more steps than tabu because each step is very fast,
        // only sampling one neighbor instead of Tabu which samples about ac.size()
        // neighbors in each step to select the (heursistic) best neighbor
        final int NR_OF_LS_STEPS = ac.size();

        double bestScore = -Double.MAX_VALUE;
        List<Accession> bestCore = new ArrayList<Accession>();

        Random rg = new Random();

        Neighborhood randNh = new RandomSingleNeighborhood(sampleMin, sampleMax);
        Neighborhood heurNh = new HeuristicSingleNeighborhood(sampleMin, sampleMax);

        // create, init and store initial replicas (local search)
        List<Replica> replicas = new ArrayList<Replica>(nrOfNonTabuReplicas);
        // add Local Search Replicas
        for (int i=0; i< nrOfNonTabuReplicas; i++){
            Replica rep;

            // initially, create some extra LS Replica
            rep = new LocalSearchReplica(ac, pm, randNh.clone(), NR_OF_LS_STEPS, -1, sampleMin, sampleMax);
            
            // Init replica
            rep.init();
            replicas.add(rep);
        }
        
        int nrOfTabus = 0;
        int nrOfNonTabus = nrOfNonTabuReplicas;
        int nrStuck = 0;

        // create and init one LR Semi replica
        LRReplica lrrep = new LRReplica(ac, pm, NR_OF_LR_STEPS, -1, sampleMin, sampleMax, LR_L, LR_R, LR_EXH_START);
        lrrep.init();

        List<Future> localAndMCReplicas = new ArrayList<Future>(nrOfNonTabuReplicas);
        List<Future> tabuFutures = new ArrayList<Future>(nrOfTabuReplicas);
        List<List<Accession>> parents = new ArrayList<List<Accession>>();
        List<List<Accession>> children = new ArrayList<List<Accession>>();

        // create thread pool
        final ThreadGroup threadGroup = new ThreadGroup("replicaThreadGroup");

        ThreadFactory factory = new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thr = new Thread(threadGroup, r);
                thr.setPriority(Thread.MIN_PRIORITY);
                return thr;
            }
        };

        ExecutorService pool = Executors.newCachedThreadPool(factory);

        boolean cont = true, impr;
        double prevBestScore = bestScore, prog;
        int prevBestSize = ac.size();
        long lastImprTime = 0;
        long prevRoundTime = 0;
        int numround = 1;
        double lastBoostTime = 0;

        boolean lrChecked = false;

        long sTime = System.currentTimeMillis();
        long eTime = sTime + (long)(runtime * 1000);

        ProgressWriter pw;
        if(WRITE_PROGRESS_FILE){
            pw = new ProgressWriter(PROGRESS_FILE_PATH, PROGRESS_WRITE_PERIOD);
            pw.start();
        }
        // start LR replica, continuously runs in background until finished
        Thread lrThread = factory.newThread(lrrep);
        lrThread.setPriority(Thread.MAX_PRIORITY);
        lrThread.start();

        long firstRounds = 0;

        while ( cont && System.currentTimeMillis() < eTime ){
            // submit all tabu replicas
            for(Replica rep : replicas){
                if(rep.shortType().equals("Tabu")){
                    tabuFutures.add(pool.submit(rep));
                }
            }
            
            // loop submission of Local and REMC replicas (short runs)
            while((firstRounds < roundsWithoutTabu || tabuReplicasBusy(tabuFutures))
                    && cont && System.currentTimeMillis() < eTime){

                firstRounds++;

                localAndMCReplicas.clear();
                // Submit non-tabu replicas
                for(int i=0; i<replicas.size(); i++){
                    Replica rep = replicas.get(i);
                    if(!rep.shortType().equals("Tabu")){
                        localAndMCReplicas.add(pool.submit(rep));
                    }
                }

                // Wait until all non-tabu replicas have completed their current run
                for(int i=0; i<localAndMCReplicas.size(); i++){
                    try {
                        //System.out.println("Waiting for non-tabu rep #" + (i+1));
                        localAndMCReplicas.get(i).get(); // doesn't return a result, but blocks until done
                    } catch (InterruptedException ex) {
                        System.err.println("Error in thread pool: " + ex);
                        ex.printStackTrace();
                        System.exit(1);
                    } catch (ExecutionException ex) {
                        System.err.println("Error in thread pool: " + ex);
                        ex.printStackTrace();
                        System.exit(1);
                    }
                }

                // Non-tabu replicas are done, inspect results
                impr = false;
                nrStuck = 0;
                Iterator<Replica> itr = replicas.iterator();
                while(itr.hasNext()){
                    Replica rep = itr.next();
                    if(!rep.shortType().equals("Tabu")){
                        // check for better solution
                        if (rep.getBestScore() > bestScore
                                 || (rep.getBestScore() == bestScore && rep.getBestCore().size() < bestCore.size())){

                            // store better core
                            bestScore = rep.getBestScore();
                            bestCore.clear();
                            bestCore.addAll(rep.getBestCore());

                            impr = true;
                            lastImprTime = System.currentTimeMillis() - sTime;
                            System.out.println("best score: " + bestScore + "\tsize: " + bestCore.size() +
                                               "\ttime: " + lastImprTime/1000.0 +
                                               "\t#rep: " + replicas.size() + "\tfound by: " + rep.type());
                            // update progress writer
                            if(WRITE_PROGRESS_FILE){
                                pw.updateScore(bestScore);
                            }
                        }
                        // count nr of stuck non-tabu reps
                        if(rep.stuck()){
                            nrStuck++;
                        }
                    }
                }

                // Check LR result, if done and not checked before
                if(lrrep.isDone() && !lrChecked){
                    if (lrrep.getBestScore() > bestScore
                            || (lrrep.getBestScore() == bestScore && lrrep.getBestCore().size() < bestCore.size())){

                        // store better core
                        bestScore = lrrep.getBestScore();
                        bestCore.clear();
                        bestCore.addAll(lrrep.getBestCore());

                        impr = true;
                        lastImprTime = System.currentTimeMillis() - sTime;
                        System.out.println("best score: " + bestScore + "\tsize: " + bestCore.size() +
                                           "\ttime: " + lastImprTime/1000.0 +
                                           "\t#rep: " + replicas.size() + "\tfound by: " + lrrep.type());
                        // update progress writer
                        if(WRITE_PROGRESS_FILE){
                            pw.updateScore(bestScore);
                        }
                    }
                    lrChecked = true;
                    // Since LR is done, we add it to the list of replicas so that its result can be used for merging
                    replicas.add(lrrep);
                    nrOfNonTabus++;
                }

                // update boost time
                if(!boostTimeLocked){
                    boostTime = boostTime / boostTimeFactor;
                    boostTime = (boostTime * (numround-1) + (System.currentTimeMillis() - sTime - prevRoundTime)/1000.0)/numround;
                    boostTime = boostTime * boostTimeFactor;
                    prevRoundTime = System.currentTimeMillis() - sTime;
                }

                prog = bestScore - prevBestScore;

                // check min progression
                if(impr && bestCore.size() >= prevBestSize && prog < minProg){
                    cont = false;
                }
                // check stuckTime
                if((System.currentTimeMillis()-sTime-lastImprTime)/1000.0 > stuckTime){
                    cont = false;
                }

                // check boost prog
                if(impr && prog < boostMinProg){
                    
                    lastBoostTime = System.currentTimeMillis()-sTime;
                    // only boost with some fraction of the normal nr of boost replicas in case of min prog boost
                    int progBoostNr = boostNr/PROG_BOOST_FACTOR;
                    boostReplicas(replicas, progBoostNr, ac, pm, randNh, NR_OF_LS_STEPS, sampleMin, sampleMax);
                    nrOfNonTabus += progBoostNr;
                    //System.out.println("[progBoost] - #rep: " + replicas.size());
                    
                }

                // check boost time -- do not boost if previous boost effect still visible!
                if((System.currentTimeMillis()-sTime-Math.max(lastImprTime, lastBoostTime))/1000.0 > Math.max(boostTime, minBoostTime)
                        && replicas.size() == nrOfNonTabuReplicas + nrOfTabuReplicas){
                    
                    lastBoostTime = System.currentTimeMillis()-sTime;
                    boostReplicas(replicas, boostNr, ac, pm, randNh, NR_OF_LS_STEPS, sampleMin, sampleMax);
                    nrOfNonTabus += boostNr;
                    boostTimeLocked = true;
                    //System.out.println("[timeBoost] - #rep: " + replicas.size());
                    
                }

                // Merge replicas to create new MC replicas (non-tabu)
                int nonTabuChildren = nrOfNonTabuReplicas - (nrOfNonTabus-nrStuck);
                if(nonTabuChildren > 0){
                    // Select parents from non-tabu replicas only! (tabus are still being manipulated, so skip these)
                    selectParents(replicas, parents, 2*nonTabuChildren, tournamentSize, rg, "Tabu");
                    // Create new children by merging parents
                    createNewChildren(parents, children, rg);
                    // Create new MC recplicas which use merged children as initial solutions
                    for(List<Accession> child : children){
                        // New REMC replicas
                        Replica rep = new SimpleMonteCarloReplica(ac, pm, randNh.clone(), NR_OF_LS_STEPS, -1,
                                    sampleMin, sampleMax, minMCTemp + rg.nextDouble()*(maxMCTemp-minMCTemp));
                        nrOfNonTabus++;
                        
                        rep.init(child);
                        replicas.add(rep);
                    }
                }


                // Now permanently delete stuck non-tabu replicas
                itr = replicas.iterator();
                while(itr.hasNext()){
                    Replica rep = itr.next();
                    if(rep.stuck() && !rep.shortType().equals("Tabu")){
                        itr.remove();
                        nrOfNonTabus--;
                    }
                }

                prevBestScore = bestScore;
                prevBestSize = bestCore.size();

                numround++;
            }

            if(!tabuReplicasBusy(tabuFutures)){
                // Tabu replicas have finished --> check for improvements & count stuck tabus
                nrStuck = 0;
                Iterator<Replica> itr = replicas.iterator();
                while(itr.hasNext()){
                    Replica rep = itr.next();
                    if(rep.shortType().equals("Tabu")){
                        // check for better solution
                        if (rep.getBestScore() > bestScore
                                 || (rep.getBestScore() == bestScore && rep.getBestCore().size() < bestCore.size())){

                            // store better core
                            bestScore = rep.getBestScore();
                            bestCore.clear();
                            bestCore.addAll(rep.getBestCore());

                            impr = true;
                            lastImprTime = System.currentTimeMillis() - sTime;
                            System.out.println("best score: " + bestScore + "\tsize: " + bestCore.size() +
                                               "\ttime: " + lastImprTime/1000.0 +
                                               "\t#rep: " + replicas.size() + "\tfound by: " + rep.type());
                            // update progress writer
                            if(WRITE_PROGRESS_FILE){
                                pw.updateScore(bestScore);
                            }
                        }
                        // count nr of stuck non-tabu reps
                        if(rep.stuck()){
                            nrStuck++;
                        }
                    }
                }

                // Create new tabus by merging current results (from all replicas!!!)
                int tabuChildren = nrOfTabuReplicas - (nrOfTabus-nrStuck);
                if(tabuChildren > 0){
                    // Select parents from all replicas!
                    selectParents(replicas, parents, 2*tabuChildren, tournamentSize, rg);
                    // Merge parents to create children
                    createNewChildren(parents, children, rg);
                    // Create new tabu replicas with merged children as initial solutions
                    for(List<Accession> child : children){
                        // new Tabu replicas
                        int listsize = rg.nextInt(tabuListSize)+1;
                        Replica rep = new TabuReplica(ac, pm, heurNh.clone(), nrOfTabuSteps, -1, sampleMin, sampleMax, listsize);
                        nrOfTabus++;

                        rep.init(child);
                        replicas.add(rep);
                    }
                }

                // Now permanently remove stuck tabus
                itr = replicas.iterator();
                while(itr.hasNext()){
                    Replica rep = itr.next();
                    if(rep.stuck() && rep.shortType().equals("Tabu")){
                        itr.remove();
                        nrOfTabus--;
                    }
                }
                
            } else {
                // Tabu replicas have not finished, which means search was stopped during inner loop
                // of non-tabu replicas. Search will stop, so don't do anything anymore at this point.
            }

        }
        if(WRITE_PROGRESS_FILE){
            pw.stop();
        }
        lrrep.stop();
        
        System.out.println("### End time: " + (System.currentTimeMillis() - sTime)/1000.0);

	AccessionCollection bestCoreCol = new AccessionCollection();
	bestCoreCol.add(bestCore);

	return bestCoreCol;

    }

    private static boolean tabuReplicasBusy(List<Future> tabuFutures){
        // remove all tabu replica futures which are already done
        Iterator<Future> itr = tabuFutures.iterator();
        while(itr.hasNext()){
            if(itr.next().isDone()){
                itr.remove();
            }
        }
        // if busy futures remain, return true
        return tabuFutures.size() > 0;
    }

    /**
     * Boost replicas with new randomly initialized LS replicas
     */
    private static void boostReplicas(List<Replica> replicas, int boost, AccessionCollection ac,
                               PseudoMeasure pm, Neighborhood randNh, int nrOfLsSteps, int sampleMin, int sampleMax){

        // Boost with new LS replicas
        for(int i=0; i<boost; i++){
            Replica rep;
            // create LS Replica
            rep = new LocalSearchReplica(ac, pm, randNh.clone(), nrOfLsSteps, -1, sampleMin, sampleMax);
            rep.init();
            replicas.add(rep);
        }

    }

    private static void selectParents(List<Replica> replicas, List<List<Accession>> parents,
                                        int nrOfParents, int T, Random rg){

        selectParents(replicas, parents, nrOfParents, T, rg, null);

    }

    private static void selectParents(List<Replica> replicas, List<List<Accession>> parents,
                                        int nrOfParents, int T, Random rg, String skipType){
        double bestParScore, parScore;
        List<Accession> bestPar = null, nextPar;
        String bestParType = null;
        parents.clear();
        for(int i=0; i<nrOfParents; i++){
            // Tournament selection: choose T random, select best.
            // Repeat for each parent.
            bestParScore = -Double.MAX_VALUE;
            for(int j=0; j<T; j++){
                // Choose random individual
                int k = rg.nextInt(replicas.size());
                Replica rep = replicas.get(k);
                if(skipType == null || !rep.shortType().equals(skipType)){
                    nextPar = rep.getBestCore();
                    parScore = rep.getBestScore();
                    // Check if new best parent found
                    if(parScore > bestParScore){
                        bestParScore = parScore;
                        bestPar = nextPar;
                        bestParType = rep.type();
                    }
                } else {
                    j--; // ignore cases when a skipped replica was drawn
                }
            }
            parents.add(bestPar);
            //System.out.println("Parent: " + bestParType + ", score: " + bestParScore);
        }
    }

    private static void createNewChildren(List<List<Accession>> parents, List<List<Accession>> children, Random rg){

        List<Accession> parent1, parent2, child;
        int p1size, p2size, childSize;

        children.clear();
        for(int i=0; i<parents.size()-1; i+=2){

            // Cross-over

            // Get parents (make sure parent1 is the SMALLEST one)
            if(parents.get(i).size() <= parents.get(i+1).size()){
                parent1 = parents.get(i);
                p1size = parent1.size();
                parent2 = parents.get(i+1);
                p2size = parent2.size();

            } else {
                parent1 = parents.get(i+1);
                p1size = parent1.size();
                parent2 = parents.get(i);
                p2size = parent2.size();
            }
            // Create child (cross-over)
            childSize = p1size + rg.nextInt(p2size-p1size+1);
            child = new ArrayList<Accession>(childSize);


            
            // Get some parts of parent1
            for(int j=0; j<p1size; j++){
                // Randomly decide wether to add the accession at
                // index j in parent1 to the child (probability of 50%)
                if(rg.nextBoolean()){
                    child.add(parent1.get(j));
                }
            }
            // Get remaining parts from parent2
            int j=rg.nextInt(p2size); // Start looping over parent2 at random index
            // While child not full: add new accessions from parent2
            Accession a;
            while(child.size() < childSize){
                // Add new accession from parent2 if not already present in child
                a = parent2.get(j);
                if(!child.contains(a)){
                    child.add(a);
                }
                j = (j+1)%p2size;
            }

            // Add new child to list
            children.add(child);
        }
    }

    public static AccessionCollection lrSearch(AccessionCollection ac, PseudoMeasure pm, int sampleMin, int sampleMax,
                                               int l, int r, boolean exhaustiveFirstPair) {

        List<Accession> core, unselected;
        List<Accession> accessions = ac.getAccessions();
        double score, newScore, bestNewScore, dscore;
        String cacheID = PseudoMeasure.getUniqueId();
        int bestAddIndex = -1, bestRemIndex = -1;
        Stack<SinglePerturbation> history = new Stack<SinglePerturbation>();

        ThreadMXBean tb = ManagementFactory.getThreadMXBean();
	double sTime = tb.getCurrentThreadCpuTime();

        boolean skipadd = false;
        if (l>r) {
            // Start with minimal set, stepwise increase size
            if(exhaustiveFirstPair){
                // Because distance measures require at least two accessions to be
                // computable, exhaustively select the best core set of size 2
                core = exhaustiveSearch(ac, pm, 2, 2, false).getAccessions();
            } else {
                // Random first pair, to save computational cost: this transforms the
                // deterministic lr search into a semi-random method
                core = CoreSubsetSearch.randomSearch(ac, 2, 2).getAccessions();
            }
            unselected = new ArrayList<Accession>(accessions);
            unselected.removeAll(core);
        } else {
            // Start with full set, stepwise decrease size
            core = new ArrayList<Accession>(accessions);
            unselected = new ArrayList<Accession>();
            skipadd = true;
        }
        score = pm.calculate(core, cacheID);
        bestNewScore = score;
        System.out.println("best score: " + score + "\tsize: " + core.size() +
                           "\ttime: " + (tb.getCurrentThreadCpuTime() - sTime)/1000000000);

        boolean cont = true;
        while(cont){
            // Add l new accessions to core
            if(!skipadd){
                for(int i=0; i<l; i++){
                    // Search for best new accession
                    bestNewScore = -Double.MAX_VALUE;
                    for(int j=0; j<unselected.size(); j++){
                        Accession add = unselected.get(j);
                        core.add(add);
                        newScore = pm.calculate(core, cacheID);
                        if(newScore > bestNewScore){
                            bestNewScore = newScore;
                            bestAddIndex = j;
                        }
                        core.remove(core.size()-1);
                    }
                    // Add best new accession
                    core.add(unselected.remove(bestAddIndex));
                    history.add(new Addition(bestAddIndex));
                }
                skipadd=false;
            }
            // Remove r accessions from core
            for(int i=0; i<r; i++){
                // Search for worst accession
                bestNewScore = -Double.MAX_VALUE;
                for(int j=0; j<core.size(); j++){
                    Accession rem = core.remove(j);
                    newScore = pm.calculate(core, cacheID);
                    if(newScore > bestNewScore){
                        bestNewScore = newScore;
                        bestRemIndex = j;
                    }
                    core.add(j, rem);
                }
                // Remove worst accession
                unselected.add(core.remove(bestRemIndex));
                history.add(new Deletion(bestRemIndex));
            }

            dscore = bestNewScore - score;
            score = bestNewScore;

            // Determine whether to continue search
            if(l > r){
                // Increasing core size
                if (core.size() > sampleMin && dscore <= 0) {
                    cont = false; // Equal or worse score and size increased
                    // Restore previous core
                    for(int i=0; i<l+r; i++){
                        history.pop().undo(core, unselected);
                    }
                } else if(core.size()+l-r > sampleMax){
                    cont = false; // Max size reached
                }

            } else {
                // Decreasing core size
                if (core.size() < sampleMax && dscore < 0){
                    cont = false; // Worse score
                    // Restore previous core
                    for(int i=0; i<l+r; i++){
                        history.pop().undo(core, unselected);
                    }
                } else if (core.size()+l-r < sampleMin){
                    cont = false; // Min size reached
                }
            }

            // Print core information
            System.out.println("best score: " + score + "\tsize: " + core.size() +
                               "\ttime: " + (tb.getCurrentThreadCpuTime() - sTime)/1000000000);
        }

        System.out.println("### End time: " + (tb.getCurrentThreadCpuTime() - sTime)/1000000000);

        AccessionCollection bestCore = new AccessionCollection();
	bestCore.add(core);

	return bestCore;
        
    }

    public static AccessionCollection lrSearch(AccessionCollection ac, PseudoMeasure pm, int sampleMin, int sampleMax, int l, int r) {

        return lrSearch(ac, pm, sampleMin, sampleMax, l, r, true);

    }

    public static AccessionCollection semiLrSearch(AccessionCollection ac, PseudoMeasure pm, int sampleMin, int sampleMax, int l, int r) {

        return lrSearch(ac, pm, sampleMin, sampleMax, l, r, false);

    }

    public static AccessionCollection forwardSelection(AccessionCollection ac, PseudoMeasure pm, int sampleMin, int sampleMax) {

        return lrSearch(ac, pm, sampleMin, sampleMax, 1, 0);

    }

    public static AccessionCollection semiForwardSelection(AccessionCollection ac, PseudoMeasure pm, int sampleMin, int sampleMax) {

        return semiLrSearch(ac, pm, sampleMin, sampleMax, 1, 0);

    }

    public static AccessionCollection backwardSelection(AccessionCollection ac, PseudoMeasure pm, int sampleMin, int sampleMax) {

        return lrSearch(ac, pm, sampleMin, sampleMax, 0, 1);

    }

}
