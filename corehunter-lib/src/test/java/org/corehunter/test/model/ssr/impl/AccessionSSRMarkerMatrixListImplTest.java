package org.corehunter.test.model.ssr.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.corehunter.model.DuplicateEntityException;
import org.corehunter.model.EntityIndexedDataset;
import org.corehunter.model.UnknownEntityException;
import org.corehunter.model.accession.Accession;
import org.corehunter.model.accession.impl.AccessionImpl;
import org.corehunter.model.impl.OrderedEntityDatasetListImpl;
import org.corehunter.model.ssr.SSRAllele;
import org.corehunter.model.ssr.SSRMarker;
import org.corehunter.model.ssr.impl.AccessionSSRMarkerMatrixListImpl;
import org.corehunter.model.ssr.impl.SSRAlleleImpl;
import org.corehunter.model.ssr.impl.SSRMarkerImpl;

public class AccessionSSRMarkerMatrixListImplTest extends
    AccessionSSRMarkerMatrixListImpl
{

	public AccessionSSRMarkerMatrixListImplTest(
      Collection<String> accessionNames,
      Map<String, List<String>> markersToAlleles) throws DuplicateEntityException
  {
	  super("test", createAccssions(accessionNames), createMarkers(markersToAlleles), createElements(accessionNames, markersToAlleles));
  }
	
	private static List<List<List<Double>>> createElements(Collection<String> accessionNames, Map<String, List<String>> markersToAlleles)
  {
		List<List<List<Double>>> elements ;
		Iterator<String> accessions = accessionNames.iterator() ;
		
		elements = new ArrayList<List<List<Double>>>(accessionNames.size()) ;
		
		while (accessions.hasNext())
		{
			elements.add(createAccssionElements(accessions.next(), markersToAlleles)) ;
		}
		
	  return elements;
  }

	private static List<List<Double>> createAccssionElements(String accessionName,
      Map<String, List<String>> markersToAlleles)
  {
		List<List<Double>> elements = new ArrayList<List<Double>>(markersToAlleles.size()) ;
		
		Iterator<Entry<String, List<String>>> iterator = markersToAlleles.entrySet().iterator() ;
		
		while (iterator.hasNext())
			elements.add(createMarkerElements(iterator.next())) ;
		
	  return elements ;
  }

	private static List<Double> createMarkerElements(
      Entry<String, List<String>> markersToAllele)
  {
		List<Double> elements = new ArrayList<Double>(markersToAllele.getValue().size()) ;
		
		for (int i = 0 ; i < markersToAllele.getValue().size() ; ++i)
			elements.add(null) ;
		
	  return elements ;
  }

	private static EntityIndexedDataset<Integer, Accession> createAccssions(
      Collection<String> accessionNames)
  {
		List<Accession> accessions = new ArrayList<Accession>(accessionNames.size()) ;
		Iterator<String> iterator = accessionNames.iterator() ;
		
		while (iterator.hasNext())
			accessions.add(createAccssion(iterator.next())) ;
		
	  return new OrderedEntityDatasetListImpl<Accession>("accessions", accessions);
  }

	private static Accession createAccssion(String accessionName)
  {
	  return new AccessionImpl(accessionName);
  }
	
	private static EntityIndexedDataset<Integer, SSRMarker> createMarkers(
			Map<String, List<String>> markersToAlleles) throws DuplicateEntityException
  {
		List<SSRMarker> markers = new ArrayList<SSRMarker>(markersToAlleles.size()) ;
		Iterator<Entry<String, List<String>>> iterator = markersToAlleles.entrySet().iterator() ;
		
		while (iterator.hasNext())
			markers.add(createMarker(iterator.next())) ;
		
	  return new OrderedEntityDatasetListImpl<SSRMarker>("markers", markers);
  }

	private static SSRMarker createMarker(Entry<String, List<String>> markerToAlleles) throws DuplicateEntityException
  {
		SSRMarker marker = createMarker(markerToAlleles.getKey());
		
		Iterator<String> iterator = markerToAlleles.getValue().iterator() ;
		
		while (iterator.hasNext())
			marker.addAllele(createAllele(iterator.next(), marker)) ;
		
	  return marker;
  }


	private static SSRMarker createMarker(String markerName)
  {
	  return new SSRMarkerImpl(markerName);
  }
	
	private static SSRAllele createAllele(String alleleName, SSRMarker marker)
  {
	  return new SSRAlleleImpl(alleleName, marker);
  }

	public Double getValue(String accessionName, String markerName, String alleleName) throws UnknownEntityException
  {
	  return getValue(getRowHeaders().getElementByName(accessionName), getColumnHeaders().getElementByName(markerName), getColumnHeaders().getElementByName(markerName).getAlleleByName(alleleName)) ;
  }
	
	public void setValue(String accessionName, String markerName, String alleleName, Double value) throws UnknownEntityException
  {
		setValue(getRowHeaders().getElementByName(accessionName), getColumnHeaders().getElementByName(markerName), getColumnHeaders().getElementByName(markerName).getAlleleByName(alleleName), value) ;
  }
	
	public List<Double> getValues(String accessionName, String markerName) throws UnknownEntityException
  {
		return getValues(getRowHeaders().getElementByName(accessionName), getColumnHeaders().getElementByName(markerName)) ;
  }
	
	public void setValues(String accessionName, String markerName, List<Double> values) throws UnknownEntityException
  {
		setValues(getRowHeaders().getElementByName(accessionName), getColumnHeaders().getElementByName(markerName), values) ;
  }
}
