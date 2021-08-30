/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springsource.restbucks.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Association;
import org.jmolecules.ddd.types.Identifier;
import org.springframework.util.Assert;
import org.springsource.restbucks.order.Order;
import org.springsource.restbucks.order.Order.OrderIdentifier;
import org.springsource.restbucks.payment.Payment.PaymentIdentifier;

/**
 * Baseclass for payment implementations.
 *
 * @author Oliver Gierke
 */
@Getter
@ToString
@NoArgsConstructor(force = true)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Payment<T extends AggregateRoot<T, PaymentIdentifier<T>>>
		implements AggregateRoot<T, PaymentIdentifier<T>> {

	private final PaymentIdentifier<T> id;

	@Column(name = "rborder") //
	private final Association<Order, OrderIdentifier> order;
	private final LocalDateTime paymentDate;

	/**
	 * Creates a new {@link Payment} referring to the given {@link Order}.
	 *
	 * @param order must not be {@literal null}.
	 */
	protected Payment(Order order) {

		Assert.notNull(order, "Order must not be null!");

		this.id = new PaymentIdentifier<T>(UUID.randomUUID().toString());
		this.order = Association.forAggregate(order);
		this.paymentDate = LocalDateTime.now();
	}

	/**
	 * Returns a receipt for the {@link Payment}.
	 *
	 * @return
	 */
	public Receipt getReceipt() {
		return new Receipt(paymentDate, order);
	}

	/**
	 * A receipt for an {@link Order} and a payment date.
	 *
	 * @author Oliver Gierke
	 */
	@Value
	public static class Receipt {

		private final LocalDateTime date;
		private final Association<Order, OrderIdentifier> order;
	}

	@Value
	public static class PaymentIdentifier<T extends AggregateRoot<T, PaymentIdentifier<T>>>
			implements Identifier<T, PaymentIdentifier<T>> {
		String id;
	}
}
