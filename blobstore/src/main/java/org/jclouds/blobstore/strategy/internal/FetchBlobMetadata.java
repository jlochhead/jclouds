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
package org.jclouds.blobstore.strategy.internal;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static org.jclouds.concurrent.ConcurrentUtils.awaitCompletion;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.blobstore.AsyncBlobStore;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;
import org.jclouds.blobstore.domain.internal.PageSetImpl;
import org.jclouds.blobstore.internal.BlobRuntimeException;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

/**
 * Retrieves all blobmetadata in the list as efficiently as possible
 * 
 * @author Adrian Cole
 */
@NotThreadSafe
public class FetchBlobMetadata implements
         Function<PageSet<? extends StorageMetadata>, PageSet<? extends StorageMetadata>> {

   protected final BackoffLimitedRetryHandler retryHandler;
   protected final AsyncBlobStore ablobstore;
   protected final ExecutorService userExecutor;
   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   protected Logger logger = Logger.NULL;

   private String container;
   /**
    * maximum duration of an blob Request
    */
   @Inject(optional = true)
   @Named(Constants.PROPERTY_REQUEST_TIMEOUT)
   protected Long maxTime;

   @Inject
   FetchBlobMetadata(@Named(Constants.PROPERTY_USER_THREADS) ExecutorService userExecutor,
            AsyncBlobStore ablobstore, BackoffLimitedRetryHandler retryHandler) {
      this.userExecutor = userExecutor;
      this.ablobstore = ablobstore;
      this.retryHandler = retryHandler;
   }

   public FetchBlobMetadata setContainerName(String container) {
      this.container = container;
      return this;
   }

   public PageSet<? extends StorageMetadata> apply(PageSet<? extends StorageMetadata> in) {
      checkState(container != null, "container name should be initialized");
      Map<? extends StorageMetadata, Exception> exceptions = Maps.newHashMap();
      final Set<StorageMetadata> metadata = Sets.newHashSet();
      Map<StorageMetadata, ListenableFuture<?>> responses = Maps.newHashMap();
      for (StorageMetadata md : in) {
         if (md.getType() == StorageType.BLOB) {
            final ListenableFuture<? extends StorageMetadata> future = ablobstore.blobMetadata(
                     container, md.getName());
            future.addListener(new Runnable() {
               @Override
               public void run() {
                  try {
                     metadata.add(future.get());
                  } catch (InterruptedException e) {
                     Throwables.propagate(e);
                  } catch (ExecutionException e) {
                     Throwables.propagate(e);
                  }
               }
            }, sameThreadExecutor());
            responses.put(md, future);
         } else {
            metadata.add(md);
         }
      }
      exceptions = awaitCompletion(responses, userExecutor, maxTime, logger, String.format(
               "getting metadata from containerName: %s", container));
      if (exceptions.size() > 0)
         throw new BlobRuntimeException(String.format("errors getting from container %s: %s",
                  container, exceptions));
      return new PageSetImpl<StorageMetadata>(metadata, in.getNextMarker());
   }
}