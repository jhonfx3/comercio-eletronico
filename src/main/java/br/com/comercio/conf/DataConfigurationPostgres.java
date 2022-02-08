package br.com.comercio.conf;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@Profile(value = "prod")
public class DataConfigurationPostgres {
	@Value("${spring.datasource.url}")
	private String dbUrl;

	@Bean
	public DataSource dataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(dbUrl);
		return new HikariDataSource(config);
	}
	/*
	 * @Bean public JpaVendorAdapter jpaVendorAdapter() { HibernateJpaVendorAdapter
	 * adapter = new HibernateJpaVendorAdapter();
	 * adapter.setDatabase(Database.POSTGRESQL); adapter.setShowSql(false);
	 * adapter.setGenerateDdl(true);
	 * adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQL95Dialect");
	 * adapter.setPrepareConnection(true); return adapter; }
	 */

}
