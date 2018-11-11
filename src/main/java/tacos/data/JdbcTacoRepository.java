package tacos.data;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import tacos.Taco;

@Slf4j
@Repository
public class JdbcTacoRepository implements TacoRepository {
	
	private JdbcTemplate jdbc;
	
	public JdbcTacoRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}
	
	@Override
	public Taco save(Taco taco) {
		long tacoId = saveTacoInfo(taco);
		taco.setId(tacoId);
		
		for (String ingredient : taco.getIngredients()) {
			saveIngredientToTaco(ingredient, tacoId);
		}
		
		return taco;
	}

	private void saveIngredientToTaco(String ingredient, long tacoId) {
		jdbc.update("insert into Taco_Ingredients (taco, ingredient) values (?, ?)",
				tacoId, ingredient);
		
	}

	private long saveTacoInfo(Taco taco) {
		taco.setCreatedAt(new Date());
		
		log.info("taco: " + taco);
		
		PreparedStatementCreator psc = new PreparedStatementCreatorFactory(
				"insert into Taco (name, createdAt) values (?, ?)", 
				Types.VARCHAR, Types.TIMESTAMP
				).newPreparedStatementCreator(
						Arrays.asList(taco.getName(), new Timestamp(taco.getCreatedAt().getTime()))
						);
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbc.update(psc, keyHolder);
		
		log.info("keyholder: " + keyHolder.getKeyList());
		return keyHolder.getKey().longValue();
	}
	
	
}
