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
package org.jclouds.http.functions;

import static org.jclouds.util.Utils.propagateOrNull;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponseException;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

@Singleton
public class ReturnTrueOn404 implements Function<Exception, Boolean> {

   public Boolean apply(Exception from) {
      Iterable<HttpResponseException> throwables = Iterables.filter(
               Throwables.getCausalChain(from), HttpResponseException.class);
      if (Iterables.size(throwables) >= 1
               && Iterables.get(throwables, 0).getResponse().getStatusCode() == 404) {
         return true;
      }
      return Boolean.class.cast(propagateOrNull(from));
   }

}