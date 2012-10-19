/**
 * Copyright (c) 2012-2012 Malhar, Inc.
 * All rights reserved.
 */
package com.malhartech.dag;

import com.malhartech.annotation.InputPortFieldAnnotation;
import com.malhartech.annotation.OutputPortFieldAnnotation;
import com.malhartech.api.DefaultOutputPort;
import com.malhartech.api.Operator;
import com.malhartech.api.Operator.InputPort;
import com.malhartech.api.Operator.OutputPort;
import com.malhartech.api.Operator.Port;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;

/**
 * Utilities for dealing with {@link Operator} instances.
 */
public class Operators
{
  public interface OperatorDescriptor
  {
    public void addInputPort(Operator.InputPort<?> port, Field field, InputPortFieldAnnotation a);

    public void addOutputPort(Operator.OutputPort<?> port, Field field, OutputPortFieldAnnotation a);
  }

  public static class PortMappingDescriptor implements OperatorDescriptor
  {
    final public LinkedHashMap<String, Operator.InputPort<?>> inputPorts = new LinkedHashMap<String, Operator.InputPort<?>>();
    final public LinkedHashMap<String, Operator.OutputPort<?>> outputPorts = new LinkedHashMap<String, Operator.OutputPort<?>>();

    @Override
    public void addInputPort(Operator.InputPort<?> port, Field field, InputPortFieldAnnotation a)
    {
      String portName = (a == null || a.name() == null) ? field.getName() : a.name();
      inputPorts.put(portName, port);
    }

    @Override
    public void addOutputPort(Operator.OutputPort<?> port, Field field, OutputPortFieldAnnotation a)
    {
      String portName = (a == null || a.name() == null) ? field.getName() : a.name();
      outputPorts.put(portName, port);
    }
  };

  public static void describe(Operator operator, OperatorDescriptor descriptor)
  {
    Field[] fields = operator.getClass().getDeclaredFields();
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      field.setAccessible(true);
      InputPortFieldAnnotation inputAnnotation = field.getAnnotation(InputPortFieldAnnotation.class);
      OutputPortFieldAnnotation outputAnnotation = field.getAnnotation(OutputPortFieldAnnotation.class);

      try {
        Object portObject = field.get(operator);

        if (inputAnnotation == null && outputAnnotation == null) {
          if (portObject instanceof InputPort) {
            descriptor.addInputPort((Operator.InputPort<?>)portObject, field, inputAnnotation);
          }
          if (portObject instanceof OutputPort) {
            descriptor.addOutputPort((Operator.OutputPort<?>)portObject, field, outputAnnotation);
          }
        }
        else if (inputAnnotation == null) {
          if (portObject instanceof OutputPort) {
            descriptor.addOutputPort((Operator.OutputPort<?>)portObject, field, outputAnnotation);
          }
          else {
            throw new IllegalArgumentException("port is not of type " + OutputPort.class.getName());
          }
        }
        else if (outputAnnotation == null) {
          if (portObject instanceof InputPort) {
            descriptor.addInputPort((Operator.InputPort<?>)portObject, field, inputAnnotation);
          }
          else {
            throw new IllegalArgumentException("port is not of type " + InputPort.class.getName());
          }
        }
        else {
          if (portObject instanceof OutputPort) {
            descriptor.addOutputPort((Operator.OutputPort<?>)portObject, field, outputAnnotation);
          }
          else {
            throw new IllegalArgumentException("port is not of type " + OutputPort.class.getName());
          }
          if (portObject instanceof InputPort) {
            descriptor.addInputPort((Operator.InputPort<?>)portObject, field, inputAnnotation);
          }
          else {
            throw new IllegalArgumentException("port is not of type " + InputPort.class.getName());
          }
        }
      }
      catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }
}