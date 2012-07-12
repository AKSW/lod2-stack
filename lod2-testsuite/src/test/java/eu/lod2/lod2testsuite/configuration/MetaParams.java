package eu.lod2.lod2testsuite.configuration;

/**
 *
 * @author Stefan Schurischuster
 */
public class MetaParams {
    private String currentLine;
    private String csvFile;
    private int currentLineNr;
    
    /**
     * 
     * @param currentLine
     *          The current line of parameters from the csv-file.
     * @param currentLineNr
     *          The number of the current line that is proceeded. The number
     *          starts at 2 because the first line is the header.
     * @param csvFile 
     */
    public MetaParams(String currentLine, int currentLineNr, String csvFile) {
        this.currentLine = currentLine;
        this.csvFile = csvFile;
        this.currentLineNr = currentLineNr;
    }

    public String getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(String csvFile) {
        this.csvFile = csvFile;
    }

    public String getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(String currentLine) {
        this.currentLine = currentLine;
    }

    public int getCurrentLineNr() {
        return currentLineNr;
    }

    public void setCurrentLineNr(int currentLineNr) {
        this.currentLineNr = currentLineNr;
    }

    @Override
    public String toString() {
        return "MetaParams{" + "currentLine=" + currentLine + ", csvFile=" + csvFile + ", currentLineNr=" + currentLineNr + '}';
    }
}
