/*
A KBase module: GrowthDataUtils
Growth Data Utilites.
*/

module GrowthDataUtils {
    /*
        A string representing a workspace name.
    */
    typedef string workspace_name;


    /*
        A string representing a GrowthMatrix id.
    */
    typedef string growthmatrix_id;

    typedef structure {
        workspace_name workspace;
        growthmatrix_id input_growthmatrix_id;
        string result_id;
		int std_dev;
		int std_err;
    } GroupReplicatesParams;


    /*
        Group replicates by samples, calculate average and stderr
    */
    funcdef group_replicates(GroupReplicatesParams params) returns (growthmatrix_id) authentication required;

};
