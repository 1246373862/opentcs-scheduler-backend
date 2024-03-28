package com.server.web.kernel;

import org.opentcs.access.KernelServicePortal;
import org.opentcs.access.rmi.KernelServicePortalBuilder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class KernelServiceConfig {
  private static KernelServicePortal kernelServicePortal;

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON) // 设置为单例
  public KernelServicePortal kernelServicePortal() {
    if (kernelServicePortal == null) {
      // 在此处配置连接参数，例如用户名、密码等
      kernelServicePortal = new KernelServicePortalBuilder("Alice", "xyz").build();
      kernelServicePortal.login("127.0.0.1", 1099);
    }
    return kernelServicePortal;
  }
}
