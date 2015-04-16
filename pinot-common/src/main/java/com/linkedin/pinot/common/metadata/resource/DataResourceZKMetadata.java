/**
 * Copyright (C) 2014-2015 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkedin.pinot.common.metadata.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.helix.ZNRecord;

import com.linkedin.pinot.common.metadata.ZKMetadata;
import com.linkedin.pinot.common.utils.CommonConstants.Helix;
import com.linkedin.pinot.common.utils.CommonConstants.Helix.ResourceType;


public abstract class DataResourceZKMetadata implements ZKMetadata {
  private String _resourceName;
  private ResourceType _resourceType = null;
  private List<String> _tableList = new ArrayList<String>();
  private String _timeColumnName;
  private String _timeType;
  private int _numDataInstances;
  private int _numDataReplicas;
  private TimeUnit _retentionTimeUnit;
  private int _retentionTimeValue;
  private String _brokerTag;
  private int _NumBrokerInstance;
  private Map<String, String> _metadata = new HashMap<String, String>();

  public DataResourceZKMetadata() {
    _retentionTimeUnit = TimeUnit.DAYS;
    _retentionTimeValue = -1;
  }

  public DataResourceZKMetadata(ZNRecord znRecord) {
    _resourceName = znRecord.getSimpleField(Helix.DataSource.RESOURCE_NAME);
    _resourceType = znRecord.getEnumField(Helix.DataSource.RESOURCE_TYPE, ResourceType.class, ResourceType.OFFLINE);
    _tableList = znRecord.getListField(Helix.DataSource.TABLE_NAME);
    _timeColumnName = znRecord.getSimpleField(Helix.DataSource.TIME_COLUMN_NAME);
    _timeType = znRecord.getSimpleField(Helix.DataSource.TIME_TYPE);
    _numDataInstances = znRecord.getIntField(Helix.DataSource.NUMBER_OF_DATA_INSTANCES, -1);
    _numDataReplicas = znRecord.getIntField(Helix.DataSource.NUMBER_OF_COPIES, -1);
    _retentionTimeUnit = znRecord.getEnumField(Helix.DataSource.RETENTION_TIME_UNIT, TimeUnit.class, TimeUnit.DAYS);
    _retentionTimeValue = znRecord.getIntField(Helix.DataSource.RETENTION_TIME_VALUE, -1);
    _brokerTag = znRecord.getSimpleField(Helix.DataSource.BROKER_TAG_NAME);
    _NumBrokerInstance = znRecord.getIntField(Helix.DataSource.NUMBER_OF_BROKER_INSTANCES, -1);
    _metadata = znRecord.getMapField(Helix.DataSource.METADATA);
  }

  public String getResourceName() {
    return _resourceName;
  }

  public void setResourceName(String resourceName) {
    _resourceName = resourceName;
  }

  public ResourceType getResourceType() {
    return _resourceType;
  }

  protected void setResourceType(ResourceType resourceType) {
    _resourceType = resourceType;
  }

  public List<String> getTableList() {
    return _tableList;
  }

  public void setTableList(List<String> tableList) {
    _tableList = tableList;
  }

  public void addToTableList(String newTableToAdd) {
    _tableList.add(newTableToAdd);
  }

  public String getTimeColumnName() {
    return _timeColumnName;
  }

  public void setTimeColumnName(String timeColumnName) {
    _timeColumnName = timeColumnName;
  }

  public String getTimeType() {
    return _timeType;
  }

  public void setTimeType(String timeType) {
    _timeType = timeType;
  }

  public int getNumDataInstances() {
    return _numDataInstances;
  }

  public void setNumDataInstances(int numDataInstances) {
    _numDataInstances = numDataInstances;
  }

  public int getNumDataReplicas() {
    return _numDataReplicas;
  }

  public void setNumDataReplicas(int numDataReplicas) {
    _numDataReplicas = numDataReplicas;
  }

  public TimeUnit getRetentionTimeUnit() {
    return _retentionTimeUnit;
  }

  public void setRetentionTimeUnit(TimeUnit retentionTimeUnit) {
    _retentionTimeUnit = retentionTimeUnit;
  }

  public int getRetentionTimeValue() {
    return _retentionTimeValue;
  }

  public void setRetentionTimeValue(int retentionTimeValue) {
    _retentionTimeValue = retentionTimeValue;
  }

  public String getBrokerTag() {
    return _brokerTag;
  }

  public void setBrokerTag(String brokerTag) {
    _brokerTag = brokerTag;
  }

  public int getNumBrokerInstance() {
    return _NumBrokerInstance;
  }

  public void setNumBrokerInstance(int numBrokerInstance) {
    _NumBrokerInstance = numBrokerInstance;
  }

  public Map<String, String> getMetadata() {
    return _metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
    _metadata = metadata;
  }

  public void addToMetadata(String key, String value) {
    _metadata.put(key, value);
  }

  @Override
  public boolean equals(Object dataResourceMetadata) {
    if (!(dataResourceMetadata instanceof DataResourceZKMetadata)) {
      return false;
    }

    DataResourceZKMetadata resourceMetadata = (DataResourceZKMetadata) dataResourceMetadata;
    if (!getResourceName().equals(resourceMetadata.getResourceName()) ||
        getResourceType() != resourceMetadata.getResourceType() ||
        !getTimeColumnName().equals(resourceMetadata.getTimeColumnName()) ||
        !getTimeType().equals(resourceMetadata.getTimeType()) ||
        getNumDataInstances() != resourceMetadata.getNumDataInstances() ||
        getNumDataReplicas() != resourceMetadata.getNumDataReplicas() ||
        getNumBrokerInstance() != resourceMetadata.getNumBrokerInstance() ||
        getRetentionTimeUnit() != resourceMetadata.getRetentionTimeUnit() ||
        getRetentionTimeValue() != resourceMetadata.getRetentionTimeValue() ||
        !getBrokerTag().equals(resourceMetadata.getBrokerTag())) {
      return false;
    }
    if (getTableList().size() == resourceMetadata.getTableList().size()) {
      if (!getTableList().isEmpty()) {
        String[] tableArray1 = getTableList().toArray(new String[0]);
        String[] tableArray2 = resourceMetadata.getTableList().toArray(new String[0]);
        Arrays.sort(tableArray1);
        Arrays.sort(tableArray2);
        for (int i = 0; i < tableArray1.length; ++i) {
          if (!tableArray1[i].equals(tableArray2[i])) {
            return false;
          }
        }
      }
    } else {
      return false;
    }
    if (getMetadata().size() == resourceMetadata.getMetadata().size()) {
      if (!getMetadata().isEmpty()) {
        for (String key : getMetadata().keySet()) {
          if (resourceMetadata.getMetadata().containsKey(key)) {
            if (getMetadata().get(key) == null) {
              if (resourceMetadata.getMetadata().get(key) != null) {
                return false;
              }
            } else {
              if (!getMetadata().get(key).equals(resourceMetadata.getMetadata().get(key))) {
                return false;
              }
            }
          } else {
            return false;
          }
        }
      }
    } else {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = _resourceName != null ? _resourceName.hashCode() : 0;
    result = 31 * result + (_resourceType != null ? _resourceType.hashCode() : 0);
    result = 31 * result + (_tableList != null ? _tableList.hashCode() : 0);
    result = 31 * result + (_timeColumnName != null ? _timeColumnName.hashCode() : 0);
    result = 31 * result + (_timeType != null ? _timeType.hashCode() : 0);
    result = 31 * result + _numDataInstances;
    result = 31 * result + _numDataReplicas;
    result = 31 * result + (_retentionTimeUnit != null ? _retentionTimeUnit.hashCode() : 0);
    result = 31 * result + _retentionTimeValue;
    result = 31 * result + (_brokerTag != null ? _brokerTag.hashCode() : 0);
    result = 31 * result + _NumBrokerInstance;
    result = 31 * result + (_metadata != null ? _metadata.hashCode() : 0);
    return result;
  }

  @Override
  public ZNRecord toZNRecord() {
    ZNRecord znRecord = new ZNRecord(_resourceName);
    znRecord.setSimpleField(Helix.DataSource.RESOURCE_NAME, _resourceName);
    znRecord.setEnumField(Helix.DataSource.RESOURCE_TYPE, _resourceType);
    znRecord.setListField(Helix.DataSource.TABLE_NAME, _tableList);
    znRecord.setSimpleField(Helix.DataSource.TIME_COLUMN_NAME, _timeColumnName);
    znRecord.setSimpleField(Helix.DataSource.TIME_TYPE, _timeType);
    znRecord.setIntField(Helix.DataSource.NUMBER_OF_DATA_INSTANCES, _numDataInstances);
    znRecord.setIntField(Helix.DataSource.NUMBER_OF_COPIES, _numDataReplicas);
    znRecord.setEnumField(Helix.DataSource.RETENTION_TIME_UNIT, _retentionTimeUnit);
    znRecord.setIntField(Helix.DataSource.RETENTION_TIME_VALUE, _retentionTimeValue);
    znRecord.setSimpleField(Helix.DataSource.BROKER_TAG_NAME, _brokerTag);
    znRecord.setIntField(Helix.DataSource.NUMBER_OF_BROKER_INSTANCES, _NumBrokerInstance);
    znRecord.setMapField(Helix.DataSource.METADATA, _metadata);
    return znRecord;
  }

  @Override
  public String toString() {
    return "DataResourceZKMetadata{" + toZNRecord().toString() + "}";
  }
}
