package eu.lod2.lod2testsuite.testcases;

import eu.lod2.lod2testsuite.configuration.MetaParams;

/**
 *
 * @author Stefan Schurischuster
 */
public abstract class ParameterizedTestCase extends TestCase {
    protected MetaParams csvMetaParams;
    
    public MetaParams getCsvMetaParams() {
        return csvMetaParams;
    }

    public void setCsvMetaParams(MetaParams csvMetaParams) {
        this.csvMetaParams = csvMetaParams;
    }
}
