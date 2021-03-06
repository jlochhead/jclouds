/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.chef;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.chef.config.ChefRestClientModule;
import org.jclouds.chef.filters.SignedHeaderAuth;
import org.jclouds.chef.filters.SignedHeaderAuthTest;
import org.jclouds.chef.functions.ParseKeyFromJson;
import org.jclouds.chef.functions.ParseKeySetFromJson;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.io.Files;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Tests annotation parsing of {@code ChefAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "chef.ChefAsyncClientTest")
public class ChefAsyncClientTest extends RestClientTest<ChefAsyncClient> {

   public void testGetCookbook() throws SecurityException,
         NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("getCookbook",
            String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor
            .createRequest(method, "cookbook");
      assertRequestLineEquals(httpRequest,
            "GET http://localhost:4000/cookbooks/cookbook HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest,
            ReturnStringIf200.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteCookbook() throws SecurityException,
         NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("deleteCookbook",
            String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor
            .createRequest(method, "cookbook");
      assertRequestLineEquals(httpRequest,
            "DELETE http://localhost:4000/cookbooks/cookbook HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest,
            CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   private static final String COOOKBOOK_BODY =

   "----JCLOUDS--\r\n"
         + "Content-Disposition: form-data; name=\"name\"\r\n\r\n"
         + "cookbook\r\n"
         + "----JCLOUDS--\r\n"
         + "Content-Disposition: form-data; name=\"file\"; filename=\"cookbook.tar.gz\"\r\n"
         + "Content-Type: application/octet-stream\r\n\r\n\r\n"
         + "----JCLOUDS----\r\n";

   public void testCreateCookbookFile() throws SecurityException,
         NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("createCookbook",
            String.class, File.class);

      File file = File.createTempFile("jclouds-chef=test", ".tar.gz");
      file.deleteOnExit();
      Files.write("".getBytes(), file);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor
            .createRequest(method, "cookbook", file);

      assertRequestLineEquals(httpRequest,
            "POST http://localhost:4000/cookbooks HTTP/1.1");
      assertHeadersEqual(
            httpRequest,
            "Accept: application/json\nContent-Length: "
                  + (file.getName().length() + 206)
                  + "\nContent-Type: multipart/form-data; boundary=--JCLOUDS--\n");
      assertPayloadEquals(httpRequest, COOOKBOOK_BODY.replace(
            "cookbook.tar.gz", file.getName()));

      assertResponseParserClassEquals(method, httpRequest,
            CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testCreateCookbookByte() throws SecurityException,
         NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("createCookbook",
            String.class, byte[].class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor
            .createRequest(method, "cookbook", "".getBytes());

      assertRequestLineEquals(httpRequest,
            "POST http://localhost:4000/cookbooks HTTP/1.1");
      assertHeadersEqual(
            httpRequest,
            "Accept: application/json\nContent-Length: 221\nContent-Type: multipart/form-data; boundary=--JCLOUDS--\n");
      assertPayloadEquals(httpRequest, COOOKBOOK_BODY);

      assertResponseParserClassEquals(method, httpRequest,
            CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testUpdateCookbookFile() throws SecurityException,
         NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("updateCookbook",
            String.class, File.class);

      File file = File.createTempFile("jclouds-chef=test", ".tar.gz");
      file.deleteOnExit();
      Files.write("".getBytes(), file);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor
            .createRequest(method, "cookbook", file);

      assertRequestLineEquals(httpRequest,
            "PUT http://localhost:4000/cookbooks/cookbook/_content HTTP/1.1");
      assertHeadersEqual(
            httpRequest,
            "Accept: application/json\nContent-Length: "
                  + (file.getName().length() + 206)
                  + "\nContent-Type: multipart/form-data; boundary=--JCLOUDS--\n");
      assertPayloadEquals(httpRequest, COOOKBOOK_BODY.replace(
            "cookbook.tar.gz", file.getName()));

      assertResponseParserClassEquals(method, httpRequest,
            CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testUpdateCookbookByte() throws SecurityException,
         NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("updateCookbook",
            String.class, byte[].class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor
            .createRequest(method, "cookbook", "".getBytes());

      assertRequestLineEquals(httpRequest,
            "PUT http://localhost:4000/cookbooks/cookbook/_content HTTP/1.1");
      assertHeadersEqual(
            httpRequest,
            "Accept: application/json\nContent-Length: 221\nContent-Type: multipart/form-data; boundary=--JCLOUDS--\n");
      assertPayloadEquals(httpRequest, COOOKBOOK_BODY);

      assertResponseParserClassEquals(method, httpRequest,
            CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListCookbooks() throws SecurityException,
         NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("listCookbooks");
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor
            .createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:4000/cookbooks HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest,
            ParseKeySetFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testClientExists() throws SecurityException,
         NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("clientExists",
            String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor
            .createRequest(method, "client");
      assertRequestLineEquals(httpRequest,
            "HEAD http://localhost:4000/clients/client HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest,
            ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteClient() throws SecurityException,
         NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("deleteClient",
            String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor
            .createRequest(method, "client");
      assertRequestLineEquals(httpRequest,
            "DELETE http://localhost:4000/clients/client HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest,
            ReturnStringIf200.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGenerateKeyForClient() throws SecurityException,
         NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("generateKeyForClient",
            String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor
            .createRequest(method, "client");
      assertRequestLineEquals(httpRequest,
            "PUT http://localhost:4000/clients/client HTTP/1.1");
      assertHeadersEqual(
            httpRequest,
            "Accept: application/json\nContent-Length: 44\nContent-Type: application/json\n");
      assertPayloadEquals(httpRequest,
            "{\"clientname\":\"client\", \"private_key\": true}");

      assertResponseParserClassEquals(method, httpRequest,
            ParseKeyFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testCreateClient() throws SecurityException,
         NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("createClient",
            String.class);
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor
            .createRequest(method, "client");

      assertRequestLineEquals(httpRequest,
            "POST http://localhost:4000/clients HTTP/1.1");
      assertHeadersEqual(
            httpRequest,
            "Accept: application/json\nContent-Length: 23\nContent-Type: application/json\n");
      assertPayloadEquals(httpRequest, "{\"clientname\":\"client\"}");

      assertResponseParserClassEquals(method, httpRequest,
            ParseKeyFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testListClients() throws SecurityException,
         NoSuchMethodException, IOException {
      Method method = ChefAsyncClient.class.getMethod("listClients");
      GeneratedHttpRequest<ChefAsyncClient> httpRequest = processor
            .createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:4000/clients HTTP/1.1");
      assertHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null);

      assertResponseParserClassEquals(method, httpRequest,
            ParseKeySetFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<ChefAsyncClient> httpRequest) {
      assertEquals(httpRequest.getFilters().size(), 1);
      assertEquals(httpRequest.getFilters().get(0).getClass(),
            SignedHeaderAuth.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ChefAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ChefAsyncClient>>() {
      };
   }

   @Override
   protected Module createModule() {
      return new ChefRestClientModule() {
         @Override
         protected void configure() {
            Names.bindProperties(binder(), new ChefPropertiesBuilder(
                  "chef-validator", SignedHeaderAuthTest.PRIVATE_KEY).build());
            install(new NullLoggingModule());
            super.configure();
         }

         @Override
         protected String provideTimeStamp(@TimeStamp Supplier<String> cache) {
            return "timestamp";
         }
      };
   }
}
