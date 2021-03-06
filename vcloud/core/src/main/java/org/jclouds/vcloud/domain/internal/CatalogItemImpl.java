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
package org.jclouds.vcloud.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.SortedMap;

import javax.annotation.Nullable;

import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.NamedResource;

import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class CatalogItemImpl extends NamedResourceImpl implements CatalogItem {

   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;
   private final String description;
   private final NamedResource entity;
   private final SortedMap<String, String> properties = Maps.newTreeMap();

   public CatalogItemImpl(String id, String name, URI location, @Nullable String description,
            NamedResource entity, SortedMap<String, String> properties) {
      super(id, name, VCloudMediaType.CATALOGITEM_XML, location);
      this.description = description;
      this.entity = checkNotNull(entity, "entity");
      this.properties.putAll(checkNotNull(properties, "properties"));
   }

   @Override
   public String getType() {
      return VCloudMediaType.CATALOGITEM_XML;
   }

   public NamedResource getEntity() {
      return entity;
   }

   @Override
   public String getDescription() {
      return description;
   }

   public SortedMap<String, String> getProperties() {
      return properties;
   }

   @Override
   public String toString() {
      return "CatalogItemImpl [id=" + getId() + ", name=" + getName() + ", location="
               + getLocation() + ", type=" + getType() + ", description=" + getDescription()
               + ", entity=" + entity + ", properties=" + properties + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((entity == null) ? 0 : entity.hashCode());
      result = prime * result + ((properties == null) ? 0 : properties.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      CatalogItemImpl other = (CatalogItemImpl) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (entity == null) {
         if (other.entity != null)
            return false;
      } else if (!entity.equals(other.entity))
         return false;
      if (properties == null) {
         if (other.properties != null)
            return false;
      } else if (!properties.equals(other.properties))
         return false;
      return true;
   }

}