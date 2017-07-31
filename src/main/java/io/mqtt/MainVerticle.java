package io.mqtt;

import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {
    vertx.deployVerticle(MqttServerVerticle.class.getCanonicalName());
    vertx.setTimer(5000, t -> vertx.deployVerticle(MqttClientVerticle.class.getCanonicalName()));
  }
}
