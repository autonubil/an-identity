package com.autonubil.identity.less.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sommeri.less4j.Less4jException;

@Service
public class LessConfigService {

	private static final String List = null;
	private Map<String,String> config = new HashMap<>();
	private String css; 

	@Autowired
	private LessRenderer lessRenderer;
	
	
	private Path basePath;
	
	@Qualifier(value="lessDataSource")
	@Autowired
	private DataSource dataSource;
	
	public String getCss() {
		return css;
	}
	
	
	private Map<String,String> loadDefaults() throws JsonParseException, JsonMappingException, IOException {
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		try {
			String c =  templ.queryForObject("select config from less_config", new HashMap<String, Object>(), String.class);
			return new ObjectMapper().readValue(c, new TypeReference<Map<String,String>>() {});
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		File f = new File(basePath.toAbsolutePath()+"/less/defaults.json");
		return new ObjectMapper().readValue(f, new TypeReference<Map<String,String>>() {});
		
	}
	
	private void unpackSources() throws IOException {
		
		basePath = java.nio.file.Files.createTempDirectory("less");
		
		InputStream is = this.getClass().getResourceAsStream("/com/autonubil/intranet/less/less.zip");
		
		ZipInputStream zipIn = new ZipInputStream(is);
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String filePath = basePath.toAbsolutePath() + File.separator + entry.getName();
            if (!entry.isDirectory()) {
            	OutputStream tos = null;
            	try {
            		tos = new FileOutputStream(filePath);
            		IOUtils.copy(zipIn, tos);
				} catch (Exception e) {
					break;
				} finally {
					tos.flush();
					tos.close();
				}
            } else {
                new File(filePath).mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();        
		
	}
	
	
	
	
	@PostConstruct
	public void init() throws JsonParseException, JsonMappingException, IOException, Less4jException {
		unpackSources();
		this.config = loadDefaults();
		render();
	}
	
	public void render() {
		try {
			css = lessRenderer.render(new File(basePath.toAbsolutePath()+"/less/bootstrap.less"),config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setConfig(Map<String,String> config) throws IOException, Less4jException {
		this.config.clear();
		if(config!=null) {
			this.config.putAll(config);
		}
		render();
		Map<String,Object> p = new HashMap<>();
		NamedParameterJdbcTemplate templ = new NamedParameterJdbcTemplate(dataSource);
		templ.update("delete from less_config", p);
		p.put("id", "default");
		p.put("config", new ObjectMapper().writeValueAsString(this.config));
		templ.update("insert into less_config (id, config) VALUES (:id,:config)", p);
	}
	
	public Map<String,String> getConfig() {
		return new HashMap<>(this.config);
	}
	
	
	

	
}
