/*
 *  The MIT License
 *
 *  Copyright 2011 Praqma A/S.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package net.praqma.jenkins.plugin.reloaded;

import hudson.matrix.*;
import hudson.model.*;
import net.praqma.jenkins.plugin.reloaded.MatrixReloadedAction.BuildType;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

public class MatrixReloadedActionTest {

    @Rule
    public JenkinsRule r = new JenkinsRule();

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

    @Test
    public void testCombinationExists() throws Exception {
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

    @Test
    public void testCombinationExists2() throws InterruptedException, ExecutionException, IOException {
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
        assertFalse( mra.combinationExists(mb, c_bad) );
    }

    @Test
    public void testCombinationExists3() throws InterruptedException, ExecutionException, IOException {
        init();
        FreeStyleProject mp = r.createFreeStyleProject("test");
        FreeStyleBuild mb = mp.scheduleBuild2(0, new Cause.UserCause(), new ParametersAction()).get();
        MatrixReloadedAction mra = new MatrixReloadedAction();
        assertFalse( mra.combinationExists(mb, c_good) );
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
    public void testForm() throws IOException, InterruptedException, ExecutionException {
        /* Create a previous build */
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

        /* Create form elements */
        Map<String, String[]> form = new HashMap<String, String[]>();

        form.put("MRP::NUMBER", new String[] { "1" } );

        //form.put("MRP::dim1=1,dim2=a", false);
        form.put("MRP::dim1=1,dim2=b", new String[] { "0" });
        form.put("MRP::dim1=2,dim2=a", new String[] { "0" });
        //form.put("MRP::dim1=2,dim2=b", false);
        form.put("MRP::", new String[] { "0" });

        MatrixReloadedAction mra = new MatrixReloadedAction();
        mra.getRebuildAction(form);
    }

    @Test
    public void testFormNoParms() throws IOException, InterruptedException, ExecutionException {
        /* Create a previous build */
        init();

        MatrixProject mp = r.createProject(MatrixProject.class, "test");
        mp.setAxes(axes);

        MatrixBuild mb = mp.scheduleBuild2(0).get();

        /* Create form elements */
        Map<String, String[]> form = new HashMap<String, String[]>();

        form.put("MRP::NUMBER", new String[] { "1" });

        //form.element("MRP::dim1=1,dim2=a", false);
        form.put("MRP::dim1=1,dim2=b", new String[] { "0" });
        form.put("MRP::dim1=2,dim2=a", new String[] { "0" });
        //form.element("MRP::dim1=2,dim2=b", false);

        MatrixReloadedAction mra = new MatrixReloadedAction();
        mra.getRebuildAction(form);
    }

    @Test
    public void testFormFalseParms() throws Exception {
        /* Create a previous build */
        init();

        MatrixProject mp = r.createProject(MatrixProject.class, "test");
        mp.setAxes(axes);

        MatrixBuild mb = mp.scheduleBuild2(0).get();

        /* Create form elements */
        Map<String, String[]> form = new HashMap<String, String[]>();

        form.put("MRP::NUMBER", new String[] { "1" });

        form.put("MRPFALSE1", new String[] { "0" });
        form.put("MRPFALSE2", new String[] { "1" });

        MatrixReloadedAction mra = new MatrixReloadedAction();
        mra.getRebuildAction(form);
    }

    @Test
    public void testFormFalseNumberParm() throws IOException, InterruptedException,
            ExecutionException {
        /* Create a previous build */
        init();

        MatrixProject mp = r.createProject(MatrixProject.class, "test");
        mp.setAxes(axes);

        MatrixBuild mb = mp.scheduleBuild2(0).get();

        /* Create form elements */
        Map<String, String[]> form = new HashMap<String, String[]>();

        form.put("MRP::NUMBER", new String[] { "fail" });

        MatrixReloadedAction mra = new MatrixReloadedAction();
        mra.getRebuildAction(form);
    }

    @Test
    public void testEnv() throws IOException, InterruptedException, ExecutionException {
        /* Create a previous build */
        init();
        MatrixProject mp = r.createProject(MatrixProject.class, "test");
        mp.setAxes(axes);
        MatrixBuild mb = mp.scheduleBuild2(0).get();
        MatrixReloadedEnvironmentContributor mrec = new MatrixReloadedEnvironmentContributor();
        mrec.buildEnvironmentFor(mb, mb.getEnvironment(null), null);
    }

}
