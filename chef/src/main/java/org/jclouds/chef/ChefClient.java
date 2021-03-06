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
/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.io.File;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;

/**
 * Provides synchronous access to Chef.
 * <p/>
 * 
 * @see ChefAsyncClient
 * @see <a href="TODO: insert URL of Chef documentation" />
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface ChefClient {

   /**
    * 
    * @return a list of all the cookbook names
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have permission to see the
    *            cookbook list.
    */
   Set<String> listCookbooks();

   /**
    * Creates (uploads) a cookbook with the name present from the tar/gz file.
    * 
    * @param cookbookName
    *           matches the root directory path of the archive
    * @param tgzArchive
    *           tar gz archive, with a base path of {@code cookbookName}
    *           <p/>
    *           "401 Unauthorized" if the caller is not a recognized user.
    *           <p/>
    *           "403 Forbidden" if you do not have permission to create
    *           cookbooks.
    * @throws HttpResponseException
    *            "409 Conflict" if the cookbook already exists
    */
   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   void createCookbook(String cookbookName, File tgzArchive);

   /**
    * like {@link #createCookbook(String, File)}, except that a byte stream is
    * allowed.
    */
   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   void createCookbook(String cookbookName, byte[] tgzArchive);

   /**
    * Overrides (uploads) a cookbook with the content in the tar/gz file.
    * 
    * @param cookbookName
    *           matches the root directory path of the archive
    * @param tgzArchive
    *           tar gz archive, with a base path of {@code cookbookName}
    *           <p/>
    *           "401 Unauthorized" if the caller is not a recognized user.
    *           <p/>
    *           "403 Forbidden" if you do not have permission to update
    *           cookbooks.
    * @throws ResourceNotFoundException
    *            if the cookbook does not exist
    */
   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   void updateCookbook(String cookbookName, File tgzArchive);

   /**
    * like {@link #updateCookbook(String, File)}, except that a byte stream is
    * allowed.
    */
   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   void updateCookbook(String cookbookName, byte[] tgzArchive);

   /**
    * deletes an existing cookbook.
    * 
    * @return last state of the client you deleted or null, if not found
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have Delete rights on the
    *            cookbook.
    */
   String deleteCookbook(String cookbookName);

   /**
    * Returns a description of the cookbook, with links to all of its component
    * parts, and the metadata.
    * 
    * @return the cookbook or null, if not found
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to view the
    *            cookbook.
    */
   String getCookbook(String cookbookName);

   /**
    * creates a new client
    * 
    * @return the private key of the client. You can then use this client name
    *         and private key to access the Opscode API.
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to create a
    *            client.
    * @throws HttpResponseException
    *            "409 Conflict" if the client already exists
    */
   @Timeout(duration = 120, timeUnit = TimeUnit.SECONDS)
   String createClient(String name);

   /**
    * generate a new key-pair for this client, and return the new private key in
    * the response body.
    * 
    * @return the new private key
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if the caller is not a recognized user.
    *            <p/>
    *            "403 Forbidden" if the caller is not authorized to modify the
    *            client.
    */
   @Timeout(duration = 120, timeUnit = TimeUnit.SECONDS)
   String generateKeyForClient(String name);

   /**
    * @return list of client names.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to list clients.
    */
   Set<String> listClients();

   /**
    * 
    * @return true if the specified client name exists.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have rights to view the client.
    */
   boolean clientExists(String name);

   /**
    * deletes an existing client.
    * 
    * @return last state of the client you deleted or null, if not found
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have Delete rights on the client.
    */
   String deleteClient(String name);

   /**
    * gets an existing client.
    * 
    * @throws AuthorizationException
    *            <p/>
    *            "401 Unauthorized" if you are not a recognized user.
    *            <p/>
    *            "403 Forbidden" if you do not have view rights on the client.
    */
   String getClient(String name);
}
