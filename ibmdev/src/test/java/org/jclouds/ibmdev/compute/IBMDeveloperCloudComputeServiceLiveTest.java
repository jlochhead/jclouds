/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ibmdev.compute;

import static org.testng.Assert.assertEquals;

import org.jclouds.ibmdev.IBMDeveloperCloudAsyncClient;
import org.jclouds.ibmdev.IBMDeveloperCloudClient;
import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "ibmdevelopercloud.IBMDeveloperCloudComputeServiceLiveTest")
public class IBMDeveloperCloudComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   @BeforeClass
   @Override
   public void setServiceDefaults() {
      service = "ibmdev";
   }

   @Test
   public void testTemplateBuilder() {
      Template defaultTemplate = client.templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getArchitecture(), Architecture.X86_32);
      assertEquals(defaultTemplate.getImage().getOsFamily(), OsFamily.RHEL);
      assertEquals(defaultTemplate.getLocation().getId(), "1");
      assertEquals(defaultTemplate.getSize().getCores(), 2.0d);
   }
   
   @Override
   public void testListNodes() throws Exception {
      for (ComputeMetadata node : client.listNodes()) {
         assert node.getProviderId() != null;
        // assert node.getLocation() != null;
         assertEquals(node.getType(), ComputeType.NODE);
      }
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   public void testAssignability() throws Exception {
      @SuppressWarnings("unused")
      RestContext<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient> tmContext = new ComputeServiceContextFactory()
               .createContext(service, user, password).getProviderSpecificContext();
   }

}