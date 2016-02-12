package growthdatautils;

import java.io.File;
import us.kbase.auth.AuthToken;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.common.service.JsonServerSyslog;
import us.kbase.common.service.RpcContext;

//BEGIN_HEADER
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import us.kbase.common.service.Tuple11;
import us.kbase.common.service.UObject;
import us.kbase.kbaseenigmametals.GrowthMatrix;
import us.kbase.kbaseenigmametals.MetadataProperties;
import us.kbase.workspace.ObjectIdentity;
import us.kbase.workspace.ObjectSaveData;
import us.kbase.workspace.ProvenanceAction;
import us.kbase.workspace.SaveObjectsParams;
import us.kbase.workspace.WorkspaceClient;
//END_HEADER

/**
 * <p>Original spec-file module name: GrowthDataUtils</p>
 * <pre>
 * A KBase module: GrowthDataUtils
 * Growth Data Utilites.
 * </pre>
 */
public class GrowthDataUtilsServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;

    //BEGIN_CLASS_HEADER
    private final String wsUrl;
    //END_CLASS_HEADER

    public GrowthDataUtilsServer() throws Exception {
        super("GrowthDataUtils");
        //BEGIN_CONSTRUCTOR
        wsUrl = config.get("workspace-url");
        //END_CONSTRUCTOR
    }

    /**
     * <p>Original spec-file function name: group_replicates</p>
     * <pre>
     * Group replicates by samples, calculate average and stderr
     * </pre>
     * @param   params   instance of type {@link growthdatautils.GroupReplicatesParams GroupReplicatesParams}
     * @return   instance of original type "growthmatrix_id" (A string representing a GrowthMatrix id.)
     */
    @JsonServerMethod(rpc = "GrowthDataUtils.group_replicates", async=true)
    public String groupReplicates(GroupReplicatesParams params, AuthToken authPart, RpcContext jsonRpcContext) throws Exception {
        String returnVal = null;
        //BEGIN group_replicates
        MetadataProperties.startup();

        if (params.getWorkspace() == null)
            throw new IllegalStateException("Parameter workspace is not set in input arguments");
        String workspaceName = params.getWorkspace();
        if (params.getInputGrowthmatrixId() == null)
            throw new IllegalStateException("Parameter input_growthmatrix_id is not set in input arguments");
        String inputGrowthMatrixId = params.getInputGrowthmatrixId();
        if (params.getResultId() == null)
            throw new IllegalStateException("Parameter result_id is not set in input arguments");
        String outputGrowthMatrixId = params.getResultId();
        if (params.getStdDev() == null)
            throw new IllegalStateException("Parameter std_dev is not set in input arguments");
        long calculateStdDev = params.getStdDev();
        if ((calculateStdDev < 0)||(calculateStdDev < 1))
            throw new IllegalStateException("std_dev parameter should be either 0 or 1 (" + calculateStdDev  + ")");
        if (params.getStdDev() == null)
            throw new IllegalStateException("Parameter std_dev is not set in input arguments");
        long calculateStdErr = params.getStdErr();
        if ((calculateStdErr < 0)||(calculateStdErr < 1))
            throw new IllegalStateException("std_err parameter should be either 0 or 1 (" + calculateStdErr  + ")");
        
        WorkspaceClient wc = new WorkspaceClient(new URL(this.wsUrl), authPart);
        wc.setAuthAllowedForHttp(true);

        GrowthMatrix matrix;
        try {
        	matrix = wc.getObjects(Arrays.asList(new ObjectIdentity().withRef(
                    workspaceName + "/" + inputGrowthMatrixId))).get(0).getData().asClassInstance(GrowthMatrix.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Error loading input GrowthMatrix object from workspace", ex);
        }
        
        System.out.println("Got GrowthMatrix data.");

        matrix=GrowthDataUtilsImpl.createStatValuesMatrix(matrix, calculateStdDev, calculateStdErr);
        matrix.setName(outputGrowthMatrixId);
        
        //Save resulting matrix
        Tuple11<Long, String, String, String, Long, String, Long, String, String, Long, Map<String,String>> info;
        try {
            info = wc.saveObjects(new SaveObjectsParams().withWorkspace(workspaceName)
                .withObjects(Arrays.asList(new ObjectSaveData()
                .withType("KBaseEnigmaMetals.GrowthMatrix").withName(outputGrowthMatrixId)
                .withData(new UObject(matrix))
                .withProvenance((List<ProvenanceAction>)jsonRpcContext.getProvenance())))).get(0);
        } catch (Exception ex) {
            throw new IllegalStateException("Error saving output GrowthMatrix object to workspace", ex);
        }
                        
        System.out.println("saved:" + info);
        
        //returnVal = info.getE7() + "/" + info.getE1() + "/" + info.getE5();
        returnVal = info.getE2();
        //END group_replicates
        return returnVal;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            new GrowthDataUtilsServer().startupServer(Integer.parseInt(args[0]));
        } else if (args.length == 3) {
            JsonServerSyslog.setStaticUseSyslog(false);
            JsonServerSyslog.setStaticMlogFile(args[1] + ".log");
            new GrowthDataUtilsServer().processRpcCall(new File(args[0]), new File(args[1]), args[2]);
        } else {
            System.out.println("Usage: <program> <server_port>");
            System.out.println("   or: <program> <context_json_file> <output_json_file> <token>");
            return;
        }
    }
}
