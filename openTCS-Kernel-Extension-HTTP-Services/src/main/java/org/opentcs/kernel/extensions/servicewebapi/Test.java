package org.opentcs.kernel.extensions.servicewebapi;

import org.opentcs.components.kernel.KernelExtension;

public class Test implements KernelExtension {

  private volatile boolean enabled;

  @Override
  public void initialize() {
    if (enabled){
      return;
    }
    System.out.println("初始化~~~~~初始化~~~~~初始化~~~~~初始化~~~~~初始化~~~~~初始化~~~~~");
  }

  @Override
  public boolean isInitialized() {
    return enabled;
  }

  @Override
  public void terminate() {
    if (!enabled) {
      return;
    }
    enabled = false;
    System.out.println("结束！！");
  }
}
