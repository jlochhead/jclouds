/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.blobstore;

import java.net.URI;

import javax.inject.Provider;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.domain.ContainerMetadata;
import org.jclouds.cloud.internal.CloudContextImpl;
import org.jclouds.lifecycle.Closer;

/**
 * @author Adrian Cole
 */
public class BlobStoreContextImpl<S extends BlobStore<C, M, B>, C extends ContainerMetadata, M extends BlobMetadata, B extends Blob<M>>
         extends CloudContextImpl<S> implements BlobStoreContext<S, C, M, B> {
   private final BlobMap.Factory<M, B> blobMapFactory;
   private final InputStreamMap.Factory<M> inputStreamMapFactory;
   private final Provider<B> blobProvider;

   public BlobStoreContextImpl(BlobMap.Factory<M, B> blobMapFactory,
            InputStreamMap.Factory<M> inputStreamMapFactory, Closer closer,
            Provider<B> blobProvider, S defaultApi, URI endPoint, String account) {
      super(closer, defaultApi, endPoint, account);
      this.blobMapFactory = blobMapFactory;
      this.inputStreamMapFactory = inputStreamMapFactory;
      this.blobProvider = blobProvider;
   }

   public BlobMap<M, B> createBlobMap(String container) {
      return blobMapFactory.create(container);
   }

   public InputStreamMap<M> createInputStreamMap(String container) {
      return inputStreamMapFactory.create(container);
   }

   @SuppressWarnings("unchecked")
   public B newBlob(String key) {
      Object object = blobProvider.get();
      B blob = (B) object;
      blob.getMetadata().setKey(key);
      return blob;
   }
}