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
package org.jclouds.azure.storage.queue;

import java.util.List;
import java.util.Properties;

import org.jclouds.azure.storage.queue.config.AzureQueueContextModule;
import org.jclouds.azure.storage.queue.config.AzureQueueRestClientModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Creates {@link AzureQueueContext} or {@link Injector} instances based on the most commonly
 * requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole
 * @see AzureQueueContext
 */
public class AzureQueueContextBuilder extends
         RestContextBuilder<AzureQueueClient, AzureQueueAsyncClient> {

   public AzureQueueContextBuilder(String providerName, Properties properties) {
      super(providerName, AzureQueueClient.class, AzureQueueAsyncClient.class, properties);
   }

   @Override
   protected void addContextModule(String providerName, List<Module> modules) {
      modules.add(new AzureQueueContextModule(providerName));
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new AzureQueueRestClientModule());
   }

}
