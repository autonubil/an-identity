package com.autonubil.identity.less.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.autonubil.identity.less.entities.CssLink;

import de.disk0.db.sqlbuilder.SqlBuilderFactory;
import de.disk0.db.sqlbuilder.enums.Comparator;
import de.disk0.db.sqlbuilder.interfaces.Delete;
import de.disk0.db.sqlbuilder.interfaces.Insert;
import de.disk0.db.sqlbuilder.interfaces.Select;
import de.disk0.db.sqlbuilder.interfaces.Table;
import de.disk0.db.sqlbuilder.interfaces.Update;

@Service
public class CssConfigService {

	private static Log log = LogFactory.getLog(CssConfigService.class);
	
	@Autowired
	@Qualifier(value="lessDataSource")
	private DataSource dataSource;
	
	public List<CssLink> list(String id) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		Select s = SqlBuilderFactory.select();
		Table t = s.fromTable("css_config");
		if(id!=null) {
			s.where(s.condition(t,"id",Comparator.EQ,id));
		}
		
		s.order(t, "ord", true);
		
		return templ.query(s.toSQL(), s.getParams(), new RowMapper<CssLink>() {

			@Override
			public CssLink mapRow(ResultSet rs, int arg1) throws SQLException {
				CssLink l = new CssLink();
				l.setId(rs.getString("id"));
				l.setName(rs.getString("name"));
				l.setRel(rs.getString("r"));
				l.setType(rs.getString("t"));
				l.setHref(rs.getString("h"));
				l.setOrder(rs.getInt("ord"));
				return l;
			}
		});
	}
	
	public CssLink get(String id) {
		List<CssLink> cs = list(id);
		if(cs.size()>0) {
			return cs.get(0);
		}
		return null;
	}
	
	
	public CssLink save(CssLink link) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		if(link.getId() == null) {
			link.setId(UUID.randomUUID().toString());
			Insert insert = SqlBuilderFactory.insert("css_config");
			insert.addField("id", link.getId());
			insert.addField("name", link.getName());
			insert.addField("r", link.getRel());
			insert.addField("t", link.getType());
			insert.addField("h", link.getHref());
			insert.addField("ord", link.getOrder());
			templ.update(insert.toSQL(), insert.getParams());
		} else {
			Update update = SqlBuilderFactory.update("css_config");
			update.set("id", link.getId());
			update.set("name", link.getName());
			update.set("r", link.getRel());
			update.set("t", link.getType());
			update.set("h", link.getHref());
			update.set("ord", link.getOrder());
			update.where(update.condition(update.getTable(), "id", Comparator.EQ, link.getId()));
			templ.update(update.toSQL(), update.getParams());
		}
		return get(link.getId());
	}
	
	public void delete(String id) {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		Delete d = SqlBuilderFactory.delete("css_config");
		d.where(d.condition(d.getTable(), "id", Comparator.EQ, id));
		templ.update(d.toSQL(), d.getParams());
	}
	
	
	
}
