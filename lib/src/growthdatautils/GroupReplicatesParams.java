
package growthdatautils;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: GroupReplicatesParams</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "workspace",
    "input_growthmatrix_id",
    "result_id",
    "std_dev",
    "std_err"
})
public class GroupReplicatesParams {

    @JsonProperty("workspace")
    private String workspace;
    @JsonProperty("input_growthmatrix_id")
    private String inputGrowthmatrixId;
    @JsonProperty("result_id")
    private String resultId;
    @JsonProperty("std_dev")
    private Long stdDev;
    @JsonProperty("std_err")
    private Long stdErr;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("workspace")
    public String getWorkspace() {
        return workspace;
    }

    @JsonProperty("workspace")
    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public GroupReplicatesParams withWorkspace(String workspace) {
        this.workspace = workspace;
        return this;
    }

    @JsonProperty("input_growthmatrix_id")
    public String getInputGrowthmatrixId() {
        return inputGrowthmatrixId;
    }

    @JsonProperty("input_growthmatrix_id")
    public void setInputGrowthmatrixId(String inputGrowthmatrixId) {
        this.inputGrowthmatrixId = inputGrowthmatrixId;
    }

    public GroupReplicatesParams withInputGrowthmatrixId(String inputGrowthmatrixId) {
        this.inputGrowthmatrixId = inputGrowthmatrixId;
        return this;
    }

    @JsonProperty("result_id")
    public String getResultId() {
        return resultId;
    }

    @JsonProperty("result_id")
    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public GroupReplicatesParams withResultId(String resultId) {
        this.resultId = resultId;
        return this;
    }

    @JsonProperty("std_dev")
    public Long getStdDev() {
        return stdDev;
    }

    @JsonProperty("std_dev")
    public void setStdDev(Long stdDev) {
        this.stdDev = stdDev;
    }

    public GroupReplicatesParams withStdDev(Long stdDev) {
        this.stdDev = stdDev;
        return this;
    }

    @JsonProperty("std_err")
    public Long getStdErr() {
        return stdErr;
    }

    @JsonProperty("std_err")
    public void setStdErr(Long stdErr) {
        this.stdErr = stdErr;
    }

    public GroupReplicatesParams withStdErr(Long stdErr) {
        this.stdErr = stdErr;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return ((((((((((((("GroupReplicatesParams"+" [workspace=")+ workspace)+", inputGrowthmatrixId=")+ inputGrowthmatrixId)+", resultId=")+ resultId)+", stdDev=")+ stdDev)+", stdErr=")+ stdErr)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
