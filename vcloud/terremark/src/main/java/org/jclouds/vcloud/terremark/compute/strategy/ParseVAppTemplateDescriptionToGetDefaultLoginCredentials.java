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
package org.jclouds.vcloud.terremark.compute.strategy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.domain.VAppTemplate;

/**
 * @author Adrian Cole
 */
@Singleton
public class ParseVAppTemplateDescriptionToGetDefaultLoginCredentials implements
         PopulateDefaultLoginCredentialsForImageStrategy {
   
   public static final Pattern USER_PASSWORD_PATTERN = Pattern
            .compile(".*[Uu]sername: ([a-z]+) ?.*\n[Pp]assword: ([^ ]+) ?\n.*");

   @Override
   public Credentials execute(Object resourceToAuthenticate) {
      checkNotNull(resourceToAuthenticate);
      checkArgument(resourceToAuthenticate instanceof VAppTemplate,
               "Resource must be an VAppTemplate (for Terremark)");
      VAppTemplate template = (VAppTemplate) resourceToAuthenticate;
      if (template.getDescription().indexOf("Windows") >= 0) {
         return new Credentials("Administrator", null);
      } else {
         Matcher matcher = USER_PASSWORD_PATTERN.matcher(template.getDescription());
         if (matcher.find()) {
            return new Credentials(matcher.group(1), matcher.group(2));
         } else {
            throw new RuntimeException("could not parse username/password for image: "
                     + template.getId() + "\n" + template.getDescription());
         }
      }
   }
}
