package com.sky.listener;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.sky.context.BaseContext;
import com.sky.service.ShoppingCartService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubmitStatusListener {

  private final ShoppingCartService shoppingCartService;

  @RabbitListener(bindings = @QueueBinding(value = @Queue(name = "trade.submit.success.queue", durable = "true"), exchange = @Exchange(name = "submit.direct"), key = "submit.success"))
  public void listenSubmit(Long userId) {
    BaseContext.setCurrentId(userId);
    shoppingCartService.cleanCart();
  }
}
