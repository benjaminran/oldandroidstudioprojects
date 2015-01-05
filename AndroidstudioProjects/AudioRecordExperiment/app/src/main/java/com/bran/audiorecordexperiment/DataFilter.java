package com.bran.audiorecordexperiment;

/**
 * Created by Beni on 7/29/14.
 */
public interface DataFilter {
    /**
     * This method performs all of the processing the filter does on the data, as well as initiating the result, whatever that may be.
     * @param buffer An array containing the original values from the recorder. Do not modify.
     * @param read The number of elements (starting at index 0) that were written to. The rest of the array is meaningless.
     */
    public void processBuffer(short[] buffer, int read);

    public void refreshData();
}
