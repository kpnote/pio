package util;

public class ReplaceInput {

	public String doReplaceInput(String inputData){
	    String outputData = inputData;

	    outputData = outputData.replace("&", "&amp;");
	    outputData = outputData.replace("\"", "&quot;");
	    outputData = outputData.replace("<", "&lt;");
	    outputData = outputData.replace(">", "&gt;");
	    outputData = outputData.replace("'", "&#39;");
	    outputData = outputData.replace("\n", "\t");

	    return outputData;
	}
}
