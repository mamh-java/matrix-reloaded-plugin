package net.praqma.jenkins.plugin.reloaded;

import java.util.HashMap;
import java.util.Map;

import hudson.matrix.Combination;
import hudson.model.Action;

/**
 * This is the action for a matrix build, that determines which(if any) combinations, that should be rebuild.
 * @author wolfgang
 *
 */
public class RebuildAction implements Action {

	/**
     * Determines whether a configuration should be reused or rebuild
     */
    private Map<String, Boolean> configurations = new HashMap<String, Boolean>();
	
    /**
     * The base build for this rebuild action
     */
	private int baseBuildNumber = 0;
	
	/**
	 * Determines whether to propagate matrix reloaded to downstream builds
	 */
	private boolean rebuildDownstream = false; 
	
	public RebuildAction() {
	}
	
	public void setBaseBuildNumber( int baseBuildNumber ) {
		this.baseBuildNumber = baseBuildNumber;
	}
	
	public void setRebuildDownstream( boolean b ) {
		this.rebuildDownstream = b;
	}
	
	public int getBaseBuildNumber() {
		return baseBuildNumber;
	}
	
	public boolean doRebuildDownstream() {
		return rebuildDownstream;
	}
	
	public RebuildAction clone( int baseBuildNumber ) {
		RebuildAction ra = new RebuildAction();
		ra.baseBuildNumber = baseBuildNumber;
		ra.configurations = this.configurations;
		ra.rebuildDownstream = this.rebuildDownstream;
		
		return ra;
	}
	
	public RebuildAction clone( ) {
		RebuildAction ra = new RebuildAction();
		ra.baseBuildNumber = this.baseBuildNumber;
		ra.configurations = this.configurations;
		ra.rebuildDownstream = this.rebuildDownstream;
		
		return ra;
	}
	

    public void addConfiguration(Combination combination, boolean reuse) {
        this.configurations.put(combination.toString(), reuse);
    }

    public boolean getConfiguration(Combination combination) {
        if (configurations.containsKey(combination.toString())) {
            return configurations.get(combination.toString());
        }

        return false;
    }
	
	public String getDisplayName() {
		return null;
	}

	public String getIconFileName() {
		return null;
	}

	public String getUrlName() {
		return null;
	}

	public String toString() {
		return "Rebuild(" + baseBuildNumber + "/" + rebuildDownstream + "): " + configurations.size();
	}
}
