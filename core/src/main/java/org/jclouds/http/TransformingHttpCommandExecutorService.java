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
package org.jclouds.http;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Executor which will invoke and transform the response of an {@code EndpointCommand} into generic
 * type <T>.
 * 
 * @author Adrian Cole
 */
public interface TransformingHttpCommandExecutorService {
   /**
    * 
    * Submits the command and transforms the result before requested via {@link ListenableFuture#get()}.
    * 
    * @param <T>
    *           type that is required from the value.
    * @param command
    *           what to execute
    * @param responseTransformer
    *           how to transform the response from the above command
    * @return value of the intended response.
    */
   public <T> ListenableFuture<T> submit(HttpCommand command, Function<HttpResponse, T> responseTransformer);

}
