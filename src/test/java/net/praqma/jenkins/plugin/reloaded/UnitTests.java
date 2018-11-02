package net.praqma.jenkins.plugin.reloaded;

import hudson.matrix.*;
import hudson.model.*;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UnitTests  {

    @ClassRule
    public static JenkinsRule r = new JenkinsRule();

    @Test
    public void testGetDisplayName() {
        MatrixReloadedAction mra = new MatrixReloadedAction();
        assertEquals(mra.getDisplayName(), Definitions.__DISPLAY_NAME);
    }

    @Test
    public void testGetIconFileName() {
        MatrixReloadedAction mra = new MatrixReloadedAction();
        assertEquals(mra.getIconFileName(), Definitions.__ICON_FILE_NAME);
    }

    @Test
    public void testGetUrlName() {
        MatrixReloadedAction mra = new MatrixReloadedAction();
        assertEquals(mra.getUrlName(), Definitions.__URL_NAME);
    }

    @Test
    public void testPrefix() {
        MatrixReloadedAction mra = new MatrixReloadedAction();
        assertEquals(mra.getPrefix(), Definitions.__PREFIX);
    }

    @Test
    public void testGetChecked1() {
        MatrixReloadedAction mra = new MatrixReloadedAction();
        assertNull(mra.getChecked());
    }

    @Test
    public void testGetChecked2() {
        MatrixReloadedAction mra = new MatrixReloadedAction("test");
        assertNotNull(mra.getChecked());
        assertEquals(mra.getChecked(), "test");
    }
    
    private AxisList axes = null;

    private Combination c_good = null;
    private Combination c_bad = null;

    
    public void init() {

        axes = new AxisList(new Axis("dim1", "1", "2"), new Axis("dim2", "a", "b"));

        Map<String, String> r = new HashMap<String, String>();
        r.put("dim1", "1");
        r.put("dim2", "a");
        c_good = new Combination(r);
        
        Map<String, String> r2 = new HashMap<String, String>();
        r2.put("dim1", "11");
        r2.put("dim2", "a");
        c_bad = new Combination(r2);
    }

    @Test
    public void testCombinationExists() throws InterruptedException, ExecutionException, IOException {
        init();
        MatrixProject mp = r.createProject(MatrixProject.class, "test");
        mp.setAxes(axes);
        List<ParameterDefinition> list = new ArrayList<ParameterDefinition>();
        list.add(new StringParameterDefinition("key", "value"));
        ParametersDefinitionProperty pdp = new ParametersDefinitionProperty(list);
        mp.addProperty(pdp);

        /*
         * Create some parameters to test continuation of parameters from reused
         * to new build
         */
        List<ParameterValue> values = new ArrayList<ParameterValue>();
        values.add(new StringParameterValue("key", "value"));

        MatrixBuild mb = mp.scheduleBuild2(0, new Cause.UserCause(), new ParametersAction(values)).get();
        
        MatrixReloadedAction mra = new MatrixReloadedAction();
        
        assertTrue( mra.combinationExists(mb, c_good) );
    }
}