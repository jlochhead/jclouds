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
package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.core.HttpHeaders;

import org.jclouds.encryption.internal.JCEEncryptionService;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import com.google.inject.name.Names;
import org.jclouds.vcloud.functions.ParseLoginResponseFromHeaders;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VCloudLogin}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VCloudLoginTest")
public class VCloudLoginTest extends RestClientTest<VCloudLoginAsyncClient> {

   public void testLogin() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VCloudLoginAsyncClient.class.getMethod("login");
      GeneratedHttpRequest<VCloudLoginAsyncClient> httpMethod = processor.createRequest(method);

      assertEquals(httpMethod.getRequestLine(), "POST http://localhost:8080/login HTTP/1.1");
      assertHeadersEqual(httpMethod, HttpHeaders.ACCEPT
               + ": application/vnd.vmware.vcloud.organizationList+xml\n");
      assertPayloadEquals(httpMethod, null);

      assertResponseParserClassEquals(method, httpMethod, ParseLoginResponseFromHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<VCloudLoginAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VCloudLoginAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VCloudLoginAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), checkNotNull(
                     new VCloudPropertiesBuilder(new Properties()).build(), "properties"));
            bind(URI.class).annotatedWith(org.jclouds.vcloud.endpoints.VCloudLogin.class)
                     .toInstance(URI.create("http://localhost:8080/login"));
            try {
               bind(BasicAuthentication.class).toInstance(
                        new BasicAuthentication("user", "pass", new JCEEncryptionService()));
            } catch (UnsupportedEncodingException e) {
               throw new RuntimeException(e);
            }
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

      };
   }

}
