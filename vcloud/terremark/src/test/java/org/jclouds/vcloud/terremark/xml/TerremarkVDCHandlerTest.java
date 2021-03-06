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
package org.jclouds.vcloud.terremark.xml;

import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.internal.NamedResourceImpl;
import org.jclouds.vcloud.endpoints.VCloudApi;
import org.jclouds.vcloud.terremark.domain.TerremarkVDC;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code TerremarkVDCHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.TerremarkVDCHandlerTest")
public class TerremarkVDCHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/terremark/vdc.xml");
      Injector injector = Guice.createInjector(new ParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
         }

         @SuppressWarnings("unused")
         @Provides
         @VCloudApi
         URI provide() {
            return URI.create("https://services.vcloudexpress.terremark.com/api/v0.8");
         }

      });
      Factory factory = injector.getInstance(ParseSax.Factory.class);

      TerremarkVDC result = (TerremarkVDC) factory.create(
               injector.getInstance(TerremarkVDCHandler.class)).parse(is);
      assertEquals(result.getName(), "Miami Environment 1");
      assertEquals(result.getLocation(), URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32"));
      assertEquals(
               result.getResourceEntities(),
               ImmutableMap
                        .<String, NamedResource> of(
                                 "adriantest",
                                 new NamedResourceImpl(
                                          "15124",
                                          "adriantest",
                                          VCloudMediaType.VAPP_XML,
                                          URI
                                                   .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/15124")),
                                 "centos-53",
                                 new NamedResourceImpl(
                                          "15120",
                                          "centos-53",
                                          VCloudMediaType.VAPP_XML,
                                          URI
                                                   .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/15120"))));

      assertEquals(
               result.getAvailableNetworks(),
               ImmutableMap
                        .of(
                                 "10.114.34.128/26",
                                 new NamedResourceImpl(
                                          "1708",
                                          "10.114.34.128/26",
                                          "application/vnd.vmware.vcloud.network+xml",
                                          URI
                                                   .create("https://services.vcloudexpress.terremark.com/api/v0.8/network/1708"))));
      assertEquals(result.getCatalog(), new NamedResourceImpl("catalog", "Miami Environment 1",
               CATALOG_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/catalog")));
      assertEquals(
               result.getPublicIps(),
               new NamedResourceImpl(
                        "publicIps",
                        "Public IPs",
                        "application/xml",
                        URI
                                 .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/publicIps")));
      assertEquals(
               result.getInternetServices(),
               new NamedResourceImpl(
                        "internetServices",
                        "Internet Services",
                        "application/xml",
                        URI
                                 .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/internetServices")));
   }
}
