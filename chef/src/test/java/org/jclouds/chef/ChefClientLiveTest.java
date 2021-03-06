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
 *   http://www.apache.org/licenses/LICENSE-2.0
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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

/**
 * Tests behavior of {@code ChefClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "chef.ChefClientLiveTest")
public class ChefClientLiveTest {

   private static final String COOKBOOK_NAME = "runit";
   private static final String COOKBOOK_URI = "https://s3.amazonaws.com/opscode-community/cookbook_versions/tarballs/195/original/runit.tar.gz";
   private RestContext<ChefClient, ChefAsyncClient> validatorConnection;
   private RestContext<ChefClient, ChefAsyncClient> clientConnection;
   private RestContext<ChefClient, ChefAsyncClient> adminConnection;

   private String clientKey;
   private String endpoint;
   private String validator;
   private String user;
   private byte[] cookbookContent;
   private File cookbookFile;

   public static final String PREFIX = System.getProperty("user.name")
         + "-jcloudstest";

   @BeforeClass(groups = { "live" })
   public void setupClient() throws IOException {
      endpoint = checkNotNull(System.getProperty("jclouds.test.endpoint"),
            "jclouds.test.endpoint");
      validator = System.getProperty("jclouds.test.validator");
      if (validator == null || validator.equals(""))
         validator = "chef-validator";
      String validatorKey = System.getProperty("jclouds.test.validator.key");
      if (validatorKey == null || validatorKey.equals(""))
         validatorKey = System.getProperty("user.home")
               + "/.chef/validation.pem";
      user = checkNotNull(System.getProperty("jclouds.test.user"));
      String keyfile = System.getProperty("jclouds.test.key");
      if (keyfile == null || keyfile.equals(""))
         keyfile = System.getProperty("user.home") + "/.chef/" + user + ".pem";
      validatorConnection = createConnection(validator, Files.toString(
            new File(validatorKey), Charsets.UTF_8));
      adminConnection = createConnection(user, Files.toString(
            new File(keyfile), Charsets.UTF_8));
   }

   private RestContext<ChefClient, ChefAsyncClient> createConnection(
         String identity, String key) throws IOException {
      return ChefContextFactory.createContext(URI.create(endpoint), identity,
            key, new Log4JLoggingModule());
   }

   @Test
   public void testListClients() throws Exception {
      Set<String> clients = validatorConnection.getApi().listClients();
      assertNotNull(clients);
      assert clients.contains(validator) : "validator: " + validator
            + " not in: " + clients;
   }

   @Test(dependsOnMethods = "testListClients")
   public void testCreateClient() throws Exception {
      validatorConnection.getApi().deleteClient(PREFIX);
      clientKey = validatorConnection.getApi().createClient(PREFIX);
      assertNotNull(clientKey);
      System.out.println(clientKey);
      clientConnection = createConnection(PREFIX, clientKey);
      clientConnection.getApi().clientExists(PREFIX);
   }

   @Test(dependsOnMethods = "testCreateClient")
   public void testGenerateKeyForClient() throws Exception {
      clientKey = validatorConnection.getApi().generateKeyForClient(PREFIX);
      assertNotNull(clientKey);
      clientConnection.close();
      clientConnection = createConnection(PREFIX, clientKey);
      clientConnection.getApi().clientExists(PREFIX);
   }

   @Test(dependsOnMethods = "testCreateClient")
   public void testClientExists() throws Exception {
      assertNotNull(validatorConnection.getApi().clientExists(PREFIX));
   }

   @Test
   public void testCreateCookbook() throws Exception {
      adminConnection.getApi().deleteCookbook(COOKBOOK_NAME);
      InputStream in = null;
      try {
         in = URI.create(COOKBOOK_URI).toURL().openStream();

         cookbookContent = ByteStreams.toByteArray(in);

         cookbookFile = File.createTempFile("foo", ".tar.gz");
         Files.write(cookbookContent, cookbookFile);
         cookbookFile.deleteOnExit();

         adminConnection.getApi().createCookbook(COOKBOOK_NAME, cookbookFile);
         adminConnection.getApi().deleteCookbook(COOKBOOK_NAME);
         adminConnection.getApi()
               .createCookbook(COOKBOOK_NAME, cookbookContent);

      } finally {
         if (in != null)
            in.close();
      }
   }

   @Test(dependsOnMethods = "testCreateCookbook")
   public void testUpdateCookbook() throws Exception {
      adminConnection.getApi().updateCookbook(COOKBOOK_NAME, cookbookFile);
      // TODO verify timestamp or something
      adminConnection.getApi().updateCookbook(COOKBOOK_NAME, cookbookContent);
   }

   @Test(dependsOnMethods = "testUpdateCookbook")
   public void testListCookbooks() throws Exception {
      for (String cookbook : adminConnection.getApi().listCookbooks())
         System.err.println(adminConnection.getApi().getCookbook(cookbook));
   }

   @AfterClass(groups = { "live" })
   public void teardownClient() throws IOException {
      if (validatorConnection.getApi().clientExists(PREFIX))
         validatorConnection.getApi().deleteClient(PREFIX);
      if (clientConnection != null)
         clientConnection.close();
      if (validatorConnection != null)
         validatorConnection.close();
      if (adminConnection != null)
         adminConnection.close();
   }
}
