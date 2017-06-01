package com.autonubil.identity.persistence.impl;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PersistenceTest {

	
	private BasicPersistenceService bps;
	
	
	@Before
	public void init() {
		bps = new BasicPersistenceService();
		bps.setBasePath("./target_test/"+Math.random());
	}
	
	@After
	public void exit() {
		File f = new File(bps.getBasePath());
		f.delete();
	}
	
	
	@Test
	public void a() {
		
	}
	
	
}
