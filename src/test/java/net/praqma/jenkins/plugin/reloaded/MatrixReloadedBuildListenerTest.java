package net.praqma.jenkins.plugin.reloaded;

import hudson.matrix.Axis;
import hudson.matrix.AxisList;
import hudson.matrix.Combination;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.matrix.MatrixRun;
import hudson.model.ParameterValue;
import hudson.model.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.JenkinsRule;

import static junit.framework.TestCase.assertNotNull;

public class MatrixReloadedBuildListenerTest {

    @Rule
    public JenkinsRule r = new JenkinsRule();
    private static AxisList axes = null;
    private static Combination c = null;

    @BeforeClass
    public static void init() {
        axes = new AxisList(new Axis("dim1", "1", "2", "3"), new Axis("dim2", "a", "b", "c"));
        Map<String, String> r = new HashMap<String, String>();
        r.put("dim1", "1");
        r.put("dim2", "a");
        c = new Combination(r);
    }

    @Test
    public void testReloaded() throws IOException, InterruptedException, ExecutionException {
        MatrixProject mp = r.createProject(MatrixProject.class, "test");
        mp.setAxes(axes);

        List<ParameterValue> values = new ArrayList<ParameterValue>();
        /* UUID */
        String uuid = "myuuid";
        MatrixBuild mb = mp.scheduleBuild2(0).get();
        MatrixRun mr = mb.getRun(c);
        Result r = mr.getResult();

        assertNotNull(mb);
    }
}
