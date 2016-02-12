package growthdatautils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import us.kbase.kbaseenigmametals.FloatMatrix2D;
import us.kbase.kbaseenigmametals.GrowthMatrix;
import us.kbase.kbaseenigmametals.Matrix2DMetadata;
import us.kbase.kbaseenigmametals.MetadataProperties;
import us.kbase.kbaseenigmametals.PropertyValue;

public class GrowthDataUtilsImpl {
	
	public static GrowthMatrix createStatValuesMatrix (GrowthMatrix m, long calculateStdDev, long calculateStdErr) {

		//check if the input matrix contains raw values
		boolean isStatValues = false;
		for (PropertyValue p : m.getMetadata().getMatrixMetadata() ) {
			if (p.getCategory().equals(MetadataProperties.DATAMATRIX_METADATA_TABLE_MEASUREMENT)&&p.getPropertyName().equals(MetadataProperties.DATAMATRIX_METADATA_TABLE_MEASUREMENT_VALUES)&& p.getPropertyValue().equals(MetadataProperties.DATAMATRIX_METADATA_TABLE_MEASUREMENT_VALUES_VALUE_STATVALUES)) {
				isStatValues = true;
			}
		}
		if (isStatValues) throw new IllegalStateException("Input object must contain raw values");
		
		// Build a list of series

		Map<String, List<String>> seriesIds2columnIds = new Hashtable<String, List<String>>();

		for(Entry<String, List<PropertyValue>> entry: m.getMetadata().getColumnMetadata().entrySet()){
			String columnId = entry.getKey();
			for(PropertyValue pv: entry.getValue()){
				if((pv.getCategory().equals(MetadataProperties.GROWTHMATRIX_METADATA_COLUMN_DATASERIES))&&(pv.getPropertyName().equals(MetadataProperties.GROWTHMATRIX_METADATA_COLUMN_DATASERIES_SERIESID))){
					String seriesId = pv.getPropertyValue();
					List<String> columnIds  = seriesIds2columnIds.get(seriesId);
					if(columnIds == null){
						columnIds = new Vector<String>();
					}
					columnIds.add(columnId);
					seriesIds2columnIds.put(seriesId, columnIds);
				}
			}	
		}
		
		System.out.println(seriesIds2columnIds);
		
		Map<String, List<PropertyValue>> outputColumnMetadata = new Hashtable<String, List<PropertyValue>>();

		List<String> colIds = m.getData().getColIds();
		List<String> rowIds = m.getData().getRowIds();
		
		List<List<Double>> outputDataValues = new ArrayList<List<Double>>();
		List<String> outputColIds = new ArrayList<String>();
		
		int columnCount = 0;

		//initialize rows of data
		for (int i = 0; i < rowIds.size(); i++) {
			List<Double> row = new ArrayList<Double>();
			outputDataValues.add(row);
		}
		
		//this loop processes series

		for(Entry<String, List<String>> entry: seriesIds2columnIds.entrySet()){
			String seriesId = entry.getKey();
			List<Integer> columnIndices = new ArrayList<Integer>();
			List<PropertyValue> seriesProperties = new ArrayList<PropertyValue>();
			
			//fill list of column indices and list of PropertyValues for all columns within a series
			for (String columnId: entry.getValue()) {
				seriesProperties.addAll(m.getMetadata().getColumnMetadata().get(columnId));
				for (int j = 0; j < colIds.size(); j++) {
					if (columnId.equals(colIds.get(j))) columnIndices.add(j);
				}
			}
			
			List<PropertyValue> columnProperties = reducePropertyValuesList(seriesProperties);

			//fill list of series/row values:
			
			for (int row = 0; row < m.getData().getValues().size(); row++) {
				double[] dataPoints = new double[entry.getValue().size()];
				for (int k = 0; k < columnIndices.size(); k++) {
					dataPoints[k] = m.getData().getValues().get(row).get(columnIndices.get(k));
				}
				outputDataValues.get(row).add(mean(dataPoints));
			}
			
			columnCount++;
			String columnId = "C"+columnCount;
			outputColIds.add(columnId);
			//build new column metadata
			List<PropertyValue> averageColumnProperties = new ArrayList<PropertyValue>(); 
			averageColumnProperties.addAll(columnProperties);
			averageColumnProperties.add(new PropertyValue().withCategory(MetadataProperties.DATAMATRIX_METADATA_COLUMN_MEASUREMENT).withPropertyName(MetadataProperties.DATAMATRIX_METADATA_COLUMN_MEASUREMENT_VALUETYPE).withPropertyValue("Average"));
			averageColumnProperties.add(new PropertyValue().withCategory(MetadataProperties.GROWTHMATRIX_METADATA_COLUMN_DATASERIES).withPropertyName(MetadataProperties.GROWTHMATRIX_METADATA_COLUMN_DATASERIES_SERIESID).withPropertyValue(seriesId));
			outputColumnMetadata.put(columnId, averageColumnProperties);
			
				
			if (calculateStdDev == 1L) {
				for (int row = 0; row < m.getData().getValues().size(); row++) {
					double[] dataPoints = new double[entry.getValue().size()];
					for (int k = 0; k < columnIndices.size(); k++) {
						dataPoints[k] = m.getData().getValues().get(row).get(columnIndices.get(k));
					}
					if (dataPoints.length == 1) {
						outputDataValues.get(row).add(0.0);
					} else {
						outputDataValues.get(row).add(sdev(dataPoints));
					}
					
				}
				
				columnCount++;
				columnId = "C"+columnCount;
				outputColIds.add(columnId);
				//build new column metadata
				List<PropertyValue> stdDevColumnProperties = new ArrayList<PropertyValue>();
				stdDevColumnProperties.addAll(columnProperties);
				stdDevColumnProperties.add(new PropertyValue().withCategory(MetadataProperties.DATAMATRIX_METADATA_COLUMN_MEASUREMENT).withPropertyName(MetadataProperties.DATAMATRIX_METADATA_COLUMN_MEASUREMENT_VALUETYPE).withPropertyValue("SD"));
				stdDevColumnProperties.add(new PropertyValue().withCategory(MetadataProperties.GROWTHMATRIX_METADATA_COLUMN_DATASERIES).withPropertyName(MetadataProperties.GROWTHMATRIX_METADATA_COLUMN_DATASERIES_SERIESID).withPropertyValue(seriesId));
				outputColumnMetadata.put(columnId, stdDevColumnProperties);
			}
			
			if (calculateStdErr == 1L) {
				for (int row = 0; row < m.getData().getValues().size(); row++) {
					double[] dataPoints = new double[entry.getValue().size()];
					for (int k = 0; k < columnIndices.size(); k++) {
						dataPoints[k] = m.getData().getValues().get(row).get(columnIndices.get(k));
					}
					if (dataPoints.length == 1) {
						outputDataValues.get(row).add(0.0);
					} else {
						outputDataValues.get(row).add(stderr(dataPoints));
					}
				}
				
				columnCount++;
				columnId = "C"+columnCount;
				outputColIds.add(columnId);
				//build new column metadata
				List<PropertyValue> stdErrColumnProperties = new ArrayList<PropertyValue>();
				stdErrColumnProperties.addAll(columnProperties);
				stdErrColumnProperties.add(new PropertyValue().withCategory(MetadataProperties.DATAMATRIX_METADATA_COLUMN_MEASUREMENT).withPropertyName(MetadataProperties.DATAMATRIX_METADATA_COLUMN_MEASUREMENT_VALUETYPE).withPropertyValue("SE"));
				stdErrColumnProperties.add(new PropertyValue().withCategory(MetadataProperties.GROWTHMATRIX_METADATA_COLUMN_DATASERIES).withPropertyName(MetadataProperties.GROWTHMATRIX_METADATA_COLUMN_DATASERIES_SERIESID).withPropertyValue(seriesId));
				outputColumnMetadata.put(columnId, stdErrColumnProperties);
			}
			
		}
				
		FloatMatrix2D outputFloatMatrix = new FloatMatrix2D().withColIds(outputColIds).withRowIds(rowIds).withValues(outputDataValues);

		//Build table metadata
		List<PropertyValue> outputMatrixMetadata = new ArrayList<PropertyValue>();
		outputMatrixMetadata.add(new PropertyValue().withCategory(MetadataProperties.DATAMATRIX_METADATA_TABLE_DESCRIPTION).withPropertyName("").withPropertyValue("Created by group_replicates method"));
		outputMatrixMetadata.add(new PropertyValue().withCategory(MetadataProperties.DATAMATRIX_METADATA_TABLE_MEASUREMENT).withPropertyName(MetadataProperties.DATAMATRIX_METADATA_TABLE_MEASUREMENT_VALUES).withPropertyValue(MetadataProperties.DATAMATRIX_METADATA_TABLE_MEASUREMENT_VALUES_VALUE_STATVALUES));
		
		//Row metadata will be taken from input object
		Matrix2DMetadata outputMetadata = new Matrix2DMetadata().withMatrixMetadata(outputMatrixMetadata).withColumnMetadata(outputColumnMetadata).withRowMetadata(m.getMetadata().getRowMetadata());
		GrowthMatrix returnVal = new GrowthMatrix().withData(outputFloatMatrix).withMetadata(outputMetadata).withDescription("Created by group_replicates method");
		
		return returnVal;
	}
	
	  private static List<PropertyValue> reducePropertyValuesList(
			List<PropertyValue> seriesProperties) {
		  
		List<PropertyValue> returnVal = new ArrayList<PropertyValue>();
		List<String> concatenatedProperties = new ArrayList<String>();
		
		for (PropertyValue p : seriesProperties) {
			if (p.getCategory().equals(MetadataProperties.GROWTHMATRIX_METADATA_COLUMN_DATASERIES)&& p.getPropertyName().equals(MetadataProperties.GROWTHMATRIX_METADATA_COLUMN_DATASERIES_SERIESID)) {
				//skip old DataSeries entries
			} else if (!concatenatedProperties.contains(p.getCategory()+p.getPropertyName()+p.getPropertyUnit()+p.getPropertyValue())){
				returnVal.add(p);
				concatenatedProperties.add(p.getCategory()+p.getPropertyName()+p.getPropertyUnit()+p.getPropertyValue());
			}
		}
		
		return returnVal;
	}

	  public static double mean(double[] v) {
	    double sum = 0.0;
	    for (int i = 0; i < v.length; i++)
	      sum += v[i];
	    return sum / v.length;
	  }

	  public static double sdev(double[] v) {
	    return Math.sqrt(variance(v));
	  }

	  /**
	   * Returns the standard error of an array of double, where this is defined
	   * as the standard deviation of the sample divided by the square root of the
	   * sample size.
	   */

	  public static double stderr(double[] v) {
	    return sdev(v) / Math.sqrt(v.length);
	  }

	  public static double variance(double[] v) {
	    double mu = mean(v);
	    double sumsq = 0.0;
	    for (int i = 0; i < v.length; i++)
	      sumsq += (mu - v[i])*(mu - v[i]);
	    return sumsq / (v.length - 1);
	  }

}
