/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.vcloud.hostingdotcom.compute;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.vcloud.compute.VCloudComputeServiceLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "compute.HostingDotComVCloudComputeServiceLiveTest")
public class HostingDotComVCloudComputeServiceLiveTest extends VCloudComputeServiceLiveTest {
   @BeforeClass
   @Override
   public void setServiceDefaults() {
      service = "hostingdotcom";
   }

   @Test
   public void testTemplateBuilder() {
      Template defaultTemplate = client.templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getArchitecture(), Architecture.X86_64);
      assertEquals(defaultTemplate.getImage().getOsFamily(), OsFamily.CENTOS);
      assertEquals(defaultTemplate.getLocation().getId(), "188849");
      assertEquals(defaultTemplate.getSize().getCores(), 1.0d);
   }

}