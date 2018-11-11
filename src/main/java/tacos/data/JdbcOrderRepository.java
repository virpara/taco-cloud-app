package tacos.data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import tacos.Order;
import tacos.Taco;

@Slf4j
@Repository
public class JdbcOrderRepository implements OrderRepository{
	
	private SimpleJdbcInsert orderInserter;
	private SimpleJdbcInsert orderTacoInserter;
	private ObjectMapper objectMapper;
	
	@Autowired
	public JdbcOrderRepository(JdbcTemplate jdbc) {
		this.orderInserter = new SimpleJdbcInsert(jdbc)
				.withTableName("Taco_Order")
				.usingGeneratedKeyColumns("id");
		
		this.orderTacoInserter = new SimpleJdbcInsert(jdbc)
				.withTableName("Taco_Order_Tacos");
		
		this.objectMapper = new ObjectMapper();
	}
	
	@Override
	public Order save(Order order) {
		order.setPlacedAt(new Date());
		long orderId = saveOrderDetails(order);
		order.setId(orderId);
		
		List<Taco> tacos = order.getTacos();
		
		for (Taco taco : tacos) {
			saveTacoToOrder(taco, orderId);
		}
		
		return order;
	}

	private void saveTacoToOrder(Taco taco, long orderId) {
		Map<String, Object> values = new HashMap<>();
		values.put("tacoOrder", orderId);
		values.put("taco", taco.getId());
		
		log.info("Taco_Order_Tacos: " + values);
		orderTacoInserter.execute(values);
	}

	private long saveOrderDetails(Order order) {
		@SuppressWarnings("unchecked")
		
		// there is discrepancy with column names and variable names in Order
		Map<String, Object> values = objectMapper.convertValue(order, Map.class);

		// objectMapper converts date to long which is incompatible with placedAt field in the table
//		values.put("placedAt", order.getPlacedAt());
		
		log.info("Taco_Order: " + values);
		
		long orderId = orderInserter.executeAndReturnKey(values).longValue();
		
		return orderId;
	}
	
	
}
